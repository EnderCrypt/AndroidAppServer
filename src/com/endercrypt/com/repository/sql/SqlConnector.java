package com.endercrypt.com.repository.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.DatagramPacket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.endercrypt.com.Main;
import com.endercrypt.com.repository.ClientBeacon;
import com.endercrypt.com.repository.Repository;
import com.endercrypt.com.util.ByteUtil;

public class SqlConnector implements Repository
{
	private static String DB_FILE = "LogDatabase.db";
	private static final boolean JAR = Main.class.getResource(Main.class.getSimpleName() + ".class").toString().startsWith("rsrc:");

	private Connection connection;

	public SqlConnector() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
		executeSqlScript("createTables");
	}

	private BufferedReader readResource(String resource) throws FileNotFoundException
	{
		Reader fileReader;
		if (JAR)
		{
			InputStream inputStream = Main.class.getResourceAsStream("/src/com/endercrypt/com/repository/" + resource);
			fileReader = new InputStreamReader(inputStream);
		}
		else
		{
			fileReader = new FileReader("src/com/endercrypt/com/repository/" + resource);
		}
		return new BufferedReader(fileReader);
	}

	private String readSqlScript(String name)
	{
		StringBuilder sqlScriptBuilder = new StringBuilder();
		try (BufferedReader br = readResource("sql/" + name + ".sql"))
		{
			char c;
			while ((c = (char) br.read()) != 65535)
			{
				sqlScriptBuilder.append(c);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return sqlScriptBuilder.toString();
	}

	private void executeSqlScript(String name)
	{
		String sqlScript = readSqlScript(name);
		try (Statement statement = connection.createStatement())
		{
			statement.executeUpdate(sqlScript);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	private int getBeaconID(ClientBeacon beacon)
	{
		String sql = "SELECT ID FROM Beacon WHERE Address=?";
		try (PreparedStatement statment = connection.prepareStatement(sql))
		{
			statment.setString(1, beacon.getBeaconAddress());
			try (ResultSet resultSet = statment.executeQuery())
			{
				if (resultSet.next())
				{
					return resultSet.getInt(1);
				}
				throw new RuntimeException("Not a known beacon");
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(boolean enterArea, ClientBeacon client)
	{
		int BeaconID = getBeaconID(client);
		String sql = "INSERT INTO Log (BeaconID,EnterArea,FirstName,LastName,TimeStamp) VALUES (?,?,?,?,?)";
		try (PreparedStatement statment = connection.prepareStatement(sql))
		{
			statment.setInt(1, BeaconID);
			statment.setBoolean(2, enterArea);
			statment.setString(3, client.getFirstName());
			statment.setString(4, client.getLastName());
			statment.setLong(5, client.getTimestamp());
			statment.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public DatagramPacket infoReq(String address, OutputStream response)
	{
		String sql = "SELECT Name FROM Beacon WHERE Address=?";
		try (PreparedStatement statment = connection.prepareStatement(sql))
		{
			statment.setString(1, address);
			try (ResultSet resultSet = statment.executeQuery())
			{
				ByteUtil.writeString(response, address); // beacon address
				if (resultSet.next())
				{
					ByteUtil.writeString(response, resultSet.getString(1)); // beacon name
				}
				else
				{
					ByteUtil.writeNulls(response, 1, ByteUtil.BYTE);
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}
}
