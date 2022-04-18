package com.malaia.tetris.object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import com.malaia.tetris.renderer.ObjectRenderer;

/**
 * ���� ���� ���̴� ��� ������Ʈ
 * ������Ʈ != ������, �����͸� �а� �׿� ���� ȭ�鿡 �׸��� ������ ����
 */
public abstract class GameObject
{
	/*
	 * Variable
	 */
	public Point position = new Point(0, 0); // ��ǥ
	public ObjectRenderer renderer; // �׸��� ����
	protected boolean isActivated = false; // Ȱ��ȭ ����
	
	/*
	 * Constructor
	 */
	public GameObject() {}
	public GameObject(Point position, BufferedImage image)
	{ this(position, image, 1.0f); }
	public GameObject(Point position, BufferedImage image, float opacity)
	{
		if (position != null)
			this.position = position;
		try
		{
			renderer = new ObjectRenderer(image, opacity);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Method
	 */
	public abstract void update(); // ���� ����

	// ������Ʈ Ȱ��ȭ, Ȱ��ȭ�ÿ��� ���̰� �ƴϸ� ������
	public void setActive(boolean active)
	{
		isActivated = active;
		if (isActivated)
			renderer.setOpacity(1.0f);
		else
			renderer.setOpacity(0.0f);
	}
	
	// ������Ʈ �׸���, Ȯ�强�� ������ �׸��� ������ ���� ����
	public void render(Graphics2D g, ImageObserver observer)
	{
		renderer.render(position, g, observer);
	}
}
