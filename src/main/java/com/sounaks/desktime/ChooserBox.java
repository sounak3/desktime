package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.border.*;

public class ChooserBox extends JDialog implements ActionListener, ItemListener, ListSelectionListener, ChangeListener
{
	private JTabbedPane tabPane;
	private ListChooser font,style,size;
	private JButton setFont,resetFont,selFontCol,resFontCol;
	private TLabel fontPreview;
	private BorderPreview borderPreview;
	private JRadioButton bRaised,bLowered,eRaised,eLowered,lBorder;
	private JCheckBox nBorder,fixPlace,enTooltip,nativeLook;
	private JButton selLineCol,resLineCol;
	private JRadioButton curTimeZone,GMTimeZone,sysUpTime;
	private JTextField sysDateFormat,gmtDateFormat,uSymbol,hSymbol,mSymbol,sSymbol;
	private JButton resetDefs,helpFormat;
	private JLabel transSlide;
	private JSlider transLevel;
	private JCheckBox useImg,useCol,useTrans;
	private FileList fileList;
	private JButton selectDir,selBackCol,resBackCol;
	private TLabel picLabel;
	private JRadioButton rbHtile,rbTile,rbVtile,rbCenter,rbFit,rbStretch;
	private JList alarmList;
	private JLabel aboutAlarm;
	private JButton add,remove,edit,test,browse;
	private JPanel bottomCards;
	private JComboBox<String> period, dateOrWeek;
	private JCheckBox rept,runCom,runMsg,runSnd;
	private JTextField cmdToRun, alarmName;
	private JRadioButton option1, option2;
	private JSpinner timeSpinner1, timeSpinner2;
	private DateChooser choosefrom;
	private int opmode   = 0;
	private int selIndex = -1;
	private JButton ok,cancel;
	public Vector <TimeBean>data;
	public InitInfo information;

