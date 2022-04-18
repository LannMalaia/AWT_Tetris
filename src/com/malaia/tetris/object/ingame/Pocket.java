package com.malaia.tetris.object.ingame;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.malaia.tetris.data.ShapeData;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

/**
 *	�� ����Ʈ, Ȧ�� ǥ���� ������Ʈ
 *	���� Ȥ�� ���� ���� ���� ������Ʈ�� ������
 */
public class Pocket extends GuiObject
{
	/*
	 * Variable
	 */
	private GuiObject text; // ���� ǥ���� ����
	private Slot[] slots; // ���� �迭
	
	/*
	 * Constructor
	 */
	public Pocket(Point position, BufferedImage textImage, int size)
	{
		super(position, null);
		// ���� �̹��� ����
		text = new GuiObject(new Point(position.x, position.y - 20),
				new Point(position.x - 30, position.y - 20), textImage, 0.0f);
		// ���� �迭 ����
		slots = new Slot[size]; // ���� �迭�� ũ�⸦ �Ű������� ���� ��ŭ �������ش�
		for (int i = 0; i < slots.length; i++) // ���� ������ �����
			slots[i] = new Slot(new Point(position.x, position.y + 102 * i));
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		
		// ���� ������Ʈ�� ���ʴ�� ǥ����Ű�� �ִϸ��̼� ������
		new Thread(() -> {
			try
			{
				text.setActive(active);
				for (GuiObject slot : slots)
				{
					Thread.sleep(100);
					slot.setActive(active);
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}
	
	@Override
	public void update()
	{
		super.update();
		text.update();
		for (GuiObject slot : slots)
			slot.update();
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		for (GuiObject slot : slots)
			slot.render(g, observer);
		text.render(g, observer);
	}
	
	/*
	 * Method
	 */
	// �� ����Ʈ�� ���� ��� �迭�� ���� ������Ʈ
	public void updateBlocks(Queue<ShapeData> shapedatas)
	{
		for (int i = 0; i < slots.length; i++)
			slots[i].updateBlock(shapedatas.poll());
	}
	// �� �����͸� �ϳ��� ���� ��� �ϳ��� ������Ʈ
	public void updateBlock(ShapeData shapedata)
	{
		slots[0].updateBlock(shapedata);
	}
}
