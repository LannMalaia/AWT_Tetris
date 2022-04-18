package com.malaia.tetris.data;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.SOUND;

/**
 * �÷��̾� ������
 * �ش� �÷��̾��� ��Ʈ���� ���¿� ���� ��� ���� ��´�
 */
public final class PlayerTetrisData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int NEXT_NEED = 3; // ���� �������� �ʿ��� ���� ����
	
	private int[][] stagedata; // ���� ���������� ����
	public Queue<ShapeData> nexts; // ������ ���� �� ����Ʈ
	public ShapeData hold;	// ���� Ȧ������ ��
	public ShapeData block;	// ���� ��Ʈ������ ��

	Random rand; // ���� ��¿�
	private long ms = 0; // �÷��� �� �帥 �ð�
	private long intervalCount = 0; // ��� ���� ����
	private boolean holded = false;
	private int liveCount = 0; // ����� ��ġ�Ǳ� ���� �����Ǵ� �ð�
	
	private boolean isGameOver; // ���� ����
	public boolean isGameOvered() { return isGameOver; }

	private int level; // ����
	public int getLevel() { return level; }
	
	private long score; // ����
	public long getScore() { return score; }
	
	private int lineAttack; // ������ ����
	public int getAttack() { int result = lineAttack; lineAttack = 0; return result; }
	
	private int nextlevel; // ���� ������ ������� �ʿ��� �� ��
	
	/*
	 * Constructor
	 */
	public PlayerTetrisData(long seed)
	{
		rand = new Random(seed);
		level = 1;
		score = lineAttack = 0;
		nextlevel = NEXT_NEED;
		stagedata = new int[20][10];
		nexts = new LinkedList<ShapeData>();
		makeSevenBag(); // �� ����Ʈ ����
		pollNextBlock(); // �������� ���� �����Ƿ� �� �ϳ� ��������
	}
	
	// ȭ�� ���� �ӵ��� ���� ������Ʈ ����
	public void update(long ms)
	{
		if (isGameOver)
			return;
		
		ms += ms;
		
		// ������ ���߾� ��� �ð��� ª������, �ش� �ð��� �ʰ��ϸ� ���� ������ �����´�
		intervalCount += ms;
		int interval = (20 - level + 1) * 100;
		if (intervalCount >= interval)
		{
			intervalCount = 0;
			putDownBlock(false);
		}
	}
	
	// ���� ���� ó��
	public void gameover()
	{
		isGameOver = true;
		
		// ���������� ���� ������ ä���ִ� �ִϸ��̼� ������ (���� ������ �ȵɵ�)
		new Thread(() -> {
			try
			{
				for (int y = stagedata.length - 1; y >= 0; y--)
				{
					for (int x = 0; x < stagedata[y].length; x++)
						stagedata[y][x] = 9;
					Thread.sleep(30);
				}
			}
			catch(InterruptedException e)
			{ }
		}).start();
	}
	
	// �������� ���� �������� ������ ��ȯ�Ѵ�
	// �������� ���̳� �׸��� ���� �׷����Ƿ� ���� �������� �����ʹ� �� �ٸ���
	public int[][] getStage()
	{
		int[][] stage = new int[stagedata.length][stagedata[0].length];
		// ���� ����
		for (int y = 0; y < stagedata.length; y++)
			for (int x = 0; x < stagedata[y].length; x++)
				stage[y][x] = stagedata[y][x];
		
		// ���� ���� ���¶�� �׸��ڳ� �ٸ� ���� �׸��� ����
		if (isGameOver)
			return stage;
		
		// �׸��� �� �׸���
		ShapeData shade = block.shade(stage);
		for (int y = 0; y < shade.data.length; y++)
			for (int x = 0; x < shade.data[y].length; x++)
			{
				int newX = shade.position.x - shade.offset.x + x;
				int newY = shade.position.y - shade.offset.y + y;
				if (newX < 0 || 10 <= newX || newY < 0 || 20 <= newY)
					continue;
				if (shade.data[y][x] > 0)
					stage[newY][newX] = shade.color;
			}
		
		// ��Ʈ���ϴ� �� �׸���
		for (int y = 0; y < block.data.length; y++)
			for (int x = 0; x < block.data[y].length; x++)
			{
				int newX = block.position.x - block.offset.x + x;
				int newY = block.position.y - block.offset.y + y;
				if (newX < 0 || 10 <= newX || newY < 0 || 20 <= newY)
					continue;
				if (block.data[y][x] > 0)
					stage[newY][newX] = block.color;
			}
		return stage;
	}
	
	// ������ -> �������� �ӵ��� ���
	public void levelUp()
	{
		level = Math.min(20, level + 1);
	}
	
	// �� ������
	public boolean putDownBlock(boolean isDrop)
	{
		// ����Ʈ ���(�Ʒ� Ű�� ���� ������)�� ��� ��ġ���� �ʰ� ����
		if (isDrop)
			AudioUtil.playSound(SOUND.SOFTDROP);
		
		// ��ġ ����
		block.position = new Point(block.position.x, block.position.y + 1);
		
		// �浹 ����
		int conflict = block.canBlockIn(stagedata, block.data);
		if (CONFLICT_SIDE.getSideCount(conflict) > 0) // ������ ��ġ�� ������ ���� �� ���� ���
		{
			// ���� ���
			liveCount--;
			
			// ��ġ�� �ٽ� ���� �ø���
			block.position = new Point(block.position.x, block.position.y - 1);
			
			if (isDrop || liveCount <= 0) // �÷��̾ ������ ����߰ų� ������ �ٵ����� ��ġ ����
			{
				AudioUtil.playSound(SOUND.LANDING);
				// �ش� ������ ��� ��ġ
				for (int y = 0; y < block.data.length; y++)
					for (int x = 0; x < block.data[y].length; x++)
					{
						int newX = block.position.x - block.offset.x + x;
						int newY = block.position.y - block.offset.y + y;
						if (newX < 0 || 10 <= newX || newY < 0 || 20 <= newY)
							continue;
						if (block.data[y][x] != 0)
							stagedata[newY][newX] = block.color;
					}
				// ���� Ŭ���� üũ
				int clearedLine = checkLineClear();
				
				// �������̶�� ������ ������ ���� �ʿ��ϹǷ� �װ͵� ����صд�
				lineAttack += clearedLine > 1 ? clearedLine - 1 : 0;
				
				// ���ھ� ����
				score += clearedLine * 1000;
				
				// ���� ���� (���� �� ����)
				nextlevel -= clearedLine;
				while (nextlevel <= 0)
				{
					levelUp();
					nextlevel = NEXT_NEED + nextlevel;
				}
				
				// ���� ������ ���� ���� ��µǴ� �Ҹ��� ����
				switch(clearedLine)
				{
				case 1:
					AudioUtil.playSound(SOUND.LINE_1);
					break;
				case 2:
					AudioUtil.playSound(SOUND.LINE_2);
					break;
				case 3:
					AudioUtil.playSound(SOUND.LINE_3);
					break;
				case 4:
					AudioUtil.playSound(SOUND.LINE_4);
					break;
				}
				
				// ���ο� ��� ����
				pollNextBlock();
				return true; // -> ����� ��ġ�ߴ�
			}
		}
		return false; // -> ��ġ���� ���ߴ�
	}
	// �翷 ������
	public void move(boolean isLeft)
	{
		AudioUtil.playSound(SOUND.MOVE);
		CONFLICT_SIDE sc = isLeft ? CONFLICT_SIDE.LEFT : CONFLICT_SIDE.RIGHT;
		block.position = new Point(block.position.x + (isLeft ? -1 : 1), block.position.y);

		// �浹�� �����ؼ� ������ ���� �� ���� ��쿡�� �ൿ�� ��ҽ�Ų��
		if (CONFLICT_SIDE.hasSide(block.canBlockIn(stagedata, block.data), sc))
			block.position = new Point(block.position.x + (isLeft ? 1 : -1), block.position.y);
	}
	// ȸ��
	public void rotate(boolean isCounterClockwise)
	{
		AudioUtil.playSound(SOUND.ROTATE);
		block.rotate(stagedata, isCounterClockwise);
	}
	// �ϵ� ���
	public void hardDrop()
	{
		AudioUtil.playSound(SOUND.HARDDROP);
		while (!putDownBlock(true)); // ��ġ�� �� ������ ���� �Ʒ��� ������
	}
	// �� Ȧ��
	public void holdBlock()
	{
		if (holded)
			return;
		holded = true;
		ShapeData tempBlock = ShapeData.makeShape(block.shapeType.getInt());
		if (hold == null)
			pollNextBlock();
		else
			block = ShapeData.makeShape(hold.shapeType.getInt());
		hold = tempBlock;
	}
	
	// ������ ä���� ������ �ִ��� Ȯ���ؼ� ������ ó��
	public int checkLineClear()
	{
		int lineCount = 0;
		// ���� �ؿ������� ���� �ö󰡸� ��ĵ
		for (int y = stagedata.length - 1; y >= 0; y--) 
		{
			// x ��ǥ�� ���� ���� ����
			int blockCount = 0;
			for (int x = 0; x < stagedata[y].length; x++) 
			{
				if (stagedata[y][x] > 0)
					blockCount++;
			}
			// �ش� ���ο� ���� ������ ä���� ���
			if (blockCount == stagedata[y].length)
			{
				// �� �� ���ε��� ���� ������ ����
				for (int y2 = y; y2 >= 1; y2--)
					for (int x2 = 0; x2 < stagedata[y2].length; x2++)
						stagedata[y2][x2] = stagedata[y2 - 1][x2];
				
				// ���ȣ��� ���� Ŭ��� �Ұ����� ������ �ؿ������� ���
				lineCount = 1 + checkLineClear();
			}
		}
		// �������� ���� ������ ���� ���´�
		return lineCount;
	}
	// �ؿ� ������ ������ �߰�
	public void AddTrashLine(int count)
	{
		int exceptX = rand.nextInt(10);
		for (int i = 0; i < count; i++)
		{
			// ������ �Ʒ��� ��ĵ, ���� �� ĭ ���� ����
			for (int y = 0; y < stagedata.length - 1; y++)
				for (int x = 0; x < stagedata[y].length; x++)
					stagedata[y][x] = stagedata[y + 1][x];
			
			// ���� �� ���� ������ ������ ������ ���´�
			for (int x = 0; x < stagedata[0].length; x++)
				stagedata[stagedata.length - 1][x] = (x == exceptX) ? 0 : 9;
		}
	}

	// ���� �� ��������
	void pollNextBlock()
	{
		liveCount = 2; // ���� ����
		holded = false; // Ȧ�� ���� ����
		block = nexts.poll(); // ť���� �� ��������
		
		// �� ��ġ�� ����
		int conflict = block.canBlockIn(stagedata, block.data);
		while (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.UP) && block.position.y < 20)
		{
			block.position = new Point(block.position.x, block.position.y + 1);
			conflict = block.canBlockIn(stagedata, block.data);
		}
		
		// �� �� ������ ��ǥ�� 0~2�� �ƴ϶�� ���� �������� ���ٴ� ���̹Ƿ� ���� ����
		if (block.position.y > 3)
			gameover();
		
		// ������ �� �� ������ �ٷιٷ� �߰�
		if (nexts.size() < 7)
			makeSevenBag();
	}
	
	// ��� ����Ʈ ����
	// 7���� ���� �����ϰ� �ϳ��� �����
	public void makeSevenBag()
	{
		boolean[] checkBag = new boolean[7];
		while (true)
		{
			int shapeN = rand.nextInt(7);
			
			// ���濡 ä���
			if (checkBag[shapeN] == false)
			{
				checkBag[shapeN] = true;
				nexts.offer(ShapeData.makeShape(shapeN));
			}
			
			// 7���� ���� �� ���Դ��� üũ, �ϳ��� �����ϸ� ��ŵ
			boolean check = false;
			for (int i = 0; i < checkBag.length; i++)
				if (checkBag[i] == false)
					check = true;
			if (!check)
				break;
		}
	}
}