package com.endercrypt.com.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.endercrypt.com.util.ByteUtil;

public class ClientBeacon
{
	private final String firstName;
	private final String lastName;
	private final String beaconAddress;
	private final long timestamp;
	private final InetAddress clientAddress;

	public ClientBeacon(ClientBeacon beacon)
	{
		this.firstName = beacon.firstName;
		this.lastName = beacon.lastName;
		this.beaconAddress = beacon.beaconAddress;
		this.timestamp = System.currentTimeMillis();
		this.clientAddress = beacon.clientAddress;
	}

	public ClientBeacon(InputStream input, DatagramPacket packet) throws IOException
	{
		firstName = ByteUtil.readString(input);
		lastName = ByteUtil.readString(input);
		beaconAddress = ByteUtil.readString(input);
		timestamp = System.currentTimeMillis();
		clientAddress = packet.getAddress();
	}

	public InetAddress getClientAddress()
	{
		return clientAddress;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getBeaconAddress()
	{
		return beaconAddress;
	}

	public String getLastName()
	{
		return lastName;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beaconAddress == null) ? 0 : beaconAddress.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ClientBeacon other = (ClientBeacon) obj;
		if (beaconAddress == null)
		{
			if (other.beaconAddress != null) return false;
		}
		else if (!beaconAddress.equals(other.beaconAddress)) return false;
		if (firstName == null)
		{
			if (other.firstName != null) return false;
		}
		else if (!firstName.equals(other.firstName)) return false;
		if (lastName == null)
		{
			if (other.lastName != null) return false;
		}
		else if (!lastName.equals(other.lastName)) return false;
		return true;
	}

}
