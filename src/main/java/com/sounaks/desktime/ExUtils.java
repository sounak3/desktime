package com.sounaks.desktime;

import javax.swing.*;
import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ExUtils
{
	public static final int V_TILE  = 1;
	public static final int H_TILE  = 2;
	public static final int CENTER  = 4;
	public static final int STRETCH = 8;
	public static final int FIT     = 16;
	public static final int TILE    = 32;

	public static final int AUDIO_ALARM   = 1;
	public static final int BEEP_ALARM    = 2;
	public static final int MESSAGE_ALARM = 4;

	public static final String SETTINGS_FILE          = "DeskTime.xml";
	public static final String INTERNAL_SETTINGS_FILE = "app/DeskTime.xml";
	public static final String ALARMS_FILE            = "Alarms.xml";
	public static final String INTERNAL_ALARMS_FILE   = "app/Alarms.xml";
	private static File jarDir;

	public enum ROUND_CORNERS {		
		SQUARE(0), MINIMAL(1), STANDARD(4), SQUIRCLE(16), CIRCLE(20);

		private int roundType;

		ROUND_CORNERS (int roundType) {
			this.roundType = roundType;
		}

		public int getRoundType() {
			return roundType;
		}

		public static String nameOfValue(int value) {
			for (ROUND_CORNERS corner : ROUND_CORNERS.values()) {
				if (corner.getRoundType() == value) {
					return corner.name();
				}
			}
			return ROUND_CORNERS.SQUARE.name();
		}
	}

	private static OS os = null;

	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	} // Operating systems.

	public static OS getOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                os = OS.LINUX;
            } else if (operSys.contains("mac")) {
                os = OS.MAC;
            } else if (operSys.contains("sunos")) {
                os = OS.SOLARIS;
            }
        }
        return os;
    }

	public static void	addComponent(Container container, Component	component, int[] gridXYWH, double d, double	d1, ActionListener alistener) throws AWTException
	{
		java.awt.LayoutManager layoutmanager = container.getLayout();
		if (!(layoutmanager instanceof GridBagLayout))
			throw new AWTException("Invalid Layout " + layoutmanager);
		GridBagConstraints gridbagconstraints            = new	GridBagConstraints();
		                   gridbagconstraints.gridx      = gridXYWH[0];
		                   gridbagconstraints.gridy      = gridXYWH[1];
		                   gridbagconstraints.gridwidth  = gridXYWH[2];
		                   gridbagconstraints.gridheight = gridXYWH[3];
		                   gridbagconstraints.weightx    = d;
		                   gridbagconstraints.weighty    = d1;
		                   gridbagconstraints.fill       = GridBagConstraints.BOTH;
		                   gridbagconstraints.anchor     = GridBagConstraints.WEST;
		                   gridbagconstraints.insets     = new	Insets(5, 5, 5,	5);
		((GridBagLayout)layoutmanager).setConstraints(component, gridbagconstraints);
		container.add(component);
		if (component instanceof	JButton)
			((JButton)component).addActionListener(alistener);
		if (component instanceof	JRadioButton)
			((JRadioButton)component).addActionListener(alistener);
		if (component instanceof	JCheckBox)
			((JCheckBox)component).addActionListener(alistener);
	}

	public static void showPopup(JPopupMenu jpopupmenu, Component parent, Component component, Point point, Dimension dimension)
	{
		boolean flag  = false;
		boolean flag1 = false;
		SwingUtilities.convertPointToScreen(point, parent);
		if (dimension.width - point.getX()  < jpopupmenu.getWidth())
			flag  = true;
		if (dimension.height - point.getY() < jpopupmenu.getHeight())
			flag1 = true;
		SwingUtilities.convertPointFromScreen(point, parent);
		if (flag && !flag1)
			jpopupmenu.show(component, (int)point.getX() - jpopupmenu.getWidth(), (int)point.getY());
		else
		if (!flag && flag1)
			jpopupmenu.show(component, (int)point.getX(), (int)point.getY() - jpopupmenu.getHeight());
		else
		if (flag && flag1)
			jpopupmenu.show(component, (int)point.getX() - jpopupmenu.getWidth(), (int)point.getY() - jpopupmenu.getHeight());
		else
			jpopupmenu.show(component, (int)point.getX(), (int)point.getY());
	}

	public static boolean containsNumbers(String input) {
		return input.matches(".*\\d.*");
	}
	
	public static boolean contains(String s, String s1, boolean ignoreCase)
	{
		String s2;
		String s3;
		if (ignoreCase)
		{
			s2 = s.toLowerCase();
			s3 = s1.toLowerCase();
		}
		else
		{
			s2 = s;
			s3 = s1;
		}
		for (int i = 0; i < s.length(); i++)
			if (s3.charAt(0) == s2.charAt(i) && s.regionMatches(ignoreCase, i, s1, 0, s1.length()))
				return true;

		return false;
	}
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
	
	public static boolean checkAWTPermission(String permission)
	{
		AWTPermission   awtp    = new AWTPermission(permission);
		try
		{
			awtp.checkGuard(null);
			return true;
		}
		catch(SecurityException se)
		{
			return false;
		}
	}
	
	public static boolean dateCompareUptoSecond(Date dt1, Date dt2)
	{
		GregorianCalendar gc1 = new GregorianCalendar();
		GregorianCalendar gc2 = new GregorianCalendar();
		gc1.setTime(dt1);
		gc2.setTime(dt2);
		return gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)&&
		gc1.get(Calendar.MONTH)       == gc2.get(Calendar.MONTH)&&
		gc1.get(Calendar.DATE)        == gc2.get(Calendar.DATE)&&
		gc1.get(Calendar.HOUR_OF_DAY) == gc2.get(Calendar.HOUR_OF_DAY)&&
		gc1.get(Calendar.MINUTE)      == gc2.get(Calendar.MINUTE)&&
		gc1.get(Calendar.SECOND)      == gc2.get(Calendar.SECOND);
	}
	
	public static void showErrorMessage(String messageStr, String errTitle, Component parent, int displaySec) {
		Dimension   scsize      = Toolkit.getDefaultToolkit().getScreenSize();
		JOptionPane opt         = new JOptionPane(messageStr,JOptionPane.ERROR_MESSAGE);
		final       JDialog dlg = opt.createDialog(parent, errTitle);
		int         errShow     = displaySec * 1000; // 60 sec x 1000 ms
		Timer       timer       = new Timer(errShow, (ActionEvent e) -> dlg.dispose());
		timer.setRepeats(false);
		timer.start();
		dlg.setLocation((scsize.width - dlg.getWidth()) / 2, (scsize.height - dlg.getHeight()) / 2);
		dlg.setAlwaysOnTop(true);
		dlg.setModal(false);
		dlg.setVisible(true);
	}

	public static void runProgram(String[] command, Component parent) {
		Runtime   nativePro = Runtime.getRuntime();
		try
		{
			nativePro.exec(command);
		}
		catch(Exception e)
		{
			String errStr = "A command was to be run now, but the following error occured\n" + e.getMessage();
			showErrorMessage(errStr, "Program error", parent, 50);
		}
	}

	public static void runAlarm(TimeBean tmpb, Component parent, int numSec)
	{
		JOptionPane opt;
		Dimension scsize    = Toolkit.getDefaultToolkit().getScreenSize();
		String    almStr    = "";
		int       type      = tmpb.getAlarmExecutionOutputType();
		int       almShow   = numSec * 1000; // 60 sec x 1000 ms
		if ((type % 3 == 0) || (type == 2))  //for 2 beep;
		{
			Toolkit.getDefaultToolkit().beep();
			System.out.println("\007");
		}
		if (type % 2 != 0)  //for 1 to play sound;
		{
			try {
				SoundPlayer.playAudio(tmpb.getAlarmSound(), almShow);
			} catch (FileNotFoundException fe) {
				almStr = "The alarm sound file is not found:\n" + fe.getMessage();
				showErrorMessage(almStr, "Alarm Error...", parent, numSec);
			} catch (JavaLayerException je) {
				almStr = "Cannot play alarm:\n" + je.getMessage();
				showErrorMessage(almStr, "Alarm Error...", parent, numSec);
			}
		}
		if (type > 3) //for 4 to show Message;
		{
			try
			{
				GregorianCalendar ccal = new GregorianCalendar();
				ccal.add(Calendar.SECOND, 1);
				almStr = "<html><font size=+3><i>Alarm! \"" + tmpb.getName() + "\"</i></font><p>This alarm was scheduled to run now: <font color=blue>" + tmpb.getNextAlarmTriggerTime() + "</font>.</html>";
				opt    = new JOptionPane(almStr,JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION, new ImageIcon("duke.gif"));
				final JDialog dlg    = opt.createDialog(parent,"Alarm...");
				Timer timer = new Timer(almShow, (ActionEvent e) -> dlg.dispose());
				timer.setRepeats(false);
				timer.start();
				dlg.setLocation((scsize.width - dlg.getWidth()) / 2, (scsize.height - dlg.getHeight()) / 2);
				dlg.setAlwaysOnTop(true);
				dlg.setVisible(true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static Date getSystemStartTime()
	{
		long nanosToSeconds = Math.abs((long)Math.floor(System.nanoTime() / 1000000000.00d));
		int  days           = -1 * (int)nanosToSeconds / 86400;
		int  hours          = -1 * (int)(nanosToSeconds % 86400) / 3600;
		int  minutes        = -1 * (int)(nanosToSeconds % 86400 % 3600) / 60;
		int  seconds        = -1 * (int)(nanosToSeconds % 86400 % 3600) % 60;
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.add(Calendar.SECOND, seconds); // adding seconds is necessary even when we are removing it later
		gcal.add(Calendar.MINUTE, minutes);
		gcal.add(Calendar.HOUR_OF_DAY, hours);
		gcal.add(Calendar.DATE, days);
		gcal.set(Calendar.SECOND, 0);  // Second 0 for a match;
		return gcal.getTime();
	}

	public static String formatUptime(Duration duration, String pattern)
	{
		String []  arr          = pattern.split("\'");
		StringBuilder formattedUptime = new StringBuilder();
		boolean outOfQuote      = true;
		for (String val : arr)
		{
			if (outOfQuote)
			{
				if (val.contains("DD")) {
					formattedUptime.append(val.replace("DD", String.format("%02d", duration.toDays())));
					duration = duration.minusDays(duration.toDays());
				}
				else if (val.contains("HH")) {
					formattedUptime.append(val.replace("HH", String.format("%02d", duration.toHours())));
				}
				else if (val.contains("mm"))
				{
					formattedUptime.append(val.replace("mm", String.format("%02d", duration.toMinutes() % 60)));
				}
				else if (val.contains("ss"))
				{
					formattedUptime.append(val.replace("ss", String.format("%02d", (duration.getSeconds() % 60) % 60)));
				}
				else
				{
					formattedUptime.append(val);
				}
			}
			else
			{
				formattedUptime.append(val);
			}
			outOfQuote = !outOfQuote;
		}
		return formattedUptime.toString();
	}

	public static String formatPomodoroTime(Duration duration, String pattern, String label, boolean leadLabel) throws IllegalArgumentException
	{
		String        [] arr        = pattern.split("\'");
		StringBuilder formattedTime = new StringBuilder();
		boolean       outOfQuote    = true;
		Calendar      cal           = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, (int)duration.getSeconds());
		for (String val : arr)
		{
			if (outOfQuote)
			{
				if (val.toLowerCase().contains("hh")) {
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					formattedTime.append(sdf.format(cal.getTime()));
				}
				else
				{
					formattedTime.append(val.replace("mm", String.format("%02d", duration.toMinutes())));
					formattedTime.replace(0, formattedTime.length(), formattedTime.toString().replace("ss", String.format("%02d", (duration.getSeconds() % 60) % 60)));
				}
			}
			else
			{
				formattedTime.append(val);
			}
			outOfQuote = !outOfQuote;
		}

		if (leadLabel)
			formattedTime.insert(0, "[" + label + "] ");
		else
			formattedTime.append(" [" + label + "]");
		return formattedTime.toString();
	}

	public static File getJarExtractedDirectory(File sourceJar)
	{
		File destLocation = new File(System.getProperty("java.io.tmpdir") + "/desktime");
		Path destDir = destLocation.toPath();
		try (JarFile jarFile = new JarFile(sourceJar)) {
			Files.createDirectories(destDir);
			java.util.List<? extends JarEntry> entries = jarFile.stream()
														.sorted(Comparator.comparing(JarEntry::getName))
														.collect(Collectors.toList());
			for (JarEntry entry : entries) {
				Path outputFile = destDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					// Create directories if it's a directory
					if (!Files.exists(outputFile)) Files.createDirectory(outputFile);
					continue;
				}

				if (!Files.exists(outputFile))
					Files.copy(jarFile.getInputStream(entry), outputFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			destLocation = new File(System.getProperty("user.home"));
		}
		// System.out.println(destLocation.getAbsolutePath());
		return destLocation;
	}

	public static String toCamelCase(String input) {
		return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
	}

	public String getMetalLookAndFeelName()
	{
		return "javax.swing.plaf.metal.MetalLookAndFeel";
	}

	public String getMotifLookAndFeelName()
	{
		return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	}

	public String getWindowsLookAndFeelName()
	{
		return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	}

	public String getGTKLookAndFeelName()
	{
		return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	}

	public static File getJarDir() {
		if (jarDir != null && jarDir.exists()) {
			return jarDir;
		}
		File jarFile;
		try {
			jarFile = new File(DeskStop.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException ue) {
			jarFile = new File(System.getProperty("user.home"));
		}
		if (jarFile.isDirectory() && jarFile.getName().equals("classes")) {
			jarFile = jarFile.getParentFile();
		}

		return jarFile.getParentFile();
	}

	public static java.util.List<InitInfo> loadDeskStops()
	{
		ArrayList<InitInfo> data = new ArrayList<>();
		try
		{
			XMLDecoder decoder;
			File parentDir        = getJarDir();
			File settingsFile     = new File(parentDir, SETTINGS_FILE);
			File internalSettings = new File(parentDir, INTERNAL_SETTINGS_FILE);
			if (!settingsFile.exists() && internalSettings.exists()) {
				decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(internalSettings)));
			} else {
				decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(settingsFile)));
			}

			Object content = decoder.readObject();
			decoder.close();
			if (content instanceof ArrayList) {
				ArrayList<?> tmpArrayList = (ArrayList<?>)content;
				for (int cnt = 0; cnt < tmpArrayList.size(); cnt++) {
					if (tmpArrayList.get(cnt) instanceof InitInfo) data.add((InitInfo)tmpArrayList.get(cnt));
				}
			}
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("File missing-\"" + SETTINGS_FILE + "\": " + exclusive.toString());
			exclusive.printStackTrace();
		}
		return data;
	}
	
	public static void saveDeskStops(InitInfo currInitInfo, java.util.List<InitInfo> currDeskStops)
	{
		int id = currInitInfo.getID();
		int cnt = 0;
		do {
			int thisID = 0;
			if (currDeskStops.isEmpty()) {
				currDeskStops.add(currInitInfo);
			} else {
				thisID = (currDeskStops.get(cnt)).getID();
				if (thisID == id) currDeskStops.set(cnt, currInitInfo);
			}
			cnt++;
		} while (cnt < currDeskStops.size());

		try
		{
			XMLEncoder xencode;
			File parentDir        = getJarDir();
			File settingsFile     = new File(parentDir, SETTINGS_FILE);
			File internalSettings = new File(parentDir, INTERNAL_SETTINGS_FILE);
			if (!settingsFile.exists() && internalSettings.exists()) {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(internalSettings)));
			} else {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(settingsFile)));
			}
			xencode.writeObject(currDeskStops);
			xencode.close();
		}
		catch (Exception fne)
		{
			System.out.println("Exception while saving properties file-\"" + SETTINGS_FILE + "\": " + fne.toString());
			fne.printStackTrace();
		}
	}

	public static void saveDeskStops(java.util.List<InitInfo> currDeskStops)
	{
		try
		{
			XMLEncoder xencode;
			File parentDir        = getJarDir();
			File settingsFile     = new File(parentDir, SETTINGS_FILE);
			File internalSettings = new File(parentDir, INTERNAL_SETTINGS_FILE);
			if (!settingsFile.exists() && internalSettings.exists()) {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(internalSettings)));
			} else {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(settingsFile)));
			}
			xencode.writeObject(currDeskStops);
			xencode.close();
		}
		catch (Exception fne)
		{
			System.out.println("Exception while saving properties file-\"" + SETTINGS_FILE + "\": " + fne.toString());
			fne.printStackTrace();
		}
	}

	public static Vector<TimeBean> loadAlarms()
	{
		Vector<TimeBean> data = new Vector<>();
		try
		{
			XMLDecoder decoder;
			File parentDir      = getJarDir();
			File alarmsFile     = new File(parentDir, ALARMS_FILE);
			File internalAlarms = new File(parentDir, INTERNAL_ALARMS_FILE);
			if (!alarmsFile.exists() && internalAlarms.exists()) {
				decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(internalAlarms)));
			} else {
				decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(alarmsFile)));
			}
			Object settingsObj = decoder.readObject();
			decoder.close();
			if (settingsObj instanceof Vector) {
				Vector<?> tmpVec = (Vector<?>)settingsObj;
				for (int cnt = 0; cnt < tmpVec.size(); cnt++) {
					if (tmpVec.elementAt(cnt) instanceof TimeBean) data.add((TimeBean)tmpVec.elementAt(cnt));
				}
			}
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("Exception while loading properties file-\"" + ALARMS_FILE + "\": " + exclusive.getMessage());
			exclusive.printStackTrace();
		}
		return data;
	}

	public static void saveAlarms(Vector <TimeBean>data)
	{
		try
		{
			XMLEncoder xencode;
			File parentDir      = getJarDir();
			File alarmsFile     = new File(parentDir, ALARMS_FILE);
			File internalAlarms = new File(parentDir, INTERNAL_ALARMS_FILE);
			if (!alarmsFile.exists() && internalAlarms.exists()) {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(internalAlarms)));
			} else {
				xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(alarmsFile)));
			}
			xencode.writeObject(data);
			xencode.close();
		}
		catch (Exception fne)
		{
			System.out.println("Exception while saving alarms file \"" + ALARMS_FILE + "\": " + fne.toString());
			fne.printStackTrace();
		}
	}
}