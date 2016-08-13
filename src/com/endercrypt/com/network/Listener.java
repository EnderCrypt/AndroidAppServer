package com.endercrypt.com.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Listener
{
	public void start(DatagramSocket socket);

	public void stop();

	public void shutdown();

	public void onNetworkActivity(InputStream stream, DatagramPacket packet) throws IOException;
}
