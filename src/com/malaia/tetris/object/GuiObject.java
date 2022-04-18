package com.malaia.tetris.object;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.util.MathUtil;

/**
 *	GUI 오브젝트
 *	게임 오브젝트에 기능을 좀 더 더한거
 *	아주 간단한 UI 애니메이션을 사용 가능
 */
public class GuiObject extends GameObject
{
	/*
	 * Variable
	 */
	protected Point inactivePosition, activePosition; // 비활성화, 활성화 시의 위치, 이것으로 setActive()를 호출할 때 움직이게 할 수 있음
	
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
		this.position = inactivePosition; // 기본적으로 비활성화 상태로 만들어지기에 첫 위치는 비활성화 됐을 때의 위치
	}

	/*
	 * Override Method
	 */
	@Override
	public void update()
	{
		// 활성화 여부를 판단해 해당 위치로 스멀스멀 이동시킨다
		position = MathUtil.Lerp(position, isActivated ? activePosition : inactivePosition, 0.1);
	}

	@Override
	public void setActive(boolean active)
	{
		// GameObject는 활성화시 투명화 값이 곧바로 적용되지만, GuiObject는 서서히 변화한다
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
