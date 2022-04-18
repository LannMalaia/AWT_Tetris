package com.malaia.tetris.scene;

import java.awt.Point;

import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.object.title.TitleLogo;
import com.malaia.tetris.object.title.TitleMenu;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

/**
 *	타이틀 씬
 */
public final class TitleScene extends Scene
{
	/*
	 * Variable
	 */
	private GuiObject bg; // 배경
	private TitleMenu menu; // 메뉴창
	private TitleLogo logo; // 타이틀 제목
	
	/*
	 * Constructor
	 */
	public TitleScene()
	{
		super();
	}

	/*
	 * Override Method
	 */
	@Override
	public void initialize()
	{
		bg = new GuiObject(new Point(0, 0), ImageUtil.loadImage(IMAGE.BACKGROUND));
		logo = new TitleLogo(new Point(100, 120), -100, ImageUtil.loadImage(IMAGE.TITLE_LOGO));
		menu = new TitleMenu(new Point(200, 200));

		objects.add(bg);
		objects.add(logo);
		objects.add(menu);
		
		new Thread(() -> {
			try
			{
				Thread.sleep(1000);
				logo.setActive(true);
				Thread.sleep(400);
				menu.setActive(true);
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	@Override
	public void dispose()
	{
		new Thread(() -> {
			try
			{
				logo.setActive(false);
				Thread.sleep(200);
				menu.setActive(false);
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
}
