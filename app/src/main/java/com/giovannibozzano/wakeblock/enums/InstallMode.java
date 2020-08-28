package com.giovannibozzano.wakeblock.enums;

public enum InstallMode
{
	SCHEDULED(0),
	MANUAL(1),
	MAGISK(2);
	private final int id;

	InstallMode(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public static InstallMode getFromId(int id)
	{
		for (InstallMode installMode : InstallMode.values()) {
			if (installMode.id == id) {
				return installMode;
			}
		}
		return null;
	}
}