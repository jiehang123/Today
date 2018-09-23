### Executor、ExecutorService、Executors、Callable、Future与FutureTask  

##1. 引子
初学Java多线程，常使用Thread与Runnable创建、启动线程。如下例:
```
Thread t1 = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
});
t1.start();
```
我们需要自己创建、启动Thread对象。

> 重要概念：

1. 实现Runnable的类应该被看作一项任务，而不是一个线程。在Java多线程中我们一定要有一个明确的理解，任务和线程是不同的概念。可以使用线程(Thread)执行任务(比如Runnable)，但任务不是线程。
2. Java多线程中有两种不同类型的任务，Runnable类型任务（无返回值）与Callable类型任务(有返回值)。  
## 2. 使用Executor执行线程  
一些已有的执行器可以帮我们管理Thread对象。你无需自己创建与控制Thread对象。比如，你不用在代码中编写new Thread或者thread1.start()也一样可以使用多线程。如下例:  
```
ExecutorService exec = Executors.newCachedThreadPool();
for (int i = 0; i < 5; i++) {//5个任务
    exec.submit(new Runnable() {
        @Override
        public void run() {            
            System.out.println(Thread.currentThread().getName()+" doing task");
         }
     });
}
exec.shutdown();  //关闭线程池
```
输出如下:
```
pool-1-thread-2 doing task
pool-1-thread-1 doing task
pool-1-thread-3 doing task
pool-1-thread-4 doing task
pool-1-thread-5 doing task
```
从输出我们可以看到，exec使用了线程池1中的5个线程做了这几个任务。  
  
这个例子中exec这个Executor负责管理任务，所谓的任务在这里就是实现了Runnable接口的匿名内部类。至于要使用几个线程，什么时候启动这些线程，是用线程池还是用单个线程来完成这些任务，我们无需操心。完全由exec这个执行器来负责。在这里exec(newCachedThreadPool)指向是一个可以根据需求创建新线程的线程池。

> Executors相当于执行器的工厂类，包含各种常用执行器的工厂方法，可以直接创建常用的执行器。几种常用的执行器如下：
  
Executors.newCachedThreadPool,根据需要可以创建新线程的线程池。线程池中曾经创建的线程，在完成某个任务后也许会被用来完成另外一项任务。
  
Executors.newFixedThreadPool(int nThreads) ,创建一个可重用固定线程数的线程池。这个线程池里最多包含nThread个线程。
  
Executors.newSingleThreadExecutor() ,创建一个使用单个 worker 线程的 Executor。即使任务再多，也只用1个线程完成任务。
  
Executors.newSingleThreadScheduledExecutor() ,创建一个单线程执行程序，它可安排在给定延迟后运行命令或者定期执行。
  
newSingleThreadExecutor例子如下：
```
ExecutorService exec = Executors.newSingleThreadExecutor();
for (int i = 0; i < 5; i++) {
    exec.execute(new Runnable() {//execute方法接收Runnable对象，无返回值
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
        }
    });
}
exec.shutdown();
```
输出如下：
```
pool-1-thread-1
pool-1-thread-1
pool-1-thread-1
pool-1-thread-1
pool-1-thread-1
```
可以看出，虽然有5个任务(5个new Runnable)，但是只由1个线程来完成。
  
最佳实践：我们应该使用现有Executor或ExecutorService实现类。比如前面说的newCachedThreadPool可以使用线程池帮我们降低开销（创建一个新的线程是有一定代价的），而newFixedThreadPool则可以限制并发线程数。即，我们一般使用Executors的工厂方法来创建我们需要的执行器。

### Executor与ExecutorService的常用方法
> execute方法：  
Executor接口只有void execute(Runnable command)方法。从方法声明中我们可以看到入参为Runnable类型对象。常用的例子如下:  
```
Executor executor = anExecutor;
executor.execute(new RunnableTask1());
```
但里面具体怎么执行，是否调用线程执行由相应的Executor接口实现类决定。比如前面的newCachedThreadPool使用线程池来进行执行。Executor将任务提交与每个任务如何运行（如何使用线程、调度）相分离。
  
> submit方法：
  
ExecutorService接口继承自Executor接口，扩展了父接口中的execute方法。有两个常用的submit方法
```
Future<?> submit(Runnable task) 
<T> Future<T> submit(Callable<T> task)
```
可以看到这两个常用方法一个接收Runnable类型入参，一个接收Callable类型入参。Callable入参允许任务返回值，而Runnable无返回值。也就是说如果我们希望线程有一个返回结果，我们应该使用Callable类型入参。
  
> invokeAll与invokeAny方法:
  
批量执行一组Callable任务。其中invokeAll是等所有任务完成后返回代表结果的Future列表。而invokeAny是等这一批任务中的任何一个任务完成后就返回。从两个方法的返回结果我们也可以看出两个方法的不同：
```
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
<T> T invokeAny(Collection<? extends Callable<T>> tasks)
```
invokeAll返回的是List<Future，而invoke返回的是T。
  
> shutdown()方法:
  
