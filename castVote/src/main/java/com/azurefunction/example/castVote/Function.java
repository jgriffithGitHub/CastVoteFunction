package com.azurefunction.example.castVote;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Function
{
	private static final Logger log;

	static
	{
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
		log = Logger.getLogger(Function.class.getName());
	}

	@FunctionName("CastVote")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods =
	{ HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context)
	{
		context.getLogger().info("Java HTTP trigger processed a request.");

		// Parse query parameter
		Optional<String> body = request.getBody();

		if (body == null)
		{
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a vote.").build();
		}

		VoteModel voteModel = new VoteModel(body.get());
		castVote(voteModel);
		return request.createResponseBuilder(HttpStatus.OK).body("Thanks for voting.").build();
	}

	private void castVote(VoteModel voteModel)
	{
		try
		{
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
			System.out.println("Exception: " + e.getCause().getMessage());
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
