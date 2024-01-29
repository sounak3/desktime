package com.sounaks.desktime;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.Border;

class BorderPreview	extends	JPanel
{
	public BorderPreview()
	{
		setLayout(new GridLayout(3,	3));
		pre	= new JLabel("Preview",	0);
		pre.setFont(new	Font(pre.getFont().getFamily(),	1, 14));
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		add(pre);
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		add(Box.createVerticalGlue());
		setBorder(BorderFactory.createBevelBorder(1));
	}

	public void	setPreview(Border border)
	{
		pre.setBorder(border);
	}

	public Border getPreview()
	{
		return pre.getBorder();
	}

	private	JLabel pre;
}