package com.malaia.tetris.object;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.util.MathUtil;

/**
 *	GUI ������Ʈ
 *	���� ������Ʈ�� ����� �� �� ���Ѱ�
 *	���� ������ UI �ִϸ��̼��� ��� ����
 */
public class GuiObject extends GameObject
{
	/*
	 * Variable
	 */
	protected Point inactivePosition, activePosition; // ��Ȱ��ȭ, Ȱ��ȭ ���� ��ġ, �̰����� setActive()�� ȣ���� �� �����̰� �� �� ����
	
	/*
	 * Constructor
	 */
	public GuiObject(Point position, BufferedImage image)
	{ this(position, image, 1.0f); }
	public GuiObject(Point position, BufferedImage image, float opacity)
	{ this(position, new Point(position), image, opacity); }
	public GuiObject(Point position, Point inactivePosition, BufferedImage image, float opacity)
	{
		super(position, image, opacity);
		this.activePosition  = position;
		this.inactivePosition = inactivePosition;
		this.position = inactivePosition; // �⺻������ ��Ȱ��ȭ ���·� ��������⿡ ù ��ġ�� ��Ȱ��ȭ ���� ���� ��ġ
	}

	/*
	 * Override Method
	 */
	@Override
	public void update()
	{
		// Ȱ��ȭ ���θ� �Ǵ��� �ش� ��ġ�� ���ֽ��� �̵���Ų��
		position = MathUtil.Lerp(position, isActivated ? activePosition : inactivePosition, 0.1);
	}

	@Override
	public void setActive(boolean active)
	{
		// GameObject�� Ȱ��ȭ�� ����ȭ ���� ��ٷ� ���������, GuiObject�� ������ ��ȭ�Ѵ�
		isActivated = active;
		if (isActivated)
			renderer.setTargetOpacity(1.0f);
		else
			renderer.setTargetOpacity(0.0f);
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver observer)
	{
		super.render(g, observer);
	}
}
