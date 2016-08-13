package com.endercrypt.com.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.endercrypt.com.exception.AndroidServerException;

public class AndroidServer
{
	public static final int PACKAGE_LIMIT = 1024;

	private final int port;
	private final DatagramSocket socket;
	private Optional<Listener> listener = Optional.empty();

	private boolean alive = true;
	private boolean running = false;
	private BlockingQueue<DatagramPacket> queue;
	private PacketReceiver packetReceiver;
	private PacketProcessor packetProcessor;

	public AndroidServer(int port) throws SocketException
	{
		this.port = port;
		socket = new DatagramSocket(port);
	}

	public void setListener(Listener listener)
	{
		this.listener = Optional.of(listener);
	}

	public void start()
	{
		if (running)
			throw new IllegalStateException("The server is already running");
		if (alive == false)
			throw new AndroidServerException("Server has been shutdown and cannot be restarted");
		if (listener.isPresent() == false)
			throw new AndroidServerException("Server listener not set");
		// prepare queue for transfering packets
		queue = new LinkedBlockingQueue<>();
		// prepare listener for UDP packets
		packetReceiver = new PacketReceiver(queue, socket);
		packetReceiver.start();
		// prepare processor for processing the UDP packets
		packetProcessor = new PacketProcessor(queue, listener.get());
		packetProcessor.start();
		// start the server listener
		listener.get().start(socket);

		running = true;
	}

	public void stop()
	{
		if (running == false)
			throw new IllegalStateException("The server was already paused");
		if (alive == false)
			throw new AndroidServerException("Server has been shutdown");
		// turn off
		running = false;
		// clear unprocessed packets
		queue.clear();
		queue = null;
		// stop udp listener
		packetReceiver.interrupt();
		packetReceiver = null;
		// stop udp packet processor
		packetProcessor.interrupt();
		packetProcessor = null;
		// stop server listener
		listener.get().stop();
	}

	public void shutdown()
	{
		if (isRunning())
		{
			stop();
		}
		alive = false;
		listener.get().shutdown();
	}

	public boolean isRunning()
	{
		return running;
	}
}
