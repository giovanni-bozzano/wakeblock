package com.giovannibozzano.wakeblock.patcher;

public class PatcherError extends PatcherStatus
{
	private final String message;

	public PatcherError(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
