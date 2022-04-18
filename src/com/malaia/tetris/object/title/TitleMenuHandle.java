package com.malaia.tetris.object.title;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.MathUtil;


/**
 * Ÿ��Ʋ �޴� ������ ���̴� �ڵ�
 * Ȱ��ȭ/��Ȱ��ȭ���� �̹����� �ٸ�
 */
public final class TitleMenuHandle extends GuiObject
{
	/*
	 * Variable
	 */
	private boolean cursorEnable; // ���� �ش� ������Ʈ�� ���������� �ִ���
	private BufferedImage inactiveImage, activeImage; // Ȱ��ȭ/��Ȱ������ �̹���
	
	/*
	 * Constructor
	 */
	public TitleMenuHandle(Point position, int inactiveXgap, String image)
	{
		super(position, new Point(position.x - inactiveXgap, position.y),
				ImageUtil.loadImage(IMAGE.valueOf(image + "_INACTIVE")), 0.0f);
		// �̹��� �ε�
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
