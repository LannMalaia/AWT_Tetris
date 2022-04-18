package com.malaia.tetris.data.packet;


/**
 * ���� ���� ��Ŷ
 * ��ȣ ����ȭ(������ ���� ���̳� �� �׷� �͵�)�� ���� ���� �Լ� ����� ���� �õ带 ���� ������
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
