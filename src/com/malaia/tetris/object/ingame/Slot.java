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
 *	���� ������Ʈ, Ȧ�峪 ���� �� �̸����⿡ ����
 *	���� ǥ���ϴ� �뵵
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
		blocks = new Block[4]; // ��� ���� 4����
	}

	/*
	 * Override Method
	 */
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		
		// ������ �ִ� ���� �׸���
		for (Block block : blocks)
		{
			if (block != null)
				block.render(g, observer);
		}
	}
	
	/*
	 * Method
	 */
	// �� ���� ������ �ڽ��� ���Կ� �ش� ���� �׷��ִ´�
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
					// ��ǥ = ���� �׷����� ��ġ + (�̹��� ���� * 0.5) - �� �߽��� + �迭 �ε��� * 30
					int posX = position.x + (renderer.image.getWidth() / 2) - data.slotOffset.x + x * 30;
					int posY = position.y + (renderer.image.getHeight() / 2) - data.slotOffset.y + y * 30;
					blocks[index++] = new Block(new Point(posX, posY), data.color);
				}
			}
	}
}
