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
		put("FOREGROUND", Color.black);
		put("LINE_COLOR", Color.black);
		put("BORDER", BorderFactory.createEmptyBorder());
		put("LOCATION", new Point(0,0));
		put("DISPLAY_METHOD", "CURTZ");
		put("UPTIME_FORMAT", "'Up-Time: 'HH'-hour(s), 'mm'-minute(s), 'ss'-second(s)'");
		put("TTZ_FORMAT", "zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
		put("GMT_FORMAT", "zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
		put("TOOLTIP", true);
		put("NATIVE_LOOK", false);
		put("FIXED", false);
		put("USING_IMAGE", false);
		put("GLASS_EFFECT", false);
		put("IMAGEFILE", ".");
		put("IMAGE_STYLE", 16);
		put("ON_TOP", false);
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

	public String getThisTimeZoneFormat()
	{
		return (String)get("TTZ_FORMAT");
	}

	public void setThisTimeZoneFormat(String s)
	{
		if(s != null)
			put("TTZ_FORMAT", s);
	}

	public String getGMTZoneFormat()
	{
		return (String)get("GMT_FORMAT");
	}

	public void setGMTZoneFormat(String s)
	{
		if(s != null)
			put("GMT_FORMAT", s);
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
}