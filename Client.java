package com.jiehang;

import java.sql.Connection;
import java.sql.SQLException;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        for (int i = 0; i < 12; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection p = connectionPool.getConnection();
                    System.out.println(p);
                    try {
                        Thread.sleep(200);
                        if (finalI > 3) {
                            System.out.println(finalI);
                            p.close();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        Thread.sleep(50000000);

    }

}
