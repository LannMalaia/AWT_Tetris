package com.malaia.tetris.data;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.SOUND;

/**
 * 플레이어 데이터
 * 해당 플레이어의 테트리스 상태에 관한 모든 것을 담는다
 */
public final class PlayerTetrisData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int NEXT_NEED = 3; // 다음 레벨업에 필요한 라인 개수
	
	private int[][] stagedata; // 현재 스테이지의 상태
	public Queue<ShapeData> nexts; // 다음에 나올 블럭 리스트
	public ShapeData hold;	// 현재 홀드중인 블럭
	public ShapeData block;	// 현재 컨트롤중인 블럭

	Random rand; // 난수 출력용
	private long ms = 0; // 플레이 후 흐른 시간
	private long intervalCount = 0; // 블록 낙하 계산용
	private boolean holded = false;
	private int liveCount = 0; // 블록이 설치되기 전에 유지되는 시간
	
	private boolean isGameOver; // 게임 오버
	public boolean isGameOvered() { return isGameOver; }

	private int level; // 레벨
	public int getLevel() { return level; }
	
	private long score; // 점수
	public long getScore() { return score; }
	
	private int lineAttack; // 공격할 라인
	public int getAttack() { int result = lineAttack; lineAttack = 0; return result; }
	
	private int nextlevel; // 다음 레벨로 가기까지 필요한 줄 수
	
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
		makeSevenBag(); // 블럭 리스트 생성
		pollNextBlock(); // 조종중인 블럭이 없으므로 블럭 하나 꺼내오기
	}
	
	// 화면 갱신 속도에 맞춰 오브젝트 갱신
	public void update(long ms)
	{
		if (isGameOver)
			return;
		
		ms += ms;
		
		// 레벨에 맞추어 대기 시간이 짧아지며, 해당 시간을 초과하면 블럭이 강제로 내려온다
		intervalCount += ms;
		int interval = (20 - level + 1) * 100;
		if (intervalCount >= interval)
		{
			intervalCount = 0;
			putDownBlock(false);
		}
	}
	
	// 게임 오버 처리
	public void gameover()
	{
		isGameOver = true;
		
		// 스테이지를 검은 블럭으로 채워넣는 애니메이션 스레드 (여기 있으면 안될듯)
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
	
	// 렌더링을 위한 스테이지 정보를 반환한다
	// 조종중인 블럭이나 그림자 블럭이 그려지므로 실제 스테이지 정보와는 좀 다르다
	public int[][] getStage()
	{
		int[][] stage = new int[stagedata.length][stagedata[0].length];
		// 원본 복사
		for (int y = 0; y < stagedata.length; y++)
			for (int x = 0; x < stagedata[y].length; x++)
				stage[y][x] = stagedata[y][x];
		
		// 게임 오버 상태라면 그림자나 다른 블럭을 그리지 않음
		if (isGameOver)
			return stage;
		
		// 그림자 블럭 그리기
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
		
		// 컨트롤하는 블럭 그리기
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
	
	// 레벨업 -> 내려오는 속도가 상승
	public void levelUp()
	{
		level = Math.min(20, level + 1);
	}
	
	// 블럭 내리기
	public boolean putDownBlock(boolean isDrop)
	{
		// 소프트 드랍(아래 키로 강제 내리기)인 경우 설치되지 않게 방지
		if (isDrop)
			AudioUtil.playSound(SOUND.SOFTDROP);
		
		// 위치 갱신
		block.position = new Point(block.position.x, block.position.y + 1);
		
		// 충돌 감지
		int conflict = block.canBlockIn(stagedata, block.data);
		if (CONFLICT_SIDE.getSideCount(conflict) > 0) // 내려온 위치에 온전히 놓을 수 없는 경우
		{
			// 수명 깎기
			liveCount--;
			
			// 위치는 다시 위로 올리기
			block.position = new Point(block.position.x, block.position.y - 1);
			
			if (isDrop || liveCount <= 0) // 플레이어가 강제로 드롭했거나 수명이 다됐으면 설치 시작
			{
				AudioUtil.playSound(SOUND.LANDING);
				// 해당 지점에 블록 설치
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
				// 라인 클리어 체크
				int clearedLine = checkLineClear();
				
				// 대전중이라면 공격할 라인의 수도 필요하므로 그것도 계산해둔다
				lineAttack += clearedLine > 1 ? clearedLine - 1 : 0;
				
				// 스코어 갱신
				score += clearedLine * 1000;
				
				// 레벨 갱신 (레벨 업 포함)
				nextlevel -= clearedLine;
				while (nextlevel <= 0)
				{
					levelUp();
					nextlevel = NEXT_NEED + nextlevel;
				}
				
				// 지운 라인의 수에 따라 출력되는 소리를 조절
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
				
				// 새로운 블록 등장
				pollNextBlock();
				return true; // -> 제대로 설치했다
			}
		}
		return false; // -> 설치하지 못했다
	}
	// 양옆 움직임
	public void move(boolean isLeft)
	{
		AudioUtil.playSound(SOUND.MOVE);
		CONFLICT_SIDE sc = isLeft ? CONFLICT_SIDE.LEFT : CONFLICT_SIDE.RIGHT;
		block.position = new Point(block.position.x + (isLeft ? -1 : 1), block.position.y);

		// 충돌을 감지해서 온전히 놓을 수 없는 경우에는 행동을 취소시킨다
		if (CONFLICT_SIDE.hasSide(block.canBlockIn(stagedata, block.data), sc))
			block.position = new Point(block.position.x + (isLeft ? 1 : -1), block.position.y);
	}
	// 회전
	public void rotate(boolean isCounterClockwise)
	{
		AudioUtil.playSound(SOUND.ROTATE);
		block.rotate(stagedata, isCounterClockwise);
	}
	// 하드 드롭
	public void hardDrop()
	{
		AudioUtil.playSound(SOUND.HARDDROP);
		while (!putDownBlock(true)); // 설치가 될 때까지 블럭을 아래로 내린다
	}
	// 블럭 홀드
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
	
	// 완전히 채워진 라인이 있는지 확인해서 있으면 처리
	public int checkLineClear()
	{
		int lineCount = 0;
		// 가장 밑에서부터 위로 올라가며 스캔
		for (int y = stagedata.length - 1; y >= 0; y--) 
		{
			// x 좌표로 블럭의 수를 센다
			int blockCount = 0;
			for (int x = 0; x < stagedata[y].length; x++) 
			{
				if (stagedata[y][x] > 0)
					blockCount++;
			}
			// 해당 라인에 블럭이 완전히 채워진 경우
			if (blockCount == stagedata[y].length)
			{
				// 그 위 라인들을 전부 밑으로 당긴다
				for (int y2 = y; y2 >= 1; y2--)
					for (int x2 = 0; x2 < stagedata[y2].length; x2++)
						stagedata[y2][x2] = stagedata[y2 - 1][x2];
				
				// 재귀호출로 라인 클리어가 불가능할 때까지 밑에서부터 계산
				lineCount = 1 + checkLineClear();
			}
		}
		// 마지막엔 지운 라인의 수가 남는다
		return lineCount;
	}
	// 밑에 쓰레기 라인을 추가
	public void AddTrashLine(int count)
	{
		int exceptX = rand.nextInt(10);
		for (int i = 0; i < count; i++)
		{
			// 위에서 아래로 스캔, 전부 한 칸 위로 당긴다
			for (int y = 0; y < stagedata.length - 1; y++)
				for (int x = 0; x < stagedata[y].length; x++)
					stagedata[y][x] = stagedata[y + 1][x];
			
			// 이후 맨 밑의 라인을 쓰레기 블럭으로 덮는다
			for (int x = 0; x < stagedata[0].length; x++)
				stagedata[stagedata.length - 1][x] = (x == exceptX) ? 0 : 9;
		}
	}

	// 다음 블럭 가져오기
	void pollNextBlock()
	{
		liveCount = 2; // 수명 갱신
		holded = false; // 홀드 봉인 제거
		block = nexts.poll(); // 큐에서 블럭 가져오기
		
		// 블럭 위치를 갱신
		int conflict = block.canBlockIn(stagedata, block.data);
		while (CONFLICT_SIDE.hasSide(conflict, CONFLICT_SIDE.UP) && block.position.y < 20)
		{
			block.position = new Point(block.position.x, block.position.y + 1);
			conflict = block.canBlockIn(stagedata, block.data);
		}
		
		// 이 때 갱신한 좌표가 0~2가 아니라면 블럭을 놓을데가 없다는 뜻이므로 게임 오버
		if (block.position.y > 3)
			gameover();
		
		// 가방이 빈 것 같으면 바로바로 추가
		if (nexts.size() < 7)
			makeSevenBag();
	}
	
	// 블록 리스트 생성
	// 7개의 블럭을 균일하게 하나씩 만든다
	public void makeSevenBag()
	{
		boolean[] checkBag = new boolean[7];
		while (true)
		{
			int shapeN = rand.nextInt(7);
			
			// 가방에 채우기
			if (checkBag[shapeN] == false)
			{
				checkBag[shapeN] = true;
				nexts.offer(ShapeData.makeShape(shapeN));
			}
			
			// 7가지 블럭이 다 나왔는지 체크, 하나라도 부족하면 스킵
			boolean check = false;
			for (int i = 0; i < checkBag.length; i++)
				if (checkBag[i] == false)
					check = true;
			if (!check)
				break;
		}
	}
}