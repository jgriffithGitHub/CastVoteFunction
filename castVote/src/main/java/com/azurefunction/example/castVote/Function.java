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
import java.util.*;
import java.util.Map.Entry;
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

		String principalName = "None provided";
		String principalId = "None provided";
		
		String retText = "Headers: ";
		Map<String,String> headers = request.getHeaders();
		
		if(headers == null || headers.isEmpty())
		{
			log.info("No headers");			
		}
		else
		{
			Set<Entry<String, String>> keySet = headers.entrySet();
			if(keySet == null || keySet.isEmpty())
			{
				log.info("No headers");			
			}
			else
			{				
				principalName = headers.get("x-ms-client-principal-name");
				principalId = headers.get("x-ms-client-principal-id");
			}
		}
		
		log.info("principalName: " + principalName);
		log.info("principalId: " + principalId);

		VoteModel voteModel = null;
		String bodyData = "Request Method = " + request.getHttpMethod();
				
		String uiTemplate = "";
		try
		{
			PageBuilder pb = new PageBuilder();
			
			pb.loadTemplate();
			int electionId = pb.setElectionId(log);			
			pb.setTitle(log);
			// Parse query parameter
			// Note that the request method can be null, so we have to assume 
			// some method in order to get the tests to work at all.
			if(request.getHttpMethod() == HttpMethod.POST)
			{
				Optional<String> body = request.getBody();
				if (body == null)
				{
					pb.setMessage(retText + "You didn't pass a body. Please pass a vote.", log);
					uiTemplate = pb.getPage();
					return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "text/html").body(uiTemplate).build();
				}
				
				bodyData = body.get();
				if ("".equals(bodyData))
				{
					pb.setMessage(retText + "The body was empty. Please pass a vote.", log);
					uiTemplate = pb.getPage();
					return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "text/html").body(uiTemplate).build();
				}

				log.info("Body: " + bodyData);
				voteModel = new VoteModel(bodyData, principalName, principalId);
			}
			else // Assume GET Method
			{
				Map<String, String> qStringParams = request.getQueryParameters();
				voteModel = new VoteModel(qStringParams, principalName, principalId);
			}
			
			VoteManager vm = new VoteManager();		
			if(!vm.castVote(voteModel, electionId, log))
			{
				pb.setMessage("We could not record your vote. Post body: " + bodyData, log);
				uiTemplate = pb.getPage();
				return request.createResponseBuilder(HttpStatus.BAD_REQUEST).header("Content-Type", "text/html").body(uiTemplate).build();
			}
			
			pb.setMessage("Thanks for voting " + principalName, log);
			uiTemplate = pb.getPage();
	 	} 
		catch (Exception e)
		{
			uiTemplate = "<html><head></head><body>Exception:<br>" + e.getMessage() + "</body></html>";
		}
		
		return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "text/html").body(uiTemplate).build();
	}
}
