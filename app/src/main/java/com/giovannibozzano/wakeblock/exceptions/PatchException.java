package com.giovannibozzano.wakeblock.exceptions;

public class PatchException extends Exception
{
	private final String message;

	public PatchException(String message, Throwable throwable)
	{
		super(message, throwable);
		this.message = message;
	}

	public PatchException(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}
}
