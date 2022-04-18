package com.malaia.tetris.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * ����� ����
 */
public class AudioUtil
{
	// �Ҹ� ���
	public static void playSound(SOUND sound)
	{
		String path = "Resources\\Sound\\" + sound.string;
		File file = new File(path);
		try
		{
			// �Ҹ��Ͼ�
			AudioInputStream ais = AudioSystem.getAudioInputStream(file); // ����� ���� ��Ʈ�� ����
			Clip clip = AudioSystem.getClip(); // Ŭ��(�ڹ� �ý��� ������ �Ҹ��� ����� �� �ְ� ���ִ� ���ñ�...) ���
			clip.open(ais); // Ŭ���� ����� ��Ʈ�� ���
			clip.setFramePosition(0); // �Ҹ� ������ ���� ��ġ�� ����
			clip.start(); // �Ҹ� ���
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}
}
