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
	
	public boolean castVote(VoteModel voteModel, int electionId, Logger logger)
	{
		boolean retVal = true;
		
		try
		{
			//Properties properties = new Properties();
			//properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));
//
			//Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));			
			DatabaseConnection dbConn = new DatabaseConnection();
			retVal = insertData(voteModel, electionId, dbConn.getConnection());
			dbConn.closeConnection();
			
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
		
		return retVal;
	}

	private boolean insertData(VoteModel voteData, int electionId, Connection connection) throws SQLException
	{
		boolean retVal = true;
		
		String voter = voteData.getVoterId();
		int vote = voteData.getVote();
		
		PreparedStatement insertStatement = connection
				.prepareStatement("INSERT INTO votes (voter, vote, idElection) VALUES (?, ?, ?);");

		insertStatement.setString(1, voter);
		insertStatement.setInt(2, vote);
		insertStatement.setInt(3, electionId);
		retVal = (insertStatement.executeUpdate() == 1);
		insertStatement.close();
		
		return retVal;
	}
}
