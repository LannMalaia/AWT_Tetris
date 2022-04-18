package com.malaia.tetris.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.malaia.tetris.data.packet.Packet;

/**
 *	패킷 받기 전용 큐
 *	패킷 수신시에 이벤트 개념으로 PacketListener를 실행시킨다
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
		thread.setDaemon(true); // 메인 스레드가 다 죽었다면 알아서 소멸
		thread.start();
	}
	
	// 패킷 리스너 교체
	public void registerListener(PacketListener pl)
	{
		listener = pl;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			// 소켓이 닫혔다면 스레드를 종료
			if (socket.isClosed())
				break;
			try
			{
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject(); // 데이터를 수신할 때까지 대기
				if (obj instanceof Packet) // 데이터가 패킷인 경우 이벤트를 실행한다
					listener.whenPacketRecv((Packet)obj);
				else // 패킷이 아닌 경우 오류 메시지 출력
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
