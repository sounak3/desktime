package com.sounaks.desktime;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class FileCellRenderer extends DefaultListCellRenderer
{

	FileCellRenderer()
	{
	}

	public Component getListCellRendererComponent(JList	jlist, Object obj, int i, boolean flag,	boolean	flag1)
	{
		super.getListCellRendererComponent(jlist, obj, i, flag,	flag1);
		setText(((File)obj).getName());
		return this;
	}
}