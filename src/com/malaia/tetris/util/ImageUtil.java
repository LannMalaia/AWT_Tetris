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
 * �̹��� ����
 */
public class ImageUtil
{
	// �̹� �ҷ��� �̹����� �� �θ� �� ���ʿ��ϰ� ������ �ҷ����� ���� �������� �ؽø�
	public static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	// �̹��� �ҷ�����
	public static BufferedImage loadImage(IMAGE name)
	{
		String path = "Resources\\Image\\" + name.string;
		
		// �̹� �ؽøʿ� �����ϴ� ��� �װ� ��ȯ
		if (images.get(path) != null)
			return images.get(path);
		
		BufferedImage image;
		File file = new File(path);
		try
		{
			// �̹����� �ҷ��ͼ� �ٷ� ���� �ʰ� ������ �༮�� ����Ѵ�
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
			System.out.println("-- ���� �б� ���� --");
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
	
	// �̹��� ����
	public static BufferedImage copyImage(BufferedImage image)
	{
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
}
