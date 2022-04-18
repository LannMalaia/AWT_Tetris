package com.malaia.tetris.object.title;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.MathUtil;


/**
 * 타이틀 메뉴 내에서 쓰이는 핸들
 * 활성화/비활성화시의 이미지가 다름
 */
public final class TitleMenuHandle extends GuiObject
{
	/*
	 * Variable
	 */
	private boolean cursorEnable; // 현재 해당 오브젝트가 가리켜지고 있는지
	private BufferedImage inactiveImage, activeImage; // 활성화/비활성시의 이미지
	
	/*
	 * Constructor
	 */
	public TitleMenuHandle(Point position, int inactiveXgap, String image)
	{
		super(position, new Point(position.x - inactiveXgap, position.y),
				ImageUtil.loadImage(IMAGE.valueOf(image + "_INACTIVE")), 0.0f);
		// 이미지 로드
		activeImage = ImageUtil.loadImage(IMAGE.valueOf(image + "_ACTIVE"));
		inactiveImage = ImageUtil.loadImage(IMAGE.valueOf(image + "_INACTIVE"));
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void update()
	{
		super.update();
		this.renderer.image = cursorEnable ? activeImage : inactiveImage;
	}

	/*
	 * Method
	 */
	public void ChangeCursorState(boolean enable)
	{
		cursorEnable = enable;
	}
}
