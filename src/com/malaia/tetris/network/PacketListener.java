package com.malaia.tetris.network;

import com.malaia.tetris.data.packet.Packet;

// ��Ŷ ���� �̺�Ʈ
@FunctionalInterface
public interface PacketListener
{
	void whenPacketRecv(Packet packet);
}
