package com.malaia.tetris.data;

import java.io.Serializable;

/**
 * 블럭이 충돌하는 부분을 알기 위해서 사용
 * 비트 연산자를 사용해 서로 겹칠 수 있다
 */
public enum CONFLICT_SIDE implements Serializable
{
	UP(0x0001), DOWN(0x0010), LEFT(0x0100), RIGHT(0x1000), NONE(0);
	
	private int value;
	CONFLICT_SIDE(int k)
	{ value = k; }
	// 충돌면 추가
	public static int addSide(int conflict, CONFLICT_SIDE side)
	{ return conflict | side.getInt(); }
	// 해당 방향에 충돌했나 확인
	public static boolean hasSide(int conflict, CONFLICT_SIDE side)
	{ return (conflict & side.getInt()) == side.getInt(); }
	// 충돌한 면의 수
	public static int getSideCount(int conflict)
	{ 
		int result = 0;
		for (CONFLICT_SIDE side : CONFLICT_SIDE.values())
		{
			if (side == NONE)
				continue;
			result += hasSide(conflict, side) ? 1 : 0;
		}
		return result;
	}
	
	public int getInt()
	{ return value; }
	
	public static String toString(int conflict)
	{
		String msg = "conflict = ";
		msg += hasSide(conflict, UP) ? "UP " : "";
		msg += hasSide(conflict, DOWN) ? "DOWN " : "";
		msg += hasSide(conflict, LEFT) ? "LEFT " : "";
		msg += hasSide(conflict, RIGHT) ? "RIGHT " : "";
		return msg;
	}
};
