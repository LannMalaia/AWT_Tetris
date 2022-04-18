package com.malaia.tetris.data.packet;

/**
 * 플레이어 공격 패킷
 * 라인 클리어로 상대에게 쓰레기 라인을 날릴 수 있을 때 해당 패킷을 전송
 */
public class PlayerAttackPacket extends Packet
{
	private static final long serialVersionUID = 1L;
	
	public int lineCount;
	
	public PlayerAttackPacket(int lineCount)
	{
		this.lineCount = lineCount;
	}
}
