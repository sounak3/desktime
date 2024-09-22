package com.sounaks.desktime;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

public class TLabel extends JLabel
{
	public enum DIAL_OBJECTS_SIZE {		
		MINIMAL(0.2F), SMALL(0.3f), MEDIUM(0.4f), LARGE(0.5f);

		private float dialObjectsSize;

		DIAL_OBJECTS_SIZE (float dialObjectsSize) {
			this.dialObjectsSize = dialObjectsSize;
		}

		public float getSizeValue() {
			return dialObjectsSize;
		}

		public static String nameOfValue(float value) {
			for (DIAL_OBJECTS_SIZE currSize : DIAL_OBJECTS_SIZE.values()) {
				if (currSize.getSizeValue() == value) {
					return currSize.name();
				}
			}
			return DIAL_OBJECTS_SIZE.LARGE.name();
		}
	}

	private transient Graphics2D g2;
	private transient Image image, backupImage;
	private int imgLayout, analogClockOptions;
	private float imageAlpha;
	protected boolean hasImage;
	protected boolean forceTrans;
	public static final String V_TILE_TEXT  = "Tile Vertically";
	public static final String H_TILE_TEXT  = "Tile Horizontally";
	public static final String CENTER_TEXT  = "Center";
	public static final String STRETCH_TEXT = "Stretch to Fit";
	public static final String FIT_TEXT     = "Fit - Maintain aspect ratio";
	public static final String TILE_TEXT    = "Tile";
	public static final int V_TILE        = 1;
	public static final int H_TILE        = 2;
	public static final int CENTER        = 4;
	public static final int STRETCH       = 8;
	public static final int FIT           = 16;
	public static final int TILE          = 32;
	public static final int SHOW_NONE     = 0;
	public static final int SHOW_AM_PM    = 1000;
	public static final int SHOW_TIMEZONE = 2000;
	public static final int SHOW_DAYMONTH = 4000;
	public static final int SHOW_WEEKDAY  = 8000;
	public static final int ANALOG_BORDER = 100;
	public static final int MAJOR_TICK    = 200;
	public static final int HOUR_TICK     = 400;
	public static final int MINUTE_TICK   = 800;

