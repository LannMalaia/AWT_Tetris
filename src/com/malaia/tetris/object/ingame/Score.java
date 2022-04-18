package com.malaia.tetris.object.ingame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import com.malaia.tetris.object.GuiObject;

/**
 *	���ھ� ��¿� ������Ʈ
 *	��Ʈ �� �Ẹ�� �;���
 */
public class Score extends GuiObject
{
	static final String PATH = "Resources\\font.ttf"; // ��Ʈ ���

	// Variables
	Font font;
	
	/*
	 * Constructor
	 */
	public Score(Point position)
	{
		super (position, null);
		try
		{
			// ��Ʈ�� �������� �ҷ��´�
			font = Font.createFont(Font.TRUETYPE_FONT, new File(PATH));
			font = font.deriveFont(Font.PLAIN, 80f); // ��Ÿ�ϰ� ������ ����
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
		}
		updateScore(1, 0);
	}
	
	/*
	 * Method
	 */
	// ������ ���ھ� ������Ʈ
	public void updateScore(int level, long score)
	{
		BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		// �׸���
		g2d.setColor(new Color(0x473866));//0x473866));
		g2d.drawString("LEVEL", 4, 44);
		g2d.drawString("" + level, 4, 84);
		g2d.drawString("SCORE", 4, 124);
		g2d.drawString("" + score, 4, 164);
		// ����
		g2d.setColor(new Color(0xFFFFFF));//0x473866));
		g2d.drawString("LEVEL", 0, 40);
		g2d.drawString("" + level, 0, 80);
		g2d.drawString("SCORE", 0, 120);
		g2d.drawString("" + score, 0, 160);
		
		g2d.dispose(); // �� �׷����� �׸��µ� ���� �ȷ�Ʈ�� ���� ������
		renderer.image = bi; // �̹��� ����
	}
}
