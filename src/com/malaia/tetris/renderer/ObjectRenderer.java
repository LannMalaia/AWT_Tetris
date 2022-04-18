package com.malaia.tetris.renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;

import com.malaia.tetris.util.ImageUtil;

/*
 * 오브젝트를 실제로 그리는 클래스
 * 이미지를 표현, 가공할 수 있다
 */
public class ObjectRenderer
{
	// Variable
	public BufferedImage image = null;
	float opacity = 0, targetOpacity = 0; // 현재 투명도와 목표 투명도, 서로 같을 때까지 계속 값이 변경된다
	
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
	// 투명도를 강제로 설정
	public void setOpacity(float opacity)
	{
		if (image == null)
			return;
		
		this.opacity = targetOpacity = opacity;
	}
	// 목표 투명도 설정
	public void setTargetOpacity(float opacity)
	{
		if (image == null)
			return;
		
		targetOpacity = opacity;
	}
	// 이미지에 색상 덮어쓰기(ARGB -> 0xAARRGGBB)
	public void fixColor(int color)
	{
		if (image == null)
			return;
		
		image = ImageUtil.copyImage(image);
		
		// 이미지와 크기가 똑같은 빈 도화지를 만들고 거기에 색을 넣는다
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int cx = 0; cx < newImage.getWidth(); cx++)
			for (int cy = 0; cy < newImage.getHeight(); cy++)
				newImage.setRGB(cx, cy, color);

		// 이후 이미지에 해당 도화지를 덮어쓴다
		Graphics g = image.getGraphics();
		g.drawImage(newImage, 0, 0, null);
		g.dispose();
	}
	
	// 그리기
	// awt 프레임의 paint 부분에서 호출된다
	public void render(Point position, Graphics2D g2d, ImageObserver observer)
	{
		if (image == null)
			return;

		// 투명도 설정
		opacity = opacity + (targetOpacity - opacity) * 0.15f;
		opacity = Math.max(0.0f, Math.min(1.0f, opacity));
		byte alpha = (byte)(opacity * 255f);
		
		// 그리려는 이미지는 원본 이미지에서 복사한 것을 사용
		BufferedImage newImage = ImageUtil.copyImage(image);
		if (opacity != 1.0f) // 조금이라도 투명한 경우
		{
			// 이미지 픽셀을 전부 훑으며 픽셀 값을 조정
			for (int cx = 0; cx < newImage.getWidth(); cx++)
				for (int cy = 0; cy < newImage.getHeight(); cy++)
				{
					int color = newImage.getRGB(cx, cy);
					int mc = (alpha << 24) | 0x00ffffff;
					int newColor = color & mc;
					newImage.setRGB(cx, cy, newColor);
				}
		}
		
		// 그래픽스에 이미지를 그린다
		g2d.drawImage(newImage, position.x, position.y, observer);
	}
}
