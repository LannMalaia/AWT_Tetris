package com.malaia.tetris.util;

public enum SOUND
{
	// SE
	SE_CURSOR_DECISION("SE\\Cursor_Decision.wav"),
	SE_CURSOR("SE\\Cursor.wav"),
	SE_CURSOR_CANCEL("SE\\Cursor_Cancel.wav"),
	
	// BLOCK
	HARDDROP("SE\\Block_HardDrop.wav"),
	HOLD("SE\\Block_Hold.wav"),
	LANDING("SE\\Block_Landing.wav"),
	MOVE("SE\\Block_Move.wav"),
	ROTATE("SE\\Block_Rotate.wav"),
	SOFTDROP("SE\\Block_SoftDrop.wav"),
	LINE_1("SE\\LineClear_1.wav"),
	LINE_2("SE\\LineClear_2.wav"),
	LINE_3("SE\\LineClear_3.wav"),
	LINE_4("SE\\LineClear_4.wav");
	
	public String string;
	SOUND(String string)
	{ this.string = string; }
}
