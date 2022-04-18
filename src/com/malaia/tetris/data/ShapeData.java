package com.malaia.tetris.data;

import java.awt.Point;
import java.io.Serializable;

/*
 * ���� ������
 */
public class ShapeData implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;
	
	public int color = 0;
	public Point position; // ���� ��ǥ
	public Point offset; // ��� ������ ��ǥ
	public Point slotOffset; // ���� ������ ��ǥ(�߽�)
	public int[][] data = new int[4][4];
	public SHAPE shapeType;
	
	public static ShapeData I = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{1, 2, 1, 1},
		{0, 0, 0, 0},
		{0, 0, 0, 0}}, new Point(4, 0), new Point(1, 1), new Point(60, 45), 5, SHAPE.I);
	public static ShapeData O = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{0, 1, 1, 0},
		{0, 1, 1, 0},
		{0, 0, 0, 0}}, new Point(4, 0), new Point(1, 1), new Point(60, 60), 3, SHAPE.O);
	public static ShapeData S = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{0, 2, 1, 0},
		{1, 1, 0, 0},
		{0, 0, 0, 0}}, new Point(5, 0), new Point(1, 1), new Point(45, 60), 4, SHAPE.S);
	public static ShapeData Z = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{1, 2, 0, 0},
		{0, 1, 1, 0},
		{0, 0, 0, 0}}, new Point(4, 0), new Point(1, 1), new Point(45, 60), 1, SHAPE.Z);
	public static ShapeData J = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{0, 1, 0, 0},
		{0, 2, 1, 1},
		{0, 0, 0, 0}}, new Point(4, 0), new Point(1, 2), new Point(75, 60), 6, SHAPE.J);
	public static ShapeData L = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{0, 0, 1, 0},
		{1, 1, 2, 0},
		{0, 0, 0, 0}}, new Point(4, 3), new Point(2, 2), new Point(45, 60), 2, SHAPE.L);
	public static ShapeData T = new ShapeData(new int[][]{
		{0, 0, 0, 0},
		{0, 1, 0, 0},
		{1, 2, 1, 0},
		{0, 0, 0, 0}}, new Point(4, 0), new Point(1, 2), new Point(45, 60), 7, SHAPE.T);
	
	private ShapeData(int[][] datas, Point pos, Point offset, Point slotOffset, int color, SHAPE shapeType)
	{
		data = datas;
		this.color = color;
		position = pos;
		this.offset = offset;
		this.slotOffset = slotOffset;
		this.shapeType = shapeType;
	}
	
	// ���� ����
	public ShapeData clone()
	{
		int[][] newData = new int[data.length][data[0].length];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				newData[i][j] = data[i][j];
		
		return new ShapeData(newData, new Point(position), new Point(offset),
				new Point(slotOffset), color, shapeType);
	}
	
	// ��� �׸��� ������Ʈ
	public ShapeData shade(int[][] stage)
	{
		ShapeData result = clone();
		result.color = 8;
		while (true)
		{
			result.position = new Point(result.position.x, result.position.y + 1);
			int conflict = result.canBlockIn(stage, result.data);
			if (CONFLICT_SIDE.getSideCount(conflict) > 0)
				break;
		}
		result.position = new Point(result.position.x, result.position.y - 1);
		
		return result;
	}
	
	// ��� ȸ�� (�Ϻ����� ����)
	public void rotate(int[][] stage, boolean reverse)
	{
		// O ����� �����°� �ǹ� ������ ��ŵ
		if (shapeType == SHAPE.O)
			return;
		
		// ȸ���� ����� ����
		int[][] newData = new int[data.length][data[0].length];
		Point newOffset = new Point();
		for (int y = 0; y < data.length; y++)
			for (int x = 0; x < data[y].length; x++)
			{
				int value = data[y][x];
				newData[reverse ? 3 - x : x][reverse ? y : 3 - y] = value;
				if (value == 2)
				{
					newOffset.x = reverse ? y : 3 - y;
					newOffset.y = reverse ? 3 - x : x;
				}
			}
				
		// �ش� ����� �� �´��� Ȯ��
		Point originalOffset = new Point(offset);
		offset = newOffset;
		boolean safe = false;
		for (int i = 0; i < 3; i++)
		{
			int conflict = canBlockIn(stage, newData);
			if (CONFLICT_SIDE.getSideCount(conflict) == 0)
			{
				data = newData;
				safe = true;
				break;
			}
			if (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.UP))
				position = new Point(position.x, position.y + 1);
			if (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.DOWN))
				position = new Point(position.x, position.y - 1);
			if (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.LEFT))
				position = new Point(position.x + 1, position.y);
			if (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.RIGHT))
				position = new Point(position.x - 1, position.y);
		}
		if (!safe) // ������ �� ��𿡵� ������� ���ٸ� ������� ����
			offset = originalOffset;
	}
		
	// �ش� ���������� �ڽ��� ���� ���������������� Ȯ���Ѵ�
	// newData�� �ڽ��� �� ����
	// �Ұ����� ��� 0x0 �̻��� ���� ��� (��=0x1000 ��=0x100 ��=0x10 ��=0x1)
	public int canBlockIn(int[][] stage, int[][] newData)
	{
		int conflict = 0;
		Point newPos = new Point(position.x - offset.x, position.y - offset.y);
		for (int y = 0; y < newData.length; y++)
		{
			for (int x = 0; x < newData[y].length; x++)
			{
				if (newData[y][x] > 0)
				{
					int newX = newPos.x + x;
					int newY = newPos.y + y;
					// ��ǥ ������ ���
					if (newX < 0 || 10 <= newX || newY < 0 || 20 <= newY)
					{
						if (newX < 0)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.LEFT);
						if (10 <= newX)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.RIGHT);
						if (newY < 0)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.UP);
						if (20 <= newY)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.DOWN);
						continue;
					}
					
					// ��ǥ�� �´µ� ���� �̹� ����
					if (stage[newY][newX] >= 1)
					{
						if (x < offset.x)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.LEFT);
						if (offset.x <= x)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.RIGHT);
						if (y < offset.y)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.UP);
						if (offset.y <= y)
							conflict = CONFLICT_SIDE.addSide(conflict, CONFLICT_SIDE.DOWN);
					}
				}
			}
		}
		return conflict;
	}
	
	// �ε����� ���� ��� ����
	public static ShapeData makeShape(int index)
	{
		switch (index)
		{
		case 0:
			return I.clone();
		case 1:
			return O.clone();
		case 2:
			return S.clone();
		case 3:
			return Z.clone();
		case 4:
			return J.clone();
		case 5:
			return L.clone();
		case 6:
			return T.clone();
		}
		return null;
	}
}
