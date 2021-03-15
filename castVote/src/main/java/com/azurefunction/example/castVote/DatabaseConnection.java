package com.azurefunction.example.castVote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection
{
	Properties properties = null;
	Connection connection = null;
			
	public DatabaseConnection()
	{
		try
		{
			properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));
	
			//logger.info("Connecting to the database");
			//logger.info("URL: " + properties.getProperty("url"));
			//logger.info("user: " + properties.getProperty("user"));
			//logger.info("password: " + properties.getProperty("password"));
	
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			//logger.info("Database connection test: " + connection.getCatalog());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		return connection;
	}
}
