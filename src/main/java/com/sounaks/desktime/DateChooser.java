package com.sounaks.desktime;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class DateChooser extends JComponent implements ActionListener
{
	protected JButton arrowButton;
	protected JFormattedTextField inputField;
	protected DatePane pane;
	private JWindow forPane=null;
	protected JDialog parentDlg;
	private Component glass;
	
	protected boolean showing;
	protected SimpleDateFormat ownFormat           = new SimpleDateFormat("EEEE',' MMM dd',' yyyy ");
	private   final transient ButtonIcon upArrow   = new ButtonIcon(ButtonIcon.UP_ARROW_SMALL, Color.BLACK);
	private   final transient ButtonIcon downArrow = new ButtonIcon(ButtonIcon.DOWN_ARROW_SMALL, Color.BLACK);
	private transient WindowHandler handler;
	
	public DateChooser()
	{
		inputField = new JFormattedTextField(ownFormat);
		inputField.setPreferredSize(new Dimension(160, 20));
		arrowButton= new JButton(downArrow);
		arrowButton.setActionCommand("POPUP");
		arrowButton.addActionListener(this);
		arrowButton.setPreferredSize(new Dimension(20, 20));
		pane = new DatePane();
		pane.setBorder(BorderFactory.createLineBorder(Color.black));
		pane.addActionListener(this);
		GridBagLayout lay = new GridBagLayout();
		setLayout(lay);
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.90, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0 ,0);
		lay.setConstraints(inputField, gbc);
		add(inputField);
		gbc = new GridBagConstraints(2, 0, 1, 1, 0.10, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
		lay.setConstraints(arrowButton, gbc);
		add(arrowButton);
		showing = false;
		inputField.setBorder(null);
		arrowButton.setBorder(null);
		arrowButton.setBorderPainted(false);
		setBorder(BorderFactory.createEtchedBorder());
		inputField.setFont(arrowButton.getFont());
		inputField.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public void setBackground(Color color, boolean paintButton)
	{
		Component[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
		{
			if ((comps[i] instanceof JButton) && paintButton)
				comps[i].setBackground(color);
			else if (comps[i] instanceof JTextField)
				((JTextField)comps[i]).setBackground(color);
		}
	}
	
	public Date getDate()
	{
		Date   temp = new Date();
		String tmp  = ownFormat.format(temp);
		try
		{
			tmp=inputField.getText();
			temp=ownFormat.parse(tmp, new ParsePosition(0));
		}
		catch (NullPointerException ne)
		{
			inputField.setText(ownFormat.format(temp));
		}
		return temp == null ? new Date() : temp;
	}
	
	public void setDate(Date date) //Assuring that input=Date() object.
	{								//And no parse exception will be thrown.
		try
		{
			inputField.setText(ownFormat.format(date));
			inputField.commitEdit();
		}
		catch (ParseException pse)
		{
			// Not implemented as not required.
		}
	}
	
	public SimpleDateFormat getFormat()
	{
		return ownFormat;
	}
	
	protected void showHide(boolean show)
	{
		if (show && forPane == null)
		{
			arrowButton.setIcon(upArrow);
			pane.setSelectedDate(getDate());
			parentDlg  = (JDialog)SwingUtilities.windowForComponent(this);
			forPane = new JWindow(parentDlg);
			forPane.add(pane);
			forPane.pack();
			setCurLocation(forPane);
			forPane.setVisible(show);
			forPane.setFocusCycleRoot(true);
			glass=parentDlg.getGlassPane();
			if (glass != null) glass.setVisible(show);
			handler = new WindowHandler();
			glass.addMouseListener(handler);
			addAncestorListener(handler);
			parentDlg.addWindowListener(handler);
			parentDlg.addWindowStateListener(handler);
		}
		else
		{
			try
			{
				arrowButton.setIcon(downArrow);
				showing = false;
				forPane.setVisible(false);
				forPane.dispose();
				forPane = null;
				glass.removeMouseListener(handler);
				glass.setVisible(show);
				parentDlg.setFocusCycleRoot(true);
				arrowButton.requestFocusInWindow();
				arrowButton.requestFocus();
				removeAncestorListener(handler);
				parentDlg.removeWindowListener(handler);
				parentDlg.removeWindowStateListener(handler);
				handler = null;
			}
			catch (Exception rme)
			{
				System.out.println("Already removed.");
			}
		}
	}
	
	protected void setCurLocation(Component pop)
	{
		Dimension screen   = Toolkit.getDefaultToolkit().getScreenSize();
		Point     location = new Point();
		          location = inputField.getLocation(location);
		SwingUtilities.convertPointToScreen(location,this);
		if (location.y + pop.getHeight() + inputField.getHeight() > screen.getHeight())
			pop.setLocation(location.x, location.y - pop.getHeight());
		else
			pop.setLocation(location.x, location.y + inputField.getHeight());
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		arrowButton.setEnabled(enabled);
		inputField.setEnabled(enabled);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getActionCommand().equals("POPUP"))
		{
			showing = !showing;
			showHide(showing);
		}
		if (pane.getInt(ae.getActionCommand()) > 0)
		{
			Date date = pane.getSelectedDate();
			inputField.setText(ownFormat.format(date));
			showHide(false);
		}
	}
	
	class WindowHandler extends WindowAdapter implements AncestorListener, MouseListener
	{
		@Override
		public void windowIconified(WindowEvent we)
		{
			showHide(false);
		}

		@Override
		public void windowStateChanged(WindowEvent we)
		{
			showHide(false);
		}

		@Override
		public void windowDeactivated(WindowEvent we)
		{
			showHide(false);
		}
		
		@Override
		public void ancestorMoved(AncestorEvent ace)
		{
			showHide(false);
		}
		
		@Override
		public void ancestorAdded(AncestorEvent ace)
		{
			// Not implemented as not required.
		}

		@Override
		public void ancestorRemoved(AncestorEvent ace)
		{
			// Not implemented as not required.
		}
		
		@Override
		public void mousePressed(MouseEvent me)
		{
			showHide(false);
		}
		
		@Override
		public void mouseReleased(MouseEvent me)
		{
			// Not implemented as not required.
		}

		@Override
		public void mouseClicked(MouseEvent me)
		{
			// Not implemented as not required.
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
			// Not implemented as not required.
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			// Not implemented as not required.
		}
	}
}