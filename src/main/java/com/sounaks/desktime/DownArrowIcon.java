package com.sounaks.desktime;

import java.awt.*;
import javax.swing.*;

public class DownArrowIcon implements Icon
{
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
		JComponent component = (JComponent)c;
		int        iconWidth = getIconWidth();
		g.translate( x, y );
		g.setColor( component.isEnabled() ? Color.black : Color.gray);
		g.drawLine( 0, 0, iconWidth - 1, 0 );
		g.drawLine( 1, 1, 1 + (iconWidth - 3), 1 );
		g.drawLine( 2, 2, 2 + (iconWidth - 5), 2 );
		g.drawLine( 3, 3, 3 + (iconWidth - 7), 3 );
		g.drawLine( 4, 4, 4 + (iconWidth - 9), 4 );
		g.translate( -x, -y );
    }
    
    public int getIconWidth()
    {
    	return 10;
    }

    public int getIconHeight()
    {
    	return 5;
    }
}