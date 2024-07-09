package com.sounaks.desktime;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

public class InitInfo extends Hashtable<String, Object>
{
	File defaultsDir;
	boolean pixelAlphaSupport, windowAlphaSupport, screenshotSupport;

	public InitInfo()
	{
		super(33,0.6f);
		try {
			CodeSource codeSrc = InitInfo.class.getProtectionDomain().getCodeSource();
			File sourceJar = new File(codeSrc.getLocation().toURI());
			if (sourceJar.getAbsolutePath().toLowerCase().endsWith(".jar")) {
				defaultsDir = ExUtils.getJarExtractedDirectory(sourceJar);
			} else {
				defaultsDir = new File(sourceJar.getAbsolutePath());
			}
		} catch (URISyntaxException ue) {
			defaultsDir = new File(".");
		}
		put("FONT", new Font("Courier New", Font.BOLD, 16));
		put("BACKGROUND", Color.white);
		put("OPACITY", 1.0F);
		put("FOREGROUND", Color.black);
		put("LINE_COLOR", Color.black);
		put("BORDER", BorderFactory.createEmptyBorder());
		put("LOCATION", new Point(10,10));
		put("DISPLAY_METHOD", "CURTZ");
		put("UPTIME_FORMAT", "'Up-Time: 'HH'-hour(s), 'mm'-minute(s), 'ss'-second(s)'");
		put("POMODORO_FORMAT", "mm:ss");
		put("POMODORO_LEAD_LABEL", false);
		put("POMODORO_COUNTDOWN_MODE", true);
		put("ZTIME_FORMAT", "zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
		put("POMODORO_TASK", "25 min. work, 5 min. break, 30 min. rest");
		put("TIMEZONE", "GMT");
		put("TOOLTIP", true);
		put("NATIVE_LOOK", false);
		put("FIXED", false);
		put("USING_IMAGE", true);
		put("GLASS_EFFECT", false);
		put("IMAGEFILE", defaultsDir.getAbsolutePath() + "/images/" + "BabyBlue.JPG");
		put("IMAGE_STYLE", ExUtils.STRETCH);
		put("ON_TOP", true);
		put("ROUND_CORNERS", true);
		put("SLOW_TRANS", false);
		// put("ALARM_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "Alarm-chosic_com.mp3");
		put("HOUR_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "beep-beep-6151.mp3");
		put("UPTIME_HOUR_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "ambient-flute-notification-3-185275.mp3");
		put("POMO_WORK_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "beep-warning-6387.mp3");
		put("POMO_BREAK_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "bright-phone-ringing-3-152490.mp3");
		put("POMO_REST_SOUND", defaultsDir.getAbsolutePath() + "/sounds/" + "chiptune-alarm-clock-112869.mp3");
		put("PANEL_ID", 0);
	}

	public int getID()
	{
		return (int)get("PANEL_ID");
	}

	public void setID(int id)
	{
		put("PANEL_ID", id);
	}

	public boolean isPixelAlphaSupported()
	{
		return pixelAlphaSupport;
	}

	public void setPixelAlphaSupport(boolean pixelAlphaSupport)
	{
		this.pixelAlphaSupport = pixelAlphaSupport;
	}

	public boolean isWindowAlphaSupported()
	{
		return windowAlphaSupport;
	}

	public void setWindowAlphaSupport(boolean windowAlphaSupport)
	{
		this.windowAlphaSupport = windowAlphaSupport;
	}

	public boolean isScreenshotSupported()
	{
		return screenshotSupport;
	}

	public void setScreenshotSupport(boolean screenshotSupport)
	{
		this.screenshotSupport = screenshotSupport;
	}

	public String getDefaultsDir()
	{
		return defaultsDir.getAbsolutePath();
	}

	public Font getFont()
	{
		return (Font)get("FONT");
	}

	public void setFont(Font font)
	{
		if(font != null)
		{
			put("FONT", font);
		}
	}

	public Color getBackground()
	{
		return (Color)get("BACKGROUND");
	}

	public void setBackground(Color color)
	{
		if(color != null)
			put("BACKGROUND", color);
	}

	public float getOpacity()
	{
		return (float)get("OPACITY");
	}

	public void setOpacity(float opacity)
	{
		put("OPACITY", opacity);
	}

	public Color getForeground()
	{
		return (Color)get("FOREGROUND");
	}

	public void setForeground(Color color)
	{
		if(color != null)
			put("FOREGROUND", color);
	}

	public Color getLineColor()
	{
		return (Color)get("LINE_COLOR");
	}

	public void setLineColor(Color color)
	{
		if(color != null)
			put("LINE_COLOR", color);
	}

	public Border getBorder()
	{
		return (Border)get("BORDER");
	}

	public void setBorder(Border border)
	{
		if(border == null) put("BORDER", BorderFactory.createEmptyBorder());
		else put("BORDER", border);
	}

	public Point getLocation()
	{
		return (Point)get("LOCATION");
	}

	public void setLocation(Point pp)
	{
		put("LOCATION", pp);
	}

	public String getDisplayMethod()
	{
		return (String)get("DISPLAY_METHOD");
	}

	public void setDisplayMethod(String s)
	{
		put("DISPLAY_METHOD",s);
	}

	public String getUpTimeFormat()
	{
		return (String)get("UPTIME_FORMAT");
	}

	public void setUpTimeFormat(String s)
	{
		if(s != null)
			put("UPTIME_FORMAT", s);
	}

	public boolean isDayShowing()
	{
		String formatString = getUpTimeFormat();
		return formatString.contains("\'DD\'");
	}

	public String getPomodoroFormat()
	{
		return (String)get("POMODORO_FORMAT");
	}

	public void setPomodoroFormat(String s)
	{
		if(s != null)
			put("POMODORO_FORMAT", s);
	}

	public String getPomodoroTask()
	{
		return (String)get("POMODORO_TASK");
	}

	public void setPomodoroTask(String s)
	{
		if(s != null)
			put("POMODORO_TASK", s);
	}

	public boolean isPomodoroLeadingLabel()
	{
		return ((Boolean)get("POMODORO_LEAD_LABEL")).booleanValue();
	}

	public void setPomodoroLeadingLabel(boolean flag)
	{
		put("POMODORO_LEAD_LABEL", flag);
	}

	public boolean isPomodoroCountdown()
	{
		return ((Boolean)get("POMODORO_COUNTDOWN_MODE")).booleanValue();
	}

	public void setPomodoroCountdown(boolean flag)
	{
		put("POMODORO_COUNTDOWN_MODE", flag);
	}

	public String getZonedTimeFormat()
	{
		return (String)get("ZTIME_FORMAT");
	}

	public void setZonedTimeFormat(String s)
	{
		if(s != null)
			put("ZTIME_FORMAT", s);
	}

	public String getTimeZone()
	{
		return (String)get("TIMEZONE");
	}

	public void setTimeZone(String s)
	{
		if(s != null)
			put("TIMEZONE", s);
	}

	public boolean hasTooltip()
	{
		return ((Boolean)get("TOOLTIP")).booleanValue();
	}

	public void setTooltip(boolean flag)
	{
		put("TOOLTIP", flag);
	}
	
	public boolean getOnTop()
	{
		return ((Boolean)get("ON_TOP")).booleanValue();
	}
	
	public void setOnTop(boolean flag)
	{
		put("ON_TOP", flag);
	}

	public boolean hasNativeLook()
	{
		return ((Boolean)get("NATIVE_LOOK")).booleanValue();
	}

	public void setNativeLook(boolean flag)
	{
		put("NATIVE_LOOK", flag);
	}

	public boolean isFixed()
	{
		return ((Boolean)get("FIXED")).booleanValue();
	}

	public void setFixed(boolean flag)
	{
		put("FIXED", flag);
	}

	public boolean isUsingImage()
	{
		return ((Boolean)get("USING_IMAGE")).booleanValue();
	}

	public void setUsingImage(boolean flag)
	{
		put("USING_IMAGE", flag);
	}

	public File getImageFile()
	{
		File ff = new File((String)get("IMAGEFILE"));
		if(ff.exists() &&  ff.isFile())
		{
			return ff;
		}
		else
			return new File(".");
	}
	
	public void setGlassEffect(boolean flag)
	{
		put("GLASS_EFFECT", flag);
	}
	
	public boolean hasGlassEffect()
	{
		return ((Boolean)get("GLASS_EFFECT")).booleanValue();
	}

	public void setImageFile(String imageFile)
	{
		put("IMAGEFILE", imageFile);
	}

	public int getImageStyle()
	{
		return ((Integer)get("IMAGE_STYLE")).intValue();
	}

	public void setImageStyle(int i)
	{
		put("IMAGE_STYLE", i);
	}

	public boolean hasRoundedCorners()
	{
		return ((Boolean)get("ROUND_CORNERS")).booleanValue();
	}

	public void setRoundCorners(boolean roundCorners)
	{
		put("ROUND_CORNERS", roundCorners);
	}

	public boolean isSlowTransUpdating()
	{
		return ((Boolean)get("SLOW_TRANS")).booleanValue();
	}

	public void setSlowTransUpdating(boolean slowTrans)
	{
		put("SLOW_TRANS", slowTrans);
	}

	// public String getAlarmSound()
	// {
	// 	return (String)get("ALARM_SOUND");
	// }

	// public void setAlarmSound(String alarmSound)
	// {
	// 	put("ALARM_SOUND", alarmSound);
	// }

	public String getHourSound()
	{
		return (String)get("HOUR_SOUND");
	}

	public void setHourSound(String hourSound)
	{
		put("HOUR_SOUND", hourSound);
	}

	public String getUptimeHourSound()
	{
		return (String)get("UPTIME_HOUR_SOUND");
	}

	public void setUptimeHourSound(String uptimeHrSound)
	{
		put("UPTIME_HOUR_SOUND", uptimeHrSound);
	}

	public String getPomodoroWorkSound()
	{
		return (String)get("POMO_WORK_SOUND");
	}

	public void setPomodoroWorkSound(String pomoWorkSound)
	{
		put("POMO_WORK_SOUND", pomoWorkSound);
	}

	public String getPomodoroBreakSound()
	{
		return (String)get("POMO_BREAK_SOUND");
	}

	public void setPomodoroBreakSound(String pomoBreakSound)
	{
		put("POMO_BREAK_SOUND", pomoBreakSound);
	}

	public String getPomodoroRestSound()
	{
		return (String)get("POMO_REST_SOUND");
	}

	public void setPomodoroRestSound(String pomoRestSound)
	{
		put("POMO_REST_SOUND", pomoRestSound);
	}
}