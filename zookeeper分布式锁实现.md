## zookeeper分布式锁

### 独占锁加锁步骤
> 步骤：  
> 1.每个线程进行上锁操作都会在zookeeper的锁根节点下创建一个临时节点  
> 2.获取锁根节点下的所有临时子节点列表，同时对根节点注册Watcher监听  
> 3.确定自己创建的节点在所有子节点中的顺序  
> 4.如果是最小就获取锁, 如果不是最小就表示没有获取到锁，把当前线程挂起  
> 5.其他线程释放锁, 接收到Watcher监听，将当前线程唤醒，再次重复步骤2  

代码实现：
```
package com.zookeeper.lock;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkDistributeLock implements DistributeLock, Watcher {

    // 锁根节点，其他线程创建的序列节点都是其子节点
    private static final String ROOT_NODE = "/DIS-LOCK";

    private ZooKeeper zooKeeper = null;

    // 创建zookeeper连接是异步操作，需要创建完后才能使用
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    // 当前线程创建的临时子节点路径
    private String currentNode;
    
    private Thread currentThread;

    /**
     * 创建zkConnection
     */
    public ZkDistributeLock(String connectionString) {
        try {
            zooKeeper = new ZooKeeper(connectionString, 5000, this);
            countDownLatch.await();
            currentThread = Thread.currentThread();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加锁入口
     * @return
     */
    @Override
    public boolean lock() {
        try {
            // 创建一个临时序列子节点
            currentNode = zooKeeper.create(ROOT_NODE + "/ZK-R-", null, OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            return tryLock();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 死循环中获取锁，要不获取锁成功，要不将当前线程挂起
     * @return
     */
    private boolean tryLock() {
        for (; ; ) {
            try {
                List<String> childrenList = zooKeeper.getChildren(ROOT_NODE, this);
                String minNode = childrenList.stream().sorted().findFirst().get();
                if (null != minNode && minNode.equals(currentNode.substring(currentNode.lastIndexOf("/") + 1))) {
                    zooKeeper.removeWatches(ROOT_NODE, this, WatcherType.Children, true);
                    return true;
                } else {
                    waitLock();
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 挂起当前线程
     */
    private void waitLock() {
        // System.out.println(new Date().getSeconds() + ": " + Thread.currentThread().getName() + " wait lock...");
        LockSupport.park();
    }

    /**
     * 删除当前线程创建的临时序列子节点
     * @return
     */
    @Override
    public boolean releaseLock() {
        try {
            zooKeeper.delete(currentNode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 注册Watcher监听实现的方法
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            // zkConnection创建的时候会进入if, 锁根节点的子节点数量改变的时候会进入else
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                countDownLatch.countDown();
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                // 唤醒特定线程
                LockSupport.unpark(currentThread);
            }
        }
    }
}

```

上面的实现有两个待改进的地方：  
> 1.独占锁每次只能有一个线程能访问共享资源，如果读多写少性能比较低，可以改为共享锁，将加锁创建的子节点分为写子节点和读子节点，
对于读请求，如果比自己小的子节点都是读子节点就可以获取共享锁，对于写请求，如果自己不是最小的子节点就进入等待；  
> 2.产生了羊群效应，所有线程注册的Watcher监听都是针对锁根节点，如果子节点列表有改动，所有线程都会被唤醒，但是最后只有最小的子节点能够获取锁，其他还是会被再次挂起，
本质上每个线程只需要关注比自己小的子节点的状态变化即可。

### 改进后的共享锁加锁步骤：  
> 步骤：    
> 1.每个读线程进行上锁操作都会在zookeeper的锁根节点下创建一个临时读子节点，写线程创建临时写子节点  
> 2.获取锁根节点下的所有临时读写子节点的列表并根据序列排序，此时不要注册任何Watcher监听  
> 3.1对于读锁就判断自己前面有没有写子节点，如果没有就获取成功  
> 3.2对于写锁就判断自己是不是最小的子节点，如果是就获取成功  
> 4.如果步骤3获取锁失败，就注册监听并把自己挂起  
> 4.1 对于读锁就调用exist()向比自己序号小的最后一个写子节点注册Watcher  
> 4.2 对于写锁就直接调用exist()向比自己序号小的最后一个节点注册Watcher  
> 5.如果一个线程释放锁, 注册Watcher监听的后一个线程会被唤醒，重复步骤2

代码改天写
