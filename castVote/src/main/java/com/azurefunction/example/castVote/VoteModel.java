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

	public VoteModel(Map<String, String> qStringParams)
	{
		try
		{
			Set<Entry<String, String>> keys = qStringParams.entrySet();
			
			Iterator<Entry<String, String>> iter = keys.iterator();
			
			while(iter.hasNext())
			{
				String name = iter.next().getKey();
				System.out.println(name);

				String value = iter.next().getValue();
				System.out.println(value);

				if (name.equalsIgnoreCase(VOTE_NAME))
					vote = Integer.parseInt(value);
				else if (name.equalsIgnoreCase(VOTER_ID_NAME))
					voterId = value;
			}
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	public VoteModel(String reqBody)
	{
		try
		{
			String[] pairs = reqBody.split("\\&");
			for (int i = 0; i < pairs.length; i++)
			{
				String[] fields = pairs[i].split("=");
				String name = URLDecoder.decode(fields[0], "UTF-8");
				System.out.println(name);

				String value = URLDecoder.decode(fields[1], "UTF-8");
				System.out.println(value);

				if (name.equalsIgnoreCase(VOTE_NAME))
					vote = Integer.parseInt(value);
				else if (name.equalsIgnoreCase(VOTER_ID_NAME))
					voterId = value;
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
