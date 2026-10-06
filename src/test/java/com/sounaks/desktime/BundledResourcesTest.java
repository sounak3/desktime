package com.sounaks.desktime;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BundledResourcesTest
{
	@TempDir Path home;
	@TempDir Path work;
	private String originalHome;

	@BeforeEach
	void isolateFromRealUser()
	{
		originalHome = System.getProperty("user.home");
		System.setProperty("user.home", home.toString());
	}

	@AfterEach
	void restore()
	{
		System.setProperty("user.home", originalHome);
	}

	// InitInfo runs from target/classes in tests, so the bundled defaults are target/classes/images and /sounds.
	@Test
	void keepsChosenImageNameWhenStoredPathIsFromAnotherMachine()
	{
		InitInfo info = new InitInfo();

		info.setImageFile("C:\\Users\\Someone\\Pictures\\block-icon.png");
		assertEquals("block-icon.png", info.getImageFile().getName());
		assertTrue(info.getImageFile().isFile());

		info.setImageFile("/home/nobody/old-install/images/background-icon.png");
		assertEquals("background-icon.png", info.getImageFile().getName());
		assertTrue(info.getImageFile().isFile());
	}

	@Test
	void fallsBackToDefaultWhenChosenFileIsNotBundled()
	{
		InitInfo info = new InitInfo();
		info.setImageFile("/nowhere/my-holiday-photo.jpg");
		assertEquals("BabyBlue.JPG", info.getImageFile().getName());

		info.setAlarmSound("C:\\Music\\not-bundled.mp3");
		assertTrue(new File(info.getAlarmSound()).isFile());
	}

	@Test
	void extractsOnlyImagesAndSoundsAndReplacesOtherBuilds() throws IOException, InterruptedException
	{
		File jar = work.resolve("desktime.jar").toFile();
		writeJar(jar, "images/a.png", "sounds/b.mp3", "com/sounaks/desktime/DeskStop.class");

		File first = ExUtils.getJarExtractedDirectory(jar);
		assertTrue(new File(first, "images/a.png").isFile());
		assertTrue(new File(first, "sounds/b.mp3").isFile());
		assertFalse(new File(first, "com").exists());

		Thread.sleep(1100);
		writeJar(jar, "images/a.png", "images/new.png");
		File second = ExUtils.getJarExtractedDirectory(jar);

		assertNotEquals(first, second);
		assertTrue(new File(second, "images/new.png").isFile());
		assertFalse(first.exists());
	}

	private static void writeJar(File jar, String... entries) throws IOException
	{
		try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar.toPath()))) {
			for (String entry : entries) {
				out.putNextEntry(new JarEntry(entry));
				out.write(entry.getBytes());
				out.closeEntry();
			}
		}
	}
}
