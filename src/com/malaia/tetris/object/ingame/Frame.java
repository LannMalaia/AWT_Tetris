package com.malaia.tetris.object.ingame;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;

import com.malaia.tetris.data.PlayerTetrisData;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.MathUtil;

/**
 * 플레이어 오브젝트 내에 쓰이는 프레임 오브젝트
 * 블럭이 실제로 표현되는 부분
 * 중요하다는 뜻
 */
public class Frame extends GuiObject
{
	// Variable
	private Block[][] blocks = new Block[20][10]; // 블럭 배열
	
	/*
	 * Constructor
	 */
	public Frame(Point position)
	{
		super(position, new Point(position.x, position.y - 30), ImageUtil.loadImage(IMAGE.GAME_FRAME), 0.0f);
		for (int y = 0; y < 20; y++)
			for (int x = 0; x < 10; x++)
				blocks[y][x] = new Block(new Point(15 + position.x + 30 * x, 15 + position.y + 30 * y));
	}

	/*
	 * Override Method
	 */
	@Override
	public void update()
	{
		super.update();
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		for (Block[] line : blocks)
			for (Block block : line)
				block.render(g, observer);
	}
	
	/*
	 * Method
	 */
	// 데이터를 받았을 때 적절히 업데이트
	public void update(PlayerTetrisData playerdata)
	{
		int[][] stage = playerdata.getStage();
		for (int y = 0; y < 20; y++)
			for (int x = 0; x < 10; x++)
				blocks[y][x].setColor(stage[y][x]);
	}
	
}
