package com.malaia.tetris.data.packet;


/**
 * 게임 시작 패킷
 * 상호 동기화(다음에 나올 블럭이나 뭐 그런 것들)를 위해 랜덤 함수 출력을 위한 시드를 같이 보낸다
 */
public class GameStartPacket extends Packet
{
	private static final long serialVersionUID = 1L;
	
	public long randomSeed;
	
	public GameStartPacket(long randomSeed)
	{
		this.randomSeed = randomSeed;
	}
}
