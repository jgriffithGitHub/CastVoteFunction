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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Function
{
	private Logger log = null;
	
	@FunctionName("CastVote")
	public HttpResponseMessage run(
				@HttpTrigger( name = "req", 
								methods =  { HttpMethod.POST }, 
								authLevel = AuthorizationLevel.ANONYMOUS) 
			HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context)
	{
		log = context.getLogger();
		log.info("Java HTTP trigger processed a request.");

		//Logger logger = Logger.getLogger("com.mysql.cj");  
		//logger.setLevel(Level.FINE);
		//logger.setParent(log);
		
		// Parse query parameter
		Optional<String> body = request.getBody();

		if (body == null || body.isEmpty())
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("You didn't pass a body. Please pass a vote.").build();

		String bodyData = body.get();
		if ("".equals(bodyData))
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("The body was empty. Please pass a vote.").build();

		log.info("Body: " + bodyData);
		VoteModel voteModel = new VoteModel(bodyData);
		VoteManager vm = new VoteManager();
		
		vm.castVote(voteModel, log);
		return request.createResponseBuilder(HttpStatus.OK).body("Thanks for voting.").build();
	}
}
