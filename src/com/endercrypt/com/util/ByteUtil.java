package com.endercrypt.com.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

public class ByteUtil
{
	private static final ByteOrder ENDIAN = ByteOrder.LITTLE_ENDIAN;

	public static final int BYTE = (8 / 8);
	public static final int SHORT = (16 / 8);
	public static final int INT = (32 / 8);
	public static final int LONG = (64 / 8);

	// READING

	public static void skipNulls(InputStream input, int count, int bytesPerSkip) throws IOException
	{
		for (int i = 0; i < count * bytesPerSkip; i++)
		{
			int got = input.read();
			if (got != 0)
				throw new RuntimeException("skipped through " + got + " expected 0 (null)");
		}
	}

	public static boolean readBoolean(InputStream input) throws IOException
	{
		int value = input.read();
		if (value == 1)
			return Boolean.TRUE;
		if (value == 0)
			return Boolean.FALSE;
		throw new RuntimeException("Expected boolean, got " + value);
	}

	public static byte readByte(InputStream input) throws IOException
	{
		return readBytes(input, BYTE).get(0);
	}

	public static short readShort(InputStream input) throws IOException
	{
		return readBytes(input, SHORT).getShort(0);
	}

	public static int readInt(InputStream input) throws IOException
	{
		return readBytes(input, INT).getInt(0);
	}

	public static long readLong(InputStream input) throws IOException
	{
		return readBytes(input, LONG).getLong(0);
	}

	private static ByteBuffer readBytes(InputStream input, int bytes) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(bytes);
		bb.order(ENDIAN);
		for (int i = 0; i < bytes; i++)
			bb.put((byte) input.read());
		return bb;
	}

	public static String readString(InputStream input) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int byteCharacter = 0;
		while ((byteCharacter = input.read()) != 0)
		{
			sb.append((char) byteCharacter);
		}
		return sb.toString();
	}

	// DEBUG

	public static void printBytes(InputStream stream) throws IOException
	{
		while (stream.available() > 0)
			System.out.println(stream.read());
	}

	public static void printBytes(InputStream stream, int bytes) throws IOException
	{
		for (int i = 0; i < bytes; i++)
			System.out.println(stream.read());
	}

	public static void exportBytes(InputStream stream, File file) throws IOException
	{
		final int CHAR_COLLUMN = 10;
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			while (stream.available() > 0)
			{
				int charsThisLine = 0;
				LinkedList<Character> chars = new LinkedList<>();
				for (int i = 0; (i < CHAR_COLLUMN) && (stream.available() > 0); i++)
				{
					int c = stream.read();
					writer.write(Integer.toString(c));
					if (c >= 32 && c < 127)
					{
						chars.add((char) c);
					}
					writer.write('\t');
					writer.write('\t');
					charsThisLine++;
				}
				for (int i = charsThisLine; i < CHAR_COLLUMN; i++)
				{
					writer.write('\t');
					writer.write('\t');
				}
				writer.write('|');
				for (char c : chars)
				{
					writer.write(c);
				}
				for (int i = chars.size(); i < CHAR_COLLUMN; i++)
					writer.write(' ');
				writer.write('|');
				writer.write('\n');
			}
		}
	}

	// WRITING

	public static void writeNulls(OutputStream output, int count, int bytesPerSkip) throws IOException
	{
		for (int i = 0; i < count * bytesPerSkip; i++)
		{
			writeByte(output, (byte) 0);
		}
	}

	public static void writeBoolean(OutputStream output, boolean value) throws IOException
	{
		writeByte(output, (byte) (value ? 1 : 0));
	}

	public static void writeByte(OutputStream output, byte[] values) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(BYTE * values.length);
		bb.order(ENDIAN);
		bb.put(values);
		output.write(bb.array(), 0, bb.array().length);
	}

	public static void writeByte(OutputStream output, byte value) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(BYTE);
		bb.order(ENDIAN);
		bb.put(value);
		output.write(bb.array(), 0, bb.array().length);
	}

	public static void writeShort(OutputStream output, short value) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(SHORT);
		bb.order(ENDIAN);
		bb.putShort(value);
		output.write(bb.array(), 0, bb.array().length);
	}

	public static void writeInt(OutputStream output, int value) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(INT);
		bb.order(ENDIAN);
		bb.putInt(value);
		output.write(bb.array(), 0, bb.array().length);
	}

	public static void writeLong(OutputStream output, long value) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(LONG);
		bb.order(ENDIAN);
		bb.putLong(value);
		output.write(bb.array(), 0, bb.array().length);
	}

	public static void writeString(OutputStream output, String value) throws IOException
	{
		if (value != null)
		{
			output.write(value.getBytes());
		}
		writeNulls(output, 1, BYTE); // terminates the string
	}

}