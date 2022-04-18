package com.malaia.tetris.object.ingame;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.util.MathUtil;

/**
 *	화면 중앙에 표시되는 이미지 오브젝트
 *	시작지점, 중간지점, 끝지점으로 나뉜다
 */
public class Notice extends GuiObject
{
	enum NOTICE_STATE { STANDBY, BEGIN, PLAY, END } // 애니메이션 표시용 열거값
	
	/*
	 * Variable
	 */
	private Point centerPosition, endPosition; // 중심 지점과 끝 지점
	private NOTICE_STATE state = NOTICE_STATE.BEGIN; // 현재 애니메이션 상태
	private long begin, stay, end; // 각 페이즈마다의 유지 시간
	
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
		case STANDBY: // 대기중이면 위치를 비활성화 지점으로 고정
			position = new Point(inactivePosition);
			break;
		case BEGIN: // 시작 중이면 위치를 활성화 지점으로 이동
			position = MathUtil.Lerp(position, activePosition, 0.1);
			begin -= TetrisFrame.FPS;
			if (begin <= 0) // 애니메이션 타이머 종료시 다음 페이즈
			{
				state = NOTICE_STATE.PLAY;
				renderer.setOpacity(1.0f);
			}
			break;
		case PLAY: // 중간 상태라면 시간이 다 될때까지 위치를 유지
			position = MathUtil.Lerp(position, activePosition, 0.1);
			stay -= TetrisFrame.FPS;
			if (stay <= 0) // 애니메이션 타이머 종료시 다음 페이즈
			{
				state = NOTICE_STATE.END;
				renderer.setTargetOpacity(0.0f);
			}
			break;
		case END: // 끝나는 중이면 위치를 종료 지점으로 이동
			position = MathUtil.Lerp(position, endPosition, 0.1);
			end -= TetrisFrame.FPS;
			if (end <= 0) // 애니메이션 타이머 종료시 다음 페이즈
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
	public void notice(BufferedImage image) // 설정 없이 이미지만 던져주면 영원히 유지한다
	{
		notice(image, 1000, 9999999999L, 1000);
	}
	// 이미지를 설정함과 동시에 화면에 바로 표현한다. begin, stay, end는 ms값을 따른다
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
