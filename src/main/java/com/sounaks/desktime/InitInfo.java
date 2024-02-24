package com.sounaks.desktime;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

public class InitInfo extends Hashtable<Object, Object>
{
	public InitInfo()
	{
		super(18,0.6f);
		put("FONT", new Font("SansSerif", Font.BOLD, 16));
		put("BACKGROUND", Color.white);
		put("OPACITY", 1.0F);
		put("FOREGROUND", Color.black);
		put("LINE_COLOR", Color.black);
		put("BORDER", BorderFactory.createEmptyBorder());
		put("LOCATION", new Point(0,0));
		put("DISPLAY_METHOD", "CURTZ");
		put("UPTIME_FORMAT", "'Up-Time: 'HH'-hour(s), 'mm'-minute(s), 'ss'-second(s)'");
		put("POMODORO_FORMAT", "mm:ss");
		put("POMODORO_LEAD_LABEL", false);
		put("ZTIME_FORMAT", "zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
		put("POMODORO_TASK", "25 min. work, 5 min. break, 30 min. rest");
		put("TIMEZONE", "GMT");
		put("TOOLTIP", true);
		put("NATIVE_LOOK", false);
		put("FIXED", false);
		put("USING_IMAGE", false);
		put("GLASS_EFFECT", false);
		put("IMAGEFILE", ".");
		put("IMAGE_STYLE", 16);
		put("ON_TOP", false);
		put("ROUND_CORNERS", false);
		put("SLOW_TRANS", false);
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

	public boolean getPomodoroLeadingLabel()
	{
		return ((Boolean)get("POMODORO_LEAD_LABEL")).booleanValue();
	}

	public void setPomodoroLeadingLabel(boolean flag)
	{
		put("POMODORO_LEAD_LABEL", flag);
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
}