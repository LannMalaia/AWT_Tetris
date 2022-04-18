package com.malaia.tetris.util;

public enum IMAGE
{
	// 뒷배경
	BACKGROUND("Background\\background_1.png"),
	
	// 캐릭터 스프라이트
	ACTOR_1("Character\\Actor_1_Normal.png"),

	// 테트리스 관련
	GAME_FRAME("Ingame\\frame.png"),
	GAME_BLOCK("Ingame\\block.png"),
	GAME_HOLD("Ingame\\text_hold.png"),
	GAME_NEXT("Ingame\\text_next.png"),
	GAME_POCKET("Ingame\\pocket.png"),
	GAME_1("Ingame\\notice_1.png"),
	GAME_2("Ingame\\notice_2.png"),
	GAME_3("Ingame\\notice_3.png"),
	GAME_START("Ingame\\notice_start.png"),
	GAME_WIN("Ingame\\notice_win.png"),
	GAME_LOSE("Ingame\\notice_lose.png"),
	GAME_DRAW("Ingame\\notice_draw.png"),
	GAME_GAMEOVER("Ingame\\notice_gameover.png"),
	GAME_SERV_CONNECT("Ingame\\notice_server_connecting.png"),
	GAME_SERV_WAIT("Ingame\\notice_server_waiting.png"),
	GAME_SERV_FAIL("Ingame\\notice_server_connect_failed.png"),
	
	// GUI
	INGAME_INDEX("GUI\\Ingame_Index.png"),
	INGAME_INDEX_LIGHT("GUI\\Ingame_Index_Light.png"),
	TITLE_LOGO("GUI\\Title_Logo.png"),
	TITLE_BUTTON_1_INACTIVE("GUI\\Title_Button1_1.png"),
	TITLE_BUTTON_1_ACTIVE("GUI\\Title_Button1_2.png"),
	TITLE_BUTTON_2_INACTIVE("GUI\\Title_Button2_1.png"),
	TITLE_BUTTON_2_ACTIVE("GUI\\Title_Button2_2.png"),
	TITLE_BUTTON_3_INACTIVE("GUI\\Title_Button3_1.png"),
	TITLE_BUTTON_3_ACTIVE("GUI\\Title_Button3_2.png"),
	TITLE_BUTTON_4_INACTIVE("GUI\\Title_Button4_1.png"),
	TITLE_BUTTON_4_ACTIVE("GUI\\Title_Button4_2.png"),
	LOGO_1("GUI\\Logo_1.png"),
	LOGO_2("GUI\\Logo_2.png");
	
	
	public String string;
	IMAGE(String string)
	{ this.string = string; }
}
