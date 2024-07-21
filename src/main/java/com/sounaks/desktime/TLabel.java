package com.sounaks.desktime;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class TLabel extends JLabel
{

	private Graphics2D g2;
	private Image image, backupImage;
	private int position, inuse, analogClockOptions;
	protected boolean hasImage;
	protected boolean forceTrans;
	public static final int V_TILE        = 1;
	public static final int H_TILE        = 2;
	public static final int CENTER        = 4;
	public static final int STRETCH       = 8;
	public static final int FIT           = 16;
	public static final int TILE          = 32;
	public static final int SHOW_NONE     = 0;
	public static final int SHOW_AM_PM    = 1000;
	public static final int SHOW_TIMEZONE = 2000;
	public static final int SHOW_AM_PM_TZ = 3000;
	public final int gap                  = 5;
	private int labelWidth, labelHeight, imgWidth, imgHeight;
	private double aspectWidth;
	private double aspectHeight;
	private Dimension newWH;
	private int time_hr, time_mn, time_sc;
	private boolean clockMode;
	private String time_zn, time_ampm;
	private FontMetrics fm;
	private Font anaClkFnt;

	public TLabel(String s, Image image1)
	{
		this(s);
		hasImage    = (image1 != null);
		image       = image1;
		backupImage = image1;
		position    = TLabel.STRETCH;
	}

	public TLabel(String s, Image image1, int i)
	{
		this(s);
		hasImage = (image1 != null);
		if (i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		image       = image1;
		backupImage = image1;
	}

	public TLabel(String s, Image image1, int i, boolean forceTrans)
	{
		this(s);
		hasImage = (image1 != null);
		if (i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		this.forceTrans = forceTrans;
		image           = image1;
		backupImage     = image1;
	}

	public TLabel(String s)
	{
		super(s, 0);
		hasImage   = false;
		forceTrans = false;
		g2         = (Graphics2D)super.getGraphics();
		anaClkFnt  = getFont().deriveFont(getFont().getSize() >= 12 ? (float)getFont().getSize() - 2 : 10.0f);
		fm         = getFontMetrics(anaClkFnt);
		setOpaque(false);
		setVerticalAlignment(0);
		time_hr   = 10;
		time_mn   = 10;
		time_sc   = 5;
		time_ampm = "";
		time_zn   = "";
		clockMode = false;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		anaClkFnt  = font.deriveFont(font.getSize() >= 12 ? (float)font.getSize() - 2 : 10.0f);
		fm         = getFontMetrics(anaClkFnt);
	}

	public void setTime(int hour, int min, int sec, String ampm, String timeZone)
	{
		time_hr         = hour;
		time_mn         = min;
		time_sc         = sec;
		time_ampm       = ampm;
		time_zn         = timeZone;
		repaint();
	}

	public void setTime(String hour, String min, String sec, String ampm, String timeZone)
	{
		time_hr = Integer.parseInt(hour);
		time_mn = Integer.parseInt(min);
		time_sc = Integer.parseInt(sec);
		this.setTime(time_hr, time_mn, time_sc, ampm, timeZone);
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
		int xCenter = (labelWidth - 2 * gap) / 2;
		int yCenter = (labelHeight - 2 * gap)/ 2;
		int radius  = Math.min(xCenter, yCenter);
		int diff    = Math.abs(xCenter - yCenter);
		double radians = (degrees - 90) * 2 * Math.PI / 360;  // 0 deg = 12:00
		double xr = Math.cos(radians);
		double yr = Math.sin(radians);
		int xr1 = (int)(radius * xr * r1) + gap;  // line endpoints relative to center
		int yr1 = (int)(radius * yr * r1) + gap;
		int xr2 = (int)(radius * xr * r2) + gap;
		int yr2 = (int)(radius * yr * r2) + gap;
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

	public void setImagePosition(int i)
	{
		if (i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		repaint();
	}

	public int getImagePosition()
	{
		return position;
	}

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

	protected void paintComponent(Graphics g)
	{
		g2 = (Graphics2D)g;
		labelWidth   = getWidth();
		labelHeight  = getHeight();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		if (hasImage)
		{
			imgWidth     = image.getWidth(this);
			imgHeight    = image.getHeight(this);
			aspectWidth  = (double)Math.max(labelWidth, imgWidth) / (double)Math.min(labelWidth, imgWidth);
			aspectHeight = (double)Math.max(labelHeight, imgHeight) / (double)Math.min(labelHeight, imgHeight);
			Point pp     = getLocation();
			SwingUtilities.convertPointToScreen(pp,this);
			if (forceTrans) // For desktop background based image on window position (Transparent effect)
			{
				g2.drawImage(image, 0, 0, this);
			}
			else
			{
				switch (position)
				{
					case TLabel.STRETCH: // For image Stretch to Fit;
						g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
						break;
					case TLabel.H_TILE: // For image Horizontal tiles;
						if (labelHeight > imgHeight)
							newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectHeight), (int)Math.ceil((double)imgHeight * aspectHeight));
						else
							newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectHeight), (int)Math.ceil((double)imgHeight / aspectHeight));
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
							newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectWidth), (int)Math.ceil((double)imgHeight * aspectWidth));
						else
							newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectWidth), (int)Math.ceil((double)imgHeight / aspectWidth));
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
						if (imgWidth > labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, 0, 0, labelWidth, labelHeight, this);
						else if (imgWidth > labelWidth && imgHeight <= labelHeight)
							g2.drawImage(image, 0, (labelHeight - imgHeight) / 2, labelWidth, imgHeight, this);
						else if (imgWidth <= labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, 0, imgWidth, labelHeight, this);
						else if (imgWidth < labelWidth && imgHeight < labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, (labelHeight - imgHeight) / 2, imgWidth, imgHeight, this);
						break;
					case TLabel.TILE: // For image Tile
						int k = (int)Math.ceil((double)labelWidth / (double)imgWidth);
						int l = (int)Math.ceil((double)labelHeight / (double)imgHeight);
						for (int i1 = 0; i1 < l; i1++)
						{
							for (int j1 = 0; j1 < k; j1++)
								g2.drawImage(image, j1 * imgWidth, i1 * imgHeight, imgWidth, imgHeight, this);
						}
						break;
				}
			}
		}

		if (clockMode) {
			int xCenter = (labelWidth - 2 * gap) / 2;
			int yCenter = (labelHeight - 2 * gap)/ 2;
			int radius  = Math.min(xCenter, yCenter);
			float stroke  = (float)radius / 50;

			g2.setColor(isEnabled() ? getForeground() : Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawOval(xCenter - radius + gap, yCenter - radius + gap, 2 * radius, 2 * radius);
			for (int i = 0; i < 360; i+=30)
				drawRadius(0.9, 1.0, i);

			g2.setFont(anaClkFnt);
			switch (analogClockOptions) {
				case TLabel.SHOW_AM_PM:
				g2.drawString(time_ampm, xCenter + gap - fm.stringWidth(time_ampm)/2, yCenter + gap + yCenter / 2);
				break;

				case TLabel.SHOW_TIMEZONE:
				g2.drawString(time_zn, xCenter + gap - Math.round(fm.stringWidth(time_zn)/2), gap + Math.round(yCenter * 0.60));
				break;

				case TLabel.SHOW_AM_PM_TZ:
				g2.drawString(time_ampm, xCenter + gap - fm.stringWidth(time_ampm)/2, yCenter + gap + yCenter / 2);
				g2.drawString(time_zn, xCenter + gap - Math.round(fm.stringWidth(time_zn)/2), gap + Math.round(yCenter * 0.60));
				break;

				default:
				break;
			}

			g2.setStroke(new BasicStroke(stroke * 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(0, 0.7, time_sc * 6);  // Second hand
			g2.setStroke(new BasicStroke(stroke * 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(0, 0.8, time_mn * 6 + time_sc / 10);  // Minutes
			g2.setStroke(new BasicStroke(stroke * 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			drawRadius(0, 0.5, time_hr * 30 + time_mn / 2);
			int doubleStroke = Math.round(2 * stroke);
			g2.fillOval(gap + xCenter - doubleStroke, gap + yCenter - doubleStroke , 2 * doubleStroke, 2 * doubleStroke);
		}

		super.paintComponent(g2);
	}
}