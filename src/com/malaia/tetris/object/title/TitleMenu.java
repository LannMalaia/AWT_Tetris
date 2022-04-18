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
 *	Ÿ��Ʋ ������ ���̴� �޴�
 */
public final class TitleMenu extends GuiObject
{
	static final int X = 600, Y = 200, Y_GAP = 55;

	/*
	 * Variable
	 */
	private MenuKeyListener keyListener = new MenuKeyListener(); // Ű �̺�Ʈ ������
	private TitleMenuHandle[] handles = new TitleMenuHandle[4]; // �޴� ���� �ڵ� ������Ʈ
	private int cursor = 0; // ���� ���° �׸��� ����Ű�� �ֳ�
	
	/*
	 * Constructor
	 */
	public TitleMenu(Point position)
	{
		super(position, null);
		// �ڵ� ������Ʈ ����
		for (int i = 0; i < handles.length; i++)
		{
			handles[i] = new TitleMenuHandle(
					new Point(position.x + X, position.y + Y + Y_GAP * i),
					100,
					"TITLE_BUTTON_" + (i + 1));
		}
		// ù Ŀ���� 0��°�� �������Ƿ� 0��° �ڵ��� Ȱ��ȭ
		handles[0].ChangeCursorState(true);
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);

		// Ȱ��ȭ�ÿ��� Ű �̺�Ʈ �����ʸ� ����Ѵ�
		if (active)
			TetrisFrame.getInstance().addKeyListener(keyListener);
		else
			TetrisFrame.getInstance().removeKeyListener(keyListener);
		
		// �ڵ� ������Ʈ�� ���������� ��Ÿ���ų� ������� �ϴ� �ִϸ��̼� ������
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
		// ������ �ִ� ������Ʈ�鵵 ������Ʈ
		for (TitleMenuHandle handle : handles)
			handle.update();
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
		// ������ �ִ� ������Ʈ�鵵 ������Ʈ
		for (TitleMenuHandle handle : handles)
			handle.render(g, observer);
	}
	
	/*
	 * Method
	 */
	// Ŀ���� ���� �ڵ� ������Ʈ���� �׸� ������Ʈ
	public void updateCursor()
	{
		for (int i = 0; i < handles.length; i++)
			handles[i].ChangeCursorState(i == cursor); // Ŀ���� �´� �ڵ� ������Ʈ�� Ȱ��ȭ��Ų��
	}
	
	// ���� Ű�� ������ �� ������ �ϵ�
	public void decision()
	{
		switch (cursor)
		{
		case 0: // ȥ�� �ϱ�
			TetrisFrame.changeScene(new SingleGameScene());
			break;
		case 1: // ��Ʈ��ũ
			TetrisFrame.changeScene(new NetworkGameScene());
			break;
		case 2:
			break;
		case 3: // ������
			System.exit(0);
			break;
		}
	}
	
	// Ű �̺�Ʈ ������
	class MenuKeyListener extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			super.keyPressed(e);
			switch (e.getKeyCode())
			{
			// ����
			case KeyEvent.VK_ENTER:
				AudioUtil.playSound(SOUND.SE_CURSOR_DECISION);
				decision();
				break;
				
			// Ŀ�� �̵�
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
