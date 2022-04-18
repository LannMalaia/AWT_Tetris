package com.malaia.tetris.main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.scene.Scene;
import com.malaia.tetris.scene.TitleScene;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

public class TetrisFrame extends Frame implements Runnable
{
	private static TetrisFrame instance;
	public static TetrisFrame getInstance() { return instance; };
	
	// 기본 설정
	private static final long serialVersionUID = 1L;
	static final Dimension RESOLUTION = new Dimension(1280, 720);
	static final int TOP_GAP = 30;
	public static final long FPS = 16;
	
	// 프로퍼티
	private static Scene scene;
	public static void changeScene(Scene scene)
	{ 
		new Thread(() -> {
			try
			{
				if (TetrisFrame.scene != null)
				{
					TetrisFrame.scene.dispose();
					Thread.sleep(1000);
				}
				TetrisFrame.scene = scene;
				TetrisFrame.scene.initialize();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}
	public static Scene getScene()
	{ return scene; }
	
	public TetrisFrame()
	{
		super("Tetris");
		instance = this;
		initialize();
		new Thread(this).start();
	}
	/*
	 * System Method
	 */
	/**
	 * 준비작업
	 */
	private void initialize()
	{
		Dimension newRes = new Dimension(RESOLUTION);
		newRes.setSize(newRes.width, newRes.height + TOP_GAP);
		super.setSize(newRes);
		super.setResizable(false);
		Point loc = new Point(Toolkit.getDefaultToolkit().getScreenSize().width /2 - newRes.width / 2,
				Toolkit.getDefaultToolkit().getScreenSize().height /2 - newRes.height / 2);
		super.setLocation(loc);
		
		registerListeners();
		changeScene(new TitleScene());
		
		super.setVisible(true);
	}
	/**
	 * 윈도우 & 키 입력 관련 이벤트 등록
	 */
	private void registerListeners()
	{
		super.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int decision = JOptionPane.showOptionDialog(TetrisFrame.this,
						"정말로 종료하시겠어요?",
						"종료 확인",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						new String[]{"예", "아니오"},
						null);
				if (decision == 0)
					System.exit(0);
			}
		});
	}
	
	/*
	 * 사실상 메인 스레드
	 */
	@Override
	public synchronized void run()
	{
		try
		{
			while(true)
			{
				wait(FPS);
				scene.update();
				super.repaint();
			}
		}
		catch (InterruptedException e)
		{ e.printStackTrace(); System.exit(ERROR); }
	}
	
	/*
	 * Graphic Method
	 * paint -> update -> paint -> update...
	 */
	private BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;
	
	// 더블 버퍼링을 위해 부모 메소드가 호출되지 않도록 오버라이드
	@Override
	public void update(Graphics g)
	{ paint(g); }
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		// preload buffer
		if (bufferedImage == null)
			bufferedImage = new BufferedImage(RESOLUTION.width, RESOLUTION.height, BufferedImage.TYPE_INT_ARGB);
		bufferedGraphics = bufferedImage.createGraphics();

		// draw to buffer
		scene.render(bufferedGraphics, this);

		// draw buffered image
		g.drawImage(bufferedImage, 0, TOP_GAP, this);
	}
}
