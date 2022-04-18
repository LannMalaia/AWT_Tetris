package com.malaia.tetris.util;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * 머리 아픈 거 해결해주는 거
 */
public class MathUtil
{
	public static double Lerp(double from, double to, double t)
	{
		return from + (to - from) * t;
	}
	public static Point Lerp(Point from, Point to, double t)
	{
		double x = from.getX() + (to.getX() - from.getX()) * t;
		if (Math.abs(from.getX() - x) < 1.0) // 차이가 너무 안나는 경우
		{
			x = from.getX() + ((to.x - from.x) < 0 ? -1 : 1);
			if (to.x - from.x == 0)
				x = to.getX();
		}
		
		double y = from.getY() + (to.getY() - from.getY()) * t;
		if (Math.abs(from.getY() - y) < 1.0) // 차이가 너무 안나는 경우
		{
			y = from.getY() + ((to.y - from.y) < 0 ? -1 : 1);
			if (to.y - from.y == 0)
				y = to.getY();
		}
		
		return new Point((int)Math.round(x), (int)Math.round(y));
	}
}