	public static final int GAP           = 5;
	public static final Color SHADOW      = new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 128);
	private static final String WRONG_ARGUMENT_ERR = "Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH";
	private int labelWidth, labelHeight;
	private int timeHrs, timeMin, timeSec;
	private boolean clockMode, previewMode, labelBorder;
	private String timeZn, timeAmPm, timeWkDy, timeDyMn;
	private FontMetrics fm;
	private Font anaClkFnt;
	private float dialObjSize;

	public TLabel(String s, Image image1)
	{
		this(s);
		hasImage    = (image1 != null);
		image       = image1;
		backupImage = image1;
		imgLayout    = TLabel.STRETCH;
	}

	public TLabel(String s, Image image1, int position)
	{
		this(s, image1);
		if (position == 4 || position == 2 || position == 1 || position == 8 || position == 16 || position == 32)
			this.imgLayout = position;
		else
			throw new IllegalArgumentException(WRONG_ARGUMENT_ERR);
	}

	public TLabel(String s, Image image1, int position, boolean forceTrans)
	{
		this(s, image1);
		if (position == 4 || position == 2 || position == 1 || position == 8 || position == 16 || position == 32)
			this.imgLayout = position;
		else
			throw new IllegalArgumentException(WRONG_ARGUMENT_ERR);
		this.forceTrans = forceTrans;
	}

	public TLabel(String s)
	{
		super(s, 0);
		hasImage    = false;
		forceTrans  = false;
		clockMode   = false;
		labelBorder = false;
		g2          = (Graphics2D)getGraphics();
		dialObjSize = 0.4f;
		anaClkFnt   = getFont().deriveFont(getFont().getSize() - (20 - dialObjSize * 20) >= 8 ? getFont().getSize() - (20 - dialObjSize * 20) : 8.0f).deriveFont(Font.PLAIN);
		fm          = getFontMetrics(anaClkFnt);
		imageAlpha  = 1.0f;
		setOpaque(false);
		setVerticalAlignment(0);
		timeHrs  = 10;
		timeMin  = 10;
		timeSec  = 5;
		timeAmPm = "";
		timeZn   = "";
		timeWkDy = "";
		timeDyMn = "";
	}

	public float getDialObjSize() {
		return dialObjSize;
	}

	public void setDialObjSize(float dialObjSize) {
		for (DIAL_OBJECTS_SIZE siz : DIAL_OBJECTS_SIZE.values()) {
			if (siz.getSizeValue() == dialObjSize) {
				this.dialObjSize = dialObjSize;
			}
		}
	}

	public boolean hasAnalogClockLabelBorders() {
		return labelBorder;
	}

	public void setAnalogClockLabelBorder(boolean labelBorder) {
		this.labelBorder = labelBorder;
		repaint();
	}

	public boolean isPreviewMode() {
		return previewMode;
	}

	public void setPreviewMode(boolean previewMode) {
		this.previewMode = previewMode;
	}

	public void setImageAlpha(float imageAlpha) {
		this.imageAlpha = imageAlpha;
	}

	public float getImageAlpha() {
		return imageAlpha;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		anaClkFnt  = font.deriveFont(font.getSize() - (20 - dialObjSize * 20) >= 8 ? font.getSize() - (20 - dialObjSize * 20) : 8.0f).deriveFont(Font.PLAIN);
		fm         = getFontMetrics(anaClkFnt);
	}

	public void setTime(int hour, int min, int sec, String ampm, String daymonth, String weekD, String timeZone)
	{
		timeHrs  = hour;
		timeMin  = min;
		timeSec  = sec;
		timeAmPm = ampm;
		timeZn   = timeZone;
		timeDyMn = daymonth;
		timeWkDy = weekD;
		repaint();
	}

	public void setTime(String hour, String min, String sec, String ampm, String daymonth, String weekday, String timeZone)
	{
		timeHrs = Integer.parseInt(hour);
		timeMin = Integer.parseInt(min);
		timeSec = Integer.parseInt(sec);
		this.setTime(timeHrs, timeMin, timeSec, ampm, daymonth, weekday, timeZone);
	}

	public void setAnalogClockOptions(int clockOptions)
	{
		analogClockOptions = clockOptions;
		repaint();
	}

	public int getAnalogClockOptions()
	{
		return analogClockOptions;
	}

	public boolean isClockMode()
	{
		return clockMode;
	}

	public void setClockMode(boolean analog)
	{
		clockMode = analog;
	}

	private void drawRadius(double r1, double r2, int degrees)
	{
		int xCenter = (labelWidth - 2 * GAP) / 2;
		int yCenter = (labelHeight - 2 * GAP)/ 2;
		int radius  = Math.min(xCenter, yCenter);
		int diff    = Math.abs(xCenter - yCenter);
		double radians = (degrees - 90) * 2 * Math.PI / 360;  // 0 deg = 12:00
		double xr = Math.cos(radians);
		double yr = Math.sin(radians);
		int xr1 = (int)(radius * xr * r1) + GAP;  // line endpoints relative to center
		int yr1 = (int)(radius * yr * r1) + GAP;
		int xr2 = (int)(radius * xr * r2) + GAP;
		int yr2 = (int)(radius * yr * r2) + GAP;
		if (xCenter > yCenter) g2.drawLine(radius + xr1 + diff, radius + yr1, radius + xr2 + diff, radius + yr2);
		else g2.drawLine(radius + xr1, radius + yr1 + diff, radius + xr2, radius + yr2 + diff);
	}

	private void drawBevelRadius(double r1, double r2, int degrees)
	{
		Color oldColor = g2.getColor();
        g2.setColor( getForeground().brighter() );
		drawRadius(r1, r2, degrees - 1);
        g2.setColor( getForeground().darker() );
		drawRadius(r1, r2, degrees + 1);
		g2.setColor( getForeground() );
		drawRadius(r1, r2 + 0.01, degrees);
		g2.setColor(oldColor);
	}

	public boolean containsImage()
	{
		return hasImage;
	}
	
	public boolean isForcedTransparent()
	{
		return forceTrans;
	}
	
	public void setTransparency(boolean forceTrans)
	{
		this.forceTrans = forceTrans;
	}

	public Image getBackImage()
	{
		return image;
	}
	
	public void setBackImage(Image image1)
	{
		hasImage    = (image1 != null);
		image       = image1;
		backupImage = image1;
		setOpaque(false);
		repaint();
	}

	private Dimension getScaledDimension(Dimension imgSize, Dimension destRect)
	{
		double wRatio = destRect.getWidth() / imgSize.getWidth();
		double hRatio = destRect.getHeight() / imgSize.getHeight();
		double ratio = Math.min(wRatio, hRatio);
		return new Dimension((int)(imgSize.width * ratio), (int)(imgSize.height * ratio));
	}

	public void setImageLayout(int layout)
	{
		if (layout == 4 || layout == 2 || layout == 1 || layout == 8 || layout == 16 || layout == 32)
			imgLayout = layout;
		else
			throw new IllegalArgumentException(WRONG_ARGUMENT_ERR);
		repaint();
	}

	public int getImageLayout()
	{
		return imgLayout;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		if (hasImage)
			image = enabled ? backupImage : getGrayImage();
	}

	protected Image getGrayImage()
	{
		BufferedImage  curImage  = ExUtils.toBufferedImage(image);
		BufferedImage  resultImg = new BufferedImage( image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_BYTE_GRAY );
		ColorConvertOp op        = new ColorConvertOp( curImage.getColorModel().getColorSpace(), resultImg.getColorModel().getColorSpace(), null );
		op.filter( curImage, resultImg );
		return resultImg;
	}

	public static Color brighter(Color color, double fraction)
	{
        int red   = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
        int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
        int blue  = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));
        int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

	public static Color darker(Color color, double fraction)
	{
        int red   = (int) Math.round(Math.max(0, color.getRed() - 255 * fraction));
        int green = (int) Math.round(Math.max(0, color.getGreen() - 255 * fraction));
        int blue  = (int) Math.round(Math.max(0, color.getBlue() - 255 * fraction));
        int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

	public static Color getInvertedColor(Color colour)
	{
		int color = colour.getRGB();
		int red   = color & 255;
		int green = (color >> 8) & 255;
		int blue  = (color >> 16) & 255;
		int alpha = (color >> 24) & 255;
		    red   = 255 - red;
		    green = 255 - green;
		    blue  = 255 - blue;
		return new Color(red + (green << 8) + ( blue << 16) + ( alpha << 24));
	}

	private void drawBackImage(Image image, int destWidth, int destHeight, int imageLayout)
	{
		int       imgWidth     = image.getWidth(this);
		int       imgHeight    = image.getHeight(this);
		double    aspectWidth  = (double)Math.max(destWidth, imgWidth) / (double)Math.min(destWidth, imgWidth);
		double    aspectHeight = (double)Math.max(destHeight, imgHeight) / (double)Math.min(destHeight, imgHeight);
		Dimension fitDim       = getScaledDimension(new Dimension(imgWidth, imgHeight), new Dimension(destWidth, destHeight));
		int inuse;
		Dimension newWH;
		Composite compositeBackup = g2.getComposite();
		AlphaComposite acp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getImageAlpha());
		g2.setComposite(acp);
		switch (imageLayout)
		{
			case TLabel.STRETCH: // For image Stretch to Fit;
				g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				break;
			case TLabel.H_TILE: // For image Horizontal tiles;
				if (destHeight > imgHeight)
					newWH = new Dimension((int)Math.ceil(imgWidth * aspectHeight), (int)Math.ceil(imgHeight * aspectHeight));
				else
					newWH = new Dimension((int)Math.ceil(imgWidth / aspectHeight), (int)Math.ceil(imgHeight / aspectHeight));
				inuse = (int)Math.ceil((double)destWidth / (double)newWH.width);
				for (int i = 0; i <= inuse; i++)
					g2.drawImage(image, i * newWH.width, 0, newWH.width, newWH.height, this);
				break;
			case TLabel.V_TILE: // For image Vertical tiles;
				if (destWidth > imgWidth)
					newWH = new Dimension((int)Math.ceil(imgWidth * aspectWidth), (int)Math.ceil(imgHeight * aspectWidth));
				else
					newWH = new Dimension((int)Math.ceil(imgWidth / aspectWidth), (int)Math.ceil(imgHeight / aspectWidth));
				inuse = (int)Math.ceil((double)destHeight / (double)newWH.height);
				for (int j = 0; j <= inuse; j++)
					g2.drawImage(image, 0, j * newWH.height, newWH.width, newWH.height, this);
				break;
			case TLabel.CENTER: // For image Center
				g2.drawImage(image, (destWidth - imgWidth) / 2, (destHeight - imgHeight) / 2, imgWidth, imgHeight, this);
				break;
			case TLabel.FIT: // For image Resize to Fit
				g2.drawImage(image, (destWidth - fitDim.width) / 2, (destHeight - fitDim.height) / 2, fitDim.width, fitDim.height, this);
				break;
			case TLabel.TILE: // For image Tile
				drawImageTiles(image, 30, 30, destWidth, destHeight);
				break;
			default: // default is stretch to fit
				g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				break;
		}
		g2.setComposite(compositeBackup);
	}

	private void drawImageTiles(Image image, int tileWidth, int tileHeight, int destWidth, int destHeight)
	{
		int       imageWidth  = image.getWidth(this);
		int       imageHeight = image.getHeight(this);
		Dimension thumbDim    = getScaledDimension(new Dimension(imageWidth, imageHeight), new Dimension(tileWidth, tileHeight));
		int       numTilesW   = (int)Math.ceil((double)destWidth / (double)thumbDim.width);
		int       numTilesH   = (int)Math.ceil((double)destHeight / (double)thumbDim.height);
		for (int i1 = 0; i1 < numTilesH; i1++)
		{
			for (int j1 = 0; j1 < numTilesW; j1++)
				g2.drawImage(image, j1 * thumbDim.width, i1 * thumbDim.height, thumbDim.width, thumbDim.height, this);
		}
	}

	private void drawDialTicks(boolean majorHours, boolean allHours, boolean minutes)
	{
		for (int i = 0; i < 360; i++)
		{
			if (i % 90 == 0 && majorHours) drawRadius(0.85, 0.97, i);
			else if (i % 30 == 0 && allHours) drawRadius(0.9, 0.97, i);
			else if (minutes)
			{
				if (i % 30 == 0) drawRadius(0.9, 0.97, i);
				else if (i % 6 == 0) drawRadius(0.95, 0.97, i);
			}
		}
	}

	private void draw3DLabelRect(String label, int locX, int locY, Color col) {
		int x = locX;
		int y = locY - fm.getAscent();
		int w = fm.stringWidth(label);
		int h = fm.getHeight();
		g2.setColor(Color.black);
		g2.fillRect(x - 5, y - 2, w + 10, h + 2);
		if (ExUtils.containsNumbers(label)) {
			g2.setColor(col);
			switch (label.length()) {
				case 5:
					int w1 = fm.stringWidth(label.substring(0, 3));
					g2.fillRect(x - 5, y - 2, w1 + 6, h + 2);
					break;
				case 4:
					int w2 = fm.stringWidth(label.substring(0, 2));
					g2.fillRect(x - 5, y - 2, w2 + 5, h + 2);
					break;
				default:
					break;
			}
		}
		g2.setColor(Color.lightGray);
		g2.draw3DRect(x - 5, y - 2, w + 10 + 1, h + 3, false);
		g2.setColor(Color.gray);
		g2.draw3DRect(x - 5 + 1, y - 1, w + 10 - 1, h + 1, false);
		g2.setColor(Color.black);
		g2.draw3DRect(x - 5 + 2, y, w + 10 - 3, h - 1, false);
	}

	private void draw3DLabelText(String str, int x, int y, Color col) {
		col = previewMode ? Color.white : col;
		Color newCol = str.length() > 3 ? Color.darkGray : col;
		String firstStr = str;
		boolean strHasN = ExUtils.containsNumbers(str);
		switch (str.length()) {
			case 5:
				firstStr = str.substring(0, 3);
				break;
			case 4:
				firstStr = str.substring(0, 2);
				break;
			default:
				break;
		}
		if (anaClkFnt.getSize() >= 28) {
			g2.setColor(col.darker());
			g2.drawString(str, x - 1, y - 1);
			if (str.length() > 3 && labelBorder && strHasN) {
				g2.setColor(newCol.darker());
				g2.drawString(firstStr, x - 1, y - 1);
			}
		}
		if (anaClkFnt.getSize() >= 20) {
			g2.setColor(col.brighter());
			g2.drawString(str, x + 1, y + 1);
			if (str.length() > 3 && labelBorder && strHasN) {
				g2.setColor(newCol.brighter());
				g2.drawString(firstStr, x + 1, y + 1);
			}
		}
		g2.setColor(col);
		g2.drawString(str, x, y);
		if (str.length() > 3 && labelBorder && strHasN) {
			g2.setColor(newCol);
			g2.drawString(firstStr, x, y);
		}
	}

	private void drawDialLabels(boolean ampmLabel, boolean tzLabel, boolean dateLabel, boolean wkDyLabel, int clockCenterX, int clockCenterY, Color selectedColor)
	{
		if (ampmLabel) {
			int strX = clockCenterX + GAP - fm.stringWidth(timeAmPm)/2;
			int strY = clockCenterY + GAP + Math.round(clockCenterY * dialObjSize);
			if (labelBorder) draw3DLabelRect(timeAmPm, strX, strY, selectedColor);
			draw3DLabelText(timeAmPm, strX, strY, selectedColor);
		}
		if (tzLabel) {
			int strX = clockCenterX + GAP - Math.round(fm.stringWidth(timeZn)/2.00f);
			int strY = clockCenterY + fm.getHeight() - Math.round(clockCenterY * dialObjSize);
			if (labelBorder) draw3DLabelRect(timeZn, strX, strY, selectedColor);
			draw3DLabelText(timeZn, strX, strY, selectedColor);
		}
		if (dateLabel) {
			// int strX = 2 * clockCenterX - Math.round(2 * GAP * dialObjSize) - fm.stringWidth(timeDyMn);
			// int strX = (int)Math.floor(2 * clockCenterX - fm.stringWidth(timeDyMn) - fm.stringWidth(timeDyMn)/(dialObjSize * 10));
			int strX = clockCenterX + GAP + Math.round((clockCenterX - fm.stringWidth(timeDyMn)) * dialObjSize);
			int strY = clockCenterY + GAP - Math.round(fm.getHeight() * dialObjSize) + fm.getAscent();
			if (labelBorder) draw3DLabelRect(timeDyMn, strX, strY, selectedColor);
			draw3DLabelText(timeDyMn, strX, strY, selectedColor);
		}
		if (wkDyLabel) {
			// int strX = clockCenterX - fm.stringWidth(timeWkDy) - Math.round(clockCenterX * dialObjSize);
			int strX = clockCenterX - Math.round((fm.stringWidth(timeWkDy) + clockCenterX) * dialObjSize);
			int strY = clockCenterY + GAP - Math.round(fm.getHeight() * dialObjSize) + fm.getAscent();
			if (labelBorder) draw3DLabelRect(timeWkDy, strX, strY, selectedColor);
			draw3DLabelText(timeWkDy, strX, strY, selectedColor);
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g2 = (Graphics2D)g;
		labelWidth   = getWidth();
		labelHeight  = getHeight();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (hasImage)
		{
			if (forceTrans) // For desktop background based image on window position (Transparent effect)
			{
				g2.drawImage(image, 0, 0, this);
			}
			else
			{
				drawBackImage(image, labelWidth, labelHeight, imgLayout);
			}
		}

		if (clockMode) {
			int     xCenter   = (labelWidth - 2 * GAP) / 2;
			int     yCenter   = (labelHeight - 2 * GAP)/ 2;
			int     radius    = Math.min(xCenter, yCenter);
			float   stroke    = (float)radius / 50;
			boolean ampm      = false;
			boolean tzone     = false;
			boolean daymon    = false;
			boolean weekday   = false;
			boolean clkBdr    = false;
			boolean majHrTick = false;
			boolean hrTick    = false;
			boolean minTick   = false;
			double  shdOffset = stroke * 2.0f;
			int borderOptions = analogClockOptions % 1000;
			int labelOptions  = Math.floorDiv(analogClockOptions, 1000) * 1000;
			
			g2.setFont(anaClkFnt);
			switch (labelOptions) {
				case TLabel.SHOW_AM_PM:
				ampm = true;
				break;

				case TLabel.SHOW_TIMEZONE:
				tzone = true;
				break;

				case TLabel.SHOW_DAYMONTH:
				daymon = true;
				break;

				case TLabel.SHOW_WEEKDAY:
				weekday = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				ampm = tzone = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_WEEKDAY):
				ampm = weekday = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH):
				ampm = daymon = true;
				break;

				case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
				tzone = weekday = true;
				break;

				case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
				tzone = daymon = true;
				break;

				case (TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				daymon = weekday = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
				ampm = tzone = daymon = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
				ampm = tzone = weekday = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				ampm = daymon = weekday = true;
				break;

				case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				tzone = daymon = weekday = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				ampm = tzone = daymon = weekday = true;
				break;

				default:
				break;
			}

			switch (borderOptions) {
				case TLabel.ANALOG_BORDER:
				clkBdr = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK:
				clkBdr = majHrTick = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.HOUR_TICK:
				clkBdr = hrTick = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK:
				clkBdr = minTick = true;
				break;

				default:
				break;
			}

			Color currColor = isEnabled() ? getForeground() : Color.LIGHT_GRAY;
			g2.setColor(currColor);
			// Draw AM/PM and time zone markers
			drawDialLabels(ampm, tzone, daymon, weekday, xCenter, yCenter, currColor);

			// Draw Hour and minute markers
			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawDialTicks(majHrTick, hrTick, minTick);
			
			// Draw dial border
			g2.setColor(TLabel.darker(getForeground(), 0.50));
			g2.setStroke(new BasicStroke(stroke * 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (clkBdr) g2.drawOval(xCenter - radius + GAP, yCenter - radius + GAP, 2 * radius, 2 * radius);
			else if (previewMode) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawOval(xCenter - radius + GAP, yCenter - radius + GAP, 2 * radius, 2 * radius);
			}
			
			// Draw hour, minute and second hands shadow
			g2.setColor(SHADOW);
			g2.setStroke(new ShadowStroke(stroke * 2.0f, shdOffset));
			drawRadius(-0.1, dialObjSize + 0.15, timeMin * 6 + timeSec / 10);  // Minutes
			g2.setStroke(new ShadowStroke(stroke * 3.0f, shdOffset));
			drawRadius(-0.1, dialObjSize + 0.0, timeHrs * 30 + timeMin / 2);  // Hours
			g2.setStroke(new ShadowStroke(stroke * 1.0f, shdOffset));
			drawRadius(0, dialObjSize + 0.1, timeSec * 6);  // Seconds

			// Draw hour, minute and second hands
			g2.setColor(TLabel.darker(getForeground(), 0.50));
			g2.setStroke(new BasicStroke(stroke * 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawBevelRadius(-0.1, dialObjSize + 0.15, timeMin * 6 + timeSec / 10);  // Minutes
			g2.setStroke(new BasicStroke(stroke * 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawBevelRadius(-0.1, dialObjSize + 0.0, timeHrs * 30 + timeMin / 2);  // Hours
			g2.setColor(TLabel.getInvertedColor(getForeground()));
			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(0, dialObjSize + 0.1, timeSec * 6);  // Seconds

			// Draw center circle
			int doubleStroke = Math.round(3 * stroke);
			g2.fillOval(GAP + xCenter - doubleStroke, GAP + yCenter - doubleStroke , 2 * doubleStroke, 2 * doubleStroke);
		}

		g2.setFont(getFont());
		super.paintComponent(g2);
	}
}