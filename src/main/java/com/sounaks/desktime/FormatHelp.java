package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class FormatHelp extends JDialog
	implements ActionListener
{

	public FormatHelp(Dialog dialog)
	{
		super(dialog);
		getContentPane().setLayout(new BorderLayout());
		setTitle("Format Help");
		JLabel jlabel = new	JLabel("<html><font	size=5 color=blue>Time Format Symbols</font></html>", 0);
		getContentPane().add(jlabel, "North");
        JTextArea jtextarea = new JTextArea("Symbol   Meaning                 Presentation        Example\n------   -------                 ------------        -------\n  G      era designator          (Text)              AD\n  y      year                    (Number)            1996\n  M      month in year           (Text & Number)     July & 07\n  d      day in month            (Number)            10\n  h      hour in am/pm (1~12)    (Number)            12\n  H      hour in day (0~23)      (Number)            0\n  m      minute in hour          (Number)            30\n  s      second in minute        (Number)            55\n  S      millisecond             (Number)            978\n  E      day in week             (Text)              Tuesday\n  D      day in year             (Number)            189\n  F      day of week in month    (Number)            2 (2nd Wed in July)\n  w      week in year            (Number)            27\n  W      week in month           (Number)            2\n  a      am/pm marker            (Text)              PM\n  k      hour in day (1~24)      (Number)            24\n  K      hour in am/pm (0~11)    (Number)            0\n  z      time zone               (Text)              Pacific Standard Time & PDT\n  '      escape for text         (Delimiter)\n  ''     single quote            (Literal)           '");
        jtextarea.setEditable(false);
		jtextarea.setFont(Font.decode("Monospaced"));
		getContentPane().add(jtextarea,	"Center");
		close = new	JButton("Close");
		close.addActionListener(this);
		getContentPane().add(close,	"South");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		Dimension dimension	= Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dimension.width - getWidth()) / 2,	(dimension.height -	getHeight()) / 2);
		setModal(true);
		setVisible(true);
	}

	public void	actionPerformed(ActionEvent	actionevent)
	{
		if(actionevent.getSource().equals(close))
			dispose();
	}

	JButton	close;
}