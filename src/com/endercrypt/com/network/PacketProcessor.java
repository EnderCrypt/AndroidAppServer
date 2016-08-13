package com.endercrypt.com.network;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

import com.endercrypt.com.util.ContainedThread;

public class PacketProcessor extends ContainedThread
{
	private BlockingQueue<DatagramPacket> queue;
	private Listener listener;

	public PacketProcessor(BlockingQueue<DatagramPacket> queue, Listener listener)
	{
		this.queue = queue;
		this.listener = listener;
	}

	@Override
	public void run()
	{
		while (Thread.interrupted() == false)
		{
			// receive
			DatagramPacket packet;
			try
			{
				packet = queue.take();
			}
			catch (InterruptedException e)
			{
				return;
			}
			byte[] bytes = packet.getData();
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

			// process
			try
			{
				listener.onNetworkActivity(stream, packet);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
