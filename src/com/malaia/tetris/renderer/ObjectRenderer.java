package com.malaia.tetris.renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;

import com.malaia.tetris.util.ImageUtil;

/*
 * ������Ʈ�� ������ �׸��� Ŭ����
 * �̹����� ǥ��, ������ �� �ִ�
 */
public class ObjectRenderer
{
	// Variable
	public BufferedImage image = null;
	float opacity = 0, targetOpacity = 0; // ���� ������ ��ǥ ����, ���� ���� ������ ��� ���� ����ȴ�
	
	/*
	 * Constructor
	 */
	public ObjectRenderer(BufferedImage image)
	{ this(image, 1.0f); }
	public ObjectRenderer(BufferedImage image, float opacity)
	{
		this.image = image;
		this.opacity = targetOpacity = opacity;
	}

	/*
	 * Method
	 */
	// ������ ������ ����
	public void setOpacity(float opacity)
	{
		if (image == null)
			return;
		
		this.opacity = targetOpacity = opacity;
	}
	// ��ǥ ���� ����
	public void setTargetOpacity(float opacity)
	{
		if (image == null)
			return;
		
		targetOpacity = opacity;
	}
	// �̹����� ���� �����(ARGB -> 0xAARRGGBB)
	public void fixColor(int color)
	{
		if (image == null)
			return;
		
		image = ImageUtil.copyImage(image);
		
		// �̹����� ũ�Ⱑ �Ȱ��� �� ��ȭ���� ����� �ű⿡ ���� �ִ´�
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int cx = 0; cx < newImage.getWidth(); cx++)
			for (int cy = 0; cy < newImage.getHeight(); cy++)
				newImage.setRGB(cx, cy, color);

		// ���� �̹����� �ش� ��ȭ���� �����
		Graphics g = image.getGraphics();
		g.drawImage(newImage, 0, 0, null);
		g.dispose();
	}
	
	// �׸���
	// awt �������� paint �κп��� ȣ��ȴ�
	public void render(Point position, Graphics2D g2d, ImageObserver observer)
	{
		if (image == null)
			return;

		// ���� ����
		opacity = opacity + (targetOpacity - opacity) * 0.15f;
		opacity = Math.max(0.0f, Math.min(1.0f, opacity));
		byte alpha = (byte)(opacity * 255f);
		
		// �׸����� �̹����� ���� �̹������� ������ ���� ���
		BufferedImage newImage = ImageUtil.copyImage(image);
		if (opacity != 1.0f) // �����̶� ������ ���
		{
			// �̹��� �ȼ��� ���� ������ �ȼ� ���� ����
			for (int cx = 0; cx < newImage.getWidth(); cx++)
				for (int cy = 0; cy < newImage.getHeight(); cy++)
				{
					int color = newImage.getRGB(cx, cy);
					int mc = (alpha << 24) | 0x00ffffff;
					int newColor = color & mc;
					newImage.setRGB(cx, cy, newColor);
				}
		}
		
		// �׷��Ƚ��� �̹����� �׸���
		g2d.drawImage(newImage, position.x, position.y, observer);
	}
}
