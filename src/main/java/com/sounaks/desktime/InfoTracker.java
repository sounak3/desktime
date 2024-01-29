package com.sounaks.desktime;

import java.awt.event.*;
import javax.swing.JOptionPane;
import java.util.Vector;

class InfoTracker extends WindowAdapter implements ActionListener
{
	ChooserBox chooseBox;
	public InitInfo INFORMATION=new InitInfo();
	public Vector <TimeBean>ALARMS=new Vector<TimeBean>();

	public InfoTracker(ChooserBox chooserbox)
	{
		chooseBox =	chooserbox;
	}

	public void	actionPerformed(ActionEvent	actionevent)
	{
		String s = actionevent.getActionCommand();
		if(s.equals("OK"))
		{
			try
			{
				ALARMS = chooseBox.applyAlarms();
				INFORMATION = chooseBox.applySettings();
			}
			catch(Exception exception)
			{
				if(exception.getMessage().equals("Blank name"))
				{
					JOptionPane.showMessageDialog(chooseBox,"Please enter a brief non-blank alarm name","Empty alarm name",JOptionPane.INFORMATION_MESSAGE);
				}
				else if(exception.getMessage().equals("Blank command"))
				{
					JOptionPane.showMessageDialog(chooseBox,"The command to be executed is empty","Empty command",JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(chooseBox, exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			chooseBox.dispose();
		}
		else if(s.equals("CANCEL"))
		{
			INFORMATION = chooseBox.information;
			ALARMS = chooseBox.data;
			chooseBox.dispose();
		}
	}

	public void	windowClosing(WindowEvent windowevent)
	{
		INFORMATION = chooseBox.information;
		ALARMS = chooseBox.data;
	}

	public InfoTracker getParameters()
	{
		return this;
	}
}