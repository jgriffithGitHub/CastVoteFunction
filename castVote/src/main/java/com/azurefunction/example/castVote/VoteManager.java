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
	private Logger log;
	
	public VoteManager()
	{
		
	}
	
	public void castVote(VoteModel voteModel, Logger logger)
	{
		try
		{
			log = logger;
			
			String voterId = voteModel.getVoterId();
			int vote = voteModel.getVote();

			log.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			log.info("Connecting to the database");
			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
			log.info("Database connection test: " + connection.getCatalog());

			// log.info("Create database schema");
			// Scanner scanner = new
			// Scanner(Function.class.getClassLoader().getResourceAsStream("schema.sql"));
			// Statement statement = connection.createStatement();
			// while (scanner.hasNextLine()) {
			// statement.execute(scanner.nextLine());
			// }

			insertData(voterId, vote, connection);

			log.info("Closing database connection");
			connection.close();
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}

	private void insertData(String voter, int vote, Connection connection) throws SQLException
	{
		log.info("Insert data");
		PreparedStatement insertStatement = connection
				.prepareStatement("INSERT INTO votes (voter, vote) VALUES (?, ?);");

		insertStatement.setString(1, voter);
		insertStatement.setInt(2, vote);
		insertStatement.executeUpdate();
	}
}
