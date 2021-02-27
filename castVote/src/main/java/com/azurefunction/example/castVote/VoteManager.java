package com.azurefunction.example.castVote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class VoteManager
{
	private Logger logger;
	
	public VoteManager()
	{
		
	}
	
	public void castVote(VoteModel voteModel, Logger logger)
	{
		try
		{
			this.logger = logger;
			
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			logger.info("Connecting to the database");
			//log.info("URL: " + properties.getProperty("url") + sslCertKey); // + logKey);
			//log.info("user: " + properties.getProperty("user"));
			//log.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());

			insertData(voteModel, connection);

			logger.info("Closing database connection");
			connection.close();
			
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}

	private void insertData(VoteModel voteData, Connection connection) throws SQLException
	{
		logger.info("Insert data");
		
		String voter = voteData.getVoterId();
		int vote = voteData.getVote();
		
		logger.info("voter: " + voter);
		logger.info("vote: " + vote);
		
		PreparedStatement insertStatement = connection
				.prepareStatement("INSERT INTO votes (voter, vote) VALUES (?, ?);");

		insertStatement.setString(1, voter);
		insertStatement.setInt(2, vote);
		insertStatement.executeUpdate();
		insertStatement.close();
	}
}
