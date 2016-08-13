package com.endercrypt.com.implementation;

public enum MessageType
{
	BEACON, // client near beacon
	LEAVE, // client left beacon
	INFO_REQUEST; // client requests information about a specific beacon

	public static MessageType get(byte type)
	{
		return values()[type];
	}
}
