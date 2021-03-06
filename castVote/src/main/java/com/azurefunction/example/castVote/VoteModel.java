package com.azurefunction.example.castVote;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class VoteModel
{
	private final String VOTE_NAME = "vote";
	private final String VOTER_ID_NAME = "voterid";

	private int vote;
	private String voterId;

	public VoteModel()
	{
	}

	public VoteModel(Map<String, String> qStringParams, String principalName, String principalId)
	{
		try
		{
			voterId = principalName + ":" + principalId;
					
			Set<Entry<String, String>> keys = qStringParams.entrySet();
			
			Iterator<Entry<String, String>> iter = keys.iterator();
			System.out.println("query string -- " + keys.size() + " parameters");
			
			while(iter.hasNext())
			{
				Entry<String, String> param = iter.next();
				String name = param.getKey();
				String value = param.getValue();
				System.out.println("query string -- " + name + ": " + value);

				if (name.equalsIgnoreCase(VOTE_NAME))
					vote = Integer.parseInt(value);
			}
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	public VoteModel(String reqBody, String principalName, String principalId)
	{
		try
		{
			voterId = principalName + ":" + principalId;

			String[] pairs = reqBody.split("\\&");
			for (int i = 0; i < pairs.length; i++)
			{
				String[] fields = pairs[i].split("=");
				String name = URLDecoder.decode(fields[0], "UTF-8");

				String value = URLDecoder.decode(fields[1], "UTF-8");

				if (name.equalsIgnoreCase(VOTE_NAME))
					vote = Integer.parseInt(value);
			}
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}

	public VoteModel(int vote, String voterId)
	{
		this.vote = vote;
		this.voterId = voterId;
	}

	public int getVote()
	{
		return vote;
	}

	public void setVote(int vote)
	{
		this.vote = vote;
	}

	public String getVoterId()
	{
		return voterId;
	}

	public void setVoterId(String voterId)
	{
		this.voterId = voterId;
	}

	@Override
	public String toString()
	{
		return "VoteModel{" + "voterId=" + voterId + ", vote='" + vote + '}';
	}
}
