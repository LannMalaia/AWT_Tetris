package com.malaia.tetris.network;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.malaia.tetris.data.packet.Packet;

/**
 *	패킷 보내기 전용 큐
 *	전송에 실패할 경우 available이 비활성화된다
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
		thread.setDaemon(true); // 메인 스레드가 다 죽었다면 알아서 소멸
		thread.start();
	}
	
	// 패킷 보내기
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
				// 큐가 빌 때까지 스트림에 데이터를 넣는다
				while (queue.size() > 0)
				{
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(queue.poll());
					// System.out.println("object sended");
				}
				// 큐가 비었으면 대기
				Thread.sleep(2);
			}
			catch (Exception e)
			{
				if (e instanceof NotSerializableException)
				{
					System.out.println("-- 직렬화 오류 --> ");
				}
				if (!socket.isClosed())
				{
					System.out.println("-- 소켓 닫힘 --> ");
					try
					{ socket.close(); }
					catch (IOException e1)
					{ e1.printStackTrace(); }
				}
				// 갖가지 이유로 전송이 실패할 수 있으므로 그 땐 센드 큐를 비활성화한다
				available = false;
				e.printStackTrace();
				break;
			}
		}
	}
}
