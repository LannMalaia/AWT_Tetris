package com.malaia.tetris.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.malaia.tetris.data.packet.Packet;

/**
 *	��Ŷ �ޱ� ���� ť
 *	��Ŷ ���Žÿ� �̺�Ʈ �������� PacketListener�� �����Ų��
 */
public class RecvQueue implements Runnable
{
	// Variable
	private Socket socket;
	private PacketListener listener;
	
	/*
	 * Constructor
	 */
	public RecvQueue(Socket socket) { this(socket, null); }
	public RecvQueue(Socket socket, PacketListener pl)
	{
		this.socket = socket;
		listener = pl;
		Thread thread = new Thread(this);
		thread.setDaemon(true); // ���� �����尡 �� �׾��ٸ� �˾Ƽ� �Ҹ�
		thread.start();
	}
	
	// ��Ŷ ������ ��ü
	public void registerListener(PacketListener pl)
	{
		listener = pl;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			// ������ �����ٸ� �����带 ����
			if (socket.isClosed())
				break;
			try
			{
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject(); // �����͸� ������ ������ ���
				if (obj instanceof Packet) // �����Ͱ� ��Ŷ�� ��� �̺�Ʈ�� �����Ѵ�
					listener.whenPacketRecv((Packet)obj);
				else // ��Ŷ�� �ƴ� ��� ���� �޽��� ���
					System.out.println("Unknown Packet Type - " + obj.getClass().getName());
			}
			catch (Exception e)
			{
				if (!socket.isClosed())
				{
					try
					{ socket.close(); }
					catch (IOException e1)
					{ e1.printStackTrace(); }
				}
				
			}
		}
	}
}
