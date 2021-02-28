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
								methods =  { HttpMethod.GET, HttpMethod.POST }, 
								authLevel = AuthorizationLevel.ANONYMOUS)
			HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context)
	{
		log = context.getLogger();
		log.info("Java HTTP trigger processed a request.");
		log.info("Request Method: " + request.getHttpMethod());
		
		Map<String,String> headers = request.getHeaders();
		
		String principalName = headers.get("X-MS-CLIENT-PRINCIPAL-NAME");
		String principalId = headers.get("X-MS-CLIENT-PRINCIPAL-ID");
		
		principalName = (principalName == null ? "" : principalName);
		principalId = (principalId == null ? "" : principalId);
		
		log.info("principalName: " + principalName);
		log.info("principalId: " + principalId);
		
		VoteModel voteModel = null;
		
		// Parse query parameter
		if(request.getHttpMethod() == HttpMethod.GET)
		{
			log.info("HTTP Method: GET");
			Map<String, String> qStringParams = request.getQueryParameters();
			voteModel = new VoteModel(qStringParams);
			if (voteModel.getVote() == 0 || voteModel.getVoterId() == null)
				return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("You didn't pass a vote on the query string. Please pass a vote.").build();
		}
		else if(request.getHttpMethod() == HttpMethod.POST)
		{
			log.info("HTTP Method: POST");
			Optional<String> body = request.getBody();
			if (body == null)
				return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("You didn't pass a body. Please pass a vote.").build();

			String bodyData = body.get();
			if ("".equals(bodyData))
				return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("The body was empty. Please pass a vote.").build();

			log.info("Body: " + bodyData);
			voteModel = new VoteModel(bodyData);
		}
		else
		{
			voteModel = new VoteModel(0, "No Name");			
		}
		
		if (voteModel == null)
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a vote.").build();

		voteModel.setVoterId(voteModel.getVoterId() + ":" + principalId + ":" + principalName);
		VoteManager vm = new VoteManager();
		
		vm.castVote(voteModel, log);
		return request.createResponseBuilder(HttpStatus.OK).body("Thanks for voting.").build();
	}
}
