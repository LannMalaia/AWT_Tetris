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
 *	�̱� ��� ��
 */
public final class SingleGameScene extends Scene
{
	// �� ���� ���� Ȯ���� ���� enum
	enum GAME_STATE { STANDBY, BEGIN, PLAY, END }
	
	/*
	 * Variable
	 */
	private GAME_STATE state; // ���� ����
	private GuiObject bg; // ��� �̹���
	private PlayerObject playerobject; // �÷��̾� ������Ʈ
	private Notice notice; // ȭ�� �� �տ� �ߴ� �˸��� �̹���
	
	/*
	 * Data
	 */
	private PlayerTetrisData playerdata; // �÷��̾� ������
	
	/*
	 * Constructor
	 */
	public SingleGameScene()
	{
		super();
		
		// ������ ����
		state = GAME_STATE.STANDBY;
		playerdata = new PlayerTetrisData(new Random().nextLong());
		
		// ��� ����, �÷��̾� ��Ʈ���� ȭ���̶� ��氣�� �� ���̰� ���� �����Ƿ� �ణ ��Ӱ� �����
		bg = new GuiObject(new Point(0, 0), ImageUtil.loadImage(IMAGE.BACKGROUND));
		bg.renderer.fixColor(0x66000000);
		
		// ���� ���� ������Ʈ ���� & �߰�
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
				// 0.5�� ���� �÷��̾� ������Ʈ Ȱ��ȭ
				Thread.sleep(500);
				playerobject.setActive(true);
				
				// Ű �Է� �����ʸ� ���
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
		switch (state) // state�� ���� ������Ʈ���� ������ ���� 
		{
		case STANDBY: // ó�� ������Ʈ�� �� �ε�� ������ ��� ���� ����
			waitReady();
			break;
		case PLAY: // ��â �ϴ� ��
			playerdata.update(16); // �÷��̾� ������ ����
			playerobject.updatePlayerTetrisData(playerdata); // �����Ϳ� ���� ������Ʈ ����

			// ���� ���� üũ
			if (playerdata.isGameOvered())
				playGameOverEffect();
			break;
		case END: // ���� ��
			playerobject.updatePlayerTetrisData(playerdata);
			break;
		}
		
	}

	// ���� ����
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
	
	// �÷��̾�� ��ٸ���
	public void waitReady()
	{
		if (!playerobject.getReady())
			return;
		
		state = GAME_STATE.BEGIN;
		
		// 3 2 1 ����~ �ϴ� �ִϸ��̼� �޼ҵ�
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

	// �÷��̾� Ű �̺�Ʈ ������
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
