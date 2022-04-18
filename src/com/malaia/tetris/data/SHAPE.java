package com.malaia.tetris.data;

import java.io.Serializable;

public enum SHAPE implements Serializable
{
	I(0), O(1), S(2), Z(3), J(4), L(5), T(6);
	
	private int value;
	SHAPE(int k)
	{ value = k; }
	public int getInt()
	{ return value; }
};
