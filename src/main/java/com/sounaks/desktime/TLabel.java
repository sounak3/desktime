package com.sounaks.desktime;

import java.awt.*;
import javax.swing.*;

public class TLabel extends JLabel
{

	private Graphics2D g2;
	private Image image;
	private int position;
	protected boolean hasImage;
	protected boolean useTrans;
	public static final int V_TILE = 1;
	public static final int H_TILE = 2;
	public static final int CENTER = 4;
	public static final int STRETCH = 8;
	public static final int FIT = 16;
	public static final int TILE = 32;
	private int inuse;
	private int labelWidth;
	private int labelHeight;
	private int imgWidth;
	private int imgHeight;
	private double aspectWidth;
	private double aspectHeight;
	private Dimension newWH;

	public TLabel(String s, Image image1)
	{
		super(s, 0);
		if(image1 == null)
			hasImage = false;
		else
			hasImage = true;
		useTrans = false;
		image = image1;
		position = 8;
		g2 = (Graphics2D)super.getGraphics();
		setOpaque(!hasImage);
		setVerticalAlignment(0);
		//setDoubleBuffered(true);
	}

	public TLabel(String s, Image image1, int i)
	{
		super(s, 0);
		if(image1 == null)
			hasImage = false;
		else
			hasImage = true;
		if(i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		useTrans = false;
		image = image1;
		g2 = (Graphics2D)super.getGraphics();
		setOpaque(!hasImage);
		setVerticalAlignment(0);
		//setDoubleBuffered(true);
	}

	public TLabel(String s, Image image1, int i, boolean useTrans)
	{
		super(s, 0);
		if(image1 == null)
			hasImage = false;
		else
			hasImage = true;
		if(i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		this.useTrans = useTrans;
		image = image1;
		g2 = (Graphics2D)super.getGraphics();
		setOpaque(!hasImage);
		setVerticalAlignment(0);
		//setDoubleBuffered(true);
	}

	public TLabel(String s)
	{
		super(s, 0);
		hasImage = false;
		useTrans = false;
		g2 = (Graphics2D)super.getGraphics();
		setOpaque(!hasImage);
		setVerticalAlignment(0);
		//setDoubleBuffered(true);
	}

	public boolean containsImage()
	{
		return hasImage;
	}
	
	public boolean isTransparent()
	{
		return useTrans;
	}
	
	public void setTransparency(boolean trans)
	{
		useTrans = trans;
	}

	public Image getBackImage()
	{
		return image;
	}
	
	public void setBackImage(Image image1)
	{
		if(image1 == null)
			hasImage = false;
		else
			hasImage = true;
		image = image1;
		setOpaque(!hasImage);
		repaint();
	}

	public void setImagePosition(int i)
	{
		if(i == 4 || i == 2 || i == 1 || i == 8 || i == 16 || i == 32)
			position = i;
		else
			throw new IllegalArgumentException("Image position must be CENTER, H_TILE, V_TILE, FIT, TILE or STRETCH");
		repaint();
	}

	public int getImagePosition()
	{
		return position;
	}

	protected void paintComponent(Graphics g)
	{
		g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		if(hasImage)
		{
			labelWidth = getWidth();
			labelHeight = getHeight();
			imgWidth = image.getWidth(this);
			imgHeight = image.getHeight(this);
			aspectWidth = (double)Math.max(labelWidth, imgWidth) / (double)Math.min(labelWidth, imgWidth);
			aspectHeight = (double)Math.max(labelHeight, imgHeight) / (double)Math.min(labelHeight, imgHeight);
			Point pp = getLocation();
			SwingUtilities.convertPointToScreen(pp,this);
			int currentX = (int)Math.ceil(pp.getX());
			int currentY = (int)Math.ceil(pp.getY());
			if(!useTrans)
			{
				switch(position)
				{
					case 8: // For image Stretch to Fit;
						g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
						break;
					case 2: // For image Horizontal tiles;
						if(labelHeight > imgHeight)
							newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectHeight), (int)Math.ceil((double)imgHeight * aspectHeight));
						else
							newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectHeight), (int)Math.ceil((double)imgHeight / aspectHeight));
						if(newWH.width >= labelWidth)
						{
							g2.drawImage(image, 0, 0, newWH.width, newWH.height, this);
						}
						else
						{
							inuse = (int)Math.ceil((double)labelWidth / (double)newWH.width);
							for(int i = 0; i <= inuse; i++)
								g2.drawImage(image, i * newWH.width, 0, newWH.width, newWH.height, this);
						}
						break;
					case 1: // For image Vertical tiles;
						if(labelWidth > imgWidth)
							newWH = new Dimension((int)Math.ceil((double)imgWidth * aspectWidth), (int)Math.ceil((double)imgHeight * aspectWidth));
						else
							newWH = new Dimension((int)Math.ceil((double)imgWidth / aspectWidth), (int)Math.ceil((double)imgHeight / aspectWidth));
						if(newWH.height >= labelHeight)
						{
							g2.drawImage(image, 0, 0, newWH.width, newWH.height, this);
						}
						else
						{
							inuse = (int)Math.ceil((double)labelHeight / (double)newWH.height);
							for(int j = 0; j <= inuse; j++)
								g2.drawImage(image, 0, j * newWH.height, newWH.width, newWH.height, this);
						}
						break;
					case 4: // For image Center
						if(imgWidth > labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, 0, 0, 0 + labelWidth, 0 + labelHeight, (imgWidth - labelWidth) / 2, (imgHeight - labelHeight) / 2, (imgWidth - labelWidth) / 2 + labelWidth, (imgHeight - labelHeight) / 2 + labelHeight, this);
						else if(imgWidth > labelWidth && imgHeight <= labelHeight)
							g2.drawImage(image, 0, (labelHeight - imgHeight) / 2, 0 + labelWidth, (labelHeight - imgHeight) / 2 + imgHeight, (imgWidth - labelWidth) / 2, 0, (imgWidth - labelWidth) / 2 + labelWidth, imgHeight, this);
						else if(imgWidth <= labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, 0, (labelWidth - imgWidth) / 2 + imgWidth, 0 + labelHeight, 0, (imgHeight - labelHeight) / 2, imgWidth, (imgHeight - labelHeight) / 2 + labelHeight, this);
						else if(imgWidth < labelWidth && imgHeight < labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, (labelHeight - imgHeight) / 2, imgWidth, imgHeight, this);
						break;
					case 16: // For image Resize to Fit
						if(imgWidth > labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, 0, 0, labelWidth, labelHeight, this);
						else if(imgWidth > labelWidth && imgHeight <= labelHeight)
							g2.drawImage(image, 0, (labelHeight - imgHeight) / 2, labelWidth, imgHeight, this);
						else if(imgWidth <= labelWidth && imgHeight > labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, 0, imgWidth, labelHeight, this);
						else if(imgWidth < labelWidth && imgHeight < labelHeight)
							g2.drawImage(image, (labelWidth - imgWidth) / 2, (labelHeight - imgHeight) / 2, imgWidth, imgHeight, this);
						break;
					case 32: // For image Tile
						int k = (int)Math.ceil((double)labelWidth / (double)imgWidth);
						int l = (int)Math.ceil((double)labelHeight / (double)imgHeight);
						for(int i1 = 0; i1 < l; i1++)
						{
							for(int j1 = 0; j1 < k; j1++)
								g2.drawImage(image, j1 * imgWidth, i1 * imgHeight, imgWidth, imgHeight, this);
						}
						break;
				}
			}
			else // For position based image (Glass)
			{
				g2.drawImage(image, 0, 0, this);
			}
		}
		super.paintComponent(g2);
	}
}