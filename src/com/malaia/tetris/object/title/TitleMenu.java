package com.malaia.tetris.object.title;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.scene.NetworkGameScene;
import com.malaia.tetris.scene.SingleGameScene;
import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.SOUND;

/**
 *	타이틀 씬에서 쓰이는 메뉴
 */
public final class TitleMenu extends GuiObject
{
	static final int X = 600, Y = 200, Y_GAP = 55;

	/*
	 * Variable
	 */
	private MenuKeyListener keyListener = new MenuKeyListener(); // 키 이벤트 리스너
	private TitleMenuHandle[] handles = new TitleMenuHandle[4]; // 메뉴 내의 핸들 오브젝트
	private int cursor = 0; // 현재 몇번째 항목을 가리키고 있나
	
	/*
	 * Constructor
	 */
	public TitleMenu(Point position)
	{
		super(position, null);
		// 핸들 오브젝트 생성
		for (int i = 0; i < handles.length; i++)
		{
			handles[i] = new TitleMenuHandle(
					new Point(position.x + X, position.y + Y + Y_GAP * i),
					100,
					"TITLE_BUTTON_" + (i + 1));
		}
		// 첫 커서는 0번째에 맞춰지므로 0번째 핸들을 활성화
		handles[0].ChangeCursorState(true);
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);

		// 활성화시에만 키 이벤트 리스너를 등록한다
		if (active)
			TetrisFrame.getInstance().addKeyListener(keyListener);
		else
			TetrisFrame.getInstance().removeKeyListener(keyListener);
		
		// 핸들 오브젝트를 순차적으로 나타나거나 사라지게 하는 애니메이션 스레드
		new Thread(() -> {
			try
			{
				for (GuiObject handle : handles)
				{
					Thread.sleep(100);
					handle.setActive(active);
				}
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	@Override
	public void update()
	{
		// 가지고 있는 오브젝트들도 업데이트
		for (TitleMenuHandle handle : handles)
			handle.update();
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		// 가지고 있는 오브젝트들도 업데이트
		for (TitleMenuHandle handle : handles)
			handle.render(g, observer);
	}
	
	/*
	 * Method
	 */
	// 커서에 따른 핸들 오브젝트들의 그림 업데이트
	public void updateCursor()
	{
		for (int i = 0; i < handles.length; i++)
			handles[i].ChangeCursorState(i == cursor); // 커서에 맞는 핸들 오브젝트는 활성화시킨다
	}
	
	// 결정 키를 눌렀을 때 실행할 일들
	public void decision()
	{
		switch (cursor)
		{
		case 0: // 혼자 하기
			TetrisFrame.changeScene(new SingleGameScene());
			break;
		case 1: // 네트워크
			TetrisFrame.changeScene(new NetworkGameScene());
			break;
		case 2:
			break;
		case 3: // 나가기
			System.exit(0);
			break;
		}
	}
	
	// 키 이벤트 리스너
	class MenuKeyListener extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			super.keyPressed(e);
			switch (e.getKeyCode())
			{
			// 결정
			case KeyEvent.VK_ENTER:
				AudioUtil.playSound(SOUND.SE_CURSOR_DECISION);
				decision();
				break;
				
			// 커서 이동
			case KeyEvent.VK_UP:
				cursor = (cursor - 1) < 0 ? handles.length - 1 : cursor - 1;
				AudioUtil.playSound(SOUND.SE_CURSOR);
				updateCursor();
				break;
			case KeyEvent.VK_DOWN:
				cursor = (cursor + 1) % handles.length;
				AudioUtil.playSound(SOUND.SE_CURSOR);
				updateCursor();
				break;
			}
		}
	}
}
