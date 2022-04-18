package com.malaia.tetris.object.ingame;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.object.GameObject;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;

/*
 * 블럭 이미지 오브젝트
 * 네모난 그거
 */
public class Block extends GameObject
{
	// Variable
	private int colorCode = 0; // 색 인덱스
	private BufferedImage blocksImage; // 블록 스프라이트 시트 아미지
	
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
	// 블럭의 색상을 설정
	public void setColor(int color)
	{
		colorCode = color;
		renderer.image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = renderer.image.getGraphics();
		
		// 색상 인덱스값을 기반으로 스프라이트 시트에서 필요한 부분만 잘라다가 그린다
		g.drawImage(blocksImage, 0, 0, 30, 30, colorCode * 30, 0, 30 + colorCode * 30, 30, null);
		g.dispose();
	}
}
