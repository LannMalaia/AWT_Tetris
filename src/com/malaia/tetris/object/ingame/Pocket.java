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
 *	블럭 리스트, 홀드 표현용 오브젝트
 *	단일 혹은 여러 개의 슬롯 오브젝트를 가진다
 */
public class Pocket extends GuiObject
{
	/*
	 * Variable
	 */
	private GuiObject text; // 위에 표시할 문구
	private Slot[] slots; // 슬롯 배열
	
	/*
	 * Constructor
	 */
	public Pocket(Point position, BufferedImage textImage, int size)
	{
		super(position, null);
		// 문구 이미지 설정
		text = new GuiObject(new Point(position.x, position.y - 20),
				new Point(position.x - 30, position.y - 20), textImage, 0.0f);
		// 슬롯 배열 설정
		slots = new Slot[size]; // 슬롯 배열의 크기를 매개변수로 받은 만큼 설정해준다
		for (int i = 0; i < slots.length; i++) // 이후 적절히 만들기
			slots[i] = new Slot(new Point(position.x, position.y + 102 * i));
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		
		// 슬롯 오브젝트를 차례대로 표현시키는 애니메이션 스레드
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
	// 블럭 리스트를 받을 경우 배열을 전부 업데이트
	public void updateBlocks(Queue<ShapeData> shapedatas)
	{
		for (int i = 0; i < slots.length; i++)
			slots[i].updateBlock(shapedatas.poll());
	}
	// 블럭 데이터를 하나만 받을 경우 하나만 업데이트
	public void updateBlock(ShapeData shapedata)
	{
		slots[0].updateBlock(shapedata);
	}
}
