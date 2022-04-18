package com.malaia.tetris.scene;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Random;

import com.malaia.tetris.data.PlayerTetrisData;
import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.object.ingame.Notice;
import com.malaia.tetris.object.ingame.PlayerObject;
import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.SOUND;

/**
 *	싱글 모드 씬
 */
public final class SingleGameScene extends Scene
{
	// 씬 진행 상태 확인을 위한 enum
	enum GAME_STATE { STANDBY, BEGIN, PLAY, END }
	
	/*
	 * Variable
	 */
	private GAME_STATE state; // 현재 상태
	private GuiObject bg; // 배경 이미지
	private PlayerObject playerobject; // 플레이어 오브젝트
	private Notice notice; // 화면 맨 앞에 뜨는 알림용 이미지
	
	/*
	 * Data
	 */
	private PlayerTetrisData playerdata; // 플레이어 데이터
	
	/*
	 * Constructor
	 */
	public SingleGameScene()
	{
		super();
		
		// 데이터 설정
		state = GAME_STATE.STANDBY;
		playerdata = new PlayerTetrisData(new Random().nextLong());
		
		// 배경 생성, 플레이어 테트리스 화면이랑 배경간에 색 차이가 별로 없으므로 약간 어둡게 만든다
		bg = new GuiObject(new Point(0, 0), ImageUtil.loadImage(IMAGE.BACKGROUND));
		bg.renderer.fixColor(0x66000000);
		
		// 씬에 쓰일 오브젝트 생성 & 추가
		playerobject = new PlayerObject(new Point(640, 0));
		notice = new Notice(new Point(640, 360));
		objects.add(bg);
		objects.add(playerobject);
		objects.add(notice);
	}

	/*
	 * Override Method
	 */
	@Override
	public void initialize()
	{
		new Thread(() -> {
			try
			{
				// 0.5초 쉬고 플레이어 오브젝트 활성화
				Thread.sleep(500);
				playerobject.setActive(true);
				
				// 키 입력 리스너를 등록
				TetrisFrame.getInstance().addKeyListener(keyListener);
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	@Override
	public void dispose()
	{
		TetrisFrame.getInstance().removeKeyListener(keyListener);
		playerobject.setActive(false);
	}
	@Override
	public void update()
	{
		super.update();
		switch (state) // state에 따라 업데이트시의 동작을 구분 
		{
		case STANDBY: // 처음 오브젝트가 다 로드될 때까지 대기 중인 상태
			waitReady();
			break;
		case PLAY: // 한창 하는 중
			playerdata.update(16); // 플레이어 데이터 갱신
			playerobject.updatePlayerTetrisData(playerdata); // 데이터에 따른 오브젝트 갱신

			// 게임 오버 체크
			if (playerdata.isGameOvered())
				playGameOverEffect();
			break;
		case END: // 게임 끝
			playerobject.updatePlayerTetrisData(playerdata);
			break;
		}
		
	}

	// 게임 오버
	public void playGameOverEffect()
	{
		state = GAME_STATE.END;
		new Thread(() -> {
			try
			{
				Thread.sleep(2000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_GAMEOVER), 1000, 1200, 1000);
				Thread.sleep(2000);
				TetrisFrame.changeScene(new TitleScene());
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	// 플레이어들 기다리기
	public void waitReady()
	{
		if (!playerobject.getReady())
			return;
		
		state = GAME_STATE.BEGIN;
		
		// 3 2 1 시작~ 하는 애니메이션 메소드
		new Thread(() -> { 
			try
			{
				Thread.sleep(2000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_3), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_2), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_1), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_START), 300, 500, 300);
				state = GAME_STATE.PLAY;
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}

	// 플레이어 키 이벤트 리스너
	PlayerKeyListener keyListener = new PlayerKeyListener();
	class PlayerKeyListener extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			super.keyPressed(e);
			
			if (state != GAME_STATE.PLAY)
				return;
			
			switch (e.getKeyCode())
			{
			case KeyEvent.VK_UP:
				playerdata.rotate(false);
				break;
			case KeyEvent.VK_DOWN:
				playerdata.putDownBlock(false);
				break;
			case KeyEvent.VK_LEFT:
				playerdata.move(true);
				break;
			case KeyEvent.VK_RIGHT:
				playerdata.move(false);
				break;
			case KeyEvent.VK_SPACE:
				playerdata.hardDrop();
				break;
			case KeyEvent.VK_SHIFT:
				playerdata.holdBlock();
				break;
			}
			playerobject.updatePlayerTetrisData(playerdata);
		}
	}
}
