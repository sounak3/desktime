package com.sounaks.desktime;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

public class TLabel extends JLabel
{

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
	public static final int ANALOG_BORDER = 100;
	public static final int MAJOR_TICK    = 200;
	public static final int HOUR_TICK     = 400;
	public static final int MINUTE_TICK   = 800;

	public static final int GAP           = 5;
	private static final String WRONG_ARGUMENT_ERR = "Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH";
	private int labelWidth, labelHeight;
	private int timeHrs, timeMin, timeSec;
	private boolean clockMode;
	private String timeZn, timeAmPm;
	private FontMetrics fm;
	private Font anaClkFnt;

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
		hasImage   = false;
		forceTrans = false;
		g2         = (Graphics2D)getGraphics();
		anaClkFnt  = getFont().deriveFont(getFont().getSize() >= 12 ? (float)getFont().getSize() - 2 : 10.0f);
		fm         = getFontMetrics(anaClkFnt);
		imageAlpha = 1.0f;
		setOpaque(false);
		setVerticalAlignment(0);
		timeHrs   = 10;
		timeMin   = 10;
		timeSec   = 5;
		timeAmPm = "";
		timeZn   = "";
		clockMode = false;
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
		anaClkFnt  = font.deriveFont(font.getSize() >= 12 ? (float)font.getSize() - 2 : 10.0f);
		fm         = getFontMetrics(anaClkFnt);
	}

	public void setTime(int hour, int min, int sec, String ampm, String timeZone)
	{
		timeHrs         = hour;
		timeMin         = min;
		timeSec         = sec;
		timeAmPm       = ampm;
		timeZn         = timeZone;
		repaint();
	}

	public void setTime(String hour, String min, String sec, String ampm, String timeZone)
	{
		timeHrs = Integer.parseInt(hour);
		timeMin = Integer.parseInt(min);
		timeSec = Integer.parseInt(sec);
		this.setTime(timeHrs, timeMin, timeSec, ampm, timeZone);
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

	@Override
	protected void paintComponent(Graphics g)
	{
		g2 = (Graphics2D)g;
		labelWidth   = getWidth();
		labelHeight  = getHeight();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		if (hasImage)
		{
			int       imgWidth     = image.getWidth(this);
			int       imgHeight    = image.getHeight(this);
			double    aspectWidth  = (double)Math.max(labelWidth, imgWidth) / (double)Math.min(labelWidth, imgWidth);
			double    aspectHeight = (double)Math.max(labelHeight, imgHeight) / (double)Math.min(labelHeight, imgHeight);
			Dimension fitDim       = getScaledDimension(new Dimension(imgWidth, imgHeight), new Dimension(labelWidth, labelHeight));
			Dimension thumbDim     = getScaledDimension(new Dimension(imgWidth, imgHeight), new Dimension(30, 30));
			Point     pp           = getLocation();
			SwingUtilities.convertPointToScreen(pp,this);
			if (forceTrans) // For desktop background based image on window position (Transparent effect)
			{
				g2.drawImage(image, 0, 0, this);
			}
			else
			{
				int inuse;
				Dimension newWH;
				Composite compositeBackup = g2.getComposite();
				g2.setComposite(AlphaComposite.Src.derive(getImageAlpha()));
				switch (imgLayout)
				{
					case TLabel.STRETCH: // For image Stretch to Fit;
						g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
						break;
					case TLabel.H_TILE: // For image Horizontal tiles;
						if (labelHeight > imgHeight)
							newWH = new Dimension((int)Math.ceil(imgWidth * aspectHeight), (int)Math.ceil(imgHeight * aspectHeight));
						else
							newWH = new Dimension((int)Math.ceil(imgWidth / aspectHeight), (int)Math.ceil(imgHeight / aspectHeight));
						if (newWH.width >= labelWidth)
						{
							g2.drawImage(image, 0, 0, newWH.width, newWH.height, this);
						}
						else
						{
							inuse = (int)Math.ceil((double)labelWidth / (double)newWH.width);
							for (int i = 0; i <= inuse; i++)
								g2.drawImage(image, i * newWH.width, 0, newWH.width, newWH.height, this);
						}
						break;
					case TLabel.V_TILE: // For image Vertical tiles;
						if (labelWidth > imgWidth)
							newWH = new Dimension((int)Math.ceil(imgWidth * aspectWidth), (int)Math.ceil(imgHeight * aspectWidth));
						else
							newWH = new Dimension((int)Math.ceil(imgWidth / aspectWidth), (int)Math.ceil(imgHeight / aspectWidth));
						if (newWH.height >= labelHeight)
						{
							g2.drawImage(image, 0, 0, newWH.width, newWH.height, this);
						}
						else
						{
							inuse = (int)Math.ceil((double)labelHeight / (double)newWH.height);
							for (int j = 0; j <= inuse; j++)
								g2.drawImage(image, 0, j * newWH.height, newWH.width, newWH.height, this);
						}
						break;
					case TLabel.CENTER: // For image Center
						if (imgWidth > labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, 0, 0, 0 + labelWidth, 0 + labelHeight, (imgWidth - labelWidth) / 2, (imgHeight - labelHeight) / 2, (imgWidth - labelWidth) / 2 + labelWidth, (imgHeight - labelHeight) / 2 + labelHeight, this);
						else if (imgWidth > labelWidth && imgHeight <= labelHeight)
							g2.drawImage(image, 0, (labelHeight - imgHeight) / 2, 0 + labelWidth, (labelHeight - imgHeight) / 2 + imgHeight, (imgWidth - labelWidth) / 2, 0, (imgWidth - labelWidth) / 2 + labelWidth, imgHeight, this);
						else if (imgWidth <= labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, 0, (labelWidth - imgWidth) / 2 + imgWidth, 0 + labelHeight, 0, (imgHeight - labelHeight) / 2, imgWidth, (imgHeight - labelHeight) / 2 + labelHeight, this);
						else if (imgWidth < labelWidth && imgHeight < labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, (labelHeight - imgHeight) / 2, imgWidth, imgHeight, this);
						break;
					case TLabel.FIT: // For image Resize to Fit
						g2.drawImage(image, (labelWidth - fitDim.width) / 2, (labelHeight - fitDim.height) / 2, fitDim.width, fitDim.height, this);
						break;
					case TLabel.TILE: // For image Tile
						int k = (int)Math.ceil((double)labelWidth / (double)thumbDim.width);
						int l = (int)Math.ceil((double)labelHeight / (double)thumbDim.height);
						for (int i1 = 0; i1 < l; i1++)
						{
							for (int j1 = 0; j1 < k; j1++)
								g2.drawImage(image, j1 * thumbDim.width, i1 * thumbDim.height, thumbDim.width, thumbDim.height, this);
						}
						break;
					default: // default is stretch to fit
						g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
						break;
				}
				g2.setComposite(compositeBackup);
			}
		}

		if (clockMode) {
			int     xCenter   = (labelWidth - 2 * GAP) / 2;
			int     yCenter   = (labelHeight - 2 * GAP)/ 2;
			int     radius    = Math.min(xCenter, yCenter);
			float   stroke    = (float)radius / 50;
			boolean ampm      = false;
			boolean tzone     = false;
			boolean clkBdr    = false;
			boolean majHrTick = false;
			boolean hrTick    = false;
			boolean minTick   = false;

			g2.setColor(isEnabled() ? getForeground() : Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			g2.setFont(anaClkFnt);
			switch (analogClockOptions) {
				case TLabel.SHOW_AM_PM:
				ampm = true;
				break;

				case TLabel.SHOW_TIMEZONE:
				tzone = true;
				break;

				case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				ampm = tzone = true;
				break;

				case TLabel.ANALOG_BORDER:
				clkBdr = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.SHOW_AM_PM):
				clkBdr = ampm = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.SHOW_TIMEZONE):
				clkBdr = tzone = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				clkBdr = tzone = ampm = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK:
				clkBdr = majHrTick = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK + TLabel.SHOW_AM_PM):
				clkBdr = majHrTick = ampm = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK + TLabel.SHOW_TIMEZONE):
				clkBdr = majHrTick = tzone = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK + TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				clkBdr = majHrTick = tzone = ampm = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.HOUR_TICK:
				clkBdr = hrTick = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.HOUR_TICK + TLabel.SHOW_AM_PM):
				clkBdr = hrTick = ampm = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.HOUR_TICK + TLabel.SHOW_TIMEZONE):
				clkBdr = hrTick = tzone = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.HOUR_TICK + TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				clkBdr = hrTick = tzone = ampm = true;
				break;

				case TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK:
				clkBdr = minTick = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK + TLabel.SHOW_AM_PM):
				clkBdr = minTick = ampm = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK + TLabel.SHOW_TIMEZONE):
				clkBdr = minTick = tzone = true;
				break;

				case (TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK + TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				clkBdr = minTick = tzone = ampm = true;
				break;

				default:
				break;
			}

			if (ampm) g2.drawString(timeAmPm, xCenter + GAP - fm.stringWidth(timeAmPm)/2, yCenter + GAP + yCenter / 2);
			if (tzone) g2.drawString(timeZn, xCenter + GAP - Math.round(fm.stringWidth(timeZn)/2.00f), GAP + Math.round(yCenter * 0.60f));
			for (int i = 0; i < 360; i++)
			{
				if (i % 90 == 0 && majHrTick) drawRadius(0.85, 0.97, i);
				if (i % 30 == 0 && hrTick) drawRadius(0.9, 0.97, i);
				if (minTick)
				{
					if (i % 30 == 0) drawRadius(0.9, 0.97, i);
					if (i % 6 == 0) drawRadius(0.95, 0.97, i);
				}
			}

			g2.setColor(TLabel.darker(getForeground(), 0.50));
			if (clkBdr) g2.drawOval(xCenter - radius + GAP, yCenter - radius + GAP, 2 * radius, 2 * radius);
			g2.setStroke(new BasicStroke(stroke * 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(-0.1, 0.8, timeMin * 6 + timeSec / 10);  // Minutes
			g2.setStroke(new BasicStroke(stroke * 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(-0.1, 0.5, timeHrs * 30 + timeMin / 2);  // Hours
			g2.setColor(TLabel.getInvertedColor(getForeground()));
			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(0, 0.7, timeSec * 6);  // Seconds
			int doubleStroke = Math.round(3 * stroke);
			g2.fillOval(GAP + xCenter - doubleStroke, GAP + yCenter - doubleStroke , 2 * doubleStroke, 2 * doubleStroke);
		}

		super.paintComponent(g2);
	}
}