package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.text.*;
import java.time.Duration;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.border.*;

public class ChooserBox extends JDialog implements ActionListener, ItemListener, ListSelectionListener, ChangeListener
{
	private JTabbedPane tabPane;
	private ListChooser cFontList,cFontStyleList,cFontSizeList;
	private JButton resetFont,selFontCol,resFontCol;
	private TLabel fontPreview, jLDigiPreview, jLAnaPreview, picLabel, bdrPreview;
	private JRadioButton bRaised,bLowered,eRaised,eLowered,lBorder;
	private JCheckBox nBorder,cbMovable,enTooltip,nativeLook,roundBdr;
	private JButton selLineCol,resLineCol;
	private JComboBox<String> comboTz, comboDateFmt, comboPomodoro, comboPomFmt;
	private JComboBox<AnalogClockFormat> comboClockFmt, comboDialBdr;
	private JRadioButton selTimeZone,sysUpTime,pomodoroTime;
	private JLabel layLabel, jLDayTxt, jLUptimeTxt, jLHourTxt, jLMinTxt, jLSecTxt, tzLabel;
	private JCheckBox analogCb2, analogCb1, analogCb0, tzCb, cbPomLabel, cbPomCountdown, cbShowDays;
	private JComboBox<String> dSymbol, uSymbol, hSymbol, mSymbol, sSymbol;
	private JButton resetDefs,helpFormat,resetImgDir;
	private JLabel jlTransSlide,jLDateFormat,jLPomFormat, thickLabel;
	private JSlider transLevel;
	private JCheckBox useImg,useCol,useTrans,slowUpd;
	private ImageFileList imgFileList;
	private JButton selectDir,selBackCol,resBackCol;
	private JComboBox<String> comboTLayout;
	private JList<TimeBean> alarmList;
	private JLabel jLAlmSame, jLAlmDays, jLAlmHrs;
	private SoundPlayer sndHour, sndUptime, sndWork, sndBrk, sndRest, sndToRun;
	private JButton add,remove,edit,test;
	private JPanel bottomCards, digitalPanel, analogPanel, cardComboPanel, borderPanel, picPanel;
	private JComboBox<String> period, dateOrWeek;
	private JCheckBox rept,runSnd,runMsg;
	private JTextField alarmName;
	private JRadioButton optStartOn, optStartAfter;
	private JSpinner timeSpinner1, timeSpinner2, countSpinner1, countSpinner2, thickSpinner;
	private DateChooser chooseDate;
	private int opmode   = ChooserBox.ALARM_DISCARD;
	private int selIndex = -1;
	private int intThick = 2;
	private boolean exceptionActive = false;
	private JButton ok,cancel;
	private Pomodoro pom;
	public Vector <TimeBean>data;
	public Vector <AnalogClockFormat> anaFormats, anaDials;
	public InitInfo information;

	public static final int FONT_TAB       = 0;
	public static final int BACKGROUND_TAB = 1;
	public static final int BORDER_TAB     = 2;
	public static final int TIMES_TAB      = 3;
	public static final int ALARMS_TAB     = 4;
	public static final int ALARM_ADD      = 11;
	public static final int ALARM_EDIT     = 13;
	public static final int ALARM_DISCARD  = 17;

	final String digiFormats[] = {
		"yyyy.MM.dd G 'at' HH:mm:ss z",
		"zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy",
		"hh:mm a, z",
		"h:mm a",
		"hh:mm:ss a",
		"H:mm:ss",
		"hh 'o''clock' a, zzzz",
		"K:mm a, z",
		"yyyyy.MMMMM.dd GGG hh:mm aaa",
		"EEE, d MMM yyyy HH:mm:ss Z",
		"yyMMddHHmmssZ",
		"YYYY-'W'ww-u",
		"EEE, MMM d, ''yy",
		"dd MMMMM yyyy",
		"dd.MM.yy",
		"MM/dd/yy"
	};