启动一次顺序关闭，执行以前提交的任务，但不接受新任务。执行此方法后，线程池等待任务结束后就关闭，同时不再接收新的任务。如果执行完shutdown()方法后，再去执行execute方法则直接抛出RejectedExecutionException。不要问我为什么知道...刚从坑里爬出来。
  
原则：只要ExecutorService(线程池)不再使用，就应该关闭，以回收资源。要注意这个不再使用。
  
上述方法较多，可以配合后面的实例进行理解。可以先记住execute方法与shutdown方法。
  
## 3. 使用Callable与Future
### Callable接口
Runnable接口中的public void run()方法无返回值，如果我们希望线程运算后将结果返回，使用Runnable就无能为力。这时候我们应使用Callable。Callable代表有返回值的任务。一个实现Callable接口的类如下所示：
```
class CalcTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        return Thread.currentThread().getName();
    }
}
```
这个任务比较简单，就是返回当前线程的名字。与Runnable相比较有一个返回值，在这里返回值类型为String，也可以为其他类型。
  
使用如下代码进行调用：
```
ExecutorService exec = Executors.newCachedThreadPool();
List<Callable<String>> taskList = new ArrayList<Callable<String>>();
/* 往任务列表中添加5个任务 */
for (int i = 0; i < 5; i++) {
    taskList.add(new CalcTask());
}
/* 结果列表:存放任务完成返回的值 */
List<Future<String>> resultList = new ArrayList<Future<String>>();
try {
    /*invokeAll批量运行所有任务, submit提交单个任务*/
    resultList = exec.invokeAll(taskList);
} catch (InterruptedException e) {
    e.printStackTrace();
}
try {
    /*从future中输出每个任务的返回值*/
    for (Future<String> future : resultList) {
        System.out.println(future.get());//get方法会阻塞直到结果返回
    }
} catch (InterruptedException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}
```
输出如下:
```
pool-1-thread-1
pool-1-thread-2
pool-1-thread-3
pool-1-thread-4
pool-1-thread-5
```
### Future接口
上面的例子中我们使用了Future接口。Future 表示异步计算的结果。它提供了检查计算是否完成的方法，以等待计算的完成，并获取计算的结果。上面的例子中exec执行器执行了一个Callable类型的任务列表然后得到了Futuer类型的结果列表resultList。
   
> get方法
等待计算完成，然后获取其结果。

> isDone方法
用来查询任务是否做完，例子如下：
```
/*新建一个Callable任务*/
Callable<Integer> callableTask = new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        System.out.println("Calculating 1+1!");
        TimeUnit.SECONDS.sleep(2);//休眠2秒
        return 2;
    }
}; 
ExecutorService executor = Executors.newCachedThreadPool();
Future<Integer> result = executor.submit(callableTask);
executor.shutdown();
while(!result.isDone()){//isDone()方法可以查询子线程是否做完
    System.out.println("子线程正在执行");
    TimeUnit.SECONDS.sleep(1);//休眠1秒
}
try {
    System.out.println("子线程执行结果:"+result.get());
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```
输出如下：
```
Calculating 1+1!
子线程正在执行
子线程正在执行
子线程执行结果:2
```
## 4.FutureTask
FutureTask类是 Future 接口的一个实现。FutureTask类实现了RunnableFuture接口，RunnableFuture继承了Runnable接口和Future接口，所以：
  
FutureTask可以作为Runnable被线程执行  
可以作为Future得到传入的Callable对象的返回值  
例子如下：  
```
FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
   @Override
   public Integer call() throws Exception {
        System.out.println("futureTask is wokring 1+1!");
        return 2;
   }
});
Thread t1 = new Thread(futureTask);//1.可以作为Runnable类型对象使用
t1.start();
try {
   System.out.println(futureTask.get());//2.可以作为Future类型对象得到线程运算返回值
} catch (ExecutionException e) {
   e.printStackTrace();
}
```
输出如下:
```
futureTask is wokring 1+1!
2
```
可以看出FutureTask可以当作一个有返回值的Runnable任务来用。

> 分析：FutureTask<Integer> futureTask = new FutureTask<>(new Callable...)相当于把Callable任务转换为Runnable任务，就可以使用线程来执行该任务。而futureTask.get()相当于将Callable转化为Future，从而得到异步运算的结果。
  
ExecutorService执行器除了接收Runnable与Callable类型的入参，也可以接收FutureTask类型，例子如下：
```
FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        System.out.println("futureTask is wokring 1+1!");
        TimeUnit.SECONDS.sleep(2);
        return 2;
    }
});
ExecutorService executor = Executors.newCachedThreadPool();
executor.submit(futureTask);//也可以使用execute，证明其是一个Runnable类型对象
executor.shutdown();
while(!futureTask.isDone()){
    System.out.println("子线程还没做完，我再睡会");
    TimeUnit.SECONDS.sleep(1);
}
try {
    System.out.println("子线程运行的结果："+futureTask.get());
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

原博地址： https://www.cnblogs.com/zhrb/p/6372799.html
