package com.endercrypt.com;

import java.net.SocketException;
import java.sql.SQLException;

import com.endercrypt.com.implementation.StandardServer;
import com.endercrypt.com.network.AndroidServer;
import com.endercrypt.com.repository.sql.SqlConnector;

public class Main
{
	private static final int PORT = 36963;
	private static AndroidServer server;

	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException
	{
		System.out.println("Initialising server on port " + PORT + "...");
		server = new AndroidServer(PORT);
		System.out.println("Starting Sql...");
		server.setListener(new StandardServer(new SqlConnector()));
		System.out.println("Starting server...");
		server.start();
		if (server.isRunning())
		{
			System.out.println("Server online!");
		}
	}
}