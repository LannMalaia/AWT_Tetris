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
 *	스코어 출력용 오브젝트
 *	폰트 좀 써보고 싶었음
 */
public class Score extends GuiObject
{
	static final String PATH = "Resources\\font.ttf"; // 폰트 경로

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
			// 폰트를 어찌저찌 불러온다
			font = Font.createFont(Font.TRUETYPE_FONT, new File(PATH));
			font = font.deriveFont(Font.PLAIN, 80f); // 스타일과 사이즈 조정
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
	// 레벨과 스코어 업데이트
	public void updateScore(int level, long score)
	{
		BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		// 그림자
		g2d.setColor(new Color(0x473866));//0x473866));
		g2d.drawString("LEVEL", 4, 44);
		g2d.drawString("" + level, 4, 84);
		g2d.drawString("SCORE", 4, 124);
		g2d.drawString("" + score, 4, 164);
		// 본문
		g2d.setColor(new Color(0xFFFFFF));//0x473866));
		g2d.drawString("LEVEL", 0, 40);
		g2d.drawString("" + level, 0, 80);
		g2d.drawString("SCORE", 0, 120);
		g2d.drawString("" + score, 0, 160);
		
		g2d.dispose(); // 다 그렸으면 그리는데 쓰인 팔레트는 갖다 버린다
		renderer.image = bi; // 이미지 갱신
	}
}
