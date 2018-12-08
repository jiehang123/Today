> 基础

1.多个线程同时读写，读线程的数量远远大于写线程，你认为应该如何解决 并发的问题？你会选择加什么样的锁？<br>
2.JAVA的AQS是否了解，它是⼲嘛的？<br>
3.除了synchronized关键字之外，你是怎么来保障线程安全的？<br>
4.什么时候需要加volatile关键字？它能保证线程安全吗？<br>
5.线程池内的线程如果全部忙，提交⼀个新的任务，会发⽣什么？队列全部 塞满了之后，还是忙，再提交会发⽣什么？<br>
6.Tomcat本身的参数你⼀般会怎么调整？<br>
7.synchronized关键字锁住的是什么东⻄？在字节码中是怎么表示的？在内 存中的对象上表现为什么？<br>
8.wait/notify/notifyAll⽅法需不需要被包含在synchronized块中？这是为什 么？<br>
9.ExecutorService你⼀般是怎么⽤的？是每个service放⼀个还是⼀个项⽬ ⾥⾯放⼀个？有什么好处？<br>

> Spring

1.你有没有⽤过Spring的AOP? 是⽤来⼲嘛的? ⼤概会怎么使⽤？<br>
2.如果⼀个接⼝有2个不同的实现, 那么怎么来Autowire⼀个指定的实现？<br>
3.Spring的声明式事务 @Transaction注解⼀般写在什么位置? 抛出了异常 会⾃动回滚吗？有没有办法控制不触发回滚?<br>
4.如果想在某个Bean⽣成并装配完毕后执⾏⾃⼰的逻辑，可以什么⽅式实 现？<br>
5.SpringBoot没有放到web容器⾥为什么能跑HTTP服务？<br>
6.SpringBoot中如果你想使⽤⾃定义的配置⽂件⽽不仅仅是 application.properties，应该怎么弄？<br>
7.SpringMVC中RequestMapping可以指定GET, POST⽅法么？怎么指定？<br>
8.SpringMVC如果希望把输出的Object(例如XXResult或者XXResponse)这 种包装为JSON输出, 应该怎么处理?<br>
9.怎样拦截SpringMVC的异常，然后做⾃定义的处理，⽐如打⽇志或者包装 成JSON<br>
10.struts1和struts2的区别<br>
11.struts2和springMVC的区别<br>
12.spring框架中需要引用哪些jar包，以及这些jar包的用途<br>
13.springMVC的原理<br>
14.springMVC注解的意思<br>
15.spring中beanFactory和ApplicationContext的联系和区别<br>
16.spring注入的几种方式<br>
17.spring如何实现事物管理的<br>
18.springIOC和AOP的原理<br>
19.hibernate中的1级和2级缓存的使用方式以及区别原理<br>
20.spring中循环注入的方式<br>

> MySQL<br>

1.如果有很多数据插入MYSQL 你会选择什么方式?<br>
2.如果查询很慢，你会想到的第⼀个⽅式是什么？索引是⼲嘛的?<br>
3.如果建了一个单列索引，查询的时候查出2列，会用到这个单列索引吗？<br>
4.如果建了一个包含多个列的索引，查询的时候只用了第一列，能不能用上这个索引？查三列呢？<br>
5.接上题，如果where条件后⾯带有⼀个 i + 5 < 100 会使用到这个索引吗？<br>
6.怎么看是否用到了某个索引？<br>
7.like %aaa%会使用索引吗? like aaa%呢?<br>
8.drop、truncate、delete的区别？<br>
9.平时你们是怎么监控数据库的? 慢SQL是怎么排查的？<br>
10.你们数据库是否支持emoji表情，如果不支持，如何操作?<br>
11.你们的数据库单表数据量是多少？一般多大的时候开始出现查询性能急 剧下降？<br>
12.查询死掉了，想要找出执行的查询进程用什么命令？找出来之后一般你会干嘛？<br>
13.读写分离是怎么做的？你认为中间件会怎么来操作？这样操作跟事务有什么关系？ <br>
14.分库分表有没有做过？线上的迁移过程是怎么样的？如何确定数据是正 确的？<br>
15.MySQL常用命令<br>
16.数据库中事物的特征？<br>
17.JDBC的使用？<br>
18.InnodB与MyISAM的区别<br>
19.MySQL为什么使用B+树作为索引？<br>

> JVM

1.你知道哪些或者你们线上使用什么GC策略? 它有什么优势，适用于什么 场景？<br>
2.JAVA类加载器包括几种？它们之间的关系是怎么样的？双亲委派机制是什么意思？有什么好处？<br>
3.如何自定义一个类加载器？你使用过哪些或者你在什么场景下需要一个自定义的类加载器吗？<br>
4.堆内存设置的参数是什么？ <br>
5.Perm Space中保存什么数据? 会引起OutOfMemory吗？ <br>
6.做gc时，一个对象在内存各个Space中被移动的顺序是什么？<br>
7.你有没有遇到过OutOfMemory问题？你是怎么来处理这个问题的？处理 过程中有哪些收获？<br>
1.8之后Perm Space有哪些变动? MetaSpace大小默认是无限的么? 还是你们会通过什么方式来指定大小?<br>
Jstack是干什么的? Jstat呢? 如果线上程序周期性地出现卡顿，你怀疑可 能是gc导致的，你会怎么来排查这个问题？线程日志一般你会看其中的什么部分？<br>
StackOverFlow异常有没有遇到过？一般你猜测会在什么情况下被触发？如何指定一个线程的堆栈大小？一般你们写多少？<br>

> 多线程

1.什么是线程？<br>
2.线程和进程有什么区别？<br>
3.如何在Java中实现线程？<br>
4.用Runnable还是Thread？<br>
6.Thread 类中的start() 和 run() 方法有什么区别？<br>
7.Java中CyclicBarrier 和 CountDownLatch有什么不同？<br>
8.Java中的volatile 变量是什么？<br>
9.Java中的同步集合与并发集合有什么区别？<br>
10.如何避免死锁？<br>
11.Java中活锁和死锁有什么区别？<br>
12.Java中synchronized 和 ReentrantLock 有什么不同？<br>
13.Java中ConcurrentHashMap的并发度是什么？<br>
14.如何在Java中创建Immutable对象？<br>
15.单例模式的双检锁是什么？<br>
16.写出3条你遵循的多线程最佳实践<br>
17.如何避免死锁？<br>
18.常用的线程池模式以及不同线程池的使用场景<br>

> Netty

1.BIO、NIO和AIO的区别？<br>
2.NIO的组成？<br>
3.Netty的特点？<br>
4.Netty的线程模型？<br>
5.TCP 粘包/拆包的原因及解决方法？<br>
6.了解哪几种序列化协议？<br>
7.如何选择序列化协议？<br>
8.Netty的零拷贝实现？<br>
9.Netty的高性能表现在哪些方面？<br>
10.NIOEventLoopGroup源码？<br>

> Redis

1.Redis与Memorycache的区别？<br>
2.Redis的五种数据结构？<br>
3.渐进式rehash过程？<br>
4.rehash源码？<br>
5.持久化机制<br>
6.reaof源码？<br>
7.事务与事件<br>
8.主从复制<br>
9.启动过程<br>
10.集群<br>
11.Redis的6种数据淘汰策略<br>
12.redis的并发竞争问题？<br>

> Hadoop

1.HDFS的特点？<br>
2.客户端从HDFS中读写数据过程？<br>
3.HDFS的文件目录结构？<br>
4.NameNode的内存结构？<br>
5.NameNode的重启优化？<br>
6.Git的使用？<br>
7.Maven的使用
