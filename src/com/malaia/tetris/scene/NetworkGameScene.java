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
 * ��Ʈ��ũ ��� ��
 * �̱� ���� �Ȱ����� ��Ʈ��ũ�� ���� �߰��� ����
 */
public final class NetworkGameScene extends Scene
{
	// �� ���� ���� Ȯ���� ���� enum
	enum GAME_STATE { STANDBY, BEGIN, PLAY, END }

	/*
	 * Variable
	 */
	private GAME_STATE state;  // ���� ����
	private GuiObject bg; // ��� �̹���
	private PlayerObject playerobject, rivalobject; // �÷��̾� �� ��� ������Ʈ
	private Notice notice; // ȭ�� �� �տ� �ߴ� �˸��� �̹���

	/*
	 * Data
	 */
	PlayerTetrisData playerdata, rivaldata; // �÷��̾� �� ��� ������
	private RecvQueue recver; // ��Ŷ �޴� �༮
	private SendQueue sender; // ��Ŷ ������ �༮

	/*
	 * Constructor
	 */
	public NetworkGameScene()
	{
		super();
		
		// ������ ����
		state = GAME_STATE.STANDBY;
		playerdata = new PlayerTetrisData(new Random().nextLong());
		rivaldata = new PlayerTetrisData(new Random().nextLong());
		
		// ��� ����, �÷��̾� ��Ʈ���� ȭ���̶� ��氣�� �� ���̰� ���� �����Ƿ� �ణ ��Ӱ� �����
		bg = new GuiObject(new Point(0, 0), ImageUtil.loadImage(IMAGE.BACKGROUND));
		bg.renderer.fixColor(0x66990000);
		
		// ���� ���� ������Ʈ ���� & �߰�
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
				// 0.5�� ���� �÷��̾� ������Ʈ Ȱ��ȭ
				Thread.sleep(500);
				playerobject.setActive(true);
				rivalobject.setActive(true);

				// Ű �Է� �����ʸ� ���
				TetrisFrame.getInstance().addKeyListener(keyListener);

				// ���� ���� �õ�
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_CONNECT));
				Socket toServer = new Socket( // �������� ��츦 ����� InetAddress ���
						InetAddress.getByName(Server.SERVER_ADDRESS), Server.SERVER_PORT);
				
				// ���� ���� ����
				ObjectInputStream ois = new ObjectInputStream(toServer.getInputStream());
				Object packet = ois.readObject();
				// ���� ��Ŷ�� ����� �� ��� ��Ŷ�� ���뿡 ���� ȣ��Ʈ/������ ����
				if (packet instanceof PlayerMatchPacket)
				{
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_WAIT));
					PlayerMatchPacket pmp = (PlayerMatchPacket)packet;
					if (pmp.isHost)
						waitMatch(pmp.port);
					else
						joinMatch(pmp.address, pmp.port);
				}
				// �������� ������ ���´�
				toServer.close();
			}
			catch (ConnectException e)
			{
				System.out.println("-- ���� ���� -- ");
				e.printStackTrace();
				notice.notice(ImageUtil.loadImage(IMAGE.GAME_SERV_FAIL), 300, 1000, 300);
				try
				{ Thread.sleep(1000); }
				catch (InterruptedException e1)
				{ e1.printStackTrace(); }
				TetrisFrame.changeScene(new TitleScene());
			}
			catch (InterruptedException e)
			{ System.out.println("-- ������ ���� --"); e.printStackTrace(); }
			catch (ClassNotFoundException e)
			{ System.out.println("-- ȣȯ���� �ʴ� Ŭ���� --"); e.printStackTrace(); }
			catch (IOException e)
			{ System.out.println("-- ����� ���� --"); e.printStackTrace(); }
			catch (Exception e)
			{ System.out.println("-- ��Ÿ ���� --"); e.printStackTrace(); }
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
		switch (state) // state�� ���� ������Ʈ���� ������ ���� 
		{
		case PLAY: // ��â �ϴ� ��
			playerdata.update(TetrisFrame.FPS); // �÷��̾� ������ ���� (����� ��� �ð��� ���� ������ ���� �ʴ´�)
			updatePlayerdata(); // �����Ϳ� ���� �ڽ��� ������Ʈ ����, �� �� ������ �ϰų� ������ ������ ��츦 �� �� �ִ�
			rivalobject.updatePlayerTetrisData(rivaldata); // �����Ϳ� ���� ��� ������Ʈ ����
			
			// ���� ���� üũ
			if (playerdata.isGameOvered() || rivaldata.isGameOvered())
				playGameOverEffect();
			break;
		case END: // ���� ��
			updatePlayerdata();
			rivalobject.updatePlayerTetrisData(rivaldata);
			break;
		}
		
	}
	
	// ���� ����
	public void playGameOverEffect()
	{
		state = GAME_STATE.END; // Ű �Է��̳� ȭ�� ������ ���� ���� ���� ����
		
		// ȭ�� �߽ɿ� �̰峪 ������ ǥ���ϰ� Ÿ��Ʋ�� ������
		new Thread(() -> { 
			try
			{
				Thread.sleep(2000);
				if (playerdata.isGameOvered())
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_LOSE), 1000, 1200, 1000);
				else if (rivaldata.isGameOvered())
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_WIN), 1000, 1200, 1000);
				else // ������ ���� ��� ���º�
					notice.notice(ImageUtil.loadImage(IMAGE.GAME_DRAW), 1000, 1200, 1000);
				Thread.sleep(2000);

				// Ÿ��Ʋ�� �̵�
				TetrisFrame.changeScene(new TitleScene());
			}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
		}).start();
	}
	
	// ���� ��ٸ���
	public void ready()
	{
		state = GAME_STATE.BEGIN;
		
		// 3 2 1 ����~ �ϴ� �ִϸ��̼� �޼ҵ�
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
	
	// ����� ��Ŷ �ޱ�
	// Recv_Queue ��ü�� �� �Լ��� ��ϵȴ�
	// ��Ŷ�� ���� �ϴ� �ൿ�� �޶�����
	public void whenPacketListen(Packet packet)
	{
		// ���� ���� ����
		if (packet instanceof GameStartPacket)
		{
			if (state != GAME_STATE.STANDBY)
				return;
			
			// ���� ����� ���۰��� �����ϰ� �����
			GameStartPacket gsp = (GameStartPacket)packet;
			playerdata = new PlayerTetrisData(gsp.randomSeed);
			rivaldata = new PlayerTetrisData(gsp.randomSeed);
			
			// ���� 3 2 1 �����ϴ� �װ� ����
			ready();
		}
		// ����� �����͸� ����
		if (packet instanceof PlayerDataPacket)
		{
			// �����͸� �����ϰ� ȭ�鵵 �����Ѵ�
			PlayerDataPacket pdp = (PlayerDataPacket)packet;
			rivaldata = pdp.playerdata;
			rivalobject.updatePlayerTetrisData(rivaldata);
		}
		// ��뿡�Լ� ������ ����
		if (packet instanceof PlayerAttackPacket)
		{
			PlayerAttackPacket pap = (PlayerAttackPacket)packet;
			playerdata.AddTrashLine(pap.lineCount); // ������ ���� �߰�
		}
	}
	
	// �ڽ��� �÷��̾� ������ ������Ʈ
	public void updatePlayerdata()
	{
		// �� �� Send_Queue ��ü�� �۵����� �ʾҴٸ� ������ ������
		if (!sender.isAvailable())
		{
			if (state != GAME_STATE.END)
				playGameOverEffect();
		}
		else
		{
			sender.sendPacket(new PlayerDataPacket(playerdata));
			int attack = playerdata.getAttack(); // ���� ���� ����԰� ���ÿ� �ش� ���� ���� �ʱ�ȭ
			if (attack > 0)
				sender.sendPacket(new PlayerAttackPacket(attack));
			playerobject.updatePlayerTetrisData(playerdata); // �ڽ��� ȭ���� ����
		}
	}
	
	// ��Ī (ȣ��Ʈ)
	void waitMatch(int port) throws IOException
	{
		// �κ� �������� ���޹��� ��Ʈ ��ȣ�� ���� ������ ����, ��븦 ��ٸ���
		ServerSocket server = new ServerSocket(port);
		Socket socket = server.accept();
		
		// ��밡 �����ߴٸ� ��� ������ ������� Recv�� ��ü�� Send�� ��ü�� �����
		recver = new RecvQueue(socket, this::whenPacketListen);
		sender = new SendQueue(socket);
		
		// ���� ��뿡�� ���� �����Ѵٴ� ��Ŷ�� ����
		GameStartPacket gsp = new GameStartPacket(new Random().nextLong());
		sender.sendPacket(gsp);
		whenPacketListen(gsp); // �����ο��Ե� �Ȱ��� ��Ŷ�� ����
		server.close(); // ���� ������ �ʿ� �������Ƿ� �����Ѵ�
	}
	// ��Ī (����)
	void joinMatch(String address, int port) throws IOException
	{
		// �κ� �������� ���޹��� �ּҿ� ��Ʈ ��ȣ�� ��󿡰� �����Ѵ�
		Socket socket = new Socket(address, port);
		
		// ���ӿ� �����ߴٸ� ��� ������ ������� Recv�� ��ü�� Send�� ��ü�� �����
		recver = new RecvQueue(socket, this::whenPacketListen);
		sender = new SendQueue(socket);
		
		// ���� ���� �����Ѵٴ� ��Ŷ�� ������ whenPacketListen �޼ҵ忡 ���� ������ ����
	}

	// �÷��̾� Ű �̺�Ʈ ������
	// �̱۰� ����
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