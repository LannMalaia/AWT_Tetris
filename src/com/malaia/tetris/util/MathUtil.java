package com.malaia.tetris.util;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * �Ӹ� ���� �� �ذ����ִ� ��
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
		if (Math.abs(from.getX() - x) < 1.0) // ���̰� �ʹ� �ȳ��� ���
		{
			x = from.getX() + ((to.x - from.x) < 0 ? -1 : 1);
			if (to.x - from.x == 0)
				x = to.getX();
		}
		
		double y = from.getY() + (to.getY() - from.getY()) * t;
		if (Math.abs(from.getY() - y) < 1.0) // ���̰� �ʹ� �ȳ��� ���
		{
			y = from.getY() + ((to.y - from.y) < 0 ? -1 : 1);
			if (to.y - from.y == 0)
				y = to.getY();
		}
		
		return new Point((int)Math.round(x), (int)Math.round(y));
	}
}
