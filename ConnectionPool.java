package com.jiehang;

import java.sql.Connection;
import java.util.*;

public class ConnectionPool {

    private int minPoolSize = 5;
    private int maxPoolSize = 8;
    private long timeOut = 20 * 1000;
    private long checkTime;

    private Vector<PooledConnection> connections;

    private ConnectionPool() {
        initConnectionPool();
        buildTimerCheck();
    }

    private void buildTimerCheck() {
        Timer timer = new Timer("ChekcTimeOutThread", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(new Date() + "------" + connections.size());

                for (PooledConnection p : connections) {
                    System.out.println(p);
                }

                if (connections.size() > minPoolSize) {
                    Iterator<PooledConnection> iterator = connections.iterator();
                    while (iterator.hasNext()) {
                        PooledConnection p = iterator.next();
                        if (connections.size() > minPoolSize && !p.isBusy() && System.currentTimeMillis() - p.getLastedTime() > timeOut) {
                            iterator.remove();
                        }
                    }
                }
            }
        }, 3 * 1000, 4 * 1000);
    }

    private static class InnerInstance {
        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }

    public static ConnectionPool getInstance() {
        return InnerInstance.INSTANCE;
    }

    public synchronized Connection getConnection() {
        if (connections == null) {
            System.out.println("please create pool first");
        }
        if (connections.size() < maxPoolSize) {
            PooledConnection pooledConnection = buildPooledConnection();
            connections.add(pooledConnection);
            pooledConnection.setBusy(true);
            return pooledConnection.getConnection();
        } else {
            for (PooledConnection pooledConnection : connections) {
                if (!pooledConnection.isBusy()) {
                    pooledConnection.setBusy(true);
                    return pooledConnection.getConnection();
                }
            }
        }
        return null;
    }

    private void initConnectionPool() {
        if (connections == null) {
            connections = new Vector<>();
        }
        while (connections.size() < minPoolSize) {
            connections.add(buildPooledConnection());
        }
    }

    private PooledConnection buildPooledConnection() {
        return new PooledConnection(false, System.currentTimeMillis());
    }


    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public Vector<PooledConnection> getConnections() {
        return connections;
    }

    public void setConnections(Vector<PooledConnection> connections) {
        this.connections = connections;
    }
}
