package com.malaia.tetris.object.ingame;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;

import com.malaia.tetris.data.ShapeData;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

/**
 *	슬롯 오브젝트, 홀드나 다음 블럭 미리보기에 쓰임
 *	블럭을 표현하는 용도
 */
public class Slot extends GuiObject
{
	/*
	 * Variable
	 */
	private Block[] blocks;

	/*
	 * Constructor
	 */
	public Slot(Point position)
	{
		super (position, new Point(position.x - 30, position.y),
				ImageUtil.loadImage(IMAGE.GAME_POCKET), 0.0f);
		blocks = new Block[4]; // 모든 블럭은 4개다
	}

	/*
	 * Override Method
	 */
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		
		// 가지고 있는 블럭들 그리기
		for (Block block : blocks)
		{
			if (block != null)
				block.render(g, observer);
		}
	}
	
	/*
	 * Method
	 */
	// 블럭 값을 받으면 자신의 슬롯에 해당 블럭을 그려넣는다
	public void updateBlock(final ShapeData data)
	{
		if (data == null)
			return;
		
		int index = 0;
		for (int y = 0; y < data.data.length; y++)
			for (int x = 0; x < data.data[y].length; x++)
			{
				if (data.data[y][x] > 0)
				{
					// 좌표 = 슬롯 그려지는 위치 + (이미지 부피 * 0.5) - 블럭 중심점 + 배열 인덱스 * 30
					int posX = position.x + (renderer.image.getWidth() / 2) - data.slotOffset.x + x * 30;
					int posY = position.y + (renderer.image.getHeight() / 2) - data.slotOffset.y + y * 30;
					blocks[index++] = new Block(new Point(posX, posY), data.color);
				}
			}
	}
}
