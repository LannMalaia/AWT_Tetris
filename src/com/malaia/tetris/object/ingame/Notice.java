package com.malaia.tetris.object.ingame;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.MathUtil;

/**
 *	ȭ�� �߾ӿ� ǥ�õǴ� �̹��� ������Ʈ
 *	��������, �߰�����, ���������� ������
 */
public class Notice extends GuiObject
{
	enum NOTICE_STATE { STANDBY, BEGIN, PLAY, END } // �ִϸ��̼� ǥ�ÿ� ���Ű�
	
	/*
	 * Variable
	 */
	private Point centerPosition, endPosition; // �߽� ������ �� ����
	private NOTICE_STATE state = NOTICE_STATE.BEGIN; // ���� �ִϸ��̼� ����
	private long begin, stay, end; // �� ��������� ���� �ð�
	
	/*
	 * Constructor
	 */
	public Notice(Point centerPosition)
	{
		super(centerPosition, null);
		this.centerPosition = centerPosition;
		inactivePosition = activePosition = endPosition = position = new Point();
	}
	
	/*
	 * Override Method
	 */
	@Override
	public void update()
	{
		switch(state)
		{
		case STANDBY: // ������̸� ��ġ�� ��Ȱ��ȭ �������� ����
			position = new Point(inactivePosition);
			break;
		case BEGIN: // ���� ���̸� ��ġ�� Ȱ��ȭ �������� �̵�
			position = MathUtil.Lerp(position, activePosition, 0.1);
			begin -= TetrisFrame.FPS;
			if (begin <= 0) // �ִϸ��̼� Ÿ�̸� ����� ���� ������
			{
				state = NOTICE_STATE.PLAY;
				renderer.setOpacity(1.0f);
			}
			break;
		case PLAY: // �߰� ���¶�� �ð��� �� �ɶ����� ��ġ�� ����
			position = MathUtil.Lerp(position, activePosition, 0.1);
			stay -= TetrisFrame.FPS;
			if (stay <= 0) // �ִϸ��̼� Ÿ�̸� ����� ���� ������
			{
				state = NOTICE_STATE.END;
				renderer.setTargetOpacity(0.0f);
			}
			break;
		case END: // ������ ���̸� ��ġ�� ���� �������� �̵�
			position = MathUtil.Lerp(position, endPosition, 0.1);
			end -= TetrisFrame.FPS;
			if (end <= 0) // �ִϸ��̼� Ÿ�̸� ����� ���� ������
			{
				state = NOTICE_STATE.STANDBY;
				renderer.setOpacity(0.0f);
			}
			break;
		}
	}

	/*
	 * Method
	 */
	public void notice(BufferedImage image) // ���� ���� �̹����� �����ָ� ������ �����Ѵ�
	{
		notice(image, 1000, 9999999999L, 1000);
	}
	// �̹����� �����԰� ���ÿ� ȭ�鿡 �ٷ� ǥ���Ѵ�. begin, stay, end�� ms���� ������
	public void notice(BufferedImage image, long begin, long stay, long end)
	{
		renderer.image = image;
		this.begin = begin; this.stay = stay; this.end = end;
		int xGap = image.getWidth() / 2;
		int yGap = image.getHeight() / 2;
		
		inactivePosition = new Point(centerPosition.x - xGap + 100, centerPosition.y - yGap);
		activePosition = new Point(centerPosition.x - xGap, centerPosition.y - yGap);
		endPosition = new Point(centerPosition.x - xGap - 100, centerPosition.y - yGap);
		position = new Point(inactivePosition);
		
		renderer.setOpacity(0.0f);
		renderer.setTargetOpacity(1.0f);
		
		state = NOTICE_STATE.BEGIN;
	}
}
