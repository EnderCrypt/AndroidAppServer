package com.endercrypt.com.repository;

import java.io.OutputStream;
import java.net.DatagramPacket;

public interface Repository
{
	public void update(boolean enterArea, ClientBeacon update);

	public DatagramPacket infoReq(String address, OutputStream responseStream);
}
