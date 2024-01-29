package com.sounaks.desktime;

import javax.swing.*;
import java.util.*;
import java.awt.*;

class AlarmListCellRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		TimeBean tmpb=(TimeBean)value;
		Date dte=new Date();
		setText(tmpb.getName());
		if(tmpb.getNextRuntime(dte).before(dte)) setForeground(Color.red);
		else setForeground(Color.black);
		return this;
	}
}
	