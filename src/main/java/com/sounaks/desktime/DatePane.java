package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class DatePane extends JComponent implements ActionListener
{
	private final String[] months = {"January", "February",
									"March", "April", "May",
									"June", "July", "August",
									"September", "October",
									"November", "December"};
	private final Insets setin = new Insets(1, 10, 1, 10);
	private JLabel monthlabel;
	private JLabel yearlabel;
	private JPanel calendarpanel;
	private Font titlefont                     = new Font("SansSerif", Font.BOLD, 13);
	private static Vector <Component>compArray = new Vector<Component>();

	protected int selectedday = -1;
	protected int month;
	protected int year;
	protected String dateselection = "";

	public DatePane()
	{
		setLayout(new BorderLayout());

		JPanel controlpanel = new JPanel();
		controlpanel.setLayout(new GridLayout(2, 1));
		
		JPanel  top             = new JPanel(new BorderLayout());
		JPanel  l               = new JPanel();
		JButton prevyearbutton  = new JButton("<<");
		JButton prevmonthbutton = new JButton("<");
		prevyearbutton.setActionCommand("PREVYEAR");
		prevmonthbutton.setActionCommand("PREVMTH");
		prevyearbutton.addActionListener(this);
		prevmonthbutton.addActionListener(this);
		prevyearbutton.setMargin(setin);
		prevmonthbutton.setMargin(setin);
		l.add(prevyearbutton);
		l.add(prevmonthbutton);
		JPanel  r               = new JPanel();
		JButton nextyearbutton  = new JButton(">>");
		JButton nextmonthbutton = new JButton(">");
		nextmonthbutton.setActionCommand("NEXTMTH");
		nextyearbutton.setActionCommand("NEXTYEAR");
		nextmonthbutton.addActionListener(this);
		nextyearbutton.addActionListener(this);
		nextmonthbutton.setMargin(setin);
		nextyearbutton.setMargin(setin);
		r.add(nextmonthbutton);
		r.add(nextyearbutton);
		JPanel m          = new JPanel();
		       monthlabel = new JLabel();
		       yearlabel  = new JLabel();
		monthlabel.setFont(titlefont);
		yearlabel.setFont(titlefont);
		m.add(monthlabel);
		m.add(new JLabel(" "));
		m.add(yearlabel);
		top.add(l, BorderLayout.WEST);
		top.add(m, BorderLayout.CENTER);
		top.add(r, BorderLayout.EAST);

		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(1, 7, 5, 5));

		JLabel sun = new JLabel("Sun", SwingConstants.CENTER);
		sun.setForeground(Color.red);
		bottom.add(sun);
		bottom.add(new JLabel("Mon", SwingConstants.CENTER));
		bottom.add(new JLabel("Tue", SwingConstants.CENTER));
		bottom.add(new JLabel("Wed", SwingConstants.CENTER));
		bottom.add(new JLabel("Thu", SwingConstants.CENTER));
		bottom.add(new JLabel("Fri", SwingConstants.CENTER));
		JLabel sat = new JLabel("Sat", SwingConstants.CENTER);
		sat.setForeground(Color.blue);
		bottom.add(sat);
		controlpanel.add(top);
		controlpanel.add(bottom);

		add(controlpanel, "North");

		calendarpanel = new JPanel(new GridLayout(6, 7, 3, 3));
		add(calendarpanel, "Center");
		setSelectedDate(null);
	}
	
	protected void updateCalendarDisplay(int day, int month, int year)
	{
		monthlabel.setText(months[month]); 
		yearlabel.setText(String.valueOf(year)); 
		calendarpanel.removeAll();
		GregorianCalendar today               = new GregorianCalendar();
		GregorianCalendar selectedday         = new GregorianCalendar(year,month,day);
		GregorianCalendar firstdayofmonth     = new GregorianCalendar(year,month,1);
		GregorianCalendar lastdayofmonth      = new GregorianCalendar(year,month,firstdayofmonth.getActualMaximum(Calendar.DAY_OF_MONTH));
		int               weeksinmonth        = lastdayofmonth.get(Calendar.WEEK_OF_MONTH);
		int               firstdayinfirstweek = firstdayofmonth.get(Calendar.DAY_OF_WEEK);
		ButtonGroup       bg                  = new ButtonGroup();
		for (int i = 1; i < firstdayinfirstweek; i++)
		{
			JToggleButton blank = new JToggleButton("--");
			blank.setEnabled(false);
			blank.setMargin(setin);
			calendarpanel.add(blank);
		}
		//since the roll function starts over, have to do all but the last day 
		//and do last day outside of for loop.
		for (GregorianCalendar currentday = firstdayofmonth; !currentday.equals(lastdayofmonth); currentday.roll(Calendar.DAY_OF_MONTH, 1))
		{
			JToggleButton nextbutton = new JToggleButton(String.valueOf(currentday.get(Calendar.DAY_OF_MONTH)));
			if (currentday.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) nextbutton.setForeground(Color.blue);
			if (currentday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) nextbutton.setForeground(Color.red);
			if (compare(currentday,today)) nextbutton.setForeground(Color.green.darker());
			if (compare(currentday,selectedday)) nextbutton.setSelected(true);
			nextbutton.addActionListener(this);
			nextbutton.setMargin(setin);
			bg.add(nextbutton);
			calendarpanel.add(nextbutton);
		}
		//now do the last day of the month
		JToggleButton lastdaybutton = new JToggleButton(String.valueOf(lastdayofmonth.get(Calendar.DAY_OF_MONTH)));
		if (lastdayofmonth.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) lastdaybutton.setForeground(Color.blue);
		if (lastdayofmonth.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) lastdaybutton.setForeground(Color.red);
		if (compare(lastdayofmonth,today)) lastdaybutton.setForeground(Color.green.darker());
		if (compare(lastdayofmonth,selectedday)) lastdaybutton.setSelected(true);
		lastdaybutton.addActionListener(this);
		lastdaybutton.setMargin(setin);
		bg.add(lastdaybutton);
		calendarpanel.add(lastdaybutton);
		int lastdayinlastweek = lastdayofmonth.get(Calendar.DAY_OF_WEEK);
		for (int i = lastdayinlastweek + 1; i <= (weeksinmonth == 4 ? 21 : (weeksinmonth == 5 ? 14 : 7)); i++)
		{
			JToggleButton blank = new JToggleButton("--");
			blank.setEnabled(false);
			blank.setMargin(setin);
			calendarpanel.add(blank);
		}
		calendarpanel.validate();
	}
	
	public void actionPerformed(ActionEvent actionevent)
	{
		String actionstring = actionevent.getActionCommand();
		if (actionstring.compareTo("PREVMTH") == 0)
		{
			month = month - 1;
			if (month < 0)
			{
				month = 11;
				year  = year - 1;
			}
			updateCalendarDisplay(selectedday == -1 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : selectedday, month, year);
		}
		else if (actionstring.compareTo("NEXTMTH") == 0)
		{
			month = month + 1;
			if (month == 12)
			{
				month = 0;
				year  = year + 1;
			}
			updateCalendarDisplay(selectedday == -1 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : selectedday, month, year);
		}
		else if (actionstring.compareTo("PREVYEAR") == 0)
		{
			year = year - 1;
			updateCalendarDisplay(selectedday == -1 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : selectedday, month, year);
		}
		else if (actionstring.compareTo("NEXTYEAR") == 0)
		{
			year = year + 1;
			updateCalendarDisplay(selectedday == -1 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : selectedday, month, year);
		}
		else
		{
			if (getInt(actionstring) > 0)
			{
				selectedday   = getInt(actionstring);
				dateselection = String.valueOf(month+1) + "/" + String.valueOf(selectedday) + "/" + String.valueOf(year);
				//System.out.println(dateselection);
				fireActionPerformed(actionevent);
			}
		}
	}
	
	public void addActionListener(ActionListener al)
	{
		listenerList.add(ActionListener.class, al);
	}
	
	public void removeActionListener(ActionListener al)
	{
		listenerList.remove(ActionListener.class, al);
	}
	
	protected void fireActionPerformed(ActionEvent eve)
	{
		ActionEvent acEvent     = eve;
		Object     [] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ActionListener.class)
			{
				if (acEvent == null)
					acEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, eve.getActionCommand());
				((ActionListener)listeners[i+1]).actionPerformed(acEvent);
			}
		}
	}

	public String getDateSelection()
	{
		return dateselection;
	}

	public Date getSelectedDate()
	{
		return new GregorianCalendar(getSelectedYear(), getSelectedMonth(), getSelectedDay()).getTime();
	}
	
	public void setSelectedDate(Date newDate)
	{
		if (newDate == null)
		{
			GregorianCalendar today       = new GregorianCalendar();
			                  selectedday = today.get(Calendar.DAY_OF_MONTH);
			                  month       = today.get(Calendar.MONTH);
			                  year        = today.get(Calendar.YEAR);
		}
		else
		{
			Calendar newTime = new GregorianCalendar();
			newTime.setTimeInMillis(newDate.getTime());
			selectedday = newTime.get(Calendar.DAY_OF_MONTH);
			month       = newTime.get(Calendar.MONTH);
			year        = newTime.get(Calendar.YEAR);
		}
		updateCalendarDisplay(selectedday, month, year);
	}

	public int getSelectedMonth()
	{
		return month;
	}

	public int getSelectedDay()
	{
		return selectedday;
	}

	public int getSelectedYear()
	{
		return year;
	}
	
	public void setBackground(Color bgColor, boolean paintButton)
	{
		super.setBackground(bgColor);
		Vector<Component> comps = getAllComponents(this);
		for (int i = 0; i < comps.size(); i++)
		{
			Component tmp = comps.elementAt(i);
			if ((tmp instanceof AbstractButton) && paintButton)
				tmp.setBackground(bgColor);
			else if (tmp instanceof JPanel)
				((JPanel)tmp).setBackground(bgColor);
			else if (tmp instanceof JLabel)
			{
				((JLabel)tmp).setOpaque(true);
				((JLabel)tmp).setBackground(bgColor);
			}
		}
	}
	
	private void fillCompArray(Component cont[])
	{
		if (cont.length == 0) return;
		Component tempArray[] = null;
		for (int x = 0; x < cont.length; x++)
		{
			if (cont[x] instanceof Container)
			{
				tempArray = ((Container)cont[x]).getComponents();
				compArray.addElement(cont[x]);
				fillCompArray(tempArray);
			}
		}
	}

	public Vector<Component> getAllComponents(Container topContainer)
	{
		fillCompArray(topContainer.getComponents());
		return compArray;
	}
	
	public int getInt(String s)
	{
		int n = -1;
		try
		{
			n = Integer.parseInt(s);
		}
		catch (NumberFormatException nfe)
		{
			n = -1;
			//System.err.println("NumberFormatException: " + nfe.getMessage());
		}
		return n;
	}

	private boolean compare(GregorianCalendar first, GregorianCalendar second)
	{
		return ((first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH))
							&& (first.get(Calendar.MONTH) == second.get(Calendar.MONTH))
							&& (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)));
	}

	public static void main(String[] args)
	{
		JFrame   dialog       = new JFrame("Calendar");
		DatePane dateselector = new DatePane();
		dialog.add(dateselector);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		dialog.setVisible(true);
	}
}