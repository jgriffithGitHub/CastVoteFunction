package com.azurefunction.example.castVote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		try
		{
			if(connection == null)
				connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return connection;
	}
	
	public void closeConnection()
	{
		try
		{
			connection.close();
			connection = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
