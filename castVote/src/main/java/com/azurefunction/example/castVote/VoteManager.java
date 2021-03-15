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
	private static final String KEY_VALUT_NAME = "azurefunctionsecrets";
	
	public VoteManager()
	{
		
	}
	
	public boolean castVote(VoteModel voteModel, Logger logger)
	{
		boolean retVal = true;
		
		try
		{
			this.logger = logger;
			
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			logger.info("Connecting to the database");
			logger.info("URL: " + properties.getProperty("url"));
			logger.info("user: " + properties.getProperty("user"));
			logger.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());
			
			retVal = insertData(voteModel, connection);

			logger.info("Closing database connection");
			connection.close();
			
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
		
		return retVal;
	}

	private boolean insertData(VoteModel voteData, Connection connection) throws SQLException
	{
		boolean retVal = true;
		
		logger.info("Insert data");
		
		String voter = voteData.getVoterId();
		int vote = voteData.getVote();
		
		logger.info("voter: " + voter);
		logger.info("vote: " + vote);
		
		PreparedStatement insertStatement = connection
				.prepareStatement("INSERT INTO votes (voter, vote) VALUES (?, ?);");

		insertStatement.setString(1, voter);
		insertStatement.setInt(2, vote);
		retVal = (insertStatement.executeUpdate() == 1);
		insertStatement.close();
		
		return retVal;
	}
}
