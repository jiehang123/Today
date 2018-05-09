>在java并发编程中，除了内置锁之外还提供了另外一种加锁机制显示锁，显示锁是对内置锁的补充，提供和内置锁一样的互斥性（只能由单个线程执行）和内存可见性（修改的变量对所有线程及时可见），并且还和内置锁一样提供了可重入语义，不同的是，加锁解锁的过程都是可见的，锁的获取，等待，都可人为控制，极大的提高了加锁的灵活性。  
>本文就从比较简单的排它锁ReentrantLock来介绍显示锁的基本用法，下面给出显示锁的基本用法：
```
//显示锁
final Lock lock = new ReentrantLock(true);
try{
    lock.lock();
    //......
    }finally {
        lock.unlock();
    }
    
//隐式锁
final Object lock = new Object();
synchronized(lock) { //加锁
    //.....
}//解锁
```
>与synchronized关键字不同，在synchronized中加锁和解锁的过程都是不可见的，而显示锁的加锁和解锁都可人为控制，这种形式比内置锁复杂，且必须要在finally代码块中释放锁，如果线程离开同步代码块而没有释放锁，线程对锁的持有并不会自动解除。  
>ReentrantLock是常见的显示锁，由名字可知它是可重入的，所谓可重入的意思是一个线程在已经持有一个锁的情况下可反复获取该锁，字面意思有点生涩难懂，简单地用代码描述就是：
```
//显示锁重入
final Lock lock = new ReentrantLock(true);
        try{
            lock.lock();
            System.out.println("第一次持有锁");
            try{
                lock.lock();
                System.out.println("第二次重入锁");
            }finally {
                lock.unlock();
            }
        }finally {
            lock.unlock();
        }
//隐式锁重入
public void handle(){
            synchronized (lock){
                System.out.println("第一次持有锁");
                valve();
            }
        }

public void valve(){
        synchronized (lock){
            System.out.println("第二次重入锁");
        }
    }
    handle();
```
>如果所不可重入的话，上面这两段代码都会造成死锁，值得注意的是锁重入几次就要解锁几次，由于隐式锁的解锁是自动进行的，这种重入会比较安全，但在显示锁中一定要小心，少解锁一次，该线程都会一直持有锁，别的线程就没法执行。  
>深入学习ReentrantLock的源码，会发现重入锁有两种类型，一种是公平锁，一种是非公平锁，区别在获取锁的时候，唤醒的线程是按照队列顺序先到先得的方式获取锁，还是抢占式获取锁，实现如下。
```
public void lock() {
        sync.lock();
    }
```
>sync是ReentrantLock的一个内部类，继承AbstractQueuedSynchronizer，有公平锁FairSync和非公平锁NonfairSync两种实现，ReentranLock的实现就是依赖于同步基类AbstractQueuedSynchronizer。  
>不光重入锁是这样的，大多数同步器都是基于AbstractQueuedSynchronizer构建的，在ReentranLock中获取锁的操作就是使用AbstractQueuedSynchronizer中的acquire方法：
```
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
>tryAcquire方法用来获取锁，参数arg始终是1，代表着当前线程请求一个锁，而tryAcquire的实现依赖ReentrantLock的两个版本的实现，以公平锁为例：
```
//acquires为1，代表线程请求一个锁。
 protected final boolean tryAcquire(int acquires) {
            //获取当前线程
            final Thread current = Thread.currentThread();
            //当前的状态，代表持有锁的线程的个数，重入的话代表重入的次数。
            int c = getState();
            //0代表锁是空闲，未被任何线程持有
            if (c == 0) {
                //判断等待队列是否有线程在当前线程前面（越前，获取锁的优先级越高）
                //存在返回true，不存在（当前线程在队列头或者队列是空），返回false
                if (!hasQueuedPredecessors() &&
                //compareAndSetState更新同步状态
                    compareAndSetState(0, acquires)) {
                    //设置当前线程为执行许可
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            //当前线程是执行许可线程且状态不为0，说明锁重入
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                //如果小于0抛异常
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }
```

>非公平的锁加锁的实现仅仅去掉队列头判断即可，如果获取锁成功，则什么都不做，如果失败，则addWaiter将线程插入等待队列尾部，并通过acquireQueued当前线程阻塞。
>公平锁和非公平锁的解锁过程是一样的，都是调用抽象类Sync中的tryRealse方法，调用过程如下：
```
//releases始终为1,代表释放一个锁
protected final boolean tryRelease(int releases) {
        //当前状态减去1,判断当前状态
            int c = getState() - releases;
            //如果释放锁的线程不是当前执行线程，抛异常
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            //线程持有锁的数量为0（释放重入锁的计数）
            if (c == 0) {
                free = true;
                //设置当前执行线程为空
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
```
>锁的获取和释放都依赖于状态state，在非重入的情况下，只有当state为0的时候才允许当前线程持有锁，如果在非重入的情况下，当state不为0仍尝试去获取锁，线程则会被阻塞并添加到等待队列中去。