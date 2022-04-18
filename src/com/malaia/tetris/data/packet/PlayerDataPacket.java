package com.malaia.tetris.data.packet;

import com.malaia.tetris.data.PlayerTetrisData;

/**
 * �÷��̾� ������ ��Ŷ
 * ��� ȭ���� ������ �� ���
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
