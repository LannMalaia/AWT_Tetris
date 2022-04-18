package com.malaia.tetris.scene;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Random;

import com.malaia.tetris.data.PlayerTetrisData;
import com.malaia.tetris.data.packet.GameStartPacket;
import com.malaia.tetris.data.packet.Packet;
import com.malaia.tetris.data.packet.PlayerAttackPacket;
import com.malaia.tetris.data.packet.PlayerDataPacket;
import com.malaia.tetris.data.packet.PlayerMatchPacket;
import com.malaia.tetris.main.Server;
import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.network.RecvQueue;
import com.malaia.tetris.network.SendQueue;
import com.malaia.tetris.object.GuiObject;
import com.malaia.tetris.object.ingame.Notice;
import com.malaia.tetris.object.ingame.PlayerObject;
import com.malaia.tetris.util.AudioUtil;
import com.malaia.tetris.util.IMAGE;
import com.malaia.tetris.util.ImageUtil;
import com.malaia.tetris.util.SOUND;

/**
 * 네트워크 모드 씬
 * 싱글 모드랑 똑같은데 네트워크가 조금 추가된 정도
 */
public final class NetworkGameScene extends Scene
{
	// 씬 진행 상태 확인을 위한 enum
	enum GAME_STATE { STANDBY, BEGIN, PLAY, END }

	/*
	 * Variable
	 */
	private GAME_STATE state;  // 현재 상태
	private GuiObject bg; // 배경 이미지
	private PlayerObject playerobject, rivalobject; // 플레이어 및 상대 오브젝트
	private Notice notice; // 화면 맨 앞에 뜨는 알림용 이미지

	/*
	 * Data
	 */
	PlayerTetrisData playerdata, rivaldata; // 플레이어 및 상대 데이터
	private RecvQueue recver; // 패킷 받는 녀석
	private SendQueue sender; // 패킷 보내는 녀석

	/*
	 * Constructor
	 */
	public NetworkGameScene()
	{
		super();
		
		// 데이터 설정
		state = GAME_STATE.STANDBY;
		playerdata = new PlayerTetrisData(new Random().nextLong());
		rivaldata = new PlayerTetrisData(new Random().nextLong());
		
		// 배경 생성, 플레이어 테트리스 화면이랑 배경간에 색 차이가 별로 없으므로 약간 어둡게 만든다
		bg = new GuiObject(new Point(0, 0), ImageUtil.loadImage(IMAGE.BACKGROUND));
		bg.renderer.fixColor(0x66990000);
		
		// 씬에 쓰일 오브젝트 생성 & 추가
		playerobject = new PlayerObject(new Point(320, 0));
		rivalobject = new PlayerObject(new Point(960, 0));
		notice = new Notice(new Point(640, 360));
		
		objects.add(bg);
		objects.add(playerobject);
		objects.add(rivalobject);
		objects.add(notice);
	}

