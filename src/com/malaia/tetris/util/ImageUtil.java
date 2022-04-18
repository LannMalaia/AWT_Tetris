package com.malaia.tetris.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * 이미지 도구
 */
public class ImageUtil
{
	// 이미 불려진 이미지를 또 부를 때 불필요하게 파일을 불러오는 것을 막기위한 해시맵
	public static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	// 이미지 불러오기
	public static BufferedImage loadImage(IMAGE name)
	{
		String path = "Resources\\Image\\" + name.string;
		
		// 이미 해시맵에 존재하는 경우 그걸 반환
		if (images.get(path) != null)
			return images.get(path);
		
		BufferedImage image;
		File file = new File(path);
		try
		{
			// 이미지를 불러와서 바로 쓰지 않고 복사한 녀석을 사용한다
			image = ImageIO.read(file);
			BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			g2d.drawImage(image, 0, 0, null);
			g2d.dispose();
			images.put(path, bi);
			return bi;
		}
		catch (IIOException ie)
		{
			System.out.println("-- 파일 읽기 오류 --");
			System.out.println(file.getAbsolutePath());
			System.exit(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	// 이미지 복사
	public static BufferedImage copyImage(BufferedImage image)
	{
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
}
