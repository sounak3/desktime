package com.sounaks.desktime;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

class FileCellRenderer extends DefaultListCellRenderer
{
	final transient Image block = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/block-icon.png"))).getImage();
	final transient Image clock = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/clock-icon.png"))).getImage();
	ImageIcon blockIcon, clockIcon;

	FileCellRenderer()
	{
		blockIcon = new ImageIcon(block.getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING));
		clockIcon = new ImageIcon(clock.getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING));
	}

	@Override
	public Component getListCellRendererComponent(JList<?> jlist, Object obj, int i, boolean flag,	boolean	flag1)
	{
		JLabel label = (JLabel) super.getListCellRendererComponent(jlist, obj, i, flag,	flag1);
		String nameString = ((File)obj).getName();
		label.setText(nameString);
		if (nameString.contains("clock") || nameString.contains("dial")) label.setIcon(clockIcon);
		else label.setIcon(blockIcon);
		return label;
	}
}