	/*
	 * Override Method
	 */
	@Override
	public void initialize()
	{
		new Thread(() -> {
			try
			{
				// 0.5초 쉬고 플레이어 오브젝트 활성화
				Thread.sleep(500);
				playerobject.setActive(true);
				rivalobject.setActive(true);

				// 키 입력 리스너를 등록
				TetrisFrame.getInstance().addKeyListener(keyListener);

				// 서버 접속 시도
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_CONNECT));
				Socket toServer = new Socket( // 도메인의 경우를 대비해 InetAddress 사용
						InetAddress.getByName(Server.SERVER_ADDRESS), Server.SERVER_PORT);
				
				// 서버 연결 성공
				ObjectInputStream ois = new ObjectInputStream(toServer.getInputStream());
				Object packet = ois.readObject();
				// 받은 패킷이 제대로 된 경우 패킷에 내용에 맞춰 호스트/조인을 구분
				if (packet instanceof PlayerMatchPacket)
				{
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_WAIT));
					PlayerMatchPacket pmp = (PlayerMatchPacket)packet;
					if (pmp.isHost)
						waitMatch(pmp.port);
					else
						joinMatch(pmp.address, pmp.port);
				}
				// 서버로의 소켓은 끊는다
				toServer.close();
			}
			catch (ConnectException e)
			{
				System.out.println("-- 서버 닫힘 -- ");
				e.printStackTrace();
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_FAIL), 300, 1000, 300);
				try
				{ Thread.sleep(1000); }
				catch (InterruptedException e1)
				{ e1.printStackTrace(); }
				TetrisFrame.changeScene(new TitleScene());
			}
			catch (InterruptedException e)
			{ System.out.println("-- 스레드 오류 --"); e.printStackTrace(); }
			catch (ClassNotFoundException e)
			{ System.out.println("-- 호환되지 않는 클래스 --"); e.printStackTrace(); }
			catch (IOException e)
			{ System.out.println("-- 입출력 오류 --"); e.printStackTrace(); }
			catch (Exception e)
			{ System.out.println("-- 기타 오류 --"); e.printStackTrace(); }
		}).start();
	}
	@Override
	public void dispose()
	{
		TetrisFrame.getInstance().removeKeyListener(keyListener);
		playerobject.setActive(false);
		rivalobject.setActive(false);
	}
	
	@Override
	public void update()
	{
		super.update();
		switch (state) // state에 따라 업데이트시의 동작을 구분 
		{
		case PLAY: // 한창 하는 중
			playerdata.update(TetrisFrame.FPS); // 플레이어 데이터 갱신 (상대의 경우 시간에 따른 갱신은 하지 않는다)
			updatePlayerdata(); // 데이터에 따른 자신의 오브젝트 갱신, 이 때 공격을 하거나 소켓이 끊어진 경우를 알 수 있다
			rivalobject.updatePlayerTetrisData(rivaldata); // 데이터에 따른 상대 오브젝트 갱신
			
			// 게임 오버 체크
			if (playerdata.isGameOvered() || rivaldata.isGameOvered())
				playGameOverEffect();
			break;
		case END: // 게임 끝
			updatePlayerdata();
			rivalobject.updatePlayerTetrisData(rivaldata);
			break;
		}
		
	}
	
	// 게임 오버
	public void playGameOverEffect()
	{
		state = GAME_STATE.END; // 키 입력이나 화면 갱신을 막기 위해 상태 변경
		
		// 화면 중심에 이겼나 졌나를 표시하고 타이틀로 나간다
		new Thread(() -> { 
			try
			{
				Thread.sleep(2000);
				if (playerdata.isGameOvered())
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_LOSE), 1000, 1200, 1000);
				else if (rivaldata.isGameOvered())
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_WIN), 1000, 1200, 1000);
				else // 소켓이 끊긴 경우 무승부
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_DRAW), 1000, 1200, 1000);
				Thread.sleep(2000);

				// 타이틀로 이동
				TetrisFrame.changeScene(new TitleScene());
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	// 시작 기다리기
	public void ready()
	{
		state = GAME_STATE.BEGIN;
		
		// 3 2 1 시작~ 하는 애니메이션 메소드
		new Thread(() -> {
			try
			{
				Thread.sleep(2000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_3), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_2), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_1), 300, 500, 300);
				Thread.sleep(1000);
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_START), 300, 500, 300);
				state = GAME_STATE.PLAY;
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	// 상대의 패킷 받기
	// Recv_Queue 객체에 이 함수가 등록된다
	// 패킷에 따라 하는 행동이 달라진다
	public void whenPacketListen(Packet packet)
	{
		// 게임 시작 사인
		if (packet instanceof GameStartPacket)
		{
			if (state != GAME_STATE.STANDBY)
				return;
			
			// 나와 상대의 시작값을 동일하게 만든다
			GameStartPacket gsp = (GameStartPacket)packet;
			playerdata = new PlayerTetrisData(gsp.randomSeed);
			rivaldata = new PlayerTetrisData(gsp.randomSeed);
			
			// 이후 3 2 1 시작하는 그거 실행
			ready();
		}
		// 상대의 데이터를 받음
		if (packet instanceof PlayerDataPacket)
		{
			// 데이터를 갱신하고 화면도 갱신한다
			PlayerDataPacket pdp = (PlayerDataPacket)packet;
			rivaldata = pdp.playerdata;
			rivalobject.updatePlayerTetrisData(rivaldata);
		}
		// 상대에게서 공격이 들어옴
		if (packet instanceof PlayerAttackPacket)
		{
			PlayerAttackPacket pap = (PlayerAttackPacket)packet;
			playerdata.AddTrashLine(pap.lineCount); // 쓰레기 라인 추가
		}
	}
	
	// 자신의 플레이어 데이터 업데이트
	public void updatePlayerdata()
	{
		// 이 때 Send_Queue 객체가 작동하지 않았다면 게임을 끝낸다
		if (!sender.isAvailable())
		{
			if (state != GAME_STATE.END)
				playGameOverEffect();
		}
		else
		{
			sender.sendPacket(new PlayerDataPacket(playerdata));
			int attack = playerdata.getAttack(); // 공격 값을 취득함과 동시에 해당 공격 값을 초기화
			if (attack > 0)
				sender.sendPacket(new PlayerAttackPacket(attack));
			playerobject.updatePlayerTetrisData(playerdata); // 자신의 화면을 갱신
		}
	}
	
	// 매칭 (호스트)
	void waitMatch(int port) throws IOException
	{
		// 로비 서버에서 전달받은 포트 번호로 서버 소켓을 생성, 상대를 기다린다
		ServerSocket server = new ServerSocket(port);
		Socket socket = server.accept();
		
		// 상대가 접속했다면 상대 소켓을 기반으로 Recv용 객체와 Send용 객체를 만든다
		recver = new RecvQueue(socket, this::whenPacketListen);
		sender = new SendQueue(socket);
		
		// 이후 상대에게 게임 시작한다는 패킷을 전송
		GameStartPacket gsp = new GameStartPacket(new Random().nextLong());
		sender.sendPacket(gsp);
		whenPacketListen(gsp); // 스스로에게도 똑같은 패킷을 전송
		server.close(); // 서버 소켓은 필요 없어지므로 제거한다
	}
	// 매칭 (조인)
	void joinMatch(String address, int port) throws IOException
	{
		// 로비 서버에서 전달받은 주소와 포트 번호로 대상에게 접속한다
		Socket socket = new Socket(address, port);
		
		// 접속에 성공했다면 상대 소켓을 기반으로 Recv용 객체와 Send용 객체를 만든다
		recver = new RecvQueue(socket, this::whenPacketListen);
		sender = new SendQueue(socket);
		
		// 이후 게임 시작한다는 패킷을 받으면 whenPacketListen 메소드에 따라 적절히 실행
	}

	// 플레이어 키 이벤트 리스너
	// 싱글과 동일
	PlayerKeyListener keyListener = new PlayerKeyListener();
	class PlayerKeyListener extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			super.keyPressed(e);
			
			if (state != GAME_STATE.PLAY)
				return;
			
			switch (e.getKeyCode())
			{
			case KeyEvent.VK_UP:
				playerdata.rotate(false);
				break;
			case KeyEvent.VK_DOWN:
				playerdata.putDownBlock(false);
				break;
			case KeyEvent.VK_LEFT:
				playerdata.move(true);
				break;
			case KeyEvent.VK_RIGHT:
				playerdata.move(false);
				break;
			case KeyEvent.VK_SPACE:
				playerdata.hardDrop();
				break;
			case KeyEvent.VK_SHIFT:
				playerdata.holdBlock();
				break;
			}
			updatePlayerdata();
		}
	}
}