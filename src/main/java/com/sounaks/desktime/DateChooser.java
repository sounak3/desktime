package com.sounaks.desktime;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class DateChooser extends JComponent implements ActionListener
{
	protected JButton BUTTON;
	protected JFormattedTextField TEXTFIELD;
	protected DatePane pane;
	private JWindow forPane=null;
	protected JDialog parent;
	private Component glass;
	
	private Point location;
	protected boolean showing;
	protected SimpleDateFormat OWN_FORMAT=new SimpleDateFormat("EEEE',' MMM dd',' yyyy ");
	private WindowHandler handler;
	
	public DateChooser()
	{
		TEXTFIELD=new JFormattedTextField(OWN_FORMAT);
		TEXTFIELD.setPreferredSize(new Dimension(160,20));
		BUTTON= new JButton(new DownArrowIcon());
		//BUTTON= new JButton("\u25BC");
		BUTTON.setActionCommand("POPUP");
		BUTTON.addActionListener(this);
		BUTTON.setPreferredSize(new Dimension(20,20));
		pane=new DatePane();
		pane.setBorder(BorderFactory.createLineBorder(Color.black));
		pane.addActionListener(this);
		GridBagLayout lay=new GridBagLayout();
		setLayout(lay);
		GridBagConstraints gbc=new GridBagConstraints(0, 0, 2, 1, 0.90, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0 ,0);
		lay.setConstraints(TEXTFIELD,gbc);
		add(TEXTFIELD);
		gbc=new GridBagConstraints(2, 0, 1, 1, 0.10, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
		lay.setConstraints(BUTTON,gbc);
		add(BUTTON);
		showing=false;
		TEXTFIELD.setBorder(null);
		BUTTON.setBorder(null);
		BUTTON.setBorderPainted(false);
		setBorder(BorderFactory.createEtchedBorder());
		TEXTFIELD.setFont(BUTTON.getFont());
		TEXTFIELD.setHorizontalAlignment(JTextField.RIGHT);
	}

	public void setBackground(Color color, boolean paintButton)
	{
		Component comps[]=getComponents();
		for(int i=0;i<comps.length;i++)
		{
			if((comps[i] instanceof JButton) && paintButton)
				comps[i].setBackground(color);
			else if(comps[i] instanceof JTextField)
				((JTextField)comps[i]).setBackground(color);
		}
	}
	
	public Date getDate()
	{
		Date temp=new Date();
		String tmp=OWN_FORMAT.format(temp);
		try
		{
			tmp=TEXTFIELD.getText();
			temp=OWN_FORMAT.parse(tmp,new ParsePosition(0));
		}
		catch(NullPointerException ne)
		{
			TEXTFIELD.setText(OWN_FORMAT.format(temp));
		}
		return temp==null?new Date():temp;
	}
	
	public void setDate(Date date) //Assuring that input=Date() object.
	{								//And no parse exception will be thrown.
		try
		{
			TEXTFIELD.setText(OWN_FORMAT.format(date));
			TEXTFIELD.commitEdit();
		}
		catch(ParseException pse){}
	}
	
	public SimpleDateFormat getFormat()
	{
		return OWN_FORMAT;
	}
	
	protected void showHide(boolean show)
	{
		if(show && forPane==null)
		{
			pane.setSelectedDate(getDate());
			parent=(JDialog)SwingUtilities.windowForComponent(this);
			forPane=new JWindow(parent);
			forPane.add(pane);
			forPane.pack();
			setCurLocation(this,forPane);
			forPane.setVisible(show);
			forPane.setFocusCycleRoot(true);
			glass=parent.getGlassPane();
			if(glass != null) glass.setVisible(show);
			handler=new WindowHandler();
			glass.addMouseListener(handler);
			addAncestorListener(handler);
			parent.addWindowListener(handler);
			parent.addWindowStateListener(handler);
		}
		else
		{
			try
			{
				showing=false;
				forPane.setVisible(false);
				forPane.dispose();
				forPane=null;
				glass.removeMouseListener(handler);
				glass.setVisible(show);
				parent.setFocusCycleRoot(true);
				BUTTON.requestFocusInWindow();
				BUTTON.requestFocus();
				removeAncestorListener(handler);
				parent.removeWindowListener(handler);
				parent.removeWindowStateListener(handler);
				handler=null;
			}
			catch(Exception rme)
			{
				System.out.println("Already removed.");
			}
		}
	}
	
	protected void setCurLocation(Component master, Component pop)
	{
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
		location=new Point();
		location=TEXTFIELD.getLocation(location);
		SwingUtilities.convertPointToScreen(location,this);
		if(location.y+pop.getHeight()+TEXTFIELD.getHeight() > screen.getHeight())
			pop.setLocation(location.x,location.y-pop.getHeight());
		else
			pop.setLocation(location.x,location.y+TEXTFIELD.getHeight());
	}
	
	public void setEnabled(boolean enabled)
	{
		BUTTON.setEnabled(enabled);
		TEXTFIELD.setEnabled(enabled);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("POPUP"))
		{
			showing=!showing;
			showHide(showing);
		}
		if(pane.getInt(ae.getActionCommand())>0)
		{
			Date date=pane.getSelectedDate();
			TEXTFIELD.setText(OWN_FORMAT.format(date));
			showHide(false);
		}
	}
	
	class WindowHandler extends WindowAdapter implements AncestorListener, MouseListener
	{
		public void windowIconified(WindowEvent we)
		{
			showHide(false);
		}
		
		public void windowStateChanged(WindowEvent we)
		{
			showHide(false);
		}
		
		public void windowDeactivated(WindowEvent we)
		{
			showHide(false);
		}
		
		public void ancestorMoved(AncestorEvent ace)
		{
			showHide(false);
		}
		
		public void ancestorAdded(AncestorEvent ace)
		{
		}		
		public void ancestorRemoved(AncestorEvent ace)
		{
		}
		
		public void mousePressed(MouseEvent me)
		{
			showHide(false);
		}
		
		public void mouseReleased(MouseEvent me)
		{
		}
		public void mouseClicked(MouseEvent me)
		{
		}
		public void mouseEntered(MouseEvent me)
		{
		}
		public void mouseExited(MouseEvent me)
		{
		}
	}
}