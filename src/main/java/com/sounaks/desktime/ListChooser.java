package com.sounaks.desktime;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ListChooser extends JPanel implements ListSelectionListener, KeyListener, FocusListener
{
	private	JTextField jtf;
	private	JList<Object> jl;
	private	JScrollPane	jsp;
	Object listDataFinal[];
	private int curPosition;
	private List<ListSelectionListener> listeners1;

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
		jtf.addFocusListener(this);
		add(jtf, "North");
		add(jsp, "Center");
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
		curPosition = 0;
		listeners1 = new ArrayList<ListSelectionListener>();
	}

	public void addListSelectionListener(ListSelectionListener toAdd)
	{
        listeners1.add(toAdd);
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
			curPosition = jtf.getCaretPosition();
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
		{
			String curTextVal = jlist.getSelectedValue().toString();
			jtf.setText(curTextVal);
			jtf.select(curPosition, curTextVal.length());
			for (ListSelectionListener listener1 : listeners1)
				listener1.valueChanged(new ListSelectionEvent(this, listselectionevent.getFirstIndex(), listselectionevent.getLastIndex(), listselectionevent.getValueIsAdjusting()));
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		jtf.select(0, jtf.getText().length());
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
}