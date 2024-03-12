package com.sounaks.desktime;

import javax.swing.*;
import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
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

	public static void	addComponent(Container container, Component	component, int i, int j, int k,	int	l, double d, double	d1, ActionListener alistener) throws AWTException
	{
		java.awt.LayoutManager layoutmanager = container.getLayout();
		if (!(layoutmanager instanceof GridBagLayout))
			throw new AWTException("Invalid	Layout " + layoutmanager);
		GridBagConstraints gridbagconstraints            = new	GridBagConstraints();
		                   gridbagconstraints.gridx      = i;
		                   gridbagconstraints.gridy      = j;
		                   gridbagconstraints.gridwidth  = k;
		                   gridbagconstraints.gridheight = l;
		                   gridbagconstraints.weightx    = d;
		                   gridbagconstraints.weighty    = d1;
		                   gridbagconstraints.fill       = GridBagConstraints.BOTH;
		                   gridbagconstraints.anchor     = GridBagConstraints.WEST;
		                   gridbagconstraints.insets     = new	Insets(5, 5, 5,	0);
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
		if ((double)dimension.width - point.getX()  < (double)jpopupmenu.getWidth())
			flag  = true;
		if ((double)dimension.height - point.getY() < (double)jpopupmenu.getHeight())
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
	
	public static Image getDesktopImage(Image image, ImageObserver observer, int deskLayout)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension newWH;
		int inuse;
		int           imgWidth     = image.getWidth(observer);
		int           imgHeight    = image.getHeight(observer);
		double        aspectWidth  = (double)Math.max(screenSize.width, imgWidth) / (double)Math.min(screenSize.width, imgWidth);
		double        aspectHeight = (double)Math.max(screenSize.height, imgHeight) / (double)Math.min(screenSize.height, imgHeight);
		Color         desktopColor = SystemColor.desktop;
		BufferedImage bim          = new BufferedImage(screenSize.width,screenSize.height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D    g2d4im       = bim.createGraphics();
		g2d4im.setColor(desktopColor);
		g2d4im.fill(new Rectangle2D.Double(0,0,screenSize.width, screenSize.height));
		switch(deskLayout)
		{
			case CENTER:
				if (imgWidth      > screenSize.width && imgHeight > screenSize.height)
					g2d4im.drawImage(image, 0, 0, 0 + screenSize.width, 0 + screenSize.height, (imgWidth - screenSize.width) / 2, (imgHeight - screenSize.width) / 2, (imgWidth - screenSize.width) / 2 + screenSize.width, (imgHeight - screenSize.height) / 2 + screenSize.height, observer);
				else if (imgWidth > screenSize.width && imgHeight <= screenSize.height)
					g2d4im.drawImage(image, 0, (screenSize.height - imgHeight) / 2, 0 + screenSize.width, (screenSize.height - imgHeight) / 2 + imgHeight, (imgWidth - screenSize.width) / 2, 0, (imgWidth - screenSize.width) / 2 + screenSize.width, imgHeight, observer);
				else if (imgWidth <= screenSize.width && imgHeight > screenSize.height)
					g2d4im.drawImage(image, (screenSize.width - imgWidth) / 2, 0, (screenSize.width - imgWidth) / 2 + imgWidth, 0 + screenSize.height, 0, (imgHeight - screenSize.height) / 2, imgWidth, (imgHeight - screenSize.height) / 2 + screenSize.height, observer);
				else if (imgWidth <= screenSize.width && imgHeight <= screenSize.height)
					g2d4im.drawImage(image, (screenSize.width - imgWidth) / 2, (screenSize.height - imgHeight) / 2, imgWidth, imgHeight, observer);
				return bim;
			case TILE:
				int k = (int)Math.ceil((double)screenSize.width / (double)imgWidth);
				int l = (int)Math.ceil((double)screenSize.height / (double)imgHeight);
				for (int i1 = 0; i1 < l; i1++)
				{
					for (int j1 = 0; j1 < k; j1++)
						g2d4im.drawImage(image, j1 * imgWidth, i1 * imgHeight, imgWidth, imgHeight, observer);
				}
				return bim;
			case STRETCH:
				g2d4im.drawImage(image, 0, 0, screenSize.width, screenSize.height, observer);
				return bim;
			case V_TILE:
				if (screenSize.width > imgWidth)
					newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectWidth), (int)Math.ceil((double)imgHeight * aspectWidth));
				else
					newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectWidth), (int)Math.ceil((double)imgHeight / aspectWidth));
				if (newWH.height >= screenSize.height)
				{
					g2d4im.drawImage(image, 0, 0, newWH.width, newWH.height, observer);
				}
				else
				{
					inuse = (int)Math.ceil((double)screenSize.height / (double)newWH.height);
					for (int j = 0; j <= inuse; j++)
						g2d4im.drawImage(image, 0, j * newWH.height, newWH.width, newWH.height, observer);
				}
				return bim;
			case H_TILE:
				if (screenSize.height > imgHeight)
					newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectHeight), (int)Math.ceil((double)imgHeight * aspectHeight));
				else
					newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectHeight), (int)Math.ceil((double)imgHeight / aspectHeight));
				if (newWH.width >= screenSize.width)
				{
					g2d4im.drawImage(image, 0, 0, newWH.width, newWH.height, observer);
				}
				else
				{
					inuse = (int)Math.ceil((double)screenSize.width / (double)newWH.width);
					for (int i = 0; i <= inuse; i++)
						g2d4im.drawImage(image, i * newWH.width, 0, newWH.width, newWH.height, observer);
				}
				return bim;
			case FIT:
				if (imgWidth > screenSize.width && imgHeight > screenSize.height)
					g2d4im.drawImage(image, 0, 0, screenSize.width, screenSize.height, observer);
				else if (imgWidth > screenSize.width && imgHeight <= screenSize.height)
					g2d4im.drawImage(image, 0, (screenSize.height - imgHeight) / 2, screenSize.width, imgHeight, observer);
				else if (imgWidth <= screenSize.width && imgHeight > screenSize.height)
					g2d4im.drawImage(image, (screenSize.width - imgWidth) / 2, 0, imgWidth, screenSize.height, observer);
				else if (imgWidth < screenSize.width && imgHeight < screenSize.height)
					g2d4im.drawImage(image, (screenSize.width - imgWidth) / 2, (screenSize.height - imgHeight) / 2, imgWidth, imgHeight, observer);
				return bim;
			default:
				return bim;
		}
	}
	
	public static Image getScreenCapture(Rectangle rect)
	{
		BufferedImage bim = new BufferedImage((int)Math.ceil(rect.getWidth()),(int)Math.ceil(rect.getHeight()),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d4im = bim.createGraphics();
		g2d4im.setColor(SystemColor.desktop);
		g2d4im.fill(rect);
		try
		{
			Robot robot = new Robot();
			bim = robot.createScreenCapture(rect);
		}
		catch(AWTException awe)
		{
		}
		return bim;
	}
	
	public static boolean checkAWTPermission(String permission)
	{
		AWTPermission   awtp    = new AWTPermission(permission);
		Object          context = null;
		SecurityManager sm      = System.getSecurityManager();
		if (sm != null)
		{
			try
			{
				context = sm.getSecurityContext(); 
				sm.checkPermission(awtp, context);
				return true;
			}
			catch(SecurityException se)
			{
				return false;
			}
		}
		return true;
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
		Timer       timer       = new Timer(errShow, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		});
		timer.setRepeats(false);
		timer.start();
		dlg.setLocation((scsize.width - dlg.getWidth()) / 2, (scsize.height - dlg.getHeight()) / 2);
		dlg.setAlwaysOnTop(true);
		dlg.setModal(false);
		dlg.setVisible(true);
	}

	public static void runProgram(String command, Component parent) {
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
		nativePro.gc();
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
				Timer timer = new Timer(almShow, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dlg.dispose();
					}
				});
				timer.setRepeats(false);
				timer.start();
				dlg.setLocation((scsize.width - dlg.getWidth()) / 2, (scsize.height - dlg.getHeight()) / 2);
				dlg.setAlwaysOnTop(true);
				dlg.setVisible(true);
			}
			catch(Exception e)
			{}
		}
		almStr = null;
		opt    = null;
	}
	
	public static Date getSystemStartTime()
	{
		long nanosToSeconds = Math.abs((long)Math.floor(System.nanoTime() / 1000000000));
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
		String  arr[]           = pattern.split("\'");
		String  upTimeFormatted = "";
		boolean outOfQuote      = true;
		for (String val : arr)
		{
			if (outOfQuote)
			{
				if (val.contains("DD")) {
					upTimeFormatted += val.replace("DD", String.format("%02d", duration.toDays()));
					duration = duration.minusDays(duration.toDays());
				}
				else if (val.contains("HH")) {
					upTimeFormatted += val.replace("HH", String.format("%02d", duration.toHours()));
				}
				else if (val.contains("mm"))
				{
					upTimeFormatted += val.replace("mm", String.format("%02d", duration.toMinutes() % 60));
				}
				else if (val.contains("ss"))
				{
					upTimeFormatted += val.replace("ss", String.format("%02d", (duration.getSeconds() % 60) % 60));
				}
				else
				{
					upTimeFormatted += val;
				}
			}
			else
			{
				upTimeFormatted += val;
			}
			outOfQuote = !outOfQuote;
		}
		return upTimeFormatted;
	}

	public static String formatPomodoroTime(Duration duration, String pattern, String label, boolean leadLabel) throws IllegalArgumentException
	{
		String arr[]         = pattern.split("\'");
		String formattedTime = "";
		boolean outOfQuote   = true;
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, (int)duration.getSeconds());
		for (String val : arr)
		{
			if (outOfQuote)
			{
				if (val.toLowerCase().contains("hh")) {
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					formattedTime += sdf.format(cal.getTime());
				}
				else
				{
					formattedTime += val.replace("mm", String.format("%02d", duration.toMinutes()));
					formattedTime = formattedTime.replace("ss", String.format("%02d", (duration.getSeconds() % 60) % 60));
				}
			}
			else
			{
				formattedTime += val;
			}
			outOfQuote = !outOfQuote;
		}

		if (leadLabel)
			formattedTime = "[" + label + "] " + formattedTime;
		else
			formattedTime = formattedTime + " [" + label + "]";
		return formattedTime;
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
}