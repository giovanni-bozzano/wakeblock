package com.giovannibozzano.wakeblock.enums;

public enum WakeLockStatus
{
	NOT_PENDING(0),
	PENDING_UPDATE(1),
	PENDING_CREATION(2);
	private final int id;

	WakeLockStatus(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}
