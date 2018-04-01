package com.jiehang.dbcp.simple;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

public class SimpleDataSourceTest {

	public static void main(String[] args) {

		DataSource dataSource = createDataSource();
		for(int i=1; i<=10; i++) {
			new Thread(new MySimpleRunnable(dataSource, i)).start();
		}

	}
	
	private static DataSource createDataSource() {

        Properties properties = new Properties();
        BasicDataSource dataSource = null;
        try {
            properties.load(SimpleDataSourceTest.class.getResourceAsStream("/dbcpConfig.properties"));
            BasicDataSourceFactory basicDataSourceFactory = new BasicDataSourceFactory();
            dataSource = basicDataSourceFactory.createDataSource(properties);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}


class MySimpleRunnable implements  Runnable {

	private DataSource dataSource;
	private int i;
	Connection connection;
	Statement statement;
	ResultSet resultSet;

	public MySimpleRunnable(DataSource dataSource, int i) {
		this.dataSource = dataSource;
		this.i = i;
	}

	@Override
	public void run() {
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("select * from blog_category limit " + i + ",3");
			while (resultSet.next()) {
				String string = resultSet.getString("name");
				System.out.println("query-" + i + "name: " + string);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("-----------");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				System.out.println("close resources is failed.");
			}
		}
	}
}