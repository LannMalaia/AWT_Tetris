package com.malaia.tetris.object.title;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.MathUtil;


/**
 * Ÿ��Ʋ ����. ��� ���� �ѵ�?
 */
public class TitleLogo extends GuiObject
{
	public TitleLogo(Point position, int inactiveXgap, BufferedImage image)
	{
		super(position, new Point(position.x - inactiveXgap, position.y), image, 0.0f);
	}
}
