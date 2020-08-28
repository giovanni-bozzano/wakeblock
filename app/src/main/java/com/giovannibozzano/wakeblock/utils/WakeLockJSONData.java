package com.giovannibozzano.wakeblock.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WakeLockJSONData implements Serializable
{
	@SerializedName("acquiringPackages") private final List<String> acquiringPackages;
	@SerializedName("blockTime") private long blockTime;
	@SerializedName("isBlocked") private boolean isBlocked;

	public WakeLockJSONData(List<String> acquiringPackages, long blockTime, boolean isBlocked)
	{
		this.acquiringPackages = acquiringPackages;
		this.blockTime = blockTime;
		this.isBlocked = isBlocked;
	}

	public void addToAcquiringPackages(String acquiringPackage)
	{
		this.acquiringPackages.add(acquiringPackage);
	}

	public List<String> getAcquiringPackages()
	{
		return this.acquiringPackages;
	}

	public long getBlockTime()
	{
		return this.blockTime;
	}

	public void setBlockTime(long blockTime)
	{
		this.blockTime = blockTime;
	}

	public boolean getIsBlocked()
	{
		return this.isBlocked;
	}

	public void setIsBlocked(boolean isBlocked)
	{
		this.isBlocked = isBlocked;
	}
}
