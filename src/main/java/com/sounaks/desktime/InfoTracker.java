package com.sounaks.desktime;

import java.awt.event.*;
import javax.swing.JOptionPane;
import java.util.Vector;

class InfoTracker extends WindowAdapter implements ActionListener
{
	ChooserBox chooseBox;
	private InitInfo trackedInfo           = new InitInfo();
	private Vector <TimeBean>trackedAlarms = new Vector<>();

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
				trackedAlarms = chooseBox.applyAlarms();
				trackedInfo   = chooseBox.applySettings();
			}
			catch(Exception exception)
			{
				if(exception.getMessage().equals("Blank name"))
				{
					JOptionPane.showMessageDialog(chooseBox, "Please enter a brief non-blank alarm name", "Empty alarm name", JOptionPane.INFORMATION_MESSAGE);
				}
				else if(exception.getMessage().equals("Blank command"))
				{
					JOptionPane.showMessageDialog(chooseBox, "The command to be executed is empty", "Empty command", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(chooseBox, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			chooseBox.dispose();
		}
		else if(s.equals("CANCEL"))
		{
			trackedInfo   = chooseBox.getExistingInfo();
			trackedAlarms = chooseBox.getExistingAlarms();
			chooseBox.dispose();
		}
	}

	@Override
	public void	windowClosing(WindowEvent windowevent)
	{
		trackedInfo   = chooseBox.getExistingInfo();
		trackedAlarms = chooseBox.getExistingAlarms();
	}

	public InfoTracker getParameters()
	{
		return this;
	}

	public InitInfo getSelectedInformation()
	{
		return trackedInfo;
	}

	public Vector<TimeBean> getSelectedAlarms()
	{
		return trackedAlarms;
	}
}