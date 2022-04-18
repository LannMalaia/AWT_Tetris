package com.malaia.tetris.data.packet;

/*
 * 매칭 패킷
 * 로비 서버에서 클라이언트끼리의 매칭을 중개하기 위해 전송하는 패킷
 * 두 명에게 보내며, 한 쪽이 호스트, 한 쪽이 유저가 된다
 * 호스트는 서버 소켓을 열고, 유저는 해당 서버로 접속함
 * 근데 포트포워딩이 안된 클라이언트는 문제를 일으킴
 */
public class PlayerMatchPacket extends Packet
{
	private static final long serialVersionUID = 1L;
	
	// 1:1에서 누가 호스트를 할 것인가를 결정
	public boolean isHost;
	
	// 호스트가 아닐 경우 접속하는 IP를 설정
	public String address;
	
	// 호스트던 아니던 연결할 포트를 설정
	public int port;
	
	public PlayerMatchPacket(boolean isHost, String address, int port)
	{
		this.isHost = isHost;
		this.address = address; this.port = port;
	}
}
