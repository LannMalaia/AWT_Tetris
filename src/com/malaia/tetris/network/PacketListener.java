package com.malaia.tetris.network;

import com.malaia.tetris.data.packet.Packet;

// 패킷 수신 이벤트
@FunctionalInterface
public interface PacketListener
{
	void whenPacketRecv(Packet packet);
}
