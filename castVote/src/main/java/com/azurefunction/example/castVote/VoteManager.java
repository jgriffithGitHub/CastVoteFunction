package com.azurefunction.example.castVote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

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
			
			logger.info("Loading secrets");
			String keyVaultName = KEY_VALUT_NAME;
			String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
			logger.info("keyVaultUri: " + keyVaultUri);

		    DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
			logger.info("Default Credential Builder created");

			SecretClient secretClient = new SecretClientBuilder()
			    .vaultUrl(keyVaultUri)
			    .credential(defaultCredential)
			    .buildClient();		
			logger.info("Secret Client created");
			
			String url = secretClient.getSecret("url").getValue();
			String user = secretClient.getSecret("user").getValue();
			String password = secretClient.getSecret("password").getValue();
			logger.info("URL: " + url);
			logger.info("user: " + user);
			logger.info("password: " + password);

			Connection connection = DriverManager.getConnection(url, user, password);
			logger.info("Database connection test: " + connection.getCatalog());

			/*
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			logger.info("Connecting to the database");
			logger.info("URL: " + properties.getProperty("url")); // + logKey);
			logger.info("user: " + properties.getProperty("user"));
			logger.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());
			 */
			
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
