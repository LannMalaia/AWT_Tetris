package com.malaia.tetris.network;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.malaia.tetris.data.packet.Packet;

/**
 *	��Ŷ ������ ���� ť
 *	���ۿ� ������ ��� available�� ��Ȱ��ȭ�ȴ�
 */
public class SendQueue implements Runnable
{
	/*
	 * Variable
	 */
	private Socket socket;
	private ConcurrentLinkedQueue<Packet> queue;
	private boolean available = true;
	public boolean isAvailable() { return available; }
	
	/*
	 * Constructor
	 */
	public SendQueue(Socket socket)
	{
		this.socket = socket;
		queue = new ConcurrentLinkedQueue<Packet>();
		Thread thread = new Thread(this);
		thread.setDaemon(true); // ���� �����尡 �� �׾��ٸ� �˾Ƽ� �Ҹ�
		thread.start();
	}
	
	// ��Ŷ ������
	public void sendPacket(Packet packet)
	{
		queue.offer(packet);
		// System.out.println("object add");
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				// ť�� �� ������ ��Ʈ���� �����͸� �ִ´�
				while (queue.size() > 0)
				{
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(queue.poll());
					// System.out.println("object sended");
				}
				// ť�� ������� ���
				Thread.sleep(2);
			}
			catch (Exception e)
			{
				if (e instanceof NotSerializableException)
				{
					System.out.println("-- ����ȭ ���� --> ");
				}
				if (!socket.isClosed())
				{
					System.out.println("-- ���� ���� --> ");
					try
					{ socket.close(); }
					catch (IOException e1)
					{ e1.printStackTrace(); }
				}
				// ������ ������ ������ ������ �� �����Ƿ� �� �� ���� ť�� ��Ȱ��ȭ�Ѵ�
				available = false;
				e.printStackTrace();
				break;
			}
		}
	}
}
