package com.malaia.tetris.data.packet;

import com.malaia.tetris.data.PlayerTetrisData;

/**
 * 플레이어 데이터 패킷
 * 상대 화면을 갱신할 때 사용
 */
public class PlayerDataPacket extends Packet
{
	private static final long serialVersionUID = 1L;
	
	public PlayerTetrisData playerdata;
	
	public PlayerDataPacket(PlayerTetrisData playerdata)
	{
		this.playerdata = playerdata;
	}
}
