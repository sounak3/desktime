package com.sounaks.desktime;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SettingsStorageTest
{
	@TempDir Path home;
	@TempDir Path jarDir;
	private String originalHome;

	@BeforeEach
	void isolateFromRealUser()
	{
		originalHome = System.getProperty("user.home");
		System.setProperty("user.home", home.toString());
		ExUtils.jarDir = jarDir.toFile();
	}

	@AfterEach
	void restore()
	{
		System.setProperty("user.home", originalHome);
		ExUtils.jarDir = null;
	}

	private static ArrayList<InitInfo> panelInZone(String zone)
	{
		InitInfo info = new InitInfo();
		info.setTimeZone(zone);
		ArrayList<InitInfo> list = new ArrayList<>();
		list.add(info);
		return list;
	}

	private File userFile(String name)
	{
		return home.resolve(".deskstop").resolve(name).toFile();
	}

	@Test
	void savesSettingsWhenNoDefaultFileSitsNextToTheJar()
	{
		ExUtils.saveDeskStops(panelInZone("Asia/Kolkata"));

		assertTrue(userFile(ExUtils.SETTINGS_FILE).isFile());
		List<InitInfo> loaded = ExUtils.loadDeskStops();
		assertEquals(1, loaded.size());
		assertEquals("Asia/Kolkata", loaded.get(0).getTimeZone());
	}

	@Test
	void savesAlarmsWhenNoDefaultFileSitsNextToTheJar()
	{
		TimeBean alarm = new TimeBean();
		alarm.setName("Wake up");
		Vector<TimeBean> alarms = new Vector<>();
		alarms.add(alarm);

		ExUtils.saveAlarms(alarms);

		Vector<TimeBean> loaded = ExUtils.loadAlarms();
		assertEquals(1, loaded.size());
		assertEquals("Wake up", loaded.get(0).getName());
	}

	@Test
	void returnsNothingWhenNoSettingsExistAnywhere()
	{
		assertTrue(ExUtils.loadDeskStops().isEmpty());
		assertTrue(ExUtils.loadAlarms().isEmpty());
	}

	@Test
	void usesDefaultFileNextToTheJarUntilTheUserSavesOwnSettings() throws IOException
	{
		ExUtils.writeXml(jarDir.resolve(ExUtils.SETTINGS_FILE).toFile(), panelInZone("Europe/Paris"));

		assertEquals("Europe/Paris", ExUtils.loadDeskStops().get(0).getTimeZone());

		ExUtils.saveDeskStops(panelInZone("America/Chicago"));
		assertEquals("America/Chicago", ExUtils.loadDeskStops().get(0).getTimeZone());
	}

	@Test
	void keepsPreviousVersionAsBackupAndRecoversFromCorruptFile() throws IOException
	{
		ExUtils.saveDeskStops(panelInZone("Asia/Tokyo"));
		ExUtils.saveDeskStops(panelInZone("Australia/Sydney"));
		File settings = userFile(ExUtils.SETTINGS_FILE);
		assertTrue(new File(settings.getParentFile(), settings.getName() + ".bak").isFile());

		Files.writeString(settings.toPath(), "<?xml version=\"1.0\"?><java><object class=\"java.util.ArrayList\">");

		assertEquals("Asia/Tokyo", ExUtils.loadDeskStops().get(0).getTimeZone());
	}

	@Test
	void leavesNoTempFilesBehind()
	{
		ExUtils.saveDeskStops(panelInZone("UTC"));
		ExUtils.saveDeskStops(panelInZone("UTC"));

		String[] files = home.resolve(".deskstop").toFile().list((dir, name) -> name.endsWith(".tmp"));
		assertEquals(0, files.length);
	}
}