	public ChooserBox(InitInfo initinfo, Vector <TimeBean>alarmData)
	{
		GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		information = initinfo;  //Transfering information from Desktop to this;
		getContentPane().setLayout(new BorderLayout());
		tabPane     = new JTabbedPane();
		//All configuration for Font Panel as follows:
		JPanel  jpanel = new JPanel(new GridBagLayout());
		setFont    	   = new JButton("Preview Font");
		resetFont  	   = new JButton("Reset Font");
		Integer ainteger[] = new Integer[20];
		int     ai[]       = {6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 
								24, 28, 32, 36, 40, 48, 56, 64, 72, 80};
		for (int i = 0; i < ai.length; i++)
			ainteger[i] = new Integer(ai[i]);
		
		String as1[] = {"Plain", "Bold", "Italic", "Bold Italic"};
		String as[]  = graphicsenvironment.getAvailableFontFamilyNames();
		font         = new ListChooser(as, "Font");
		style        = new ListChooser(as1, "Style");
		size         = new ListChooser(ainteger, "Size");
		selFontCol   = new JButton("Choose Font Color");
		resFontCol   = new JButton("Default");
		fontPreview  = new TLabel("AaBbCc...0123...!#@%&$");
		fontPreview.setBackground(Color.white);
		JScrollPane jscrollpane = new JScrollPane();
		jscrollpane.getViewport().setView(fontPreview);
		jscrollpane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font Configuration"));
		//All configurations for Border Panel as follows:
		JPanel jpanel1 = new JPanel(new GridBagLayout());
		borderPreview  = new BorderPreview();
		ButtonGroup buttongroup = new ButtonGroup();
		bRaised  = new JRadioButton("Beveled Raised Border");
		bLowered = new JRadioButton("Beveled Lowered Border");
		eRaised  = new JRadioButton("Etched Raised Border");
		eLowered = new JRadioButton("Etched Lowered Border");
		lBorder  = new JRadioButton("Line Border");
		nBorder  = new JCheckBox("Don't Use Any Border");
		buttongroup.add(bRaised);
		buttongroup.add(bLowered);
		buttongroup.add(eRaised);
		buttongroup.add(eLowered);
		buttongroup.add(lBorder);
		buttongroup.add(nBorder);
		JSeparator jseparator  = new JSeparator();
		Component component    = Box.createHorizontalStrut(8);
		selLineCol = new JButton("Line Border Color");
		resLineCol = new JButton("Default Line Color");
		JSeparator jseparator1 = new JSeparator();
		fixPlace   = new JCheckBox("Fix to Place");
		enTooltip  = new JCheckBox("Enable \"Mouse-Over-Info\" Help");
		nativeLook = new JCheckBox("Check to view this dialog box in system's look and feel");
		jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Looks and Borders"));
		//All configurations for Time Conf. Panel as follows:
		JPanel jpanel2 = new JPanel(new GridBagLayout());
		curTimeZone = new JRadioButton("<html>System's present time zone. This setting displays your current time zone i.e. time of your location in a format given below.</html>");
		GMTimeZone  = new JRadioButton("<html>GMT zone. This setting displays the world standard referance time i.e. Greenwich Mean Time (GMT) in a format given below.</html>");
		sysUpTime   = new JRadioButton("<html>System Up-Time. This setting displays the time elapsed since your system had started without a log off or shut down.</html>");
		ButtonGroup buttongroup1 = new ButtonGroup();
		buttongroup1.add(curTimeZone);
		buttongroup1.add(GMTimeZone);
		buttongroup1.add(sysUpTime);
		Component component1 = Box.createHorizontalStrut(12);
		JLabel jlabel  = new JLabel("System Date/Time Format");
		sysDateFormat  = new JTextField(17);
		JSeparator jseparator2 = new JSeparator();
		JLabel jlabel1 = new JLabel("GMT Date/Time Format");
		gmtDateFormat  = new JTextField(17);
		JSeparator jseparator3 = new JSeparator();
		JLabel jlabel2 = new JLabel("Up-time Symbol");
		JLabel jlabel3 = new JLabel("Hour Symbol");
		JLabel jlabel4 = new JLabel("Minute Symbol");
		JLabel jlabel5 = new JLabel("Second Symbol");
		uSymbol = new JTextField(7);
		hSymbol = new JTextField(7);
		mSymbol = new JTextField(7);
		sSymbol = new JTextField(7);
		JSeparator jseparator4 = new JSeparator();
		resetDefs  = new JButton("Reset To Defaults");
		helpFormat = new JButton("Help On Format");
		jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Time Display Configuration"));
		//All configurations for Background Panel as follows:
		JPanel jpanel3 = new JPanel(new GridBagLayout());
		useImg   = new JCheckBox("<html>Use background image. Use image settings from the following:</html>");
		useCol   = new JCheckBox("<html>Use Background Color. Use color settings from the following:</html>");
		useTrans = new JCheckBox("<html>Use transparent background (may cause flickers)</html>");
		ButtonGroup buttongroup2 = new ButtonGroup();
		buttongroup2.add(useImg);
		buttongroup2.add(useCol);
		buttongroup2.add(useTrans);
		Component component2 = Box.createHorizontalStrut(12);
		Component component3 = Box.createVerticalStrut(70);
		fileList = new FileList(null);
		fileList.setVisibleRowCount(7);
		fileList.setSelectionMode(0);
		fileList.setValueIsAdjusting(true);
		fileList.addListSelectionListener(this);
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.getViewport().setView(fileList);
		selectDir = new JButton("Choose Directory");
		picLabel  = new TLabel("No Preview", null, 16);
		picLabel.setBorder(BorderFactory.createBevelBorder(1));
		rbHtile      = new JRadioButton("Tile Vertically");
		rbVtile      = new JRadioButton("Tile Horizontally");
		rbTile       = new JRadioButton("Tile");
		rbStretch    = new JRadioButton("Stretch to Fit");
		rbFit        = new JRadioButton("Resize to Fit");
		rbCenter     = new JRadioButton("Center");
		ButtonGroup buttongroup3 = new ButtonGroup();
		buttongroup3.add(rbTile);
		buttongroup3.add(rbHtile);
		buttongroup3.add(rbVtile);
		buttongroup3.add(rbCenter);
		buttongroup3.add(rbFit);
		buttongroup3.add(rbStretch);
		selBackCol = new JButton("Choose Background Color");
		resBackCol = new JButton("Default Color");
		transSlide = new JLabel("Opacity [ Low \u2192 High ]");
		transLevel = new JSlider(4, 20, 10);
		transSlide.setLabelFor(transLevel);
		transLevel.setMajorTickSpacing(1);
		transLevel.setSnapToTicks(true);
		transLevel.addChangeListener(this);
		jpanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Background Configuration"));
		//All configuration for Alarm Panel as follows:
		JPanel      jpanel5 = new JPanel(new GridBagLayout());
		JPanel      topList = new JPanel(new GridBagLayout());
		JScrollPane jsp     = new JScrollPane();
		data      = new Vector<TimeBean>();
		alarmList = new JList(data);
		alarmList.setCellRenderer(new AlarmListCellRenderer());
		alarmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jsp.setViewportView(alarmList);
		alarmList.addListSelectionListener(this);
		add         = new JButton("Add");
		edit        = new JButton("Edit");
		remove      = new JButton("Remove");
		test        = new JButton("Test >>");
		bottomCards = new JPanel(new CardLayout());
		aboutAlarm  = new JLabel("");
		aboutAlarm.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
		setDescriptionText();
		JPanel settingsPane = new JPanel(new GridBagLayout());
		JLabel l1    = new JLabel("Alarm Name",JLabel.CENTER);
		alarmName    = new JTextField();
		alarmName.setToolTipText("<html>Please enter a brief description of the alarm such as:<p> &quot;John's Birthday&quot;</html>");
		ButtonGroup bgr = new ButtonGroup();
		option1         = new JRadioButton("Start on date");
		bgr.add(option1);
		choosefrom   = new DateChooser();
		timeSpinner1 = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dit1 = new JSpinner.DateEditor(timeSpinner1,"hh:mm a");
		timeSpinner1.setEditor(dit1);
		rept=new JCheckBox("and continue");
		String pds[]=new String[]{"Never", "Minutely", "Hourly", "Daily", "Weekly", "Monthly", "Yearly"};
		period=new JComboBox<String>(pds);
		period.addItemListener(this);
		JLabel label3 = new JLabel("referring");
		String dow[]  = new String[]{"Date", "Week"};
		dateOrWeek    = new JComboBox<String>(dow);
		dateOrWeek.setEnabled(false);
		option2 = new JRadioButton("Start at");
		bgr.add(option2);
		timeSpinner2             = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dit2 = new JSpinner.DateEditor(timeSpinner2,"HH:mm");
		timeSpinner2.setEditor(dit2);
		JLabel label4 = new JLabel(" hours after program start-up");
		JLabel label2 = new JLabel("On Alarm",JLabel.CENTER);
		runCom        = new JCheckBox("Start Command");
		runSnd        = new JCheckBox("Just Beep");
		runMsg        = new JCheckBox("Message");
		cmdToRun      = new JTextField(10);
		browse        = new JButton("Browse...");
		bottomCards.add(aboutAlarm, "FinishEdit");
		bottomCards.add(settingsPane,  "AddEdit");
		//For bottom panel of ok/cancel buttons.
		JPanel jpanel4 = new JPanel(new FlowLayout(2));
		JPanel grid    = new JPanel(new GridLayout(1, 0, 20, 5));
		ok             = new JButton("Ok");
		getRootPane().setDefaultButton(ok);
		ok.setActionCommand("OK");
		cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		try
		{
			ExUtils.addComponent(jpanel, font, 0, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel, style, 1, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel, size, 2, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel, setFont, 0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel, resetFont, 1, 1, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel, selFontCol, 0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel, resFontCol, 1, 2, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel, jscrollpane, 0, 3, 3, 1, 1.0D, 0.6D, this);
			ExUtils.addComponent(jpanel1, nBorder, 0, 0, 3, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bRaised, 0, 2, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, borderPreview, 2, 2, 3, 4, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bLowered, 0, 3, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eRaised, 0, 4, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eLowered, 0, 5, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator, 0, 6, 3, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, lBorder, 0, 7, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, component, 0, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, selLineCol, 1, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, resLineCol, 2, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator1, 0, 9, 3, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, fixPlace, 0, 10, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, enTooltip, 2, 10, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, nativeLook, 0, 11, 3, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, curTimeZone, 0, 0, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, component1, 0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel, 1, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sysDateFormat, 2, 1, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator2, 1, 2, 4, 1, 1.0D, 0.2D, this);
			ExUtils.addComponent(jpanel2, GMTimeZone, 0, 3, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel1, 1, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, gmtDateFormat, 2, 4, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator3, 1, 5, 4, 1, 1.0D, 0.2D, this);
			ExUtils.addComponent(jpanel2, sysUpTime, 0, 6, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel2, 1, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, uSymbol, 2, 7, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel3, 3, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, hSymbol, 4, 7, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel4, 1, 8, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, mSymbol, 2, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jlabel5, 3, 8, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sSymbol, 4, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator4, 1, 9, 4, 1, 1.0D, 0.2D, this);
			ExUtils.addComponent(jpanel2, resetDefs, 1, 10, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, helpFormat, 3, 10, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useImg, 		0, 0, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component2, 	0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, selectDir, 	1, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, picLabel, 	2, 1, 2, 2, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, component3,	0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, jscrollpane1, 1, 2, 1, 1, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, rbTile, 		1, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbHtile, 		2, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbVtile, 		3, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbCenter, 	1, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbFit, 		2, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbStretch, 	3, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useTrans, 	0, 5, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useCol, 		0, 6, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, selBackCol, 	1, 7, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, resBackCol, 	3, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, transSlide, 	1, 8, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, transLevel, 	2, 8, 3, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(topList,jsp,			0, 0, 1,	4, 1.0D, 1.0D, this);
			ExUtils.addComponent(topList,add,			1, 0, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList,edit,			1, 1, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList,remove,		1, 2, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList,test,			1, 3, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(settingsPane, l1,			0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, alarmName,	1, 0, 4,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, option1,		0, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, choosefrom,	1, 1, 3,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, timeSpinner1,4, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, option2,		0, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, timeSpinner2,1, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, label4,		2, 2, 3,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, rept,		0, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, period,		1, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, label3,		2, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, dateOrWeek,	3, 3, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, label2,		0, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, runCom,		1, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, runSnd,		2, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, runMsg,		3, 4, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, cmdToRun,	0, 5, 3,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(settingsPane, browse,		3, 5, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, topList,			0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, bottomCards,		0, 1, 1,	1, 0.0D, 0.0D, this);
		}
		catch (AWTException awtexception)
		{
			System.out.println("General AWT Exception has occurred.");
			awtexception.printStackTrace();
		}

		tabPane.add("Foreground", jpanel);
		tabPane.add("Background", jpanel3);
		tabPane.add("Border & UI", jpanel1);
		tabPane.add("Time View", jpanel2);
		tabPane.add("Alarms", jpanel5);
		getContentPane().add(tabPane, "Center");
		grid.add(ok);
		grid.add(cancel);
		jpanel4.add(grid);
		getContentPane().add(jpanel4, "South");
		initialize(information, alarmData);
	}

	public static InfoTracker showDialog(String s, int i, InitInfo initinfo, Vector <TimeBean>alarmData)
	{
		ChooserBox chooserbox = new ChooserBox(initinfo, alarmData);
		chooserbox.setTitle(s);
		InfoTracker infotracker = new InfoTracker(chooserbox);
		chooserbox.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		chooserbox.revalidate();
		chooserbox.pack();
		chooserbox.setSize(430, 470);
		chooserbox.setResizable(false);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		chooserbox.setLocation((dimension.width - chooserbox.getWidth()) / 2, (dimension.height - chooserbox.getHeight()) / 2);
		chooserbox.addWindowListener(infotracker);
		chooserbox.ok.addActionListener(infotracker);
		chooserbox.cancel.addActionListener(infotracker);
		chooserbox.tabPane.setSelectedIndex(i);
		chooserbox.setModal(true);
		chooserbox.setVisible(true);
		return infotracker;
	}

	public void initialize(InitInfo initinfo, Vector <TimeBean>alarmData) //Called by->constructor->showDialog
	{
		setFontIn(initinfo.getFont());
		fontPreview.setFont(initinfo.getFont());
		Color color = initinfo.getForeground();
		selFontCol.setBackground(color);
		fontPreview.setForeground(color);
		selFontCol.setForeground(Math.abs(color.getRGB()) >= 0x800000 ? Color.white : Color.black);
		useImg.setSelected(initinfo.isUsingImage());
		useCol.setSelected(!initinfo.isUsingImage());
		File imgFile = initinfo.getImageFile();
		fileList.setDirectory(imgFile);
		int selectIndex = fileList.getNextMatch(imgFile.toString(), 0, Position.Bias.Forward);
		if (selectIndex != -1)
			fileList.setSelectedValue(initinfo.getImageFile(), true);
		else
			fileList.setSelectedIndex(0);
		fileList.ensureIndexIsVisible(fileList.getSelectedIndex());
		useTrans.setSelected(initinfo.hasGlassEffect());
		if (initinfo.getImageFile().isFile())
		{
			picLabel.setBackImage((new ImageIcon(initinfo.getImageFile().toString())).getImage());
			picLabel.setText("");
			setTransparentBackground();
		}
		else
		{
			picLabel.setText("No Preview");
			picLabel.setBackImage(null);
		}
		picLabel.setImagePosition(initinfo.getImageStyle());
		switch (initinfo.getImageStyle())
		{
			case 16: // '\020'
				rbFit.setSelected(true);
				break;
	
			case 4: // '\004'
				rbCenter.setSelected(true);
				break;
	
			case 32: // ' '
				rbTile.setSelected(true);
				break;
	
			case 8: // '\b'
				rbStretch.setSelected(true);
				break;
	
			case 1: // '\001'
				rbHtile.setSelected(true);
				break;
	
			case 2: // '\002'
				rbVtile.setSelected(true);
				break;
				
			default:
				rbFit.setSelected(true);
				break;
		}
		color = initinfo.getBackground();
		selBackCol.setBackground(color);
		selBackCol.setForeground(Math.abs(color.getRGB()) >= 0x800000 ? Color.white : Color.black);
		transLevel.setValue(Math.round(initinfo.getOpacity() * 20));
		transSlide.setText("Opacity [ " + Math.round(initinfo.getOpacity() * 100) + " % ]");
		Border border = initinfo.getBorder();
		borderPreview.setPreview(border);
		if (border instanceof BevelBorder)
		{
			if (((BevelBorder)border).getBevelType() == 0)
				bRaised.setSelected(true);
			else
				bLowered.setSelected(true);
		}
		else if (border instanceof EtchedBorder)
		{
			if (((EtchedBorder)border).getEtchType() == 0)
				eRaised.setSelected(true);
			else
				eLowered.setSelected(true);
		}
		else if (border instanceof LineBorder)
		{
			lBorder.setSelected(true);
			Color color1 = ((LineBorder)border).getLineColor();
			selLineCol.setBackground(color1);
			selLineCol.setForeground(Math.abs(color1.getRGB()) >= 0x800000 ? Color.white : Color.black);
		}
		else
		{
			nBorder.setSelected(true);
		}
		fixPlace.setSelected(initinfo.isFixed());
		enTooltip.setSelected(initinfo.hasTooltip());
		nativeLook.setSelected(initinfo.hasNativeLook());
		String s = initinfo.getDisplayMethod();
		if (s.equals("CURTZ"))
			curTimeZone.setSelected(true);
		else if (s.equals("GMTTZ"))
			GMTimeZone.setSelected(true);
		else if (s.equals("UPTIME"))
			sysUpTime.setSelected(true);
		sysDateFormat.setText(initinfo.getThisTimeZoneFormat());
		gmtDateFormat.setText(initinfo.getGMTZoneFormat());
		String s1 = initinfo.getUpTimeFormat();
		String arr[] = s1.split("\'");
		uSymbol.setText(arr[1]);
		hSymbol.setText(arr[3]);
		mSymbol.setText(arr[5]);
		sSymbol.setText(arr.length==8 ? arr[7] : " ");
		data=alarmData;
		alarmList.setListData(data);
		setDescriptionText();
		setOneEnabled();
	}

	private void setDescriptionText()
	{
		Date dte=new Date();
		if (alarmList.getSelectedIndex()==-1)
		{
			aboutAlarm.setText("Click on an alarm list item to view its description.");
		}
		else
		{
			TimeBean         tb     = (TimeBean)data.elementAt(alarmList.getSelectedIndex());
			String           ltext  = "<html>This alarm named \"(alarm name)\" is scheduled to start on (start time) and continue (interval).<p> Its next run will be on (next run). This alarm runs a (run type) at the scheduled time.</html>";
			SimpleDateFormat tmp1   = choosefrom.getFormat();
			SimpleDateFormat tmp2   = ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat();
			String           tmp3[] = {"never", "minutely", "hourly", "daily", "weekly", "??", "yearly", "monthly", "monthly same weekday"};
			int              tmp4   = 0;
			ltext  = ltext.replace("(alarm name)", tb.getName());
			ltext  = ltext.replace("(start time)", tmp1.format(tb.getAlarmTriggerTime()) + " at " + tmp2.format(tb.getAlarmTriggerTime()));
			tmp4   = tb.getAlarmRepeatInterval().intValue();
			ltext  = ltext.replace("(interval)", tmp3[tmp4]);
			ltext  = ltext.replace("(next run)", tmp4 == 0 ? "never" : tmp1.format(tb.getNextAlarmTriggerTime(dte)) + " at " + tmp2.format(tb.getNextAlarmTriggerTime(dte)));
			tmp4   = tb.getAlarmExecutionOutputType().intValue();
			ltext  = ltext.replace("(run type)", ((tmp4 % 2 != 0) ? " command," : "") + (((tmp4 % 3 == 0) || (tmp4 == 2)) ? " sound," : "") + ((tmp4 > 3) ? " message" : ""));
			aboutAlarm.setText(ltext);
		}
	}

	private void setOneEnabled()
	{
		sysDateFormat.setEnabled(curTimeZone.isSelected());
		gmtDateFormat.setEnabled(GMTimeZone.isSelected());
		uSymbol.setEnabled(sysUpTime.isSelected());
		hSymbol.setEnabled(sysUpTime.isSelected());
		mSymbol.setEnabled(sysUpTime.isSelected());
		sSymbol.setEnabled(sysUpTime.isSelected());
		selLineCol.setEnabled(lBorder.isSelected());
		resLineCol.setEnabled(lBorder.isSelected());
		try
		{
			if (nativeLook.isSelected())
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				nativeLook.setText("Uncheck to view this dialog box in cross platform look and feel");
			}
			else
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				nativeLook.setText("Check to view this dialog box in system's own look and feel");
			}
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception exception) { exception.printStackTrace(); }
	}
	
	private void enadesa()
	{
		if (option1.isSelected())
		{
			timeSpinner1.setEnabled(true);
			timeSpinner2.setEnabled(false);
			choosefrom.setEnabled(true);
		}
		else if (option2.isSelected())
		{
			timeSpinner1.setEnabled(false);
			timeSpinner2.setEnabled(true);
			choosefrom.setEnabled(false);
		}
	}

	private void enadesa2(boolean ena)
	{
		if (ena) add.setText("Add");
		else add.setText("Commit");
		if (ena) edit.setText("Edit");
		else edit.setText("Discard");
		remove.setEnabled(ena);
		test.setEnabled(ena);
		alarmList.setEnabled(ena);
	}

	private void setFontIn(Font font1)
	{
		if (font1 == null)
			throw new NullPointerException("The Font Parameter Is Blank.");
		String  s       = font1.getFamily();
		Integer integer = new Integer(font1.getSize());
		int     i       = font1.getStyle();
		if (ExUtils.contains(font1.getFontName(), "bold", true))
			i = 1;
		else if (ExUtils.contains(font1.getFontName(), "italic", true))
			i = 2;
		else if (ExUtils.contains(font1.getFontName(), "italic", true) && ExUtils.contains(font1.getFontName(), "bold", true))
			i = 3;
		font.setSelectedValue(s, true);
		font.setText(s);
		size.setSelectedValue(integer, true);
		size.setText(integer.toString());
		style.setSelectedIndex(i);
		style.setText(style.getSelectedValue().toString());
	}

	private Font setFontOut()
	{
		String s = font.getText();
		int i;
		try
		{
			i = Integer.parseInt(size.getText());
		}
		catch (NumberFormatException numberformatexception)
		{
			i = 10;
			numberformatexception.printStackTrace();
		}
		int j = style.getSelectedIndex();
		Font font1 = new Font(s, j, i);
		return new Font(font1.getFontName(), 0, font1.getSize());
	}

	public Date timeOut()
	{
		Date temp=new Date();
		if (option1.isSelected())
		{
			String           pttn    = choosefrom.getFormat().toPattern()+((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat().toPattern();
			String           maintmp = choosefrom.getFormat().format(choosefrom.getDate())+((JSpinner.DateEditor)timeSpinner1.getEditor()).getTextField().getText();
			SimpleDateFormat sdf     = new SimpleDateFormat(pttn);
			try
			{
				temp = sdf.parse(maintmp,new ParsePosition(0));
			}
			catch (NullPointerException pe)
			{
				System.out.println("Something wrong happened in date "+pe.getMessage());
			}
		}
		else if (option2.isSelected())
		{
			temp = ((JSpinner.DateEditor)timeSpinner2.getEditor()).getModel().getDate();
		}
		GregorianCalendar second0 = new GregorianCalendar();
		second0.setTime(temp);
		second0.set(Calendar.SECOND,0);
		return second0.getTime();
	}
	
	public void timeIn(Date date)
	{
		if (option1.isSelected())
		{
			choosefrom.setDate(date);
			timeSpinner1.setValue(date);
		}
		else if (option2.isSelected())
		{
			timeSpinner2.setValue(date);
		}
	}
	
	public void intervalIn(int inv)
	{
		if (inv == 5) inv = 7;
		if (inv <= 6) period.setSelectedIndex(inv);
		else if (inv > 6 && inv < 9)
		{
			period.setSelectedIndex(5);
			dateOrWeek.setSelectedIndex(inv - 7);
		}
	}
	
	public int intervalOut()
	{
		if (period.getSelectedIndex() == 5)
			return dateOrWeek.getSelectedIndex() + 7;
		else
			return period.getSelectedIndex();
	}
	
	public void timeTypeIn(boolean flag)
	{
		option1.setSelected(!flag);
		option2.setSelected(flag);
		enadesa();
	}
	
	public boolean timeTypeOut()
	{
		enadesa();
		return option2.isSelected();
	}
	
	public void typeIn(int type)
	{
		runCom.setSelected(type % 2 != 0); //for 1;
		runMsg.setSelected(type > 3); //for 4;
		runSnd.setSelected((type % 3 == 0) || (type == 2)); //for 2;
		cmdToRun.setEnabled(type % 2 != 0);
		browse.setEnabled(type % 2 != 0);
	}
	
	public int typeOut()
	{
		int totaltmp = 0;
		if  (runCom.isSelected()) totaltmp += 1;
		if  (runSnd.isSelected()) totaltmp += 2;
		if  (runMsg.isSelected()) totaltmp += 4;
		return totaltmp;
	}

	public InitInfo applySettings() throws Exception
	{
		information.setFont(setFontOut());
		information.setForeground(selFontCol.getBackground());
		information.setUsingImage(useImg.isSelected());
		information.setGlassEffect(useTrans.isSelected());
		if (!fileList.isSelectionEmpty())
		{
			File testfile=(File)fileList.getSelectedValue();
			information.setImageFile(testfile.getAbsolutePath());
		}
		information.setImageStyle(picLabel.getImagePosition());
		information.setBackground(selBackCol.getBackground());
		information.setOpacity((float)transLevel.getValue() / 20);
		information.setBorder(borderPreview.getPreview());
		if (lBorder.isSelected())
			information.setLineColor(selLineCol.getBackground());
		information.setTooltip(enTooltip.isSelected());
		information.setFixed(fixPlace.isSelected());
		information.setNativeLook(nativeLook.isSelected());
		SimpleDateFormat simpledateformat = new SimpleDateFormat();
		if (GMTimeZone.isSelected())
		{
			information.setDisplayMethod("GMTTZ");
			try
			{
				simpledateformat.applyPattern(gmtDateFormat.getText());
			}
			catch (Exception exception)
			{
				throw new IllegalArgumentException("The following error occured in GMT date/time format field:\n" + exception.toString() + "\n" + exception.getMessage() + " in \"" + gmtDateFormat.getText() + "\"");
			}
			information.setGMTZoneFormat(gmtDateFormat.getText());
		}
		else if (curTimeZone.isSelected())
		{
			information.setDisplayMethod("CURTZ");
			try
			{
				simpledateformat.applyPattern(sysDateFormat.getText());
			}
			catch (Exception exception1)
			{
				throw new IllegalArgumentException("The following error occured in System's date/time format field:\n" + exception1.toString() + "\n" + exception1.getMessage() + " in \"" + sysDateFormat.getText() + "\"");
			}
			information.setThisTimeZoneFormat(sysDateFormat.getText());
		}
		else if (sysUpTime.isSelected())
		{
			information.setDisplayMethod("UPTIME");
			try
			{
				simpledateformat.applyPattern("'" + uSymbol.getText() + "'");
			}
			catch (Exception exception2)
			{
				throw new IllegalArgumentException("The following error occured in \"Up-time symbol\" field of Up-time format:\n" + exception2.toString() + "\n" + exception2.getMessage() + " in \"" + uSymbol.getText() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + hSymbol.getText() + "'");
			}
			catch (Exception exception3)
			{
				throw new IllegalArgumentException("The following error occured in \"Hour symbol\" field of Up-time format:\n" + exception3.toString() + "\n" + exception3.getMessage() + " in \"" + hSymbol.getText() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + mSymbol.getText() + "'");
			}
			catch (Exception exception4)
			{
				throw new IllegalArgumentException("The following error occured in \"Minute symbol\" field of Up-time format:\n" + exception4.toString() + "\n" + exception4.getMessage() + " in \"" + mSymbol.getText() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + sSymbol.getText() + "'");
			}
			catch (Exception exception5)
			{
				throw new IllegalArgumentException("The following error occured in \"Second symbol\" field of Up-time format:\n" + exception5.toString() + "\n" + exception5.getMessage() + " in \"" + sSymbol.getText() + "\"");
			}
			information.setUpTimeFormat("'" + uSymbol.getText() + "'HH'" + hSymbol.getText() + "'mm'" + mSymbol.getText() + "'ss'" + sSymbol.getText() + "'");
		}
		return information;
	}
	
	public Vector<TimeBean> applyAlarms() throws NullPointerException
	{
		if (opmode != 0) saveCurrentAlarm();
		return data;
	}
	
	private void setTransparentBackground()
	{
		if (useTrans.isSelected() && ExUtils.checkAWTPermission("createRobot") && ExUtils.checkAWTPermission("readDisplayPixels"))
		{
			useTrans.setSelected(true);
		}
		else
		{
			if (useTrans.isSelected())
				JOptionPane.showMessageDialog(this,"Error while setting transparency property. Cannot set.","System restriction",JOptionPane.ERROR_MESSAGE);
			useTrans.setSelected(false);
		}
	}
	
	private void saveCurrentAlarm() throws NullPointerException
	{
		if (alarmName.getText().equals("") || alarmName.getText().equals(null))
			throw new NullPointerException("Blank name");
		if ((cmdToRun.getText().equals("") || cmdToRun.getText().equals(null)) && runCom.isSelected())
			throw new NullPointerException("Blank command");
		TimeBean tb = new TimeBean();
		tb.setName(alarmName.getText());
		tb.setSystemStartTimeBasedAlarm(timeTypeOut()); // must be set before setRuntime();
		if (timeTypeOut()) tb.setNextAlarmTriggerTime(timeOut());//if op2 then nextRuntime
		else tb.setAlarmTriggerTime(timeOut()); // should be set instead of runtime;
		tb.setAlarmRepeatInterval(intervalOut());
		tb.setAlarmExecutionOutputType(typeOut());
		tb.setCommand(cmdToRun.getText());
		if (opmode == 1)
		{
			data.addElement(tb);
			alarmList.setListData(data);
		}
		else if (opmode == 2)
		{
			data.setElementAt(tb,selIndex);
			alarmList.setListData(data);
		}
		((CardLayout)(bottomCards.getLayout())).show(bottomCards, "FinishEdit");
		enadesa2(true);
		opmode = 0;
	}

	public void actionPerformed(ActionEvent actionevent)
	{
		String     comm = actionevent.getActionCommand();
		CardLayout cl   = (CardLayout)(bottomCards.getLayout());
		if (comm.equals("Preview Font"))
			fontPreview.setFont(setFontOut());
		else if (comm.equals("Reset Font"))
		{
			fontPreview.setFont((new JLabel()).getFont());
			setFontIn((new JLabel()).getFont());
		}
		else if (comm.equals("Choose Font Color"))
		{
			Color color  = selFontCol.getBackground();
			Color color3 = JColorChooser.showDialog(new Frame(), "Choose Font Color...", selFontCol.getBackground());
			if (color3 == null)
				color3 = color;
			selFontCol.setBackground(color3);
			fontPreview.setForeground(color3);
			selFontCol.setForeground(Math.abs(color3.getRGB()) >= 0x800000 ? Color.white : Color.black);
		}
		else if (comm.equals("Default"))
		{
			selFontCol.setBackground(Color.black);
			fontPreview.setForeground(Color.black);
			selFontCol.setForeground(Math.abs(Color.black.getRGB()) >= 0x800000 ? Color.white : Color.black);
		}
		else if (comm.equals("Beveled Raised Border"))
		{
			if (bRaised.isSelected())
				borderPreview.setPreview(BorderFactory.createRaisedBevelBorder());
		}
		else if (comm.equals("Beveled Lowered Border"))
		{
			if (bLowered.isSelected())
				borderPreview.setPreview(BorderFactory.createLoweredBevelBorder());
		}
		else if (comm.equals("Etched Raised Border"))
		{
			if (eRaised.isSelected())
				borderPreview.setPreview(BorderFactory.createEtchedBorder(0));
		}
		else if (comm.equals("Etched Lowered Border"))
		{
			if (eLowered.isSelected())
				borderPreview.setPreview(BorderFactory.createEtchedBorder(1));
		}
		else if (comm.equals("Line Border"))
		{
			if (lBorder.isSelected())
				borderPreview.setPreview(BorderFactory.createLineBorder(selLineCol.getBackground(), 2));
		}
		else if (comm.equals("Don't Use Any Border"))
		{
			if (nBorder.isSelected())
				borderPreview.setPreview(null);
		}
		else if (comm.equals("Line Border Color"))
		{
			Color color1 = selLineCol.getBackground();
			Color color4 = JColorChooser.showDialog(new Frame(), "Choose Line Color...", selLineCol.getBackground());
			if (color4 == null)
				color4 = color1;
			selLineCol.setBackground(color4);
			selLineCol.setForeground(Math.abs(color4.getRGB()) >= 0x800000 ? Color.white : Color.black);
			if (lBorder.isSelected())
				borderPreview.setPreview(BorderFactory.createLineBorder(selLineCol.getBackground(), 2));
		}
		else if (comm.equals("Default Line Color"))
		{
			selLineCol.setBackground(resLineCol.getBackground());
			selLineCol.setForeground(Math.abs(resLineCol.getBackground().getRGB()) >= 0x800000 ? Color.white : Color.black);
			if (lBorder.isSelected())
				borderPreview.setPreview(BorderFactory.createLineBorder(selLineCol.getBackground(), 2));
		}
		else if (comm.equals("Reset To Defaults"))
		{
			if (curTimeZone.isSelected())
				sysDateFormat.setText("zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
			else if (GMTimeZone.isSelected())
				gmtDateFormat.setText("zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
			else if (sysUpTime.isSelected())
			{
				uSymbol.setText("Up-Time: ");
				hSymbol.setText("-hour(s), ");
				mSymbol.setText("-minute(s), ");
				sSymbol.setText("-second(s)");
			}
		}
		else if (comm.equals("Help On Format"))
			new FormatHelp(this);
		else if (comm.equals("Choose Directory"))
		{
			JFileChooser jfilechooser = new JFileChooser(fileList.getDirectory());
			jfilechooser.setFileSelectionMode(1);
			jfilechooser.setMultiSelectionEnabled(false);
			int i = jfilechooser.showOpenDialog(this);
			if (i == 0)
				fileList.setDirectory(jfilechooser.getSelectedFile());
		}
		else if (comm.equals("Choose Background Color"))
		{
			Color color2 = selBackCol.getBackground();
			Color color5 = JColorChooser.showDialog(new Frame(), "Choose Background...", selBackCol.getBackground());
			if (color5 == null)
				color5 = color2;
			selBackCol.setBackground(color5);
			selBackCol.setForeground(Math.abs(color5.getRGB()) >= 0x800000 ? Color.white : Color.black);
		}
		else if (comm.equals("Default Color"))
		{
			selBackCol.setBackground(resBackCol.getBackground());
			selBackCol.setForeground(Math.abs(resBackCol.getBackground().getRGB()) >= 0x800000 ? Color.white : Color.black);
		}
		else if (comm.equals("Tile Vertically"))
			picLabel.setImagePosition(TLabel.V_TILE);
		else if (comm.equals("Tile"))
			picLabel.setImagePosition(TLabel.TILE);
		else if (comm.equals("Tile Horizontally"))
			picLabel.setImagePosition(TLabel.H_TILE);
		else if (comm.equals("Center"))
			picLabel.setImagePosition(TLabel.CENTER);
		else if (comm.equals("Resize to Fit"))
			picLabel.setImagePosition(TLabel.FIT);
		else if (comm.equals("Stretch to Fit"))
			picLabel.setImagePosition(TLabel.STRETCH);
		else if (comm.equals("Transparent background"))
		{
			setTransparentBackground();
		}
		else if (comm.equals("Add"))
		{
			cl.show(bottomCards,"AddEdit");
			enadesa2(false);
			opmode = 1;
			alarmName.setText("");
			timeTypeIn(false); //must be called before timeIn();
			timeIn(new Date());
			intervalIn(0);
			typeIn(4); // for msg;
		}
		else if (comm.equals("Edit"))
		{
			selIndex=alarmList.getSelectedIndex();
			if (selIndex != -1)
			{
				cl.show(bottomCards,"AddEdit");
				enadesa2(false);
				opmode      = 2;
				TimeBean tb = (TimeBean)data.elementAt(selIndex);
				alarmName.setText(tb.getName());
				timeTypeIn(tb.isSystemStartTimeBasedAlarm().booleanValue()); //must be called before timeIn();
				timeIn(tb.getAlarmTriggerTime());
				intervalIn(tb.getAlarmRepeatInterval());
				typeIn(tb.getAlarmExecutionOutputType().intValue());
				cmdToRun.setText(tb.getCommand());
			}
			else
			{
				JOptionPane.showMessageDialog(this,"Please select an alarm to edit.","Nothing to edit",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (comm.equals("Commit"))
		{
			try
			{
				saveCurrentAlarm();
			}
			catch (Exception exception)
			{
				if (exception.getMessage().equals("Blank name"))
				{
					JOptionPane.showMessageDialog(this,"Please enter a brief non-blank alarm name","Empty alarm name",JOptionPane.INFORMATION_MESSAGE);
					alarmName.grabFocus();
				}
				else if (exception.getMessage().equals("Blank command"))
				{
					JOptionPane.showMessageDialog(this,"The command to be executed is empty","Empty command",JOptionPane.INFORMATION_MESSAGE);
					cmdToRun.grabFocus();
				}
			}
		}
		else if (comm.equals("Discard"))
		{
			cl.show(bottomCards,"FinishEdit");
			enadesa2(true);
			opmode = 0;
		}
		else if (comm.equals("Remove"))
		{
			selIndex = alarmList.getSelectedIndex();
			if (selIndex != -1)
			{
				data.removeElementAt(selIndex);
				alarmList.setListData(data);
				alarmList.setSelectedIndex(selIndex);
			}
		}
		else if (comm.equals("Test >>"))
		{
			selIndex = alarmList.getSelectedIndex();
			if (selIndex != -1)
			{
				TimeBean tb = (TimeBean)data.elementAt(selIndex);
				ExUtils.runProgram(tb,this);
			}
		}
		else if (comm.equals("and continue"))
		{
			if (rept.isSelected()) period.setSelectedIndex(1);
			else period.setSelectedIndex(0);
		}
		else if (comm.equals("Browse..."))
		{
			JFileChooser fchoose   = new JFileChooser(".");
			int          chooseOpt = 1;
			try
			{
				chooseOpt=fchoose.showOpenDialog(this);
				switch(chooseOpt)
				{
					case JFileChooser.APPROVE_OPTION:
					cmdToRun.setText(cmdToRun.getText() + fchoose.getSelectedFile().getPath());
					break;
					case JFileChooser.CANCEL_OPTION:
					//do nothing;
					break;
					case JFileChooser.ERROR_OPTION:
					JOptionPane.showMessageDialog(this,"Error occured while opening the file.","File Error",JOptionPane.ERROR_MESSAGE);
					break;
				}
			}
			catch (Exception hle)
			{
				JOptionPane.showMessageDialog(this, "Error occured while opening the file.", "File Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (comm.equals("Start Command") || comm.equals("Just Beep") || comm.equals("Message"))
		{
			if (!runCom.isSelected() && !runSnd.isSelected() && !runMsg.isSelected())
				runMsg.setSelected(true);
			cmdToRun.setEnabled(runCom.isSelected());
			browse.setEnabled(runCom.isSelected());
		}
		enadesa();
		setOneEnabled();
	}

	public void itemStateChanged(ItemEvent ie)
	{
		if (ie.getStateChange() == ItemEvent.SELECTED)
		{
			if (ie.getItem().equals("Never")) rept.setSelected(false);
			else rept.setSelected(true);
			
			if (ie.getItem().equals("Monthly")) dateOrWeek.setEnabled(true);
			else dateOrWeek.setEnabled(false);
		}
	}

	public void valueChanged(ListSelectionEvent listselectionevent)
	{
		Object srcList = listselectionevent.getSource();
		if (srcList.equals(fileList))
		{
			if (fileList.isSelectionEmpty())
			{
				picLabel.setBackImage(null);
				picLabel.setText("No Preview");
			}
			else
			{
				picLabel.setBackImage((new ImageIcon(fileList.getSelectedValue().toString())).getImage());
				picLabel.setText("");
			}
		}
		else if (srcList.equals(alarmList))
		{
			setDescriptionText();
		}
	}

	public void stateChanged(ChangeEvent ce)
	{
		String opacityPercent = Math.round((float)transLevel.getValue() / 20 * 100) + " %";
		transSlide.setText("Opacity [ " + opacityPercent + " ]");
		transLevel.setToolTipText(opacityPercent);
	}
}