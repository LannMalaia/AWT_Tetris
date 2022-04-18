package com.malaia.tetris.object.ingame;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.malaia.tetris.data.PlayerTetrisData;
import com.malaia.tetris.data.ShapeData;
import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.object.title.TitleMenuHandle;
import com.malaia.tetris.scene.SingleGameScene;
import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.SOUND;

/**
 *	�÷��̾� ������Ʈ
 *	������ �׷����� ������, Ȧ��, ���� �� ����Ʈ, �������� ������
 *	¯¯ ū �����̳� Ŭ����
 */
public class PlayerObject extends GuiObject
{
	// GameObject
	private Frame frame;
	private Pocket nextPocket, holdPocket;
	private Score score;
	
	// Data
	private PlayerTetrisData data;
	private boolean isReady;
	public boolean getReady() { return isReady; }
	
	/*
	 * Constructor
	 */
	public PlayerObject(Point position)
	{
		super(position, null);
		frame = new Frame(new Point(position.x - 150, 60));
		isReady = false;
		
		holdPocket = new Pocket(new Point(position.x - 275, 80),
				ImageUtil.loadImage(IMAGE.GAME_HOLD), 1);
		nextPocket = new Pocket(new Point(position.x + 190, 80),
				ImageUtil.loadImage(IMAGE.GAME_NEXT), 5);
		score = new Score(new Point(position.x - 310, 540));
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		
		// ���������� ǥ���ϴ� �ִϸ��̼� ������
		new Thread(() -> {
			try
			{
				Thread.sleep(100);
				frame.setActive(active);
				Thread.sleep(100);
				holdPocket.setActive(active);
				Thread.sleep(100);
				nextPocket.setActive(active);
				Thread.sleep(100);
				score.setActive(active);
				Thread.sleep(1000);
				isReady = active;
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}

	@Override
	public void update()
	{
		frame.update();
		holdPocket.update();
		nextPocket.update();
		score.update();
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		frame.render(g, observer);
		
		holdPocket.render(g, observer);
		nextPocket.render(g, observer);
		score.render(g, observer);
	}

	/*
	 * Method
	 */
	// �÷��̾� �����͸� �޾��� ��, �ű� ���缭 ���� ������Ʈ���� ���� �������ش�
	public void updatePlayerTetrisData(PlayerTetrisData newdata)
	{
		data = newdata;
		frame.update(data); // ������ ����
		
		// ConcurrentLinkedQueue... -> ������ �������� ť
		// �ڲ� ConcurrentException�� ���� ��¿ �� ���� ����
		ConcurrentLinkedQueue<ShapeData> a = new ConcurrentLinkedQueue<ShapeData>(data.nexts);
		nextPocket.updateBlocks(a); // ����Ʈ ����
		holdPocket.updateBlock(data.hold); // Ȧ�� ����
		score.updateScore(data.getLevel(), data.getScore()); // ���ھ� ����
	}
}
