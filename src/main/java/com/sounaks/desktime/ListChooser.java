package com.sounaks.desktime;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ListChooser extends JPanel implements ListSelectionListener, KeyListener
{
	private	JTextField jtf;
	private	JList<Object> jl;
	private	JScrollPane	jsp;
	Object listDataFinal[];

	public ListChooser(Object aobj[], String s)
	{
		setLayout(new BorderLayout());
		listDataFinal =	aobj;
		jl = new JList<Object>(aobj);
		jl.setSelectionMode(0);
		jl.setValueIsAdjusting(true);
		jl.setVisibleRowCount(6);
		jl.addListSelectionListener(this);
		jsp	= new JScrollPane();
		jsp.getViewport().setView(jl);
		jtf	= new JTextField();
		jtf.addKeyListener(this);
		add(jtf, "North");
		add(jsp, "Center");
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
	}

	public void	keyPressed(KeyEvent	keyevent)
	{
	}

	public void	keyTyped(KeyEvent keyevent)
	{
	}

	public void	keyReleased(KeyEvent keyevent)
	{
		int	i =	keyevent.getKeyCode();
		if(i ==	38 || i	== 104 || i	== 224 || i	== 40 || i == 98 ||	i == 225)
			jl.grabFocus();
		for(int	j =	0; j < listDataFinal.length; j++)
		{
			if(!listDataFinal[j].toString().toLowerCase().startsWith(jtf.getText().toLowerCase()))
				continue;
			jl.setSelectedValue(listDataFinal[j], true);
			break;
		}
	}

	public String getText()
	{
		return jtf.getText();
	}

	public void	setText(String s)
	{
		jtf.setText(s);
	}

	public int getSelectedIndex()
	{
		if(jl.isSelectionEmpty())
		{
			jl.setSelectedValue(listDataFinal[0], true);
			return 0;
		}
		else
		{
			return jl.getSelectedIndex();
		}
	}

	public void	setSelectedIndex(int i)
	{
		jl.setSelectedIndex(i);
	}

	public Object getSelectedValue()
	{
		return jl.getSelectedValue();
	}

	public void	setSelectedValue(Object	obj, boolean flag)
	{
		jl.setSelectedValue(obj, flag);
	}

	public void	valueChanged(ListSelectionEvent	listselectionevent)
	{
		JList<?> jlist	= (JList<?>)listselectionevent.getSource();
		if(!jlist.isSelectionEmpty())
			jtf.setText(jlist.getSelectedValue().toString());
	}
}