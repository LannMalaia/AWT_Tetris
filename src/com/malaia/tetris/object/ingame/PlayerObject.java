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
 *	플레이어 오브젝트
 *	블럭들이 그려지는 프레임, 홀드, 다음 블럭 리스트, 점수판을 가진다
 *	짱짱 큰 컨테이너 클래스
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
		
		// 순차적으로 표현하는 애니메이션 스레드
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
	// 플레이어 데이터를 받았을 때, 거기 맞춰서 내부 오브젝트들을 전부 갱신해준다
	public void updatePlayerTetrisData(PlayerTetrisData newdata)
	{
		data = newdata;
		frame.update(data); // 프레임 갱신
		
		// ConcurrentLinkedQueue... -> 스레드 안정적인 큐
		// 자꾸 ConcurrentException이 떠서 어쩔 수 없이 설정
		ConcurrentLinkedQueue<ShapeData> a = new ConcurrentLinkedQueue<ShapeData>(data.nexts);
		nextPocket.updateBlocks(a); // 리스트 갱신
		holdPocket.updateBlock(data.hold); // 홀드 갱신
		score.updateScore(data.getLevel(), data.getScore()); // 스코어 갱신
	}
}
