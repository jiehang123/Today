>在java中通过对对象调用wait和notify以及notifyAll来实现对线程的阻塞和唤醒，wait和notify都是作用于锁对象的函数，对于内置锁而言：
```
synchronized(lock){
    //..........
    lock.wait();
}
synchronized(lock){
    lock.notify();
}
```
>wait和notify/notifyAll都只能在同步块中被执行，即调用线程必须持有锁对象，调用了wait函数的线程将会一直阻塞，不同于被yield和sleep阻塞的线程仍然持有锁，wait的线程会将锁让出来，直到其他线程调用了同一锁对象的notify或者notifyAll方法，但是被唤醒并不代表这会立即获得锁对象，当被唤醒的线程重新获得锁对象之后才会继续下面的动作。
```          
线程 A ====> lock.wait() =====>被唤醒，等待B结束 =====> B结束了A开始执行
                                                                        
                                        
      线程 B ====> lock.notify() ====> 离开同步块 ===>  同步执行结束
```
>线程B notify会唤醒线程A，但被唤醒的线程A只有当线程B离开同步块，让出锁对象后，A才能继续向下执行，虽然notify可以唤醒阻塞的线程，但是并不能唤醒指定的线程，也就是说在锁对象上有多个wait线程的时候，无法唤醒想要唤醒的线程，notifyAll则可以唤醒所有正在等待的线程，唤醒的线程依照优先级或者系统调度进行锁的占用并执行。
```
threadWait1.setPriority(2);
threadWait2.setPriority(3);
threadWait3.setPriority(4);
```
>虽然这种阻塞唤醒机制基本可以满足大部分需求，但缺陷也很明显，这里通过一个简单的例子进行分析。
```
public class ShareQueue2 implements Queue{

    final Object lock = new Object();

    private int shareVar;

    public ShareQueue2(int shareVar) {
        this.shareVar = shareVar;
    }

    public void dequeue() {
        shareVar--;
        lock.notifyAll();
    }

    public void enqueue() {
        shareVar++;
        lock.notifyAll();
    }

    public void take() throws InterruptedException {
        synchronized (lock){
            while(shareVar == 0)
                lock.wait();
            dequeue();
        }
    }

    public void put() throws InterruptedException {
        synchronized (lock){
            while(shareVar == 10)
                lock.wait();
            enqueue();
        }
    }
}

```
>这是一个很简单的生产者消费者例子，内部只维护一个可自增自减的整型变量，当take方法取元素时，如果无元素可取即sharevar为0则线程阻塞，当put方法添加元素时，如果队列满了shareVar为10则线程阻塞，当阻塞条件不成立时，take方法和put方法都会唤醒在lock对象上阻塞的所有线程。  
>仔细分析通过notify和wait实现的生产者消费者模式，会觉得这种阻塞唤醒机制很粗放，首先，没法指定唤醒生产者或者消费者线程，只能将全部线程唤醒，让这些线程去竞争锁资源。其次，阻塞和唤醒必须严格遵守先阻塞后唤醒的顺序，没法像信号量机制那样，唤醒可在阻塞前运行，灵活性大大不足，最后notify和wait依赖锁对象，脱离锁对象无法正常工作。  
>针对第一个缺点，我曾经想使用如下方式改进，结果发现是想多了：
```
public void take() throws InterruptedException {
        synchronized (lock1){
            while(shareVar == 0)
                lock1.wait();
            lock2.notifyAll();
        }
    }

    public void put() throws InterruptedException {
        synchronized (lock2){
            while(shareVar == 10)
                lock2.wait();
            lock1.notifyAll();
        }
    }
```
>当take取元素且不被阻塞，唤醒所有生产者线程，当put添加元素且不被阻塞，唤醒所有消费者线程。这样做看上去很美好，其实违背了锁的持有者必须是当前线程的原则，即不能在lock2同步块中使用lock1，反之亦然。  
>虽然wait/notify机制存在着一定的局限，好在java中还有另外一种阻塞机制park和unpark。  
```
Unsafe.java
public native void unpark(Object var1);

public native void park(boolean var1, long var2);
```
>park和unpark方法都是native方法，用来实现线程的阻塞和唤醒，类似于信号量机制，unpark函数为线程提供一个许可，线程调用park函数等待许可，但这个许可不能重复使用，是一次性的，举个例子，即使在线程A上unpark了多次许可，真正park的时候可以用的许可只有一次，遗憾的是我们并不能直接使用park和unpark，只有jdk许可的类才可以使用，不过LockSupport提供了park和unpark的封装，基本用法如下。
```
public class Unpark {

    volatile private static int share = 2;

    private static class OneThread implements Runnable {

        private Thread thread;

        public void run() {
            while (share == 2){
                System.out.println("share值改变前");
                LockSupport.park();
                System.out.println("share值改变后");
            }
            System.out.println("开始工作");
        }

        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }

        public Thread getThread() {
            return thread;
        }
    }

    public static void main(String[] args) {
        OneThread thread = new OneThread();
        thread.start();
        //许可两次
        LockSupport.unpark(thread.getThread());
        LockSupport.unpark(thread.getThread());
//        share = 1;
    }
}
```
>执行结果
```
share值改变前
share值改变后
share值改变前
```
>在主线程中对线程thread许可两次，但是线程只被唤醒了一次。
>这种独特的阻塞机制不仅可以指定线程的唤醒，而且unpark还可以先于park调用，所以下面代码也是可以正常运行的。
```
LockSupport.unpark(this.thread);
            while (share == 2){
                System.out.println("share值改变前");
                LockSupport.park();
                System.out.println("share值改变后");
            }
```
>最后附上显示锁实现的生产者消费者模式，里面阻塞方法底层就是采用park和unpark实现的。
```
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ShareQueue implements Queue{

    final ReentrantLock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    private int shareVar;

    public ShareQueue(boolean fair, int shareVar) {
        this.shareVar = shareVar;
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public void dequeue() {
        shareVar--;
        notFull.signal();
    }

    public void enqueue() {
        shareVar++;
        notEmpty.signal();
    }

    public void take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (shareVar == 0)
                notEmpty.await();
            dequeue();
        } finally {
            lock.unlock();
        }
    }

    public void put() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (shareVar == 10)
                notFull.await();
            enqueue();
        } finally {
            lock.unlock();
        }
    }
}
```