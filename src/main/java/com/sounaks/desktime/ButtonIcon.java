package com.sounaks.desktime;

import java.awt.*;
import javax.swing.*;

public class ButtonIcon implements Icon
{
	public static final int DOWN_ARROW  = 101;
	public static final int LEFT_ARROW  = 102;
	public static final int RIGHT_ARROW = 103;
	public static final int UP_ARROW    = 104;
	public static final int SQUARE      = 105;
	public static final int RECTANGLE   = 106;

	public int shape;
	public Color enabledColor;

	public Color getEnabledColor()
	{
		return enabledColor;
	}

	public void setEnabledColor(Color enabledColor)
	{
		this.enabledColor = enabledColor;
	}

	public ButtonIcon(int shape, Color enabledColor)
	{
		this.shape        = shape;
		this.enabledColor = enabledColor;
	}

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
		JComponent component  = (JComponent)c;
		int        iconWidth  = getIconWidth();
		int        iconHeight = getIconHeight();
		g.translate( x, y );
		g.setColor( component.isEnabled() ? enabledColor : Color.gray);
		switch (shape)
		{
			case DOWN_ARROW:
				g.drawLine( 0, 0, iconWidth - 1, 0 );
				g.drawLine( 1, 1, 1 + (iconWidth - 3), 1 );
				g.drawLine( 2, 2, 2 + (iconWidth - 5), 2 );
				g.drawLine( 3, 3, 3 + (iconWidth - 7), 3 );
				g.drawLine( 4, 4, 4 + (iconWidth - 9), 4 );
				break;
			case UP_ARROW:
				g.drawLine(0, iconHeight - 1, iconWidth - 1, iconHeight - 1);
				g.drawLine(1, iconHeight - 2, 1 + iconWidth - 3, iconHeight - 2);
				g.drawLine(2, iconHeight - 3, 2 + iconWidth - 5, iconHeight - 3);
				g.drawLine(3, iconHeight - 4, 3 + iconWidth - 7, iconHeight - 4);
				g.drawLine(4, iconHeight - 5, 4 + iconWidth - 9, iconHeight - 5);
				break;
			case RIGHT_ARROW:
				g.drawLine(0, 0, 1, 0);
				g.drawLine(0, 1, 3, 1);
				g.drawLine(0, 2, 5, 2);
				g.drawLine(0, 3, 7, 3);
				g.drawLine(0, 4, 9, 4);
				g.drawLine(0, 5, 9, 5);
				g.drawLine(0, 6, 7, 6);
				g.drawLine(0, 7, 5, 7);
				g.drawLine(0, 8, 3, 8);
				g.drawLine(0, 9, 1, 9);
				break;
			case LEFT_ARROW:
				g.drawLine(iconWidth - 1, 0, iconWidth - 1, iconHeight - 1);
				g.drawLine(iconWidth - 2, 0, iconWidth - 2, iconHeight - 1);
				g.drawLine(iconWidth - 3, 1, iconWidth - 3, iconHeight - 2);
				g.drawLine(iconWidth - 4, 1, iconWidth - 4, iconHeight - 2);
				g.drawLine(iconWidth - 5, 2, iconWidth - 5, iconHeight - 3);
				g.drawLine(iconWidth - 6, 2, iconWidth - 6, iconHeight - 3);
				g.drawLine(iconWidth - 7, 3, iconWidth - 7, iconHeight - 4);
				g.drawLine(iconWidth - 8, 3, iconWidth - 8, iconHeight - 4);
				g.drawLine(iconWidth - 9, 4, iconWidth - 9, iconHeight - 5);
				g.drawLine(iconWidth - 10, 4, iconWidth - 10, iconHeight - 5);
				break;
			case SQUARE:
				g.fillRect(0, 0, iconWidth, getIconHeight());
				if (component.isEnabled()) g.setColor(enabledColor.darker());
				g.drawRect(0, 0, iconWidth, getIconHeight());
				break;
			case RECTANGLE:
				g.fillRect(0, 0, iconWidth, getIconHeight());
				if (component.isEnabled()) g.setColor(enabledColor.darker());
				g.drawRect(0, 0, iconWidth, getIconHeight());
				break;
			default:
				g.drawLine( 0, 0, iconWidth - 1, 0 );
				g.drawLine( 1, 1, 1 + (iconWidth - 3), 1 );
				g.drawLine( 2, 2, 2 + (iconWidth - 5), 2 );
				g.drawLine( 3, 3, 3 + (iconWidth - 7), 3 );
				g.drawLine( 4, 4, 4 + (iconWidth - 9), 4 );
				break;
		}
		g.translate( -x, -y );
    }
    
    public int getIconWidth()
    {
		switch (shape)
		{
			case SQUARE:
				return 10;
			case RECTANGLE:
				return 20;
			case DOWN_ARROW:
				return 10;
			case UP_ARROW:
				return 10;
			default:
				return 10;
		}
    }

    public int getIconHeight()
    {
		switch (shape)
		{
			case SQUARE:
				return 10;
			case RECTANGLE:
				return 10;
			case DOWN_ARROW:
				return 5;
			case LEFT_ARROW:
				return 10;
			case RIGHT_ARROW:
				return 10;
			case UP_ARROW:
				return 5;
			default:
				return 10;
		}
    }
}