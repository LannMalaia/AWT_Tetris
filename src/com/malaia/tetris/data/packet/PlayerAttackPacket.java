package com.malaia.tetris.data.packet;

/**
 * �÷��̾� ���� ��Ŷ
 * ���� Ŭ����� ��뿡�� ������ ������ ���� �� ���� �� �ش� ��Ŷ�� ����
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
