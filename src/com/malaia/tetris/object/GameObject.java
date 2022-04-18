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
 * 게임 내에 쓰이는 모든 오브젝트
 * 오브젝트 != 데이터, 데이터를 읽고 그에 맞춰 화면에 그리는 정도의 역할
 */
public abstract class GameObject
{
	/*
	 * Variable
	 */
	public Point position = new Point(0, 0); // 좌표
	public ObjectRenderer renderer; // 그리는 도구
	protected boolean isActivated = false; // 활성화 여부
	
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
	public abstract void update(); // 갱신 동작

	// 오브젝트 활성화, 활성화시에는 보이고 아니면 가려짐
	public void setActive(boolean active)
	{
		isActivated = active;
		if (isActivated)
			renderer.setOpacity(1.0f);
		else
			renderer.setOpacity(0.0f);
	}
	
	// 오브젝트 그리기, 확장성을 생각해 그리는 도구가 따로 있음
	public void render(Graphics2D g, ImageObserver observer)
	{
		renderer.render(position, g, observer);
	}
}