	public ChooserBox(InitInfo initinfo, Vector <TimeBean>alarminfo)
	{
		GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		information = initinfo;  //Transfering information from Desktop to this;
		getContentPane().setLayout(new BorderLayout());
		tabPane     = new JTabbedPane();
		//All configuration for Font Panel as follows:
		JPanel  jpanel = new JPanel(new GridBagLayout());
		resetFont  	   = new JButton("Reset Font");
		Integer ainteger[] = new Integer[20];
		int     ai[]       = {6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 
								24, 28, 32, 36, 40, 48, 56, 64, 72, 80};
		for (int i = 0; i < ai.length; i++)
			ainteger[i] = Integer.valueOf(ai[i]);
		
		String as1[]   = {"Plain", "Bold", "Italic", "Bold Italic"};
		String as[]    = graphicsenvironment.getAvailableFontFamilyNames();
		cFontList      = new ListChooser(as, "Font");
		cFontStyleList = new ListChooser(as1, "Style");
		cFontSizeList  = new ListChooser(ainteger, "Size");
		selFontCol     = new JButton("Choose Font Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resFontCol     = new JButton("Reset Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		fontPreview    = new TLabel("AaBbCc...0123...!#@%&$");
		cFontList.addListSelectionListener(this);
		cFontStyleList.addListSelectionListener(this);
		cFontSizeList.addListSelectionListener(this);
		fontPreview.setBackground(Color.white);
		JScrollPane jscrollpane = new JScrollPane();
		jscrollpane.getViewport().setView(fontPreview);
		jscrollpane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font Configuration"));
		//All configurations for Border Panel as follows:
		JPanel jpanel1 = new JPanel(new GridBagLayout());
		analogCb1 = new JCheckBox("Analog clock dial");
		analogCb1.setActionCommand("AnalogClock");
		borderPanel  = new JPanel();
		SpringLayout applyLayout = new SpringLayout();
		borderPanel.setLayout(applyLayout);
		bdrPreview	= new TLabel("Preview");
		bdrPreview.setFont(new	Font(bdrPreview.getFont().getFamily(),	Font.BOLD, 14));
		borderPanel.add(bdrPreview);
		applyLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bdrPreview, 0, SpringLayout.HORIZONTAL_CENTER, borderPanel);
		applyLayout.putConstraint(SpringLayout.VERTICAL_CENTER, bdrPreview, 0, SpringLayout.VERTICAL_CENTER, borderPanel);
		borderPanel.setBorder(BorderFactory.createLoweredBevelBorder());
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
		JSeparator jseparator1  = new JSeparator();
		Component component    = Box.createHorizontalStrut(8);
		selLineCol = new JButton("Line Border Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resLineCol = new JButton("Reset", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resLineCol.setActionCommand("Default Line Color");
		JPanel    thickPanel = new JPanel();
		BoxLayout thkly      = new BoxLayout(thickPanel, BoxLayout.X_AXIS);
		thickPanel.setLayout(thkly);
		thickLabel   = new JLabel("Thickness", JLabel.RIGHT);
		thickSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		thickSpinner.addChangeListener(this);
		thickPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		thickLabel.setLabelFor(thickSpinner);
		thickPanel.add(thickLabel);
		thickPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		thickPanel.add(thickSpinner);
		thickPanel.add(Box.createHorizontalGlue());
		anaDials = new Vector<AnalogClockFormat>();
		anaDials.add(new AnalogClockFormat(TLabel.SHOW_NONE, "No dial border"));
		anaDials.add(new AnalogClockFormat(TLabel.ANALOG_BORDER, "Show dial border only"));
		anaDials.add(new AnalogClockFormat((TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK), "Dial with major hour marks"));
		anaDials.add(new AnalogClockFormat((TLabel.ANALOG_BORDER + TLabel.HOUR_TICK), "Dial with hour markers"));
		anaDials.add(new AnalogClockFormat((TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK), "Dial with minute markers"));
		comboDialBdr = new JComboBox<AnalogClockFormat>(anaDials);
		comboDialBdr.setEditable(false);
		comboDialBdr.addItemListener(this);
		JSeparator jseparator2 = new JSeparator();
		cbMovable   = new JCheckBox("Movable");
		roundBdr   = new JCheckBox("Round Corners");
		enTooltip  = new JCheckBox("Mouse-over Time info");
		nativeLook = new JCheckBox("Check to view this in system's look and feel");
		jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Looks and Borders"));
		//All configurations for Time Conf. Panel as follows:
		JPanel    jpanel2      = new JPanel(new GridBagLayout());
		pomodoroTime = new JRadioButton("Pomodoro tasks");
		pomodoroTime.setActionCommand("POMODORO_RADIO_BUTTON");
		pomodoroTime.setToolTipText("<html>Select from well known pomodoro timer schedules (in minutes).<p>The schedules repeats itself once completed.</html>");
		selTimeZone = new JRadioButton("Current Time");
		selTimeZone.setActionCommand("TIME_RADIO_BUTTON");
		selTimeZone.setToolTipText("<html>This setting displays the current time in selected time zone<p>in the date-time format selected below.</html>");
		JPanel toolPane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Image defaultPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/default-icon.png"))).getImage();
		Image helpPng    = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/help-icon.png"))).getImage();
		resetDefs   = createSmallButton(defaultPng, "Reset to default values", this);
		helpFormat  = createSmallButton(helpPng, "Help on date time format characters", this);
		toolPane1.add(resetDefs);
		toolPane1.add(helpFormat);
		sysUpTime   = new JRadioButton("System Up-Time");
		sysUpTime.setActionCommand("UPTIME_RADIO_BUTTON");
		sysUpTime.setToolTipText("<html>This setting displays the time elapsed since your system<p>had started without a log off or shut down.</html>");
		ButtonGroup buttongroup1 = new ButtonGroup();
		buttongroup1.add(pomodoroTime);
		buttongroup1.add(selTimeZone);
		buttongroup1.add(sysUpTime);
		Component timePaneRadSpace = Box.createHorizontalStrut(17);
		jLDigiPreview = new TLabel("");
		jLDigiPreview.setVerticalAlignment(JLabel.CENTER);
		jLAnaPreview  = new TLabel("");
		jLAnaPreview.setClockMode(true);
		digitalPanel    = new JPanel();
		analogPanel    = new JPanel(new BorderLayout());
		JPanel topPanePr = new JPanel(new BorderLayout());
		digitalPanel.add(jLDigiPreview);
		digitalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		analogPanel.add(jLAnaPreview, BorderLayout.CENTER);
		analogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		topPanePr.add(toolPane1, BorderLayout.LINE_START);
		topPanePr.add(digitalPanel, BorderLayout.CENTER);
		analogCb2 = new JCheckBox("Analog clock");
		analogCb2.setActionCommand("AnalogClock");
		anaFormats = new Vector<AnalogClockFormat>();
		anaFormats.add(new AnalogClockFormat(TLabel.SHOW_NONE, "No markers"));
		anaFormats.add(new AnalogClockFormat(TLabel.SHOW_AM_PM, "AM-PM markers"));
		anaFormats.add(new AnalogClockFormat(TLabel.SHOW_TIMEZONE, "Time zone marker"));
		anaFormats.add(new AnalogClockFormat((TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE), "AM-PM and Time zone"));
		comboClockFmt = new JComboBox<AnalogClockFormat>(anaFormats);
		comboClockFmt.setEditable(false);
		comboClockFmt.addItemListener(this);
		tzLabel = new JLabel("Select Timezone");
		tzCb    = new JCheckBox("Use local time");
		tzCb.setActionCommand("LocalTz");
		tzLabel.setLabelFor(tzCb);
		comboTz   = new JComboBox<String>(new DefaultComboBoxModel<String>(TimeZone.getAvailableIDs()));
		comboTz.setRenderer(new TimezoneCellRenderer());
		comboTz.addItemListener(this);
		jLDateFormat = new JLabel("Date/Time Format");
		comboDateFmt = new JComboBox<String>(digiFormats);
		comboDateFmt.setEditable(true);
		comboDateFmt.addItemListener(this);
		jLDateFormat.setLabelFor(comboDateFmt);
		cardComboPanel = new JPanel(new CardLayout());
		cardComboPanel.add(comboDateFmt, "DateFormat");
		cardComboPanel.add(comboClockFmt, "ClockFormat");
		JSeparator jseparator3 = new JSeparator();
		cbShowDays  = new JCheckBox("Show Days in Up-time");
		jLDayTxt    = new JLabel("Day Label");
		jLUptimeTxt = new JLabel("Up-time Label");
		jLHourTxt   = new JLabel("Hour Label");
		jLMinTxt    = new JLabel("Minute Label");
		jLSecTxt    = new JLabel("Second Label");
		dSymbol     = new JComboBox<String>(new String[]{"-day(s), ", "", " day(s), ", " days "});
		uSymbol     = new JComboBox<String>(new String[]{"Up-Time: ", "", "Run time: ", "Uptime: ", "System uptime: "});
		hSymbol     = new JComboBox<String>(new String[]{"-hour(s), ", "", " hour(s), ", " hrs. ", " hr(s), "});
		mSymbol     = new JComboBox<String>(new String[]{"-minute(s), ", "", " minute(s), ", " mins. ", " min(s), "});
		sSymbol     = new JComboBox<String>(new String[]{"-second(s)", "", " second(s)", " secs.", " sec(s)"});
		cbShowDays.setActionCommand("ShowDays");
		cbShowDays.addActionListener(this);
		jLDayTxt.setBorder(new EmptyBorder(0, 100, 0, 0));
		dSymbol.setEditable(true);
		dSymbol.addItemListener(this);
		uSymbol.setEditable(true);
		uSymbol.addItemListener(this);
		jLHourTxt.setBorder(new EmptyBorder(0, 100, 0, 0));
		hSymbol.setEditable(true);
		hSymbol.addItemListener(this);
		mSymbol.setEditable(true);
		mSymbol.addItemListener(this);
		jLSecTxt.setBorder(new EmptyBorder(0, 100, 0, 0));
		sSymbol.setEditable(true);
		sSymbol.addItemListener(this);
		JSeparator jseparator4 = new JSeparator();
		String pomtech[] = {
			"25 min. work, 5 min. break, 30 min. rest",
			"45 minutes work, 15 minutes break",
			"50 minutes work, 10 minutes break",
			"52 minutes work, 17 minutes break",
			"90 minutes work, 20 minutes break",
			"112 minutes work, 26 minutes break",
			"4 sec breath-in, 6 sec hold, 8 sec breath-out"
		};
		comboPomodoro = new JComboBox<String>(pomtech);
		comboPomodoro.addItemListener(this);
		jLPomFormat = new JLabel("Pomodoro format");
		String pomFormats[] = {
			"mm:ss",
			"HH:mm:ss",
		};
		comboPomFmt = new JComboBox<String>(pomFormats);
		comboPomFmt.setEditable(true);
		comboPomFmt.addItemListener(this);
		jLPomFormat.setLabelFor(comboPomFmt);
		cbPomLabel = new JCheckBox("Leading label");
		cbPomLabel.setActionCommand("Leading label");
		cbPomCountdown = new JCheckBox("Countdown");
		cbPomCountdown.setBorder(new EmptyBorder(0, 100, 0, 0));
		jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Time Display Options"));
		//All configurations for Background Panel as follows:
		JPanel jpanel3 = new JPanel(new GridBagLayout());
		useImg   = new JCheckBox("<html>Use background image. Use image settings from the following:</html>");
		useCol   = new JCheckBox("<html>Use Background Color. Use color settings from the following:</html>");
		useTrans = new JCheckBox("<html>Use transparent background (may cause flickers)</html>");
		useTrans.setActionCommand("Transparent background");
		slowUpd  = new JCheckBox("<html>Reduce flicker (slower transparent background updates)</html>");
		slowUpd.setActionCommand("Reduce flicker");
		JSeparator jSeparator5 = new JSeparator();
		ButtonGroup buttongroup2 = new ButtonGroup();
		buttongroup2.add(useImg);
		buttongroup2.add(useCol);
		buttongroup2.add(useTrans);
		Component component2 = Box.createHorizontalStrut(12);
		Component component3 = Box.createVerticalStrut(70);
		imgFileList = new ImageFileList(null);
		imgFileList.setVisibleRowCount(7);
		imgFileList.setSelectionMode(0);
		imgFileList.setValueIsAdjusting(true);
		imgFileList.addListSelectionListener(this);
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.getViewport().setView(imgFileList);
		Component component4 = Box.createHorizontalStrut(140);
		picPanel  = new JPanel();
		SpringLayout picLayout = new SpringLayout();
		picPanel.setLayout(picLayout);
		picLabel  = new TLabel("Preview", null, TLabel.FIT);
		picLabel.setFont(new Font(picLabel.getFont().getFamily(),	Font.BOLD, 16));
		picLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		picPanel.add(picLabel);
		picLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, picLabel, 0, SpringLayout.HORIZONTAL_CENTER, picPanel);
		picLayout.putConstraint(SpringLayout.VERTICAL_CENTER, picLabel, 0, SpringLayout.VERTICAL_CENTER, picPanel);
		picPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel toolPane2 = new JPanel();
		BoxLayout bly = new BoxLayout(toolPane2, BoxLayout.X_AXIS);
		toolPane2.setLayout(bly);
		Image folderPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/folder-icon.png"))).getImage();
		selectDir   = createSmallButton(folderPng, "Choose Image Directory", this);
		resetImgDir = createSmallButton(defaultPng, "Reset to default images", this);
		analogCb0 = new JCheckBox("Analog mode");
		analogCb0.setActionCommand("AnalogClock");
		analogCb0.addActionListener(this);
		layLabel  = new JLabel("Layout");
		String layouts[] = {TLabel.V_TILE_TEXT, TLabel.H_TILE_TEXT, TLabel.TILE_TEXT, TLabel.STRETCH_TEXT, TLabel.FIT_TEXT, TLabel.CENTER_TEXT};
		comboTLayout = new JComboBox<String>(layouts);
		layLabel.setLabelFor(comboTLayout);
		comboTLayout.addItemListener(this);
		toolPane2.add(selectDir);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(resetImgDir);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(analogCb0);
		toolPane2.add(Box.createHorizontalGlue());
		toolPane2.add(layLabel);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(comboTLayout);
		selBackCol = new JButton("Choose Background Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resBackCol = new JButton("Default Color", new ButtonIcon(ButtonIcon.RECTANGLE, getBackground()));
		jlTransSlide = new JLabel("Opacity [ Low \u2192 High ]");
		transLevel = new JSlider(4, 20, 10);
		jlTransSlide.setLabelFor(transLevel);
		transLevel.setMajorTickSpacing(1);
		transLevel.setSnapToTicks(true);
		transLevel.addChangeListener(this);
		jpanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Background Configuration"));
		//All configuration for Alarm Panel as follows:
		JPanel      jpanel5 = new JPanel(new GridBagLayout());
		JPanel      topList = new JPanel(new GridBagLayout());
		JScrollPane jsp     = new JScrollPane();
		data      = new Vector<TimeBean>();
		alarmList = new JList<TimeBean>(data);
		alarmList.setCellRenderer(new AlarmListCellRenderer());
		alarmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jsp.setViewportView(alarmList);
		alarmList.addMouseListener(new JListDoubleClickAction());
		add         = new JButton("Add");
		edit        = new JButton("Edit");
		remove      = new JButton("Remove");
		test        = new JButton("Test", new ButtonIcon(ButtonIcon.RIGHT_ARROW, Color.BLACK));
		bottomCards = new JPanel(new CardLayout());
		test.setHorizontalTextPosition(SwingConstants.LEADING);
		topList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Alarms List"));
		JPanel sndSet    = new JPanel(new GridBagLayout());
		JLabel jlHour    = new JLabel("Hourly sound");
		JLabel jlUpt     = new JLabel("Uptime Hour");
		JLabel jlPomWork = new JLabel("Pomodoro Work");
		JLabel jlPomBrk  = new JLabel("Pomodoro Break");
		JLabel jlPomRest = new JLabel("Pomodoro Rest");
		sndHour   = new SoundPlayer(15);
		sndUptime = new SoundPlayer(15);
		sndWork   = new SoundPlayer(15);
		sndBrk    = new SoundPlayer(15);
		sndRest   = new SoundPlayer(15);
		jlHour.setLabelFor(sndHour);
		jlUpt.setLabelFor(sndUptime);
		jlPomWork.setLabelFor(sndWork);
		jlPomBrk.setLabelFor(sndBrk);
		jlPomRest.setLabelFor(sndRest);
		sndSet.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Select sounds"));
		JPanel almSet    = new JPanel(new GridBagLayout());
		JLabel nameLabel = new JLabel("Alarm Name",JLabel.CENTER);
		       alarmName = new JTextField();
		nameLabel.setLabelFor(alarmName);
		alarmName.setToolTipText("<html>Please enter a brief description of the alarm such as:<p> &quot;John's Birthday&quot;</html>");
		ButtonGroup bgr = new ButtonGroup();
		optStartOn 		= new JRadioButton("Start on");
		bgr.add(optStartOn);
		chooseDate   = new DateChooser();
		timeSpinner1 = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor spinModel = new JSpinner.DateEditor(timeSpinner1,"hh:mm a");
		timeSpinner1.setEditor(spinModel);
		       rept   = new JCheckBox("and repeat every");
		countSpinner1 = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
		String pds[]  = new String[]{"Never", "Minute", "Hour", "Day", "Week", "Month", "Year"};
		       period = new JComboBox<String>(pds);
		period.addItemListener(this);
		String dow[]      = new String[]{"Date", "Weekday"};
		       jLAlmSame  = new JLabel("same");
		       dateOrWeek = new JComboBox<String>(dow);
		jLAlmSame.setEnabled(false);
		dateOrWeek.setEnabled(false);
		optStartAfter = new JRadioButton("Start after");
		bgr.add(optStartAfter);
		countSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
		jLAlmDays     = new JLabel(" days and");
		timeSpinner2             = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dit2 = new JSpinner.DateEditor(timeSpinner2,"HH:mm");
		timeSpinner2.setEditor(dit2);
		jLAlmHrs = new JLabel(" hours of system uptime");
		JLabel jLAlmAct = new JLabel(" On Alarm:");
		runSnd        = new JCheckBox("Play selected sound");
		runMsg        = new JCheckBox("Show message");
		sndToRun      = new SoundPlayer(60);
		almSet.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Alarm Details"));
		bottomCards.add(sndSet, "FinishEdit");
		bottomCards.add(almSet,  "AddEdit");
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
			ExUtils.addComponent(jpanel,  cFontList, 		0, 0, 1, 2, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontStyleList, 	1, 0, 1, 2, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontSizeList, 	2, 0, 1, 2, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  selFontCol, 		0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resFontCol, 		1, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resetFont, 		2, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  jscrollpane, 		0, 3, 3, 1, 1.0D, 0.6D, this);
			ExUtils.addComponent(jpanel1, nBorder, 			0, 0, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, analogCb1,		2, 0, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, comboDialBdr,		3, 0, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bRaised, 			0, 2, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, borderPanel, 		2, 2, 4, 4, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bLowered, 		0, 3, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eRaised, 			0, 4, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eLowered, 		0, 5, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator1, 		0, 6, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, lBorder, 			0, 7, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, component, 		0, 8, 1, 1, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, selLineCol, 		1, 8, 1, 1, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, resLineCol, 		2, 8, 1, 1, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, thickPanel, 		3, 8, 1, 1, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator2, 		0, 9, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, enTooltip, 		0, 10, 2, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, cbMovable, 		2, 10, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, roundBdr, 		3, 10, 2, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, nativeLook, 		0, 11, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, topPanePr,	 	0, 2, 4, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, analogPanel,		4, 2, 1, 3, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, selTimeZone, 		0, 3, 2, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, analogCb2,		2, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLDateFormat, 	1, 4, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cardComboPanel, 	2, 4, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, timePaneRadSpace, 0, 5, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, tzLabel, 			1, 5, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboTz, 			2, 5, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, tzCb,		 		4, 5, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator3, 		1, 6, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sysUpTime, 		0, 7, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbShowDays, 		2, 7, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLDayTxt, 		3, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, dSymbol,	 		4, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLUptimeTxt, 		1, 8, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, uSymbol, 			2, 8, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLHourTxt, 		3, 8, 1, 1, 0.01D, 0.0D, this);
			ExUtils.addComponent(jpanel2, hSymbol, 			4, 8, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLMinTxt, 		1, 9, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, mSymbol, 			2, 9, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLSecTxt, 		3, 9, 1, 1, 0.01D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sSymbol, 			4, 9, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator4, 		1, 10, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, pomodoroTime, 	0, 11, 2, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomodoro, 	2, 11, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLPomFormat, 		1, 12, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomFmt,		2, 12, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbPomCountdown,	3, 12, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbPomLabel,		4, 12, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useImg, 			0, 0, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component2, 		0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, jscrollpane1, 	1, 1, 1, 3, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, toolPane2, 		2, 1, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component3,		0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component4,		1, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, picPanel, 		2, 2, 2, 2, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, useCol, 			0, 5, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, selBackCol, 		1, 6, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, resBackCol, 		3, 6, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, jlTransSlide, 		1, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, transLevel, 		2, 7, 2, 1, 1.0D, 0.0D, this);
			if (!initinfo.isPixelAlphaSupported() && initinfo.isScreenshotSupported())
			{
				ExUtils.addComponent(jpanel3, useTrans, 		0, 8, 4, 1, 1.0D, 0.0D, this);
				ExUtils.addComponent(jpanel3, slowUpd,			1, 9, 7, 1, 1.0D, 0.0D, this);
			}
			else
			{
				ExUtils.addComponent(jpanel3, jSeparator5, 		0, 4, 4, 1, 1.0D, 0.0D, this);
			}
			ExUtils.addComponent(topList, jsp,				0, 0, 1,	5, 1.0D, 1.0D, this);
			ExUtils.addComponent(topList, add,				1, 0, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, edit,				1, 1, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, remove,			1, 2, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, test,				1, 3, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(almSet,  nameLabel,		0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  alarmName,		1, 0, 6,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartOn,		0, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  chooseDate,		1, 1, 5,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner1,		6, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartAfter,	0, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  countSpinner2,	1, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmDays,		2, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner2,		3, 2, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmHrs,			5, 2, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  rept,				0, 3, 2,	1, 0.4D, 0.0D, this);
			ExUtils.addComponent(almSet,  countSpinner1,	2, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  period,			3, 3, 2,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmSame,		5, 3, 1,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  dateOrWeek,		6, 3, 1,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmAct,			0, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runSnd,			1, 4, 4,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runMsg,			5, 4, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  sndToRun,			1, 5, 6,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlHour,			0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndHour,			1, 0, 1,	1, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlUpt,			0, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndUptime,		1, 1, 1,	1, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomWork,		0, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndWork,			1, 2, 1,	1, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomBrk,			0, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndBrk,			1, 3, 1,	1, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomRest,		0, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndRest,			1, 4, 1,	1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, topList,			0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, bottomCards,		0, 1, 1,	1, 0.0D, 0.0D, this);
		}
		catch (AWTException awtexception)
		{
			System.out.println("General AWT Exception has occurred.");
			awtexception.printStackTrace();
		}

		tabPane.add("Font", jpanel);
		tabPane.add("Background", jpanel3);
		tabPane.add("Border & UI", jpanel1);
		tabPane.add("Time View", jpanel2);
		tabPane.add("Alarms", jpanel5);
		getContentPane().add(tabPane, "Center");
		grid.add(ok);
		grid.add(cancel);
		jpanel4.add(grid);
		getContentPane().add(jpanel4, "South");
		initialize(information, alarminfo);
	}

	public static InfoTracker showDialog(String s, int i, InitInfo initinfo, Vector <TimeBean>alarmData)
	{
		ChooserBox chooserbox = new ChooserBox(initinfo, alarmData);
		chooserbox.setTitle(s);
		InfoTracker infotracker = new InfoTracker(chooserbox);
		chooserbox.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		chooserbox.revalidate();
		chooserbox.pack();
		chooserbox.setSize(chooserbox.getPreferredSize().width, 480);
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

	public void initialize(InitInfo initinfo, Vector <TimeBean>alarmInit) //Called by->constructor->showDialog
	{
		setSelectedFont(initinfo.getFont());
		fontPreview.setFont(initinfo.getFont());
		Color color = initinfo.getForeground();
		((ButtonIcon)selFontCol.getIcon()).setEnabledColor(color);
		fontPreview.setForeground(color);
		useImg.setSelected(initinfo.isUsingImage());
		useCol.setSelected(!initinfo.isUsingImage());
		imgFileList.setDefaultImagesDir(initinfo.getDefaultsDir() + "/images");
		updateImagesList(initinfo.getImageFile());
		useTrans.setSelected(initinfo.hasGlassEffect());
		slowUpd.setSelected(initinfo.isSlowTransUpdating());
		if (initinfo.getImageFile().isFile())
		{
			picLabel.setBackImage((new ImageIcon(initinfo.getImageFile().toString())).getImage());
			picLabel.setText("Preview");
			setTransparentBackground();
		}
		else
		{
			picLabel.setText("No Preview");
			picLabel.setBackImage(null);
		}
		picLabel.setImageLayout(initinfo.getImageStyle());
		switch (initinfo.getImageStyle())
		{
			case TLabel.FIT:
				comboTLayout.setSelectedItem(TLabel.FIT_TEXT); break;
			case TLabel.CENTER:
				comboTLayout.setSelectedItem(TLabel.CENTER_TEXT); break;
			case TLabel.TILE:
				comboTLayout.setSelectedItem(TLabel.TILE_TEXT); break;
			case TLabel.STRETCH:
				comboTLayout.setSelectedItem(TLabel.STRETCH_TEXT); break;
			case TLabel.V_TILE:
				comboTLayout.setSelectedItem(TLabel.V_TILE_TEXT); break;
			case TLabel.H_TILE:
				comboTLayout.setSelectedItem(TLabel.H_TILE_TEXT); break;	
			default:
				comboTLayout.setSelectedItem(TLabel.FIT_TEXT); break;
		}
		color = initinfo.getBackground();
		((ButtonIcon)selBackCol.getIcon()).setEnabledColor(color);
		transLevel.setMinimum(initinfo.isPixelAlphaSupported() ? 0 : 4);
		transLevel.setValue(Math.round(initinfo.getOpacity() * 20));
		jlTransSlide.setText("Opacity [ " + Math.round(initinfo.getOpacity() * 100) + " % ]");
		Border border = initinfo.getBorder();
		bdrPreview.setBorder(border);
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
			((ButtonIcon)selLineCol.getIcon()).setEnabledColor(color1);
			intThick = ((LineBorder)border).getThickness();
			thickSpinner.setValue(intThick);
		}
		else
		{
			nBorder.setSelected(true);
		}
		cbMovable.setSelected(!initinfo.isFixed());
		roundBdr.setSelected(initinfo.hasRoundedCorners());
		enTooltip.setSelected(initinfo.hasTooltip());
		nativeLook.setSelected(initinfo.hasNativeLook());
		analogCb0.setSelected(initinfo.isAnalogClock());
		analogCb1.setSelected(initinfo.isAnalogClock());
		analogCb2.setSelected(initinfo.isAnalogClock());
		int anaClkOpts = Math.floorDiv(initinfo.getAnalogClockOption(), 1000) * 1000;
		int anaBdrOpts = initinfo.getAnalogClockOption() % 1000;
		for (AnalogClockFormat formats : anaFormats) {
			if (formats.getId() == anaClkOpts) {
				comboClockFmt.setSelectedItem(formats);
			}
		}
		for (AnalogClockFormat formats : anaDials) {
			if (formats.getId() == anaBdrOpts) {
				comboDialBdr.setSelectedItem(formats);
			}
		}		
		String dispM = initinfo.getDisplayMethod();
		String privT = "";
		pom          = new Pomodoro(initinfo.getPomodoroTask());
		if (dispM.equals("CURTZ"))
		{
			tzCb.setSelected(true);
			comboTz.setSelectedItem(TimeZone.getDefault().getID());
		}
		else
		{
			tzCb.setSelected(false);
			comboTz.setSelectedItem(TimeZone.getTimeZone(initinfo.getTimeZone()).getID());
		}
		comboDateFmt.setSelectedItem(initinfo.getZonedTimeFormat());
		if (dispM.equals("GMTTZ") || dispM.equals("CURTZ")) {
			selTimeZone.setSelected(true);
			SimpleDateFormat sd = new SimpleDateFormat(initinfo.getZonedTimeFormat());
			TimeZone tempTz = TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex()));
			sd.setTimeZone(tempTz);
			Calendar toUpdate = Calendar.getInstance(tempTz);
			privT = sd.format(toUpdate.getTime());
			updateAnalogClockTimezone(tempTz);
			jLAnaPreview.setAnalogClockOptions(Math.floorDiv(initinfo.getAnalogClockOption(), 1000) * 1000);
			bdrPreview.setAnalogClockOptions(initinfo.getAnalogClockOption() % 1000);
		}
		else if (dispM.equals("UPTIME")) {
			sysUpTime.setSelected(true);
			cbShowDays.setSelected(initinfo.isDayShowing());
			privT = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), initinfo.getUpTimeFormat());
		}
		else if (dispM.equals("POMODORO")) {
			pomodoroTime.setSelected(true);
			privT = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(initinfo.isPomodoroCountdown()), initinfo.getPomodoroFormat(), pom.getRunningLabel(), initinfo.isPomodoroLeadingLabel());
		}
		jLDigiPreview.setText(privT);
		String s1    = initinfo.getUpTimeFormat();
		String arr[] = s1.split("\'");
		if (initinfo.isDayShowing())
		{
			uSymbol.setSelectedItem(arr[1]);
			dSymbol.setSelectedItem(arr[3]);
			hSymbol.setSelectedItem(arr[5]);
			mSymbol.setSelectedItem(arr[7]);
			sSymbol.setSelectedItem(arr.length == 10 ? arr[9] : " ");
		}
		else
		{
			uSymbol.setSelectedItem(arr[1]);
			hSymbol.setSelectedItem(arr[3]);
			mSymbol.setSelectedItem(arr[5]);
			sSymbol.setSelectedItem(arr.length == 8 ? arr[7] : " ");
		}
		comboPomodoro.setSelectedItem(initinfo.getPomodoroTask());
		cbPomLabel.setSelected(initinfo.isPomodoroLeadingLabel());
		cbPomCountdown.setSelected(initinfo.isPomodoroCountdown());
		comboPomFmt.setSelectedItem(initinfo.getPomodoroFormat());
		data = alarmInit;
		alarmList.setListData(data);
		sndHour.setAudioFileName(initinfo.getHourSound());
		sndUptime.setAudioFileName(initinfo.getUptimeHourSound());
		sndWork.setAudioFileName(initinfo.getPomodoroWorkSound());
		sndBrk.setAudioFileName(initinfo.getPomodoroBreakSound());
		sndRest.setAudioFileName(initinfo.getPomodoroRestSound());
		sndToRun.setDefaultSoundsDir(initinfo.getDefaultsDir() + "/sounds");
		setOneEnabled();
	}

	private void updateAnalogClockTimezone(TimeZone updZone)
	{
		SimpleDateFormat anaFormat = new SimpleDateFormat("zzz");
		anaFormat.setTimeZone(updZone);
		jLAnaPreview.setTime(10, 10, 5, "AM", anaFormat.format(new Date()));
	}

	private JButton createSmallButton(Image buttonImg, String buttonText, ActionListener actListenter)
	{
		JButton smallbutton  = new JButton(new ImageIcon(buttonImg.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		smallbutton.setActionCommand(buttonText);
		smallbutton.setToolTipText(buttonText);
		smallbutton.setMargin(new Insets(2, 5, 2, 5));
		smallbutton.addActionListener(actListenter);
		return smallbutton;
	}

	private void updateImagesList(File imgFile)
	{
		int selectIndex = -1;
		try {
			imgFileList.setDirectory(imgFile);
			selectIndex = imgFileList.getNextMatch(imgFile.toString(), 0, Position.Bias.Forward);
		} catch (IllegalArgumentException e) {
			System.out.println("Selected image " + imgFile.toString() + " Not found in list.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("Source jar file URL not formatted according to to RFC2396.");
			e.printStackTrace();
		}
		if (selectIndex != -1)
			imgFileList.setSelectedValue(imgFile, true);
		else
			imgFileList.setSelectedIndex(0);
		imgFileList.ensureIndexIsVisible(imgFileList.getSelectedIndex());
	}

	private String getAlarmDescription()
	{
		if (alarmList.getSelectedIndex() == -1)
		{
			return "Click on an alarm list item to view its description.";
		}
		else
		{
			TimeBean         tb     = (TimeBean)data.elementAt(alarmList.getSelectedIndex());
			String           ltext  = "<html>This alarm \"(alarm name)\", is scheduled to start on (start time) and repeat (multi) (interval).<p><p>Its next run will be (next run).<p><p>A (run type) runs at the scheduled time.</html>";
			SimpleDateFormat tmp1   = chooseDate.getFormat();
			SimpleDateFormat tmp2   = ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat();
			GregorianCalendar ccal  = new GregorianCalendar();
			String           tmp3[] = {"never", "minute", "hour", "day", "week", "??", "year", "month", "month same weekday"};
			int              tmp4   = 0;
			ltext  = ltext.replace("(alarm name)", tb.getName());
			ltext  = ltext.replace("(start time)", tmp1.format(tb.getAlarmTriggerTime()) + " at " + tmp2.format(tb.getAlarmTriggerTime()));
			tmp4   = tb.getAlarmRepeatInterval();
			ltext  = ltext.replace("(multi)", tmp4 == 0 ? "" : "every " + tb.getRepeatMultiple());
			ltext  = ltext.replace("(interval)", tmp4 == 0 ? tmp3[tmp4] : tmp3[tmp4] + "(s)");
			ccal.setTime(tb.getNextAlarmTriggerTime());
			ccal.add(Calendar.SECOND, 1);
			ltext  = ltext.replace("(next run)", tmp4 == 0 ? "never" : "on " + tmp1.format(ccal.getTime()) + " at " + tmp2.format(ccal.getTime()));
			tmp4   = tb.getAlarmExecutionOutputType();
			ltext  = ltext.replace("(run type)", ((tmp4 % 2 != 0) ? " alarm sound," : "") + (((tmp4 % 3 == 0) || (tmp4 == 2)) ? " system beep," : "") + ((tmp4 > 3) ? " message" : ""));
			return ltext;
		}
	}

	private void setOneEnabled()
	{
		imgFileList.setEnabled(useImg.isSelected());
		selectDir.setEnabled(useImg.isSelected());
		resetImgDir.setEnabled(useImg.isSelected());
		picLabel.setEnabled(useImg.isSelected());
		layLabel.setEnabled(useImg.isSelected());
		comboTLayout.setEnabled(useImg.isSelected());
		selBackCol.setEnabled(useCol.isSelected());
		resBackCol.setEnabled(useCol.isSelected());
		transLevel.setEnabled(useCol.isSelected() && (information.isWindowAlphaSupported() || information.isPixelAlphaSupported()));
		jlTransSlide.setEnabled(useCol.isSelected() && (information.isWindowAlphaSupported() || information.isPixelAlphaSupported()));
		slowUpd.setEnabled(useTrans.isSelected());
		comboPomodoro.setEnabled(pomodoroTime.isSelected());
		jLPomFormat.setEnabled(pomodoroTime.isSelected());
		comboPomFmt.setEnabled(pomodoroTime.isSelected());
		cbPomLabel.setEnabled(pomodoroTime.isSelected());
		cbPomCountdown.setEnabled(pomodoroTime.isSelected());
		tzLabel.setEnabled(selTimeZone.isSelected());
		tzCb.setEnabled(selTimeZone.isSelected());
		comboTz.setEnabled(selTimeZone.isSelected());
		analogCb0.setEnabled(selTimeZone.isSelected());
		analogCb1.setEnabled(selTimeZone.isSelected());
		analogCb2.setEnabled(selTimeZone.isSelected());
		cbShowDays.setEnabled(sysUpTime.isSelected());
		jLDayTxt.setEnabled(cbShowDays.isSelected());
		dSymbol.setEnabled(cbShowDays.isSelected());
		jLUptimeTxt.setEnabled(sysUpTime.isSelected());
		uSymbol.setEnabled(sysUpTime.isSelected());
		jLHourTxt.setEnabled(sysUpTime.isSelected());
		hSymbol.setEnabled(sysUpTime.isSelected());
		jLMinTxt.setEnabled(sysUpTime.isSelected());
		mSymbol.setEnabled(sysUpTime.isSelected());
		jLSecTxt.setEnabled(sysUpTime.isSelected());
		sSymbol.setEnabled(sysUpTime.isSelected());
		selLineCol.setEnabled(lBorder.isSelected());
		resLineCol.setEnabled(lBorder.isSelected());
		thickLabel.setEnabled(lBorder.isSelected());
		thickSpinner.setEnabled(lBorder.isSelected());
		if ((analogCb0.isEnabled() || analogCb1.isEnabled() || analogCb2.isEnabled()) && analogCb2.isSelected()) {
			analogCb0.setSelected(true);
			analogCb1.setSelected(true);
			analogCb2.setSelected(true);
			digitalPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
			analogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			jLDigiPreview.setEnabled(false);
			jLAnaPreview.setEnabled(true);
			((CardLayout)cardComboPanel.getLayout()).show(cardComboPanel, "ClockFormat");
		} else {
			analogCb0.setSelected(false);
			analogCb1.setSelected(false);
			analogCb2.setSelected(false);
			digitalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			analogPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
			jLDigiPreview.setEnabled(true);
			jLAnaPreview.setEnabled(false);
			((CardLayout)cardComboPanel.getLayout()).show(cardComboPanel, "DateFormat");
		}
		bdrPreview.setText(analogCb1.isSelected() ? "" : "Preview");
		bdrPreview.setClockMode(analogCb1.isSelected());
		picLabel.setText(analogCb0.isSelected() ? "" : "Preview");
		picLabel.setClockMode(analogCb0.isSelected());
		if (analogCb1.isSelected()) {
			bdrPreview.setPreferredSize(new Dimension(100, 100));
			picLabel.setPreferredSize(new Dimension(100, 100));
		} else {
			bdrPreview.setPreferredSize(new Dimension(100, 30));
			picLabel.setPreferredSize(new Dimension(200, 30));
		}
		comboDialBdr.setEnabled(analogCb1.isSelected());
		
		try
		{
			if (nativeLook.isSelected())
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				nativeLook.setText("Uncheck to view this in cross platform look and feel");
			}
			else
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				nativeLook.setText("Check to view this in system's look and feel");
			}
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception exception) { exception.printStackTrace(); }
	}
	
	private void alarmStartOnOrStartAfter()
	{
		if (optStartOn.isSelected())
		{
			timeSpinner1.setEnabled(true);
			countSpinner2.setEnabled(false);
			jLAlmDays.setEnabled(false);
			timeSpinner2.setEnabled(false);
			jLAlmHrs.setEnabled(false);
			chooseDate.setEnabled(true);
		}
		else if (optStartAfter.isSelected())
		{
			timeSpinner1.setEnabled(false);
			countSpinner2.setEnabled(true);
			jLAlmDays.setEnabled(true);
			timeSpinner2.setEnabled(true);
			jLAlmHrs.setEnabled(true);
			chooseDate.setEnabled(false);
		}
	}

	private void alarmViewOnlyMode(boolean ena)
	{
		if (ena) add.setText("Add");
		else add.setText("Commit");
		if (ena) edit.setText("Edit");
		else edit.setText("Discard");
		remove.setEnabled(ena);
		test.setEnabled(ena);
		alarmList.setEnabled(ena);
	}

	private void setSelectedFont(Font font1)
	{
		if (font1 == null)
			throw new NullPointerException("The Font Parameter Is Blank.");
		String  s       = font1.getFamily();
		Integer integer = Integer.valueOf(font1.getSize());
		int     i       = font1.getStyle();
		if (ExUtils.contains(font1.getFontName(), "bold", true))
			i = 1;
		else if (ExUtils.contains(font1.getFontName(), "italic", true))
			i = 2;
		else if (ExUtils.contains(font1.getFontName(), "italic", true) && ExUtils.contains(font1.getFontName(), "bold", true))
			i = 3;
		cFontList.setSelectedValue(s, true);
		cFontList.setText(s);
		cFontSizeList.setSelectedValue(integer, true);
		cFontSizeList.setText(integer.toString());
		cFontStyleList.setSelectedIndex(i);
		cFontStyleList.setText(cFontStyleList.getSelectedValue().toString());
	}

	private Font getSelectedFont()
	{
		String s = cFontList.getText();
		int i;
		try
		{
			i = Integer.parseInt(cFontSizeList.getText().isEmpty() ? "10" : cFontSizeList.getText());
		}
		catch (NumberFormatException numberformatexception)
		{
			i = 10;
			numberformatexception.printStackTrace();
		}
		int j = cFontStyleList.getSelectedIndex();
		Font font1 = new Font(s, j, i);
		return new Font(font1.getFontName(), 0, font1.getSize());
	}

	public Date getSelectedAlarmTime()
	{
		Date temp = new Date();
		if (optStartOn.isSelected())
		{
			String           pttn    = chooseDate.getFormat().toPattern() + ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat().toPattern();
			String           maintmp = chooseDate.getFormat().format(chooseDate.getDate())+((JSpinner.DateEditor)timeSpinner1.getEditor()).getTextField().getText();
			SimpleDateFormat sdf     = new SimpleDateFormat(pttn);
			try
			{
				temp = sdf.parse(maintmp,new ParsePosition(0));
			}
			catch (NullPointerException pe)
			{
				System.out.println("Something wrong happened in date format.");
				pe.printStackTrace();
			}
		}
		else if (optStartAfter.isSelected())
		{
			Date selDate = ((JSpinner.DateEditor)timeSpinner2.getEditor()).getModel().getDate();
			GregorianCalendar selCal = new GregorianCalendar();
			selCal.setTime(selDate);
			int selDay = Integer.parseInt(countSpinner2.getValue().toString());
			int selHour = selCal.get(Calendar.HOUR_OF_DAY);
			int selMin = selCal.get(Calendar.MINUTE);
			temp = ExUtils.getSystemStartTime();
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(temp);
			startCal.add(Calendar.DAY_OF_MONTH, selDay);
			startCal.add(Calendar.HOUR_OF_DAY, selHour);
			startCal.add(Calendar.MINUTE, selMin);
			temp = startCal.getTime();
		}
		GregorianCalendar second0 = new GregorianCalendar();
		second0.setTime(temp);
		second0.set(Calendar.SECOND,0);
		return second0.getTime();
	}
	
	public void setSelectedAlarmTime(Date date)
	{
		if (optStartOn.isSelected())
		{
			chooseDate.setDate(date);
			timeSpinner1.setValue(date);
		}
		else if (optStartAfter.isSelected())
		{
			long startDate = ExUtils.getSystemStartTime().getTime();
			long savedDate = date.getTime();
			Duration dur = Duration.ofMillis(savedDate - startDate);
			long durDays = dur.toDays();
			dur = dur.minusDays(durDays);
			long durHours = dur.toHours();
			dur = dur.minusHours(durHours);
			long durMin = dur.toMinutes();
			dur = dur.minusMinutes(durMin);
			long durSec = dur.getSeconds();
			int offset = -1 * (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings());
			Calendar setCal = Calendar.getInstance();
			setCal.setTimeInMillis(offset);
			setCal.add(Calendar.HOUR_OF_DAY, (int)durHours);
			setCal.add(Calendar.MINUTE, (int)durMin);
			setCal.add(Calendar.SECOND, (int)durSec + 1);
			countSpinner2.setValue(durDays);
			timeSpinner2.setValue(setCal.getTime());
		}
	}
	
	public void setSelectedRepeatInterval(int inv, int multiple)
	{
		if (inv == 5) inv = 7;
		if (inv <= 6) period.setSelectedIndex(inv);
		else if (inv > 6 && inv < 9)
		{
			period.setSelectedIndex(5);
			dateOrWeek.setSelectedIndex(inv - 7);
		}
		rept.setSelected(period.getSelectedIndex() != 0);
		countSpinner1.setValue(multiple);
	}
	
	public int getSelectedRepeatInterval()
	{
		if (period.getSelectedIndex() == 5)
			return dateOrWeek.getSelectedIndex() + 7;
		else
			return period.getSelectedIndex();
	}
	
	public void selectStartAfterTimeOption(boolean flag)
	{
		optStartOn.setSelected(!flag);
		optStartAfter.setSelected(flag);
		alarmStartOnOrStartAfter();
	}
	
	public boolean startAfterTimeIsSelected()
	{
		alarmStartOnOrStartAfter();
		return optStartAfter.isSelected();
	}
	
	public void selectAlarmExecOutputOption(int type)
	{
		runSnd.setSelected(type % 2 != 0); //for 1 (ExUtils.AUDIO_ALARM);
		runMsg.setSelected(type > 3); //for 4 (ExUtils.MESSAGE_ALARM);
		// runBeep.setSelected((type % 3 == 0) || (type == 2)); //for 2 (ExUtils.BEEP_ALARM);
		sndToRun.setEnabled(type % 2 != 0);
	}
	
	public int getSelectedAlarmExecOutputOption()
	{
		int totaltmp = 0;
		if  (runSnd.isSelected()) totaltmp += ExUtils.AUDIO_ALARM;  // value=1
		// if  (!runSnd.isSelected()) totaltmp += ExUtils.BEEP_ALARM;  // value=2
		if  (runMsg.isSelected()) totaltmp += ExUtils.MESSAGE_ALARM;// value=4
		return totaltmp;
	}

	
	/** 
	 * Save all settings in the form of class InitInfo.
	 * @return InitInfo
	 * @throws Exception
	 */
	public InitInfo applySettings() throws Exception
	{
		information.setFont(getSelectedFont());
		information.setForeground(((ButtonIcon)selFontCol.getIcon()).getEnabledColor());
		information.setUsingImage(useImg.isSelected());
		information.setGlassEffect(useTrans.isSelected());
		information.setSlowTransUpdating(slowUpd.isSelected());
		if (!imgFileList.isSelectionEmpty())
		{
			File testfile=(File)imgFileList.getSelectedValue();
			information.setImageFile(testfile.getAbsolutePath());
		}
		information.setImageStyle(picLabel.getImageLayout());
		information.setBackground(((ButtonIcon)selBackCol.getIcon()).getEnabledColor());
		information.setOpacity((float)transLevel.getValue() / 20);
		information.setBorder(bdrPreview.getBorder());
		if (lBorder.isSelected())
			information.setLineColor(((ButtonIcon)selLineCol.getIcon()).getEnabledColor());
		information.setTooltip(enTooltip.isSelected());
		information.setFixed(!cbMovable.isSelected());
		information.setRoundCorners(roundBdr.isSelected());
		information.setNativeLook(nativeLook.isSelected());
		information.setHourSound(sndHour.getAudioFileName());
		information.setUptimeHourSound(sndUptime.getAudioFileName());
		information.setPomodoroWorkSound(sndWork.getAudioFileName());
		information.setPomodoroBreakSound(sndBrk.getAudioFileName());
		information.setPomodoroRestSound(sndRest.getAudioFileName());
		SimpleDateFormat simpledateformat = new SimpleDateFormat();
		if (selTimeZone.isSelected())
		{
			try
			{
				simpledateformat.applyPattern(comboDateFmt.getSelectedItem().toString());
			}
			catch (Exception exception)
			{
				throw new IllegalArgumentException("The following error occured in date/time format:\n" + exception.toString() + "\n" + exception.getMessage() + " in \"" + comboDateFmt.getSelectedItem().toString() + "\"");
			}
			if (tzCb.isSelected())
				information.setDisplayMethod("CURTZ");
			else
				information.setDisplayMethod("GMTTZ");
			information.setAnalogClock(analogCb0.isSelected() || analogCb1.isSelected() || analogCb2.isSelected());
			information.setAnalogClockOption(comboClockFmt.getItemAt(comboClockFmt.getSelectedIndex()).getId() + comboDialBdr.getItemAt(comboDialBdr.getSelectedIndex()).getId());
			information.setTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex()));
			information.setZonedTimeFormat(comboDateFmt.getSelectedItem().toString());
		}
		else if (pomodoroTime.isSelected())
		{
			try
			{
				simpledateformat.applyPattern(comboPomFmt.getSelectedItem().toString());
			}
			catch (Exception exception)
			{
				throw new IllegalArgumentException("The following error occured in pomodoro format:\n" + exception.toString() + "\n" + exception.getMessage() + " in \"" + comboPomFmt.getSelectedItem().toString() + "\"");
			}
			information.setDisplayMethod("POMODORO");
			information.setPomodoroTask(comboPomodoro.getSelectedItem().toString());
			information.setPomodoroFormat(comboPomFmt.getSelectedItem().toString());
			information.setPomodoroLeadingLabel(cbPomLabel.isSelected());
			information.setPomodoroCountdown(cbPomCountdown.isSelected());
		}
		else if (sysUpTime.isSelected())
		{
			try
			{
				simpledateformat.applyPattern("'" + uSymbol.getSelectedItem().toString() + "'");
			}
			catch (Exception exception2)
			{
				throw new IllegalArgumentException("The following error occured in \"Up-time symbol\" field of Up-time format:\n" + exception2.toString() + "\n" + exception2.getMessage() + " in \"" + uSymbol.getSelectedItem().toString() + "\"");
			}
			try
			{
				if (cbShowDays.isSelected())
					simpledateformat.applyPattern("'" + dSymbol.getSelectedItem().toString() + "'");
			}
			catch (Exception exception0)
			{
				throw new IllegalArgumentException("The following error occured in \"Day symbol\" field of Up-time format:\n" + exception0.toString() + "\n" + exception0.getMessage() + " in \"" + dSymbol.getSelectedItem().toString() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + hSymbol.getSelectedItem().toString() + "'");
			}
			catch (Exception exception3)
			{
				throw new IllegalArgumentException("The following error occured in \"Hour symbol\" field of Up-time format:\n" + exception3.toString() + "\n" + exception3.getMessage() + " in \"" + hSymbol.getSelectedItem().toString() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + mSymbol.getSelectedItem().toString() + "'");
			}
			catch (Exception exception4)
			{
				throw new IllegalArgumentException("The following error occured in \"Minute symbol\" field of Up-time format:\n" + exception4.toString() + "\n" + exception4.getMessage() + " in \"" + mSymbol.getSelectedItem().toString() + "\"");
			}
			try
			{
				simpledateformat.applyPattern("'" + sSymbol.getSelectedItem().toString() + "'");
			}
			catch (Exception exception5)
			{
				throw new IllegalArgumentException("The following error occured in \"Second symbol\" field of Up-time format:\n" + exception5.toString() + "\n" + exception5.getMessage() + " in \"" + sSymbol.getSelectedItem().toString() + "\"");
			}
			information.setDisplayMethod("UPTIME");
			if (cbShowDays.isSelected())
				information.setUpTimeFormat("'" + uSymbol.getSelectedItem().toString() + "'DD'" + dSymbol.getSelectedItem().toString() + "'HH'" + hSymbol.getSelectedItem().toString() + "'mm'" + mSymbol.getSelectedItem().toString() + "'ss'" + sSymbol.getSelectedItem().toString() + "'");
			else
				information.setUpTimeFormat("'" + uSymbol.getSelectedItem().toString() + "'HH'" + hSymbol.getSelectedItem().toString() + "'mm'" + mSymbol.getSelectedItem().toString() + "'ss'" + sSymbol.getSelectedItem().toString() + "'");
		}
		return information;
	}
	
	
	/** 
	 * Save all the alarms in the form of vector list of TimeBean
	 * @return Vector<TimeBean>
	 * @throws NullPointerException
	 */
	public Vector<TimeBean> applyAlarms() throws NullPointerException
	{
		if (opmode != ChooserBox.ALARM_DISCARD) saveCurrentAlarm();
		return data;
	}
	
	private void setTransparentBackground()
	{
		if (useTrans.isSelected())
		{
			useTrans.setSelected(true);
		}
		else
		{
			if (useTrans.isSelected())
				JOptionPane.showMessageDialog(this,"Error while setting transparency property. Cannot set.","System restriction",JOptionPane.ERROR_MESSAGE);
			useTrans.setSelected(false);
			slowUpd.setSelected(false);
		}
	}
	
	
	/** 
	 * Saves the current TimeBean alarm in add or edit mode, to the data vector.
	 * This also updates the resultant data vector to the alarmList JList.
	 * @throws NullPointerException
	 */
	private void saveCurrentAlarm() throws NullPointerException
	{
		if (alarmName.getText().equals("") || alarmName.getText().equals(null))
			throw new NullPointerException("Blank name");
		if ((sndToRun.getAudioFileName().isEmpty() && runSnd.isSelected()))
			throw new NullPointerException("No Audio File");
		TimeBean tb = new TimeBean();
		tb.setName(alarmName.getText());
		tb.setSystemStartTimeBasedAlarm(startAfterTimeIsSelected()); // must be set before setRuntime();
		if (startAfterTimeIsSelected()) tb.setAlarmTriggerTime(getSelectedAlarmTime());//if op2 then nextRuntime
		else tb.setAlarmTriggerTime(getSelectedAlarmTime()); // should be set instead of runtime;
		tb.setAlarmRepeatInterval(getSelectedRepeatInterval());
		tb.setRepeatMultiple(rept.isSelected() ? (int)countSpinner1.getValue() : 0);
		tb.setAlarmExecutionOutputType(getSelectedAlarmExecOutputOption());
		tb.setAlarmSound(sndToRun.getAudioFileName());
		if (opmode == ChooserBox.ALARM_ADD)
		{
			data.addElement(tb);
			alarmList.setListData(data);
		}
		else if (opmode == ChooserBox.ALARM_EDIT)
		{
			data.setElementAt(tb, selIndex);
			alarmList.setListData(data);
		}
		((CardLayout)(bottomCards.getLayout())).show(bottomCards, "FinishEdit");
		alarmViewOnlyMode(true);
		opmode = ChooserBox.ALARM_DISCARD;
	}

	private void validateTimeFormat()
	{
		String privT = "";
		SimpleDateFormat sd = new SimpleDateFormat();
		try
		{
			if (selTimeZone.isSelected())
			{
				sd.applyPattern(comboDateFmt.getSelectedItem().toString());
				sd.setTimeZone(TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex())));
				privT = sd.format(new Date());
			}
			else if (sysUpTime.isSelected())
			{
				sd.applyPattern("'" + uSymbol.getSelectedItem().toString() + "'");
				sd.applyPattern("'" + hSymbol.getSelectedItem().toString() + "'");
				sd.applyPattern("'" + mSymbol.getSelectedItem().toString() + "'");
				sd.applyPattern("'" + sSymbol.getSelectedItem().toString() + "'");
				String pttn = "'" + uSymbol.getSelectedItem().toString() + 
				(cbShowDays.isSelected() ? "'DD'" + dSymbol.getSelectedItem().toString() : "") + 
								"'HH'" + hSymbol.getSelectedItem().toString() + 
								"'mm'" + mSymbol.getSelectedItem().toString() + 
								"'ss'" + sSymbol.getSelectedItem().toString() + "'";
				privT = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), pttn);
			}
			else if (pomodoroTime.isSelected())
			{
				String pttn = comboPomFmt.getSelectedItem().toString();
				sd.applyPattern(pttn);
				pom   = new Pomodoro(comboPomodoro.getSelectedItem().toString());
				privT = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(cbPomCountdown.isSelected()), pttn, pom.getRunningLabel(), cbPomLabel.isSelected());
			}
			jLDigiPreview.setText(privT);
			exceptionActive = false;
		}
		catch (Exception exception)
		{
			SwingUtilities.invokeLater(new ValidateTimeAid());
			exceptionActive = true;
		}
	}

	public void actionPerformed(ActionEvent actionevent)
	{
		String     comm = actionevent.getActionCommand();
		CardLayout cl   = (CardLayout)(bottomCards.getLayout());
		if (comm.equals("Reset Font"))
		{
			fontPreview.setFont((new JLabel()).getFont());
			setSelectedFont((new JLabel()).getFont());
		}
		else if (comm.equals("Choose Font Color"))
		{
			Color color  = ((ButtonIcon)selFontCol.getIcon()).getEnabledColor();
			Color color3 = JColorChooser.showDialog(new Frame(), "Choose Font Color...", color);
			if (color3 == null)
				color3 = color;
			((ButtonIcon)selFontCol.getIcon()).setEnabledColor(color3);
			fontPreview.setForeground(color3);
		}
		else if (comm.equals("Reset Color"))
		{
			((ButtonIcon)selFontCol.getIcon()).setEnabledColor(Color.BLACK);
			fontPreview.setForeground(Color.BLACK);
		}
		else if (comm.equals("Beveled Raised Border") && bRaised.isSelected())
		{
			bdrPreview.setBorder(BorderFactory.createRaisedBevelBorder());
		}
		else if (comm.equals("Beveled Lowered Border") && bLowered.isSelected())
		{
			bdrPreview.setBorder(BorderFactory.createLoweredBevelBorder());
		}
		else if (comm.equals("Etched Raised Border") && eRaised.isSelected())
		{
			bdrPreview.setBorder(BorderFactory.createEtchedBorder(0));
		}
		else if (comm.equals("Etched Lowered Border") && eLowered.isSelected())
		{
			bdrPreview.setBorder(BorderFactory.createEtchedBorder(1));
		}
		else if (comm.equals("Line Border") && lBorder.isSelected())
		{
			thickSpinner.setValue(intThick);
			bdrPreview.setBorder(BorderFactory.createLineBorder(((ButtonIcon)selLineCol.getIcon()).getEnabledColor(), intThick));
		}
		else if (comm.equals("Don't Use Any Border") && nBorder.isSelected())
		{
			bdrPreview.setBorder(null);
		}
		else if (comm.equals("Line Border Color") && lBorder.isSelected())
		{
			Color color1 = ((ButtonIcon)selLineCol.getIcon()).getEnabledColor();
			Color color4 = JColorChooser.showDialog(new Frame(), "Choose Line Color...", color1);
			if (color4 == null)
				color4 = color1;
			((ButtonIcon)selLineCol.getIcon()).setEnabledColor(color4);
			bdrPreview.setBorder(BorderFactory.createLineBorder(color4, intThick));
		}
		else if (comm.equals("Default Line Color") && lBorder.isSelected())
		{
			((ButtonIcon)selLineCol.getIcon()).setEnabledColor(resLineCol.getBackground());
			bdrPreview.setBorder(BorderFactory.createLineBorder(resLineCol.getBackground(), intThick));
		}
		else if (comm.equals("AnalogClock"))
		{
			if (actionevent.getSource().equals(analogCb2)) {
				analogCb1.setSelected(analogCb2.isSelected());
				analogCb0.setSelected(analogCb2.isSelected());
			} else if (actionevent.getSource().equals(analogCb1)) {
				analogCb2.setSelected(analogCb1.isSelected());
				analogCb0.setSelected(analogCb2.isSelected());
			} else if (actionevent.getSource().equals(analogCb0)) {
				analogCb1.setSelected(analogCb0.isSelected());
				analogCb2.setSelected(analogCb0.isSelected());
			}
			jLAnaPreview.setPreferredSize(analogPanel.getSize());
			updateAnalogClockTimezone(TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex())));
		}
		else if (comm.equals("LocalTz"))
		{
			if (tzCb.isSelected())	comboTz.setSelectedItem(TimeZone.getDefault().getID());
			else comboTz.setSelectedItem(information.getTimeZone());
		}
		else if (comm.equals("ShowDays"))
		{
			if (cbShowDays.isSelected())
			{
				String s1    = information.getUpTimeFormat();
				String arr[] = s1.split("\'");
				if (information.isDayShowing())
					dSymbol.setSelectedItem(arr[3]);
				else
					dSymbol.setSelectedIndex(0);
			}
			validateTimeFormat();
		}
		else if (comm.equals("Leading label"))
		{
			validateTimeFormat();
		}
		else if (comm.equals("Reset to default values"))
		{
			if (selTimeZone.isSelected())
			{
				tzCb.setSelected(true);
				comboTz.setSelectedItem(TimeZone.getDefault().getID());
				comboDateFmt.setSelectedItem("zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
			}
			else if (sysUpTime.isSelected())
			{
				cbShowDays.setSelected(false);
				uSymbol.setSelectedItem("Up-Time: ");
				dSymbol.setSelectedItem("-day(s)");
				hSymbol.setSelectedItem("-hour(s), ");
				mSymbol.setSelectedItem("-minute(s), ");
				sSymbol.setSelectedItem("-second(s)");
			}
			else if (pomodoroTime.isSelected())
			{
				comboPomodoro.setSelectedItem("25 min. work, 5 min. break, 30 min. rest");
				comboPomFmt.setSelectedItem("mm:ss");
				cbPomLabel.setSelected(false);
				cbPomCountdown.setSelected(false);
			}
		}
		else if (comm.equals("Help on date time format characters"))
		{
			if (!exceptionActive)
				new FormatHelp(this);
			else
				validateTimeFormat();
		}
		else if (comm.equals("Choose Image Directory"))
		{
			JFileChooser jfilechooser = new JFileChooser(imgFileList.getDirectory());
			jfilechooser.setFileSelectionMode(1);
			jfilechooser.setMultiSelectionEnabled(false);
			int i = jfilechooser.showOpenDialog(this);
			if (i == 0) {
				try {
					imgFileList.setDirectory(jfilechooser.getSelectedFile());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		else if (comm.equals("Reset to default images"))
		{
			updateImagesList(new File("."));
		}
		else if (comm.equals("Choose Background Color"))
		{
			Color color2 = ((ButtonIcon)selBackCol.getIcon()).getEnabledColor();
			Color color5 = JColorChooser.showDialog(new Frame(), "Choose Background...", color2);
			if (color5 == null)
				color5 = color2;
			((ButtonIcon)selBackCol.getIcon()).setEnabledColor(color5);
		}
		else if (comm.equals("Default Color"))
		{
			((ButtonIcon)selBackCol.getIcon()).setEnabledColor(resBackCol.getBackground());
		}
		else if (comm.equals("Transparent background"))
		{
			setTransparentBackground();
		}
		else if (comm.equals("Add"))
		{
			cl.show(bottomCards,"AddEdit");
			alarmViewOnlyMode(false);
			opmode = ChooserBox.ALARM_ADD;
			alarmName.setText("");
			selectStartAfterTimeOption(false); //must be called before timeIn();
			setSelectedAlarmTime(new Date());
			setSelectedRepeatInterval(0, 1);
			selectAlarmExecOutputOption(4); // for msg;
		}
		else if (comm.equals("Edit"))
		{
			selIndex=alarmList.getSelectedIndex();
			if (selIndex != -1)
			{
				cl.show(bottomCards,"AddEdit");
				alarmViewOnlyMode(false);
				opmode      = ChooserBox.ALARM_EDIT;
				TimeBean tb = (TimeBean)data.elementAt(selIndex);
				alarmName.setText(tb.getName());
				boolean isAfterOpt = tb.isSystemStartTimeBasedAlarm();
				selectStartAfterTimeOption(isAfterOpt); //must be called before timeIn();
				if (isAfterOpt)
					setSelectedAlarmTime(tb.getAlarmTriggerTime());
				else
					setSelectedAlarmTime(tb.getAlarmTriggerTime());
				setSelectedRepeatInterval(tb.getAlarmRepeatInterval(), tb.getRepeatMultiple());
				selectAlarmExecOutputOption(tb.getAlarmExecutionOutputType());
				sndToRun.setAudioFileName(tb.getAlarmSound());
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
				else if (exception.getMessage().equals("No Audio File"))
				{
					JOptionPane.showMessageDialog(this,"No audio file selected to play alarm.","No Alarm Sound",JOptionPane.INFORMATION_MESSAGE);
					sndToRun.grabFocus();
				}
			}
		}
		else if (comm.equals("Discard"))
		{
			cl.show(bottomCards,"FinishEdit");
			alarmViewOnlyMode(true);
			opmode = ChooserBox.ALARM_DISCARD;
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
		else if (comm.equals("Test"))
		{
			selIndex = alarmList.getSelectedIndex();
			if (selIndex != -1)
			{
				TimeBean tb = (TimeBean)data.elementAt(selIndex);
				ExUtils.runAlarm(tb, this, 50);
			}
		}
		else if (comm.equals("and repeat every"))
		{
			if (rept.isSelected()) period.setSelectedIndex(1);
			else period.setSelectedIndex(0);
		}
		else if (comm.equals("Play selected sound") || comm.equals("Show message"))
		{
			if (!runSnd.isSelected() && !runMsg.isSelected())
				runMsg.setSelected(true);
			sndToRun.setEnabled(runSnd.isSelected());
		}
		else if (comm.equals("POMODORO_RADIO_BUTTON") || comm.equals("TIME_RADIO_BUTTON") || comm.equals("UPTIME_RADIO_BUTTON"))
		{
			validateTimeFormat();
		}
		alarmStartOnOrStartAfter();
		setOneEnabled();
	}

	public void itemStateChanged(ItemEvent ie)
	{
		Object source = ie.getSource();
		if (ie.getStateChange() == ItemEvent.SELECTED && source.equals(comboTLayout))
		{
			String comm = ie.getItem().toString();
			switch (comm) {
				case TLabel.V_TILE_TEXT:
					picLabel.setImageLayout(TLabel.V_TILE); break;
				case TLabel.TILE_TEXT:
					picLabel.setImageLayout(TLabel.TILE); break;
				case TLabel.H_TILE_TEXT:
					picLabel.setImageLayout(TLabel.H_TILE); break;
				case TLabel.CENTER_TEXT:
					picLabel.setImageLayout(TLabel.CENTER); break;
				case TLabel.FIT_TEXT:
					picLabel.setImageLayout(TLabel.FIT); break;
				case TLabel.STRETCH_TEXT:
					picLabel.setImageLayout(TLabel.STRETCH); break;
				default:
					picLabel.setImageLayout(TLabel.FIT); break;
			}
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED && source.equals(comboTz))
		{
			SimpleDateFormat sd = new SimpleDateFormat(information.getZonedTimeFormat());
			String tzId = comboTz.getItemAt(comboTz.getSelectedIndex());
			tzCb.setSelected(tzId.equals(TimeZone.getDefault().getID()));
			sd.setTimeZone(TimeZone.getTimeZone(tzId));
			updateAnalogClockTimezone(TimeZone.getTimeZone(tzId));
			jLDigiPreview.setText(sd.format(new Date()));
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED && source.equals(comboClockFmt))
		{
			AnalogClockFormat tempFmt = comboClockFmt.getItemAt(comboClockFmt.getSelectedIndex());
			jLAnaPreview.setAnalogClockOptions(tempFmt.getId());
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED && source.equals(comboDialBdr))
		{
			AnalogClockFormat tempFmt = comboDialBdr.getItemAt(comboDialBdr.getSelectedIndex());
			bdrPreview.setAnalogClockOptions(tempFmt.getId());
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED && (source.equals(comboDateFmt) ||
			source.equals(comboPomodoro) || source.equals(comboPomFmt) || source.equals(uSymbol) ||
			source.equals(hSymbol) || source.equals(mSymbol) || source.equals(sSymbol) || source.equals(dSymbol)))
		{
			validateTimeFormat();
		}
		else
		{
			if (ie.getItem().equals("Never")) rept.setSelected(false);
			else rept.setSelected(true);
			
			if (ie.getItem().equals("Month"))
			{
				jLAlmSame.setEnabled(true);
				dateOrWeek.setEnabled(true);
			}
			else
			{
				jLAlmSame.setEnabled(false);
				dateOrWeek.setEnabled(false);
			}
		}
	}

	public void valueChanged(ListSelectionEvent listselectionevent)
	{
		Object srcList = listselectionevent.getSource();
		if (srcList.equals(imgFileList))
		{
			if (imgFileList.isSelectionEmpty())
			{
				picLabel.setBackImage(null);
				picLabel.setText("No Preview");
			}
			else
			{
				picLabel.setBackImage((new ImageIcon(imgFileList.getSelectedValue().toString())).getImage());
				picLabel.setText(analogCb0.isSelected() ? "" : "Preview");
				// Bugfix: Remove dial ticks showing over image by default.
				if (information.getAnalogClockOption() % 1000 != 0 && analogCb1.isSelected()) comboDialBdr.setSelectedItem(anaDials.get(0));
			}
		}
		else if (srcList.equals(cFontList) || srcList.equals(cFontStyleList) || srcList.equals(cFontSizeList))
		{
			fontPreview.setFont(getSelectedFont());
		}
	}

	public void stateChanged(ChangeEvent ce)
	{
		Object cSrc = ce.getSource();
		if (cSrc.equals(thickSpinner)) {
			Border currBorder = bdrPreview.getBorder();
			if(currBorder instanceof LineBorder) {
				intThick = (int)thickSpinner.getValue();
				bdrPreview.setBorder(new LineBorder(((LineBorder)currBorder).getLineColor(), intThick));
			}
		} else if (cSrc.equals(transLevel)) {
			String opacityPercent = Math.round((float)transLevel.getValue() / 20 * 100) + " %";
			jlTransSlide.setText("Opacity [ " + opacityPercent + " ]");
			transLevel.setToolTipText(opacityPercent);
		}
	}

	private class ValidateTimeAid implements Runnable {
		public void run() {
			ok.doClick();
			if (selTimeZone.isSelected())
				comboDateFmt.requestFocus();
			else if (sysUpTime.isSelected())
				uSymbol.requestFocus();
			else if (pomodoroTime.isSelected())
				comboPomFmt.requestFocus();
		}
	}

	protected class JListDoubleClickAction extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			Object src = me.getSource();
			if (src.equals(alarmList) && me.getClickCount() >= 2 && me.getButton() == MouseEvent.BUTTON1)
			{
				Rectangle dim = alarmList.getCellBounds(0, alarmList.getLastVisibleIndex());
				if (dim != null && dim.contains(me.getPoint())) {
					String des = getAlarmDescription();
					JOptionPane.showMessageDialog(alarmList, des,"Alarm Description",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	protected class AnalogClockFormat
	{
		private int id;
        private String description;

        AnalogClockFormat(int id, String description)
        {
            this.id = id;
            this.description = description;
        }

        public int getId()
        {
            return id;
        }

        public String getDescription()
        {
            return description;
        }

        public String toString()
        {
            return description;
        }
	}
}