package com.malaia.tetris.data.packet;

/*
 * ��Ī ��Ŷ
 * �κ� �������� Ŭ���̾�Ʈ������ ��Ī�� �߰��ϱ� ���� �����ϴ� ��Ŷ
 * �� ���� ������, �� ���� ȣ��Ʈ, �� ���� ������ �ȴ�
 * ȣ��Ʈ�� ���� ������ ����, ������ �ش� ������ ������
 * �ٵ� ��Ʈ�������� �ȵ� Ŭ���̾�Ʈ�� ������ ����Ŵ
 */
public class PlayerMatchPacket extends Packet
{
	private static final long serialVersionUID = 1L;
	
	// 1:1���� ���� ȣ��Ʈ�� �� ���ΰ��� ����
	public boolean isHost;
	
	// ȣ��Ʈ�� �ƴ� ��� �����ϴ� IP�� ����
	public String address;
	
	// ȣ��Ʈ�� �ƴϴ� ������ ��Ʈ�� ����
	public int port;
	
	public PlayerMatchPacket(boolean isHost, String address, int port)
	{
		this.isHost = isHost;
		this.address = address; this.port = port;
	}
}
