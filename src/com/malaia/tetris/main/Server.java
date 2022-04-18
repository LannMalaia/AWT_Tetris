package com.malaia.tetris.main;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.malaia.tetris.data.packet.Packet;
import com.malaia.tetris.data.packet.PlayerMatchPacket;

public class Server implements Runnable
{
	public static final String SERVER_ADDRESS = "localhost";
	// public static final String SERVER_ADDRESS = "malatime.kr";
	public static final int SERVER_PORT = 9090;
	ServerSocket server;
	
	public static void main(String[] args)
	{
		new Server();
	}
	
	public Server()
	{
		try
		{
			server = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	
	public void run()
	{
		try
		{
			System.out.println("[[ TETRIS SERVER ]]");
			log("server started");
			
			Socket host = null;
			Socket join = null;
			int port = 0;
			
			while (true)
			{
				// ���� ���
				Socket socket = server.accept();
				log("user connected - " + socket.toString());
				
				if (host == null)
				{
					host = socket;
					port = new Random().nextInt(1000) + 10000;
				}
				else
				{
					// ȣ��Ʈ�� �� �÷��̾�� ��Ŷ �������� �ȵȴ� ������ ���� ������ �̸� ȣ��Ʈ��
					try
					{ sendPacket(host, new PlayerMatchPacket(true, host.getInetAddress().getHostAddress(), port)); }
					catch (Exception e)
					{
						log(host.toString() + " disconnected, set new host...");
						host = socket;
						port = new Random().nextInt(1000) + 10000;
						continue;
					}
					
					// ȣ��Ʈ�� �����ϸ� ������ �̿��� �����϶�� ��Ŷ ����
					join = socket;
					try
					{ sendPacket(join, new PlayerMatchPacket(false, host.getInetAddress().getHostAddress(), port)); }
					catch (Exception e)
					{
						log(join.toString() + " has invalid client. connection cancelled...");
						join = null;
						continue;
					}
					
					// �κ��� ������ �������� ���� ����
					host.close();
					host = null;
					join.close();
					join = null;
				}
			}
		}
		catch (IOException e)
		{
			log(e.getMessage());
		}
		finally
		{
			try
			{ server.close(); }
			catch (IOException e)
			{ e.printStackTrace(); }
		}
	}
	
	void sendPacket(Socket socket, Packet packet) throws Exception
	{
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(packet);
		oos.flush();
	}
	
	void log(String msg)
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("[" + sdf.format(date) + "] " + msg);
	}
}
