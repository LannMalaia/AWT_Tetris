package com.malaia.tetris.object.ingame;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.object.GameObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

/*
 * �� �̹��� ������Ʈ
 * �׸� �װ�
 */
public class Block extends GameObject
{
	// Variable
	private int colorCode = 0; // �� �ε���
	private BufferedImage blocksImage; // ��� ��������Ʈ ��Ʈ �ƹ���
	
	/*
	 * Constructor
	 */
	public Block(Point position)
	{ this(position, 0); }
	public Block(Point position, int color)
	{
		super(position, null);
		blocksImage = ImageUtil.loadImage(IMAGE.GAME_BLOCK);
		setColor(color);
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void update()
	{ }

	/*
	 * Method
	 */
	// ���� ������ ����
	public void setColor(int color)
	{
		colorCode = color;
		renderer.image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = renderer.image.getGraphics();
		
		// ���� �ε������� ������� ��������Ʈ ��Ʈ���� �ʿ��� �κи� �߶�ٰ� �׸���
		g.drawImage(blocksImage, 0, 0, 30, 30, colorCode * 30, 0, 30 + colorCode * 30, 30, null);
		g.dispose();
	}
}
