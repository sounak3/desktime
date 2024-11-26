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
	transient File defaultsDir;
	transient boolean pixelAlphaSupport, windowAlphaSupport, screenshotSupport;

	private static final String KEY_FONT                    = "FONT";
	private static final String KEY_BACKGROUND              = "BACKGROUND";
	private static final String KEY_OPACITY                 = "OPACITY";
	private static final String KEY_FORE_TRANSLUCENT        = "FORE_TRANSLUCENT";
	private static final String KEY_FOREGROUND              = "FOREGROUND";
	private static final String KEY_LINE_COLOR              = "LINE_COLOR";
	private static final String KEY_BORDER                  = "BORDER";
	private static final String KEY_LABEL_BORDER            = "LABEL_BORDER";
	private static final String KEY_LOCATION                = "LOCATION";
	private static final String KEY_DISPLAY_METHOD          = "DISPLAY_METHOD";
	private static final String KEY_UPTIME_FORMAT           = "UPTIME_FORMAT";
	private static final String KEY_POMODORO_FORMAT         = "POMODORO_FORMAT";
	private static final String KEY_POMODORO_LEAD_LABEL     = "POMODORO_LEAD_LABEL";
	private static final String KEY_POMODORO_COUNTDOWN_MODE = "POMODORO_COUNTDOWN_MODE";
	private static final String KEY_ZTIME_FORMAT            = "ZTIME_FORMAT";
	private static final String KEY_POMODORO_TASK           = "POMODORO_TASK";
	private static final String KEY_TIMEZONE                = "TIMEZONE";
	private static final String KEY_ANALOG_CLOCK            = "ANALOG_CLOCK";
	private static final String KEY_ANALOG_CLOCK_OPTS       = "ANALOG_CLOCK_OPTS";
	private static final String KEY_TOOLTIP                 = "TOOLTIP";
	private static final String KEY_NATIVE_LOOK             = "NATIVE_LOOK";
	private static final String KEY_FIXED                   = "FIXED";
	private static final String KEY_USING_IMAGE             = "USING_IMAGE";
	private static final String KEY_GLASS_EFFECT            = "GLASS_EFFECT";
	private static final String KEY_IMAGEFILE               = "IMAGEFILE";
	private static final String KEY_IMAGE_STYLE             = "IMAGE_STYLE";
	private static final String KEY_ON_TOP                  = "ON_TOP";
	private static final String KEY_ROUND_CORNERS           = "ROUND_CORNERS";
	private static final String KEY_SLOW_TRANS              = "SLOW_TRANS";
	private static final String KEY_ALARM_SOUND             = "ALARM_SOUND";
	private static final String KEY_HOUR_SOUND              = "HOUR_SOUND";
	private static final String KEY_UPTIME_HOUR_SOUND       = "UPTIME_HOUR_SOUND";
	private static final String KEY_POMO_WORK_SOUND         = "POMO_WORK_SOUND";
	private static final String KEY_POMO_BREAK_SOUND        = "POMO_BREAK_SOUND";
	private static final String KEY_POMO_REST_SOUND         = "POMO_REST_SOUND";
	private static final String KEY_PANEL_ID                = "PANEL_ID";
	private static final String KEY_DIAL_OBJECTS_SIZE       = "HAND_SIZE";
	private static final String KEY_TRAY_ICON_TYPE          = "TRAY_ICON_TYPE";

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
		put(KEY_FONT, new Font("Courier New", Font.BOLD, 16));
		put(KEY_BACKGROUND, Color.black);
		put(KEY_OPACITY, 1.0F);
		put(KEY_FORE_TRANSLUCENT, true);
		put(KEY_FOREGROUND, Color.black);
		put(KEY_LINE_COLOR, Color.black);
		put(KEY_BORDER, BorderFactory.createEmptyBorder());
		put(KEY_LABEL_BORDER, true);
		put(KEY_LOCATION, new Point(10,10));
		put(KEY_DISPLAY_METHOD, DeskStop.DISPLAY_MODE_CURRENT_TIMEZONE);
		put(KEY_UPTIME_FORMAT, "'Up-Time: 'HH'-hour(s), 'mm'-minute(s), 'ss'-second(s)'");
		put(KEY_POMODORO_FORMAT, "mm:ss");
		put(KEY_POMODORO_LEAD_LABEL, false);
		put(KEY_POMODORO_COUNTDOWN_MODE, true);
		put(KEY_ZTIME_FORMAT, "zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
		put(KEY_POMODORO_TASK, "25 min. work, 5 min. break, 30 min. rest");
		put(KEY_TIMEZONE, "GMT");
		put(KEY_ANALOG_CLOCK, false);
		put(KEY_ANALOG_CLOCK_OPTS, TLabel.SHOW_NONE);
		put(KEY_TOOLTIP, true);
		put(KEY_NATIVE_LOOK, false);
		put(KEY_FIXED, false);
		put(KEY_USING_IMAGE, true);
		put(KEY_GLASS_EFFECT, false);
		put(KEY_IMAGEFILE, defaultsDir.getAbsolutePath() + "/images/" + "BabyBlue.JPG");
		put(KEY_IMAGE_STYLE, ExUtils.STRETCH);
		put(KEY_ON_TOP, true);
		put(KEY_ROUND_CORNERS, 0);
		put(KEY_SLOW_TRANS, false);
		put(KEY_ALARM_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "Alarm-chosic_com.mp3");
		put(KEY_HOUR_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "beep-beep-6151.mp3");
		put(KEY_UPTIME_HOUR_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "ambient-flute-notification-3-185275.mp3");
		put(KEY_POMO_WORK_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "beep-warning-6387.mp3");
		put(KEY_POMO_BREAK_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "bright-phone-ringing-3-152490.mp3");
		put(KEY_POMO_REST_SOUND, defaultsDir.getAbsolutePath() + "/sounds/" + "chiptune-alarm-clock-112869.mp3");
		put(KEY_PANEL_ID, 0);
		put(KEY_DIAL_OBJECTS_SIZE, 0.5f);
		put(KEY_TRAY_ICON_TYPE, ChooserBox.NO_APP_ICON);
	}

	@Override
	public synchronized boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof InitInfo)) return false;
		InitInfo other = (InitInfo) o;
		if (this.pixelAlphaSupport != other.pixelAlphaSupport) return false;
		if (this.windowAlphaSupport != other.windowAlphaSupport) return false;
		if (this.screenshotSupport != other.screenshotSupport) return false;
		if (!(this.defaultsDir.equals(other.defaultsDir))) return false;
		Enumeration<String> allKeys = this.keys();
		while (allKeys.hasMoreElements()) {
			String currKey = allKeys.nextElement();
			if (!this.get(currKey).equals(other.get(currKey))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized int hashCode() {
		return Objects.hash(this);
	}

	public int getTrayIconType()
	{
		return ((Integer)get(KEY_TRAY_ICON_TYPE)).intValue();
	}

	public void setTrayIconType(int trayOption)
	{
		put(KEY_TRAY_ICON_TYPE, trayOption);
	}

	public float getDialObjectsSize()
	{
		return (float)get(KEY_DIAL_OBJECTS_SIZE);
	}

	public void setDialObjectsSize(float dialObjectsSize)
	{
		put(KEY_DIAL_OBJECTS_SIZE, dialObjectsSize);
	}

	public boolean isAnalogClock()
	{
		return ((Boolean)get(KEY_ANALOG_CLOCK)).booleanValue();
	}

	public void setAnalogClock(boolean analog)
	{
		put(KEY_ANALOG_CLOCK, analog);
	}

	public int getAnalogClockOption()
	{
		return ((Integer)get(KEY_ANALOG_CLOCK_OPTS)).intValue();
	}

	public void setAnalogClockOption(int analogOption)
	{
		put(KEY_ANALOG_CLOCK_OPTS, analogOption);
	}

	public int getID()
	{
		return (int)get(KEY_PANEL_ID);
	}

	public void setID(int id)
	{
		put(KEY_PANEL_ID, id);
	}

	public boolean isPixelAlphaSupported()
	{
		return pixelAlphaSupport;
	}

	public void setPixelAlphaSupport(boolean pixelAlphaSupport)
	{
		this.pixelAlphaSupport = pixelAlphaSupport;
	}

	public boolean isForegroundTranslucent()
	{
		return ((Boolean)get(KEY_FORE_TRANSLUCENT)).booleanValue();
	}

	public void setForegroundTranslucent(boolean translucent)
	{
		put(KEY_FORE_TRANSLUCENT, translucent);
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
		return (Font)get(KEY_FONT);
	}

	public void setFont(Font font)
	{
		if(font != null)
		{
			put(KEY_FONT, font);
		}
	}

	public Color getBackground()
	{
		return (Color)get(KEY_BACKGROUND);
	}

	public void setBackground(Color color)
	{
		if(color != null)
			put(KEY_BACKGROUND, color);
	}

	public float getOpacity()
	{
		return (float)get(KEY_OPACITY);
	}

	public void setOpacity(float opacity)
	{
		put(KEY_OPACITY, opacity);
	}

	public Color getForeground()
	{
		return (Color)get(KEY_FOREGROUND);
	}

	public void setForeground(Color color)
	{
		if(color != null)
			put(KEY_FOREGROUND, color);
	}

	public Color getLineColor()
	{
		return (Color)get(KEY_LINE_COLOR);
	}

	public void setLineColor(Color color)
	{
		if(color != null)
			put(KEY_LINE_COLOR, color);
	}

	public Border getBorder()
	{
		return (Border)get(KEY_BORDER);
	}

	public void setBorder(Border border)
	{
		if(border == null) put(KEY_BORDER, BorderFactory.createEmptyBorder());
		else put(KEY_BORDER, border);
	}

	public boolean isAnalogClockLabelBorderShowing()
	{
		return ((Boolean)get(KEY_LABEL_BORDER)).booleanValue();
	}

	public void setAnalogClockLabelBorder(boolean flag)
	{
		put(KEY_LABEL_BORDER, flag);
	}

	public Point getLocation()
	{
		return (Point)get(KEY_LOCATION);
	}

	public void setLocation(Point pp)
	{
		put(KEY_LOCATION, pp);
	}

	public String getDisplayMethod()
	{
		return (String)get(KEY_DISPLAY_METHOD);
	}

	public void setDisplayMethod(String s)
	{
		put(KEY_DISPLAY_METHOD,s);
	}

	public String getUpTimeFormat()
	{
		return (String)get(KEY_UPTIME_FORMAT);
	}

	public void setUpTimeFormat(String s)
	{
		if(s != null)
			put(KEY_UPTIME_FORMAT, s);
	}

	public boolean isDayShowing()
	{
		String formatString = getUpTimeFormat();
		return formatString.contains("\'DD\'");
	}

	public String getPomodoroFormat()
	{
		return (String)get(KEY_POMODORO_FORMAT);
	}

	public void setPomodoroFormat(String s)
	{
		if(s != null)
			put(KEY_POMODORO_FORMAT, s);
	}

	public String getPomodoroTask()
	{
		return (String)get(KEY_POMODORO_TASK);
	}

	public void setPomodoroTask(String s)
	{
		if(s != null)
			put(KEY_POMODORO_TASK, s);
	}

	public boolean isPomodoroLeadingLabel()
	{
		return ((Boolean)get(KEY_POMODORO_LEAD_LABEL)).booleanValue();
	}

	public void setPomodoroLeadingLabel(boolean flag)
	{
		put(KEY_POMODORO_LEAD_LABEL, flag);
	}

	public boolean isPomodoroCountdown()
	{
		return ((Boolean)get(KEY_POMODORO_COUNTDOWN_MODE)).booleanValue();
	}

	public void setPomodoroCountdown(boolean flag)
	{
		put(KEY_POMODORO_COUNTDOWN_MODE, flag);
	}

	public String getZonedTimeFormat()
	{
		return (String)get(KEY_ZTIME_FORMAT);
	}

	public void setZonedTimeFormat(String s)
	{
		if(s != null)
			put(KEY_ZTIME_FORMAT, s);
	}

	public String getTimeZone()
	{
		return (String)get(KEY_TIMEZONE);
	}

	public void setTimeZone(String s)
	{
		if(s != null)
			put(KEY_TIMEZONE, s);
	}

	public boolean hasTooltip()
	{
		return ((Boolean)get(KEY_TOOLTIP)).booleanValue();
	}

	public void setTooltip(boolean flag)
	{
		put(KEY_TOOLTIP, flag);
	}
	
	public boolean getOnTop()
	{
		return ((Boolean)get(KEY_ON_TOP)).booleanValue();
	}
	
	public void setOnTop(boolean flag)
	{
		put(KEY_ON_TOP, flag);
	}

	public boolean hasNativeLook()
	{
		return ((Boolean)get(KEY_NATIVE_LOOK)).booleanValue();
	}

	public void setNativeLook(boolean flag)
	{
		put(KEY_NATIVE_LOOK, flag);
	}

	public boolean isFixed()
	{
		return ((Boolean)get(KEY_FIXED)).booleanValue();
	}

	public void setFixed(boolean flag)
	{
		put(KEY_FIXED, flag);
	}

	public boolean isUsingImage()
	{
		return ((Boolean)get(KEY_USING_IMAGE)).booleanValue();
	}

	public void setUsingImage(boolean flag)
	{
		put(KEY_USING_IMAGE, flag);
	}

	public File getImageFile()
	{
		File ff = new File((String)get(KEY_IMAGEFILE));
		if(ff.exists() &&  ff.isFile())
		{
			return ff;
		}
		else
		{
			String debugFile = defaultsDir.getAbsolutePath() + "/images/" + ff.getName();
			System.out.println(debugFile);
			return new File(debugFile);
		}
	}
	
	public void setGlassEffect(boolean flag)
	{
		put(KEY_GLASS_EFFECT, flag);
	}
	
	public boolean hasGlassEffect()
	{
		return ((Boolean)get(KEY_GLASS_EFFECT)).booleanValue();
	}

	public void setImageFile(String imageFile)
	{
		put(KEY_IMAGEFILE, imageFile);
	}

	public int getImageStyle()
	{
		return ((Integer)get(KEY_IMAGE_STYLE)).intValue();
	}

	public void setImageStyle(int i)
	{
		put(KEY_IMAGE_STYLE, i);
	}

	public int getRoundCorners()
	{
		return ((Integer)get(KEY_ROUND_CORNERS)).intValue();
	}

	public void setRoundCorners(int roundCorners)
	{
		put(KEY_ROUND_CORNERS, roundCorners);
	}

	public boolean isSlowTransUpdating()
	{
		return ((Boolean)get(KEY_SLOW_TRANS)).booleanValue();
	}

	public void setSlowTransUpdating(boolean slowTrans)
	{
		put(KEY_SLOW_TRANS, slowTrans);
	}

	public String getAlarmSound()
	{
		return (String)get(KEY_ALARM_SOUND);
	}

	public void setAlarmSound(String alarmSound)
	{
		put(KEY_ALARM_SOUND, alarmSound);
	}

	public String getHourSound()
	{
		return (String)get(KEY_HOUR_SOUND);
	}

	public void setHourSound(String hourSound)
	{
		put(KEY_HOUR_SOUND, hourSound);
	}

	public String getUptimeHourSound()
	{
		return (String)get(KEY_UPTIME_HOUR_SOUND);
	}

	public void setUptimeHourSound(String uptimeHrSound)
	{
		put(KEY_UPTIME_HOUR_SOUND, uptimeHrSound);
	}

	public String getPomodoroWorkSound()
	{
		return (String)get(KEY_POMO_WORK_SOUND);
	}

	public void setPomodoroWorkSound(String pomoWorkSound)
	{
		put(KEY_POMO_WORK_SOUND, pomoWorkSound);
	}

	public String getPomodoroBreakSound()
	{
		return (String)get(KEY_POMO_BREAK_SOUND);
	}

	public void setPomodoroBreakSound(String pomoBreakSound)
	{
		put(KEY_POMO_BREAK_SOUND, pomoBreakSound);
	}

	public String getPomodoroRestSound()
	{
		return (String)get(KEY_POMO_REST_SOUND);
	}

	public void setPomodoroRestSound(String pomoRestSound)
	{
		put(KEY_POMO_REST_SOUND, pomoRestSound);
	}
}