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
 * 오디오 도구
 */
public class AudioUtil
{
	// 소리 출력
	public static void playSound(SOUND sound)
	{
		String path = "Resources\\Sound\\" + sound.string;
		File file = new File(path);
		try
		{
			// 할말하않
			AudioInputStream ais = AudioSystem.getAudioInputStream(file); // 오디오 파일 스트림 생성
			Clip clip = AudioSystem.getClip(); // 클립(자바 시스템 내에서 소리를 재생할 수 있게 해주는 뭐시기...) 취득
			clip.open(ais); // 클립에 오디오 스트림 등록
			clip.setFramePosition(0); // 소리 파일의 실행 위치를 조정
			clip.start(); // 소리 재생
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}
}
