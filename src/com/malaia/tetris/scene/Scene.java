package com.malaia.tetris.scene;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashSet;

import com.malaia.tetris.main.TetrisFrame;
import com.malaia.tetris.object.GameObject;


/**
 * 베이스가 되는 씬 객체
 */
public abstract class Scene
{
	protected ArrayList<GameObject> objects;
	
	public Scene()
	{
		objects = new ArrayList<GameObject>();
	}
	
	// 초기화 작업
	public abstract void initialize();
	// 씬 제거 작업
	public abstract void dispose();
	
	// 프레임마다 업데이트
	public void update()
	{
		for (GameObject object : objects)
			object.update();
	}
	
	// 렌더링
	public void render(Graphics2D g, ImageObserver observer)
	{
		for (GameObject obj : objects)
			obj.render(g, observer);
	}
}
