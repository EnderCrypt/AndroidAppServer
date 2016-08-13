package com.endercrypt.com.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import com.endercrypt.com.util.ContainedThread;

public class PacketReceiver extends ContainedThread
{
	private BlockingQueue<DatagramPacket> queue;
	private DatagramSocket socket;

	public PacketReceiver(BlockingQueue<DatagramPacket> queue, DatagramSocket socket)
	{
		this.queue = queue;
		this.socket = socket;
	}

	@Override
	protected void run()
	{
		while (true)
		{
			// receive data
			byte[] bytes = new byte[AndroidServer.PACKAGE_LIMIT];
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
			try
			{
				socket.receive(packet);
			}
			catch (IOException e)
			{
				System.err.println(e);
				continue;
			}
			// transfer
			queue.add(packet);
		}
	}
}
