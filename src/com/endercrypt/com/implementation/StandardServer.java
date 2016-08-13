package com.endercrypt.com.implementation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.endercrypt.com.exception.AndroidServerException;
import com.endercrypt.com.network.Listener;
import com.endercrypt.com.repository.ClientBeacon;
import com.endercrypt.com.repository.Repository;
import com.endercrypt.com.util.ByteUtil;
import com.endercrypt.com.util.CountingHashMap;

public class StandardServer implements Listener
{
	private static final int TIMEOUT = 60;
	private static final int API_KEY = 902_658_982;

	private DatagramSocket socket;
	private Timer timer = new Timer();
	private Repository repository;
	private CountingHashMap<ClientBeacon> parkedClients = new CountingHashMap<>();

	public StandardServer(Repository repository)
	{
		this.repository = repository;
	}

	@Override
	public void start(DatagramSocket socket)
	{
		this.socket = socket;
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				pingClients();
			}
		}, 0, 1000);
	}

	@Override
	public void stop()
	{
		timer.purge();
	}

	@Override
	public void shutdown()
	{
		timer.cancel();
		allClientsLeave();
	}

	@Override
	public void onNetworkActivity(InputStream input, DatagramPacket packet) throws IOException
	{
		int apiKey = ByteUtil.readInt(input);
		if (apiKey != API_KEY)
			return;
		MessageType type = MessageType.get(ByteUtil.readByte(input));
		System.out.println(type + " from " + packet.getAddress());
		switch (type)
		{
		case BEACON:
			clientBeaconUpdate(new ClientBeacon(input, packet));
			break;
		case LEAVE:
			clientBeaconRemove(new ClientBeacon(input, packet));
			break;
		case INFO_REQUEST:
			clientInfoRequest(ByteUtil.readString(input), packet);
			break;
		default:
			throw new AndroidServerException("unknown network message type");
		}
	}

	private ByteArrayOutputStream newPacketStream(MessageType messageType) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ByteUtil.writeInt(stream, API_KEY);
		ByteUtil.writeByte(stream, (byte) messageType.ordinal());

		return stream;
	}

	private void clientBeaconUpdate(ClientBeacon client)
	{
		if (parkedClients.contains(client) == false)
		{
			repository.update(true, client);
		}
		parkedClients.set(client, TIMEOUT);
	}

	private void clientBeaconRemove(ClientBeacon client)
	{
		repository.update(false, client);
		parkedClients.remove(client);
	}

	private void clientInfoRequest(String address, DatagramPacket received)
	{
		try
		{
			ByteArrayOutputStream stream = newPacketStream(MessageType.INFO_REQUEST);
			repository.infoReq(address, stream);
			byte[] response = stream.toByteArray();
			DatagramPacket packet = new DatagramPacket(response, response.length, received.getAddress(), received.getPort());
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void allClientsLeave()
	{
		for (ClientBeacon client : parkedClients.keys())
		{
			repository.update(false, client);
		}
	}

	private void pingClients()
	{
		for (Entry<ClientBeacon, Integer> entry : parkedClients)
		{
			ClientBeacon client = entry.getKey();
			int countdown = entry.getValue();
			if (countdown <= 0)
			{
				clientBeaconRemove(new ClientBeacon(client));
			}
			else
			{
				parkedClients.dec(client);
			}
		}
	}
}
