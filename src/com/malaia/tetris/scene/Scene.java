package com.malaia.tetris.scene;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashSet;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GameObject;


/**
 * ���̽��� �Ǵ� �� ��ü
 */
public abstract class Scene
{
	protected ArrayList<GameObject> objects;
	
	public Scene()
	{
		objects = new ArrayList<GameObject>();
	}
	
	// �ʱ�ȭ �۾�
	public abstract void initialize();
	// �� ���� �۾�
	public abstract void dispose();
	
	// �����Ӹ��� ������Ʈ
	public void update()
	{
		for (GameObject object : objects)
			object.update();
	}
	
	// ������
	public void render(Graphics2D g, ImageObserver observer)
	{
		for (GameObject obj : objects)
			obj.render(g, observer);
	}
}
