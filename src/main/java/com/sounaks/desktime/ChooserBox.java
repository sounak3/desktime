package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
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
	private JRadioButton bRaised, bLowered, eRaised, eLowered, lBorder, rbNoIcon, rbTrayIcon, rbTaskIcon;
	private JCheckBox nBorder,cbMovable,enTooltip,nativeLook;
	private JButton selLineCol,resLineCol;
	private JComboBox<String> comboTz, comboDateFmt, comboPomodoro, comboPomFmt;
	private JComboBox<Item> comboClockFmt, comboDialBdr;
	private JComboBox<ExUtils.ROUND_CORNERS> roundBdr;
	private JList<TLabel.DIAL_OBJECTS_SIZE> handSize;
	private JRadioButton selTimeZone,sysUpTime,pomodoroTime;
	private JLabel layLabel, jLDayTxt, jLUptimeTxt, jLHourTxt, jLMinTxt, jLSecTxt, tzLabel, jlRoundBdr;
	private JCheckBox analogCb2, analogCb1, analogCb0, tzCb, cbPomLabel, cbPomCountdown, cbShowDays, cbLabelBdr1, cbLabelBdr2;
	private JComboBox<String> dSymbol, uSymbol, hSymbol, mSymbol, sSymbol;
	private JButton resetDefs,helpFormat,resetImgDir;
	private JLabel jlTransSlide,jLDateFormat,jLPomFormat, thickLabel;
	private JSlider transLevel;
	private TwilightPanel picBackPanel, picPanel;
	private JCheckBox useImg,useCol,foregroundTrans,useTrans,slowUpd;
	private ImageFileList imgFileList;
	private JButton selectDir,selBackCol,resBackCol;
	private JComboBox<String> comboTLayout;
	private JList<TimeBean> alarmList;
	private JLabel jLAlmSame, jLAlmDays, jLAlmHrs;
	private SoundPlayer sndHour, sndUptime, sndWork, sndBrk, sndRest, sndToRun;
	private JButton add,remove,edit,test;
	private JPanel bottomCards, digitalPanel, analogPanel, cardComboPanel, borderPanel, clockFormatsPane;
	private JComboBox<String> period, dateOrWeek;
	private JCheckBox rept, runSnd, runMsg, cbAmPm, cbTz, cbWkDy, cbDate;
	private JTextField alarmName;
	private JRadioButton optStartOn, optStartAfter;
	private JSpinner timeSpinner1, timeSpinner2, countSpinner1, countSpinner2, thickSpinner;
	private DateChooser chooseDate;
	private int opmode   = ChooserBox.ALARM_DISCARD;
	private int selIndex = -1;
	private int intThick = 2;
	private boolean exceptionActive = false;
	private JButton ok,cancel;
	private transient Pomodoro pom;
	private Vector <TimeBean>alarmsData;
	private transient Vector <Item> anaFormats, anaDials;
	private InitInfo information;

	public static final int FONT_TAB       = 0;
	public static final int BACKGROUND_TAB = 1;
	public static final int BORDER_TAB     = 2;
	public static final int TIMES_TAB      = 3;
	public static final int ALARMS_TAB     = 4;
	public static final int ALARM_ADD      = 11;
	public static final int ALARM_EDIT     = 13;
	public static final int ALARM_DISCARD  = 17;

	public static final int NO_APP_ICON    = 19;
	public static final int TASK_BAR_ICON  = 23;
	public static final int SYS_TRAY_ICON  = 29;

	private static final String PREVIEW            = "Preview";
	private static final String EMPTY              = "";
	private static final String NO_PREVIEW         = "No preview";
	private static final String CMD_ANALOG_CLOCK   = "AnalogClock";
	private static final String CMD_POM_LEAD_LABEL = "Leading label";
	private static final String CMD_ADD_EDIT       = "AddEdit";
	private static final String CMD_FINISH_EDIT    = "FinishEdit";
	private static final String IN_JOINER          = " in \"";
	private static final String CMD_CB_DIAL_OPTION = "AnalogClockDialOptions";
	private static final String CB_DIAL_LABEL_BDR  = "Dial label border";

	private static final String BEVELED_RAISED_BORDER  = "Beveled Raised Border";
	private static final String BEVELED_LOWERED_BORDER = "Beveled Lowered Border";
	private static final String ETCHED_RAISED_BORDER   = "Etched Raised Border";
	private static final String ETCHED_LOWERED_BORDER  = "Etched Lowered Border";
	private static final String LINE_BORDER            = "Line Border";
	private static final String NO_BORDER              = "Don't Use Any Border";

	private static final String[] digiFormats = {
		"yyyy.MM.dd G 'at' HH:mm:ss z",
		"zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy",
		"hh:mm a, z",
		"z':' hh:mm a",
		"h:mm a",
		"hh:mm:ss a",
		"z':' hh:mm:ss a",
		"H:mm:ss",
		"hh 'o''clock' a, zzzz",
		"K:mm a, z",
		"yyyyy.MMMMM.dd GGG hh:mm aaa",
		"EEE, d MMM yyyy HH:mm:ss Z",
		"YYYY-'W'ww-u",
		"EEE, MMM d, ''yy",
		"dd MMMMM yyyy",
		"dd.MM.yy",
		"MM/dd/yy"
	};

	final String[] layouts = {TLabel.V_TILE_TEXT, TLabel.H_TILE_TEXT, TLabel.TILE_TEXT, TLabel.STRETCH_TEXT, TLabel.FIT_TEXT, TLabel.CENTER_TEXT};

	public ChooserBox(InitInfo initinfo, Vector <TimeBean>alarminfo)
	{
		GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		information = initinfo;  //Transfering information from Desktop to this;
		getContentPane().setLayout(new BorderLayout());
		tabPane     = new JTabbedPane();
		//All configuration for Font Panel as follows:
		JPanel  jpanel      = new JPanel(new GridBagLayout());
		        resetFont   = new JButton("Reset Font");
		int    [] fontSizes = DeskStop.getFontSizes();
		Integer[] ainteger  = new Integer[fontSizes.length];
		for (int i = 0; i < fontSizes.length; i++)
			ainteger[i] = Integer.valueOf(fontSizes[i]);
		
		String[] as1   = {"Plain", "Bold", "Italic", "Bold Italic"};
		String[] as    = graphicsenvironment.getAvailableFontFamilyNames();
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
		jscrollpane.getViewport().setPreferredSize(new Dimension(200, 100));
		jscrollpane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font Preferences"));
		//All configurations for Border Panel as follows:
		JPanel jpanel1 = new JPanel(new GridBagLayout());
		analogCb1 = new JCheckBox("Analog clock dial");
		analogCb1.setActionCommand(CMD_ANALOG_CLOCK);
		cbLabelBdr1 = new JCheckBox(CB_DIAL_LABEL_BDR);
		borderPanel  = new JPanel();
		SpringLayout applyLayout = new SpringLayout();
		borderPanel.setLayout(applyLayout);
		bdrPreview	= new TLabel(PREVIEW);
		bdrPreview.setFont(new	Font(bdrPreview.getFont().getFamily(),	Font.BOLD, 14));
		borderPanel.add(bdrPreview);
		applyLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bdrPreview, 0, SpringLayout.HORIZONTAL_CENTER, borderPanel);
		applyLayout.putConstraint(SpringLayout.VERTICAL_CENTER, bdrPreview, 0, SpringLayout.VERTICAL_CENTER, borderPanel);
		borderPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		ButtonGroup buttongroup = new ButtonGroup();
		bRaised  = new JRadioButton(BEVELED_RAISED_BORDER);
		bLowered = new JRadioButton(BEVELED_LOWERED_BORDER);
		eRaised  = new JRadioButton(ETCHED_RAISED_BORDER);
		eLowered = new JRadioButton(ETCHED_LOWERED_BORDER);
		lBorder  = new JRadioButton(LINE_BORDER);
		nBorder  = new JCheckBox(NO_BORDER);
		buttongroup.add(bRaised);
		buttongroup.add(bLowered);
		buttongroup.add(eRaised);
		buttongroup.add(eLowered);
		buttongroup.add(lBorder);
		buttongroup.add(nBorder);
		JPanel handSizesPane = new JPanel(new BorderLayout());
		handSize = new JList<>(TLabel.DIAL_OBJECTS_SIZE.values());
		handSize.addListSelectionListener(this);
		handSize.setBorder(BorderFactory.createLoweredBevelBorder());
		handSizesPane.add(handSize, BorderLayout.CENTER);
		handSizesPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Hand size"));
		JSeparator jseparator1  = new JSeparator();
		Component component    = Box.createHorizontalStrut(8);
		selLineCol = new JButton("Line Border Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resLineCol = new JButton("Reset", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		resLineCol.setActionCommand("Default Line Color");
		JPanel    thickPanel = new JPanel();
		BoxLayout thkly      = new BoxLayout(thickPanel, BoxLayout.X_AXIS);
		thickPanel.setLayout(thkly);
		thickLabel   = new JLabel("Thickness", SwingConstants.RIGHT);
		thickSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		thickSpinner.addChangeListener(this);
		thickPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		thickLabel.setLabelFor(thickSpinner);
		thickPanel.add(thickLabel);
		thickPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		thickPanel.add(thickSpinner);
		thickPanel.add(Box.createHorizontalGlue());
		anaDials = new Vector<>();
		anaDials.add(new Item(TLabel.SHOW_NONE, "No dial border"));
		anaDials.add(new Item(TLabel.ANALOG_BORDER, "Show dial border only"));
		anaDials.add(new Item((TLabel.ANALOG_BORDER + TLabel.MAJOR_TICK), "Dial with major hour marks"));
		anaDials.add(new Item((TLabel.ANALOG_BORDER + TLabel.HOUR_TICK), "Dial with hour markers"));
		anaDials.add(new Item((TLabel.ANALOG_BORDER + TLabel.MINUTE_TICK), "Dial with minute markers"));
		comboDialBdr = new JComboBox<>(anaDials);
		comboDialBdr.setEditable(false);
		comboDialBdr.addItemListener(this);
		JSeparator jseparator2 = new JSeparator();
		JPanel roundBdrPane = new JPanel();
		BoxLayout rndLay = new BoxLayout(roundBdrPane, BoxLayout.X_AXIS);
		roundBdrPane.setLayout(rndLay);
		cbMovable  = new JCheckBox("Movable");
		jlRoundBdr = new JLabel("Round Corners");
		roundBdr   = new JComboBox<>(ExUtils.ROUND_CORNERS.values());
		enTooltip  = new JCheckBox("Mouse-over Time info");
		nativeLook = new JCheckBox("Check to view this in system's look and feel");
		roundBdrPane.add(jlRoundBdr);
		roundBdrPane.add(Box.createHorizontalStrut(5));
		roundBdrPane.add(roundBdr);
		JPanel iconPane = new JPanel();
		JLabel appIcon = new JLabel("App icon (needs restart):");
		BoxLayout iconLay = new BoxLayout(iconPane, BoxLayout.X_AXIS);
		iconPane.setLayout(iconLay);
		ButtonGroup bg2 = new ButtonGroup();
		rbTaskIcon = new JRadioButton("Task bar");
		rbTrayIcon = new JRadioButton("System tray");
		rbNoIcon   = new JRadioButton("None");
		bg2.add(rbTaskIcon);
		bg2.add(rbTrayIcon);
		bg2.add(rbNoIcon);
		iconPane.add(appIcon);
		iconPane.add(Box.createHorizontalStrut(5));
		iconPane.add(rbTaskIcon);
		iconPane.add(Box.createHorizontalStrut(5));
		iconPane.add(rbTrayIcon);
		iconPane.add(Box.createHorizontalStrut(5));
		iconPane.add(rbNoIcon);
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
		jLDigiPreview.setVerticalAlignment(SwingConstants.CENTER);
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
		analogCb2.setActionCommand(CMD_ANALOG_CLOCK);
		cbLabelBdr2 = new JCheckBox(CB_DIAL_LABEL_BDR);
		anaFormats = new Vector<>();
		anaFormats.add(new Item(TLabel.SHOW_NONE, "No labels"));
		anaFormats.add(new Item(TLabel.SHOW_AM_PM, "AM-PM label"));
		anaFormats.add(new Item(TLabel.SHOW_TIMEZONE, "Timezone label"));
		anaFormats.add(new Item((TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE), "AM-PM and Timezone"));
		comboClockFmt = new JComboBox<>(anaFormats);
		comboClockFmt.setEditable(false);
		comboClockFmt.addItemListener(this);
		clockFormatsPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		cbTz = new JCheckBox("Timezone");
		cbTz.setActionCommand(CMD_CB_DIAL_OPTION);
		cbTz.addActionListener(this);
		cbAmPm = new JCheckBox("AM/PM");
		cbAmPm.setActionCommand(CMD_CB_DIAL_OPTION);
		cbAmPm.addActionListener(this);
		cbWkDy = new JCheckBox("Weekday");
		cbWkDy.setActionCommand(CMD_CB_DIAL_OPTION);
		cbWkDy.addActionListener(this);
		cbDate = new JCheckBox("Date");
		cbDate.setActionCommand(CMD_CB_DIAL_OPTION);
		cbDate.addActionListener(this);
		clockFormatsPane.add(cbTz);
		clockFormatsPane.add(cbAmPm);
		clockFormatsPane.add(cbWkDy);
		clockFormatsPane.add(cbDate);
		clockFormatsPane.setBorder(BorderFactory.createEtchedBorder());
		tzLabel = new JLabel("Select Timezone");
		tzCb    = new JCheckBox("Use local time");
		tzCb.setActionCommand("LocalTz");
		tzLabel.setLabelFor(tzCb);
		comboTz   = new JComboBox<>(new DefaultComboBoxModel<>(TimeZone.getAvailableIDs()));
		comboTz.setRenderer(new TimezoneCellRenderer());
		comboTz.addItemListener(this);
		jLDateFormat = new JLabel("Date/Time Format");
		comboDateFmt = new JComboBox<>(getDigitalTimeFormats());
		comboDateFmt.setEditable(true);
		comboDateFmt.addItemListener(this);
		jLDateFormat.setLabelFor(comboDateFmt);
		cardComboPanel = new JPanel(new CardLayout());
		cardComboPanel.add(comboDateFmt, "DateFormat");
		cardComboPanel.add(clockFormatsPane, "ClockFormat");
		// cardComboPanel.add(comboClockFmt, "ClockFormat");
		JSeparator jseparator3 = new JSeparator();
		cbShowDays  = new JCheckBox("Show Days in Up-time");
		jLDayTxt    = new JLabel("Day Label");
		jLUptimeTxt = new JLabel("Up-time Label");
		jLHourTxt   = new JLabel("Hour Label");
		jLMinTxt    = new JLabel("Minute Label");
		jLSecTxt    = new JLabel("Second Label");
		dSymbol     = new JComboBox<>(new String[]{"-day(s), ", "", " day(s), ", " days "});
		uSymbol     = new JComboBox<>(new String[]{"Up-Time: ", "", "Run time: ", "Uptime: ", "System uptime: "});
		hSymbol     = new JComboBox<>(new String[]{"-hour(s), ", "", " hour(s), ", " hrs. ", " hr(s), "});
		mSymbol     = new JComboBox<>(new String[]{"-minute(s), ", "", " minute(s), ", " mins. ", " min(s), "});
		sSymbol     = new JComboBox<>(new String[]{"-second(s)", "", " second(s)", " secs.", " sec(s)"});
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
		String[] pomtech = {
			"25 min. work, 5 min. break, 30 min. rest",
			"45 minutes work, 15 minutes break",
			"50 minutes work, 10 minutes break",
			"52 minutes work, 17 minutes break",
			"90 minutes work, 20 minutes break",
			"112 minutes work, 26 minutes break",
			"4 sec breath-in, 6 sec hold, 8 sec breath-out"
		};
		comboPomodoro = new JComboBox<>(pomtech);
		comboPomodoro.addItemListener(this);
		jLPomFormat = new JLabel("Pomodoro format");
		String[] pomFormats = {
			"mm:ss",
			"HH:mm:ss",
		};
		comboPomFmt = new JComboBox<>(pomFormats);
		comboPomFmt.setEditable(true);
		comboPomFmt.addItemListener(this);
		jLPomFormat.setLabelFor(comboPomFmt);
		cbPomLabel = new JCheckBox(CMD_POM_LEAD_LABEL);
		cbPomLabel.setActionCommand(CMD_POM_LEAD_LABEL);
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
		Image backgroundPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/background-icon.png"))).getImage();
		picPanel  = new TwilightPanel(backgroundPng);
		SpringLayout picLayout = new SpringLayout();
		picPanel.setLayout(picLayout);
		picBackPanel = new TwilightPanel(new BorderLayout());
		picLabel  = new TLabel(PREVIEW, null, TLabel.FIT);
		picLabel.setFont(new Font(picLabel.getFont().getFamily(),	Font.BOLD, 16));
		picLabel.setPreviewMode(true);
		picBackPanel.add(picLabel, BorderLayout.CENTER);
		picPanel.add(picBackPanel);
		picLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, picBackPanel, 0, SpringLayout.HORIZONTAL_CENTER, picPanel);
		picLayout.putConstraint(SpringLayout.VERTICAL_CENTER, picBackPanel, 0, SpringLayout.VERTICAL_CENTER, picPanel);
		picPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel toolPane2 = new JPanel();
		BoxLayout bly = new BoxLayout(toolPane2, BoxLayout.X_AXIS);
		toolPane2.setLayout(bly);
		Image folderPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/folder-icon.png"))).getImage();
		selectDir   = createSmallButton(folderPng, "Choose Image Directory", this);
		resetImgDir = createSmallButton(defaultPng, "Reset to default images", this);
		analogCb0 = new JCheckBox("Analog mode");
		analogCb0.setActionCommand(CMD_ANALOG_CLOCK);
		analogCb0.addActionListener(this);
		layLabel  = new JLabel("Layout");
		comboTLayout = new JComboBox<>(layouts);
		layLabel.setLabelFor(comboTLayout);
		comboTLayout.addItemListener(this);
		toolPane2.add(selectDir);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(resetImgDir);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(layLabel);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(comboTLayout);
		toolPane2.add(Box.createRigidArea(new Dimension(5, 0)));
		toolPane2.add(analogCb0);
		selBackCol = new JButton("Choose Background Color", new ButtonIcon(ButtonIcon.RECTANGLE, Color.BLACK));
		JPanel colMidPanel = new JPanel();
		BoxLayout midLay = new BoxLayout(colMidPanel, BoxLayout.X_AXIS);
		colMidPanel.setLayout(midLay);
		resBackCol = new JButton("Default Color", new ButtonIcon(ButtonIcon.RECTANGLE, getBackground()));
		foregroundTrans = new JCheckBox("Blend foreground opacity");
		foregroundTrans.addActionListener(this);
		colMidPanel.add(resBackCol);
		colMidPanel.add(Box.createHorizontalGlue());
		colMidPanel.add(foregroundTrans);
		JPanel sliderPanel = new JPanel();
		BoxLayout slideLay = new BoxLayout(sliderPanel, BoxLayout.Y_AXIS);
		sliderPanel.setLayout(slideLay);
		jlTransSlide = new JLabel("Opacity [ 100 % ]", SwingConstants.CENTER);
		transLevel = new JSlider(SwingConstants.VERTICAL, 4, 20, 10);
		jlTransSlide.setLabelFor(transLevel);
		jlTransSlide.setPreferredSize(jlTransSlide.getPreferredSize());
		jlTransSlide.setAlignmentX(Component.CENTER_ALIGNMENT);
		transLevel.setMajorTickSpacing(4);
		transLevel.setMinorTickSpacing(1);
		transLevel.setPaintTicks(true);
		transLevel.setSnapToTicks(true);
		transLevel.addChangeListener(this);
		transLevel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sliderPanel.add(jlTransSlide);
		sliderPanel.add(transLevel);
		sliderPanel.setBorder(BorderFactory.createEtchedBorder());
		jpanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Background Preferences"));
		//All configuration for Alarm Panel as follows:
		JPanel      jpanel5 = new JPanel(new GridBagLayout());
		JPanel      topList = new JPanel(new GridBagLayout());
		JScrollPane jsp     = new JScrollPane();
		alarmsData      = new Vector<>();
		alarmList = new JList<>(alarmsData);
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
		JLabel nameLabel = new JLabel("Alarm Name", SwingConstants.CENTER);
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
		String[] pds  = new String[]{"Never", "Minute", "Hour", "Day", "Week", "Month", "Year"};
		       period = new JComboBox<>(pds);
		period.addItemListener(this);
		String[] dow      = new String[]{"Date", "Weekday"};
		       jLAlmSame  = new JLabel("same");
		       dateOrWeek = new JComboBox<>(dow);
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
		bottomCards.add(sndSet, CMD_FINISH_EDIT);
		bottomCards.add(almSet,  CMD_ADD_EDIT);
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
			ExUtils.addComponent(jpanel,  cFontList, 		new int[]{0, 0, 1, 2}, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontStyleList, 	new int[]{1, 0, 1, 2}, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontSizeList, 	new int[]{2, 0, 1, 2}, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  selFontCol, 		new int[]{0, 2, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resFontCol, 		new int[]{1, 2, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resetFont, 		new int[]{2, 2, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  jscrollpane, 		new int[]{0, 3, 3, 1}, 1.0D, 0.6D, this);
			ExUtils.addComponent(jpanel1, nBorder, 			new int[]{0, 0, 2, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, analogCb1,		new int[]{2, 0, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, comboDialBdr,		new int[]{3, 0, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, cbLabelBdr1,		new int[]{4, 0, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bRaised, 			new int[]{0, 2, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, borderPanel, 		new int[]{2, 2, 2, 4}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, handSizesPane, 	new int[]{4, 2, 1, 4}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bLowered, 		new int[]{0, 3, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eRaised, 			new int[]{0, 4, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eLowered, 		new int[]{0, 5, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator1, 		new int[]{0, 6, 5, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, lBorder, 			new int[]{0, 7, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, component, 		new int[]{0, 8, 1, 1}, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, selLineCol, 		new int[]{1, 8, 1, 1}, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, resLineCol, 		new int[]{2, 8, 1, 1}, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, thickPanel, 		new int[]{3, 8, 1, 1}, 0.25D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator2, 		new int[]{0, 9, 5, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, enTooltip, 		new int[]{0, 10, 2, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, cbMovable, 		new int[]{2, 10, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, roundBdrPane, 	new int[]{3, 10, 2, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, nativeLook, 		new int[]{0, 11, 3, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, iconPane, 		new int[]{3, 11, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, topPanePr,	 	new int[]{0, 2, 4, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, analogPanel,		new int[]{4, 2, 1, 3}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, selTimeZone, 		new int[]{0, 3, 2, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, analogCb2,		new int[]{2, 3, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbLabelBdr2,		new int[]{3, 3, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLDateFormat, 	new int[]{1, 4, 1, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cardComboPanel, 	new int[]{2, 4, 2, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, timePaneRadSpace, new int[]{0, 5, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, tzLabel, 			new int[]{1, 5, 1, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboTz, 			new int[]{2, 5, 2, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, tzCb,		 		new int[]{4, 5, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator3, 		new int[]{1, 6, 4, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sysUpTime, 		new int[]{0, 7, 2, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbShowDays, 		new int[]{2, 7, 1, 1}, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLDayTxt, 		new int[]{3, 7, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, dSymbol,	 		new int[]{4, 7, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLUptimeTxt, 		new int[]{1, 8, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, uSymbol, 			new int[]{2, 8, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLHourTxt, 		new int[]{3, 8, 1, 1}, 0.01D, 0.0D, this);
			ExUtils.addComponent(jpanel2, hSymbol, 			new int[]{4, 8, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLMinTxt, 		new int[]{1, 9, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, mSymbol, 			new int[]{2, 9, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLSecTxt, 		new int[]{3, 9, 1, 1}, 0.01D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sSymbol, 			new int[]{4, 9, 1, 1}, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator4, 		new int[]{1, 10, 4, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, pomodoroTime, 	new int[]{0, 11, 2, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomodoro, 	new int[]{2, 11, 3, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLPomFormat, 		new int[]{1, 12, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomFmt,		new int[]{2, 12, 1, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbPomCountdown,	new int[]{3, 12, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, cbPomLabel,		new int[]{4, 12, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useImg, 			new int[]{0, 0, 4, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component2, 		new int[]{0, 1, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, jscrollpane1, 	new int[]{1, 1, 1, 3}, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, toolPane2, 		new int[]{2, 1, 2, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component3,		new int[]{0, 2, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component4,		new int[]{1, 2, 1, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, picPanel, 		new int[]{2, 2, 2, 2}, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, useCol, 			new int[]{0, 5, 4, 1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, selBackCol, 		new int[]{1, 6, 2, 1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, colMidPanel, 		new int[]{3, 6, 1, 1}, 0.0D, 0.0D, this);
			if (!initinfo.isPixelAlphaSupported() && initinfo.isScreenshotSupported())
			{
				ExUtils.addComponent(jpanel3, sliderPanel, 		new int[]{4, 0, 1, 10}, 0.0D, 0.0D, this);
				ExUtils.addComponent(jpanel3, useTrans, 		new int[]{0, 8, 4, 1}, 1.0D, 0.0D, this);
				ExUtils.addComponent(jpanel3, slowUpd,			new int[]{1, 9, 7, 1}, 1.0D, 0.0D, this);
			}
			else
			{
				ExUtils.addComponent(jpanel3, sliderPanel, 		new int[]{4, 0, 1, 8}, 0.0D, 0.0D, this);
				ExUtils.addComponent(jpanel3, jSeparator5, 		new int[]{0, 4, 4, 1}, 1.0D, 0.0D, this);
			}
			ExUtils.addComponent(topList, jsp,				new int[]{0, 0, 1,	5}, 1.0D, 1.0D, this);
			ExUtils.addComponent(topList, add,				new int[]{1, 0, 1,	1}, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, edit,				new int[]{1, 1, 1,	1}, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, remove,			new int[]{1, 2, 1,	1}, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, test,				new int[]{1, 3, 1,	1}, 0.0D, 0.25D, this);
			ExUtils.addComponent(almSet,  nameLabel,		new int[]{0, 0, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  alarmName,		new int[]{1, 0, 6,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartOn,		new int[]{0, 1, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  chooseDate,		new int[]{1, 1, 5,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner1,		new int[]{6, 1, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartAfter,	new int[]{0, 2, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  countSpinner2,	new int[]{1, 2, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmDays,		new int[]{2, 2, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner2,		new int[]{3, 2, 2,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmHrs,			new int[]{5, 2, 2,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  rept,				new int[]{0, 3, 2,	1}, 0.4D, 0.0D, this);
			ExUtils.addComponent(almSet,  countSpinner1,	new int[]{2, 3, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  period,			new int[]{3, 3, 2,	1}, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmSame,		new int[]{5, 3, 1,	1}, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  dateOrWeek,		new int[]{6, 3, 1,	1}, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmAct,			new int[]{0, 4, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runSnd,			new int[]{1, 4, 4,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runMsg,			new int[]{5, 4, 2,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  sndToRun,			new int[]{1, 5, 6,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlHour,			new int[]{0, 0, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndHour,			new int[]{1, 0, 1,	1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlUpt,			new int[]{0, 1, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndUptime,		new int[]{1, 1, 1,	1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomWork,		new int[]{0, 2, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndWork,			new int[]{1, 2, 1,	1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomBrk,			new int[]{0, 3, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndBrk,			new int[]{1, 3, 1,	1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  jlPomRest,		new int[]{0, 4, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(sndSet,  sndRest,			new int[]{1, 4, 1,	1}, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, topList,			new int[]{0, 0, 1,	1}, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel5, bottomCards,		new int[]{0, 1, 1,	1}, 0.0D, 0.0D, this);
		}
		catch (AWTException awtexception)
		{
			System.out.println("General AWT Exception has occurred.");
			awtexception.printStackTrace();
		}

		tabPane.add("Font & Foreground", jpanel);
		tabPane.add("Background & Dials", jpanel3);
		tabPane.add("Borders & UI", jpanel1);
		tabPane.add("Time View", jpanel2);
		tabPane.add("Alarms", jpanel5);
		getContentPane().add(tabPane, "Center");
		grid.add(ok);
		grid.add(cancel);
		jpanel4.add(grid);
		getContentPane().add(jpanel4, "South");
		initialize(initinfo, alarminfo);
	}

	public static String[] getDigitalTimeFormats() {
		return digiFormats;
	}

	public InitInfo getExistingInfo() {
		return information;
	}

	public Vector<TimeBean> getExistingAlarms() {
		return alarmsData;
	}

	private int getSelectedTrayIconInfo() {
		if (rbTrayIcon.isSelected()) return SYS_TRAY_ICON;
		else if (rbTaskIcon.isSelected()) return TASK_BAR_ICON;
		else return NO_APP_ICON;
	}

	private void updateTrayIconSelection(int iconType) {
		switch (iconType) {
			case TASK_BAR_ICON:
				rbTaskIcon.setSelected(true);
				break;
			case SYS_TRAY_ICON:
				rbTrayIcon.setSelected(true);
				break;
			case NO_APP_ICON:
			default:
				rbNoIcon.setSelected(true);
				break;
		}
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
		picLabel.setForeground(color);
		useImg.setSelected(initinfo.isUsingImage());
		useCol.setSelected(!initinfo.isUsingImage());
		imgFileList.setDefaultImagesDir(initinfo.getDefaultsDir() + "/images");
		updateImagesList(initinfo.getImageFile());
		useTrans.setSelected(initinfo.hasGlassEffect());
		slowUpd.setSelected(initinfo.isSlowTransUpdating());
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
		foregroundTrans.setSelected(initinfo.isForegroundTranslucent());
		updateOpacitySlider(initinfo.getOpacity(), initinfo.isPixelAlphaSupported(), foregroundTrans.isSelected());
		jlTransSlide.setText("Opacity [ " + Math.round(initinfo.getOpacity() * 100) + " % ]");
		bdrPreview.setBorder(initinfo.getBorder());
		initFrameBorderValues(initinfo.getBorder());
		cbMovable.setSelected(!initinfo.isFixed());
		enTooltip.setSelected(initinfo.hasTooltip());
		nativeLook.setSelected(initinfo.hasNativeLook());
		analogCb0.setSelected(initinfo.isAnalogClock());
		analogCb1.setSelected(initinfo.isAnalogClock());
		analogCb2.setSelected(initinfo.isAnalogClock());
		int anaClkOpts = Math.floorDiv(initinfo.getAnalogClockOption(), 1000) * 1000;
		int anaBdrOpts = initinfo.getAnalogClockOption() % 1000;
		int rndCorners = initinfo.getRoundCorners();
		float curDSize = initinfo.getDialObjectsSize();
		for (Item formats : anaFormats) {
			if (formats.getId() == anaClkOpts) {
				comboClockFmt.setSelectedItem(formats);
			}
		}
		for (Item formats : anaDials) {
			if (formats.getId() == anaBdrOpts) {
				comboDialBdr.setSelectedItem(formats);
			}
		}
		for (ExUtils.ROUND_CORNERS rnd : ExUtils.ROUND_CORNERS.values()) {
			if (rnd.getRoundType() == rndCorners) {
				roundBdr.setSelectedItem(rnd);
			}
		}
		for (TLabel.DIAL_OBJECTS_SIZE objSiz : TLabel.DIAL_OBJECTS_SIZE.values()) {
			if (objSiz.getSizeValue() == curDSize) {
				handSize.setSelectedValue(objSiz, true);
			}
		}
		String dispM = initinfo.getDisplayMethod();
		String privT = "";
		pom          = new Pomodoro(initinfo.getPomodoroTask());
		comboTz.setSelectedItem(dispM.equals(DeskStop.DISPLAY_MODE_CURRENT_TIMEZONE) ? TimeZone.getDefault().getID() : TimeZone.getTimeZone(initinfo.getTimeZone()).getID());
		tzCb.setSelected(dispM.equals(DeskStop.DISPLAY_MODE_CURRENT_TIMEZONE));
		comboDateFmt.setSelectedItem(initinfo.getZonedTimeFormat());
		switch (dispM) {
			case DeskStop.DISPLAY_MODE_SYSTEM_UPTIME:
				sysUpTime.setSelected(true);
				cbShowDays.setSelected(initinfo.isDayShowing());
				privT = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), initinfo.getUpTimeFormat());
				break;
			case DeskStop.DISPLAY_MODE_POMODORO_TIMER:
				pomodoroTime.setSelected(true);
				privT = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(initinfo.isPomodoroCountdown()), initinfo.getPomodoroFormat(), pom.getRunningLabel(), initinfo.isPomodoroLeadingLabel());
				break;
			case DeskStop.DISPLAY_MODE_SELECTED_TIMEZONE:
			case DeskStop.DISPLAY_MODE_CURRENT_TIMEZONE:
			default:
				selTimeZone.setSelected(true);
				SimpleDateFormat sd = new SimpleDateFormat(initinfo.getZonedTimeFormat());
				TimeZone tempTz = TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex()));
				sd.setTimeZone(tempTz);
				Calendar toUpdate = Calendar.getInstance(tempTz);
				privT = sd.format(toUpdate.getTime());
				updateAnalogClockTimezone(tempTz);
				break;
		}
		jLAnaPreview.setAnalogClockOptions(Math.floorDiv(initinfo.getAnalogClockOption(), 1000) * 1000);
		updateAnalogDialOptionCBs(Math.floorDiv(initinfo.getAnalogClockOption(), 1000) * 1000);
		cbLabelBdr1.setSelected(initinfo.isAnalogClockLabelBorderShowing());
		cbLabelBdr2.setSelected(initinfo.isAnalogClockLabelBorderShowing());
		jLAnaPreview.setAnalogClockLabelBorder(initinfo.isAnalogClockLabelBorderShowing());
		jLAnaPreview.setPreviewMode(true);
		bdrPreview.setAnalogClockOptions(initinfo.getAnalogClockOption());
		bdrPreview.setAnalogClockLabelBorder(initinfo.isAnalogClockLabelBorderShowing());
		bdrPreview.setPreviewMode(true);
		jLDigiPreview.setText(privT);
		initUptimeFormatValues(initinfo.getUpTimeFormat(), initinfo.isDayShowing());
		comboPomodoro.setSelectedItem(initinfo.getPomodoroTask());
		cbPomLabel.setSelected(initinfo.isPomodoroLeadingLabel());
		cbPomCountdown.setSelected(initinfo.isPomodoroCountdown());
		comboPomFmt.setSelectedItem(initinfo.getPomodoroFormat());
		alarmsData = alarmInit;
		alarmList.setListData(alarmsData);
		sndHour.setAudioFileName(initinfo.getHourSound());
		sndUptime.setAudioFileName(initinfo.getUptimeHourSound());
		sndWork.setAudioFileName(initinfo.getPomodoroWorkSound());
		sndBrk.setAudioFileName(initinfo.getPomodoroBreakSound());
		sndRest.setAudioFileName(initinfo.getPomodoroRestSound());
		sndToRun.setDefaultSoundsDir(initinfo.getDefaultsDir() + "/sounds");
		updateTrayIconSelection(initinfo.getTrayIconType());
		setOneEnabled();
	}

	private void initUptimeFormatValues(String formatValues, boolean containsDaysFormat)
	{
		String[] arr = formatValues.split("\'");
		if (containsDaysFormat)
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
	}

	private void initFrameBorderValues(Border border)
	{
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
	}

	private void updateAnalogClockTimezone(TimeZone updZone)
	{
		SimpleDateFormat anaFormat = new SimpleDateFormat("zzz");
		anaFormat.setTimeZone(updZone);
		jLAnaPreview.setTime(10, 10, 5, "AM", "APR 01", "WED", anaFormat.format(new Date()));
		bdrPreview.setTime(10, 10, 5, "AM", "APR 01", "WED", anaFormat.format(new Date()));
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
		} catch (Exception e) {
			System.out.println("Source jar file URL not formatted according to to RFC2396.");
			e.printStackTrace();
		}
		if (selectIndex != -1)
			imgFileList.setSelectedValue(imgFile, true);
		else
			imgFileList.setSelectedIndex(0);
		imgFileList.ensureIndexIsVisible(imgFileList.getSelectedIndex());
	}

	private void setOneEnabled()
	{
		imgFileList.setEnabled(useImg.isSelected());
		selectDir.setEnabled(useImg.isSelected());
		resetImgDir.setEnabled(useImg.isSelected());
		picLabel.setEnabled(!useTrans.isSelected());
		layLabel.setEnabled(useImg.isSelected());
		comboTLayout.setEnabled(useImg.isSelected());
		selBackCol.setEnabled(useCol.isSelected());
		resBackCol.setEnabled(useCol.isSelected());
		if (useImg.isSelected())
		{
			picLabel.setBackground(null);
			picLabel.setTransparency(false);
			if (information.getImageFile().isFile() && imgFileList.getSelectedValue() != null)
			{
				picLabel.setBackImage((new ImageIcon(imgFileList.getSelectedValue().toString())).getImage());
				picLabel.setText(PREVIEW);
			}
			else
			{
				picLabel.setBackImage(null);
				picLabel.setText(NO_PREVIEW);
			}
		}
		else
		{
			picBackPanel.setBackground(((ButtonIcon)selBackCol.getIcon()).getEnabledColor());
			picLabel.setBackImage(null);
			picLabel.setTransparency(true);
			picLabel.setText(PREVIEW);
		}
		foregroundTrans.setEnabled(information.isPixelAlphaSupported());
		transLevel.setEnabled(information.isWindowAlphaSupported() || information.isPixelAlphaSupported());
		jlTransSlide.setEnabled(information.isWindowAlphaSupported() || information.isPixelAlphaSupported());
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
			cbLabelBdr1.setEnabled(true);
			cbLabelBdr2.setEnabled(true);
			digitalPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
			analogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			jLDigiPreview.setEnabled(false);
			jLAnaPreview.setEnabled(true);
			((CardLayout)cardComboPanel.getLayout()).show(cardComboPanel, "ClockFormat");
			bdrPreview.setText(EMPTY);
			bdrPreview.setClockMode(true);
			bdrPreview.setPreferredSize(new Dimension(130, 130));
			picLabel.setText(EMPTY);
			picLabel.setClockMode(true);
			picLabel.setPreferredSize(new Dimension(150, 150));
			handSize.setEnabled(true);
		} else {
			analogCb0.setSelected(false);
			analogCb1.setSelected(false);
			analogCb2.setSelected(false);
			cbLabelBdr1.setEnabled(false);
			cbLabelBdr2.setEnabled(false);
			digitalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			analogPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
			jLDigiPreview.setEnabled(true);
			jLAnaPreview.setEnabled(false);
			((CardLayout)cardComboPanel.getLayout()).show(cardComboPanel, "DateFormat");
			bdrPreview.setText(PREVIEW);
			bdrPreview.setClockMode(false);
			bdrPreview.setPreferredSize(new Dimension(100, 30));
			picLabel.setText(PREVIEW);
			picLabel.setClockMode(false);
			picLabel.setPreferredSize(new Dimension(200, 30));
			handSize.setEnabled(false);
		}
		comboDialBdr.setEnabled(analogCb1.isSelected());
		rbTrayIcon.setEnabled(SystemTray.isSupported());
		
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
	 * @throws IllegalArgumentException
	 */
	public InitInfo applySettings() throws IllegalArgumentException
	{
		information.setFont(getSelectedFont());
		information.setForeground(((ButtonIcon)selFontCol.getIcon()).getEnabledColor());
		information.setUsingImage(useImg.isSelected());
		information.setGlassEffect(useTrans.isSelected());
		information.setSlowTransUpdating(slowUpd.isSelected());
		if (!imgFileList.isSelectionEmpty())
		{
			File testfile = imgFileList.getSelectedValue();
			information.setImageFile(testfile.getAbsolutePath());
		}
		information.setImageStyle(picLabel.getImageLayout());
		information.setBackground(((ButtonIcon)selBackCol.getIcon()).getEnabledColor());
		information.setForegroundTranslucent(foregroundTrans.isSelected());
		information.setOpacity((float)transLevel.getValue() / 20);
		information.setDialObjectsSize(handSize.getSelectedValue().getSizeValue());
		information.setBorder(bdrPreview.getBorder());
		information.setAnalogClockLabelBorder(cbLabelBdr1.isSelected());
		information.setLineColor(((ButtonIcon)selLineCol.getIcon()).getEnabledColor());
		information.setTooltip(enTooltip.isSelected());
		information.setFixed(!cbMovable.isSelected());
		information.setRoundCorners(roundBdr.getItemAt(roundBdr.getSelectedIndex()).getRoundType());
		information.setNativeLook(nativeLook.isSelected());
		information.setHourSound(sndHour.getAudioFileName());
		information.setUptimeHourSound(sndUptime.getAudioFileName());
		information.setPomodoroWorkSound(sndWork.getAudioFileName());
		information.setPomodoroBreakSound(sndBrk.getAudioFileName());
		information.setPomodoroRestSound(sndRest.getAudioFileName());
		information.setTrayIconType(getSelectedTrayIconInfo());
		SimpleDateFormat simpledateformat = new SimpleDateFormat();
		if (selTimeZone.isSelected())
		{
			try
			{
				simpledateformat.applyPattern(comboDateFmt.getSelectedItem().toString());
			}
			catch (Exception exception)
			{
				throw new IllegalArgumentException("The following error occured in date/time format:\n" + exception.toString() + "\n" + exception.getMessage() + IN_JOINER + comboDateFmt.getSelectedItem().toString() + "\"");
			}
			information.setDisplayMethod(tzCb.isSelected() ? DeskStop.DISPLAY_MODE_CURRENT_TIMEZONE : DeskStop.DISPLAY_MODE_SELECTED_TIMEZONE);
			information.setAnalogClock(analogCb0.isSelected() || analogCb1.isSelected() || analogCb2.isSelected());
			information.setAnalogClockOption(getSelectedAnalogDialOptions() + comboDialBdr.getItemAt(comboDialBdr.getSelectedIndex()).getId());
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
				throw new IllegalArgumentException("The following error occured in pomodoro format:\n" + exception.toString() + "\n" + exception.getMessage() + IN_JOINER + comboPomFmt.getSelectedItem().toString() + "\"");
			}
			information.setDisplayMethod(DeskStop.DISPLAY_MODE_POMODORO_TIMER);
			information.setPomodoroTask(comboPomodoro.getSelectedItem().toString());
			information.setPomodoroFormat(comboPomFmt.getSelectedItem().toString());
			information.setPomodoroLeadingLabel(cbPomLabel.isSelected());
			information.setPomodoroCountdown(cbPomCountdown.isSelected());
		}
		else if (sysUpTime.isSelected())
		{
			validateUptimeBeforeSave();
			information.setDisplayMethod(DeskStop.DISPLAY_MODE_SYSTEM_UPTIME);
			if (cbShowDays.isSelected())
				information.setUpTimeFormat("'" + uSymbol.getSelectedItem().toString() + "'DD'" + dSymbol.getSelectedItem().toString() + "'HH'" + hSymbol.getSelectedItem().toString() + "'mm'" + mSymbol.getSelectedItem().toString() + "'ss'" + sSymbol.getSelectedItem().toString() + "'");
			else
				information.setUpTimeFormat("'" + uSymbol.getSelectedItem().toString() + "'HH'" + hSymbol.getSelectedItem().toString() + "'mm'" + mSymbol.getSelectedItem().toString() + "'ss'" + sSymbol.getSelectedItem().toString() + "'");
		}
		return information;
	}
	
	private void validateUptimeBeforeSave() throws IllegalArgumentException
	{
		Hashtable<String, String> sysUTimePattern = new Hashtable<>();
		sysUTimePattern.put("Up-time symbol", uSymbol.getSelectedItem().toString());
		sysUTimePattern.put("Day symbol",     dSymbol.getSelectedItem().toString());
		sysUTimePattern.put("Hour symbol",    hSymbol.getSelectedItem().toString());
		sysUTimePattern.put("Minute symbol",  mSymbol.getSelectedItem().toString());
		sysUTimePattern.put("Second symbol",  sSymbol.getSelectedItem().toString());
		Enumeration<String> e = sysUTimePattern.keys();

		SimpleDateFormat simpledateformat = new SimpleDateFormat();
		while (e.hasMoreElements())
		{
			String key = e.nextElement();
			try
			{
				simpledateformat.applyPattern("'" + sysUTimePattern.get(key) + "'");
			}
			catch (Exception exception2)
			{
				throw new IllegalArgumentException("The following error occured in \"" + key + "\" field of Up-time format:\n" + exception2.toString() + "\n" + exception2.getMessage() + IN_JOINER + sysUTimePattern.get(key) + "\"");
			}
		}
	}

	/** 
	 * Save all the alarms in the form of vector list of TimeBean
	 * @return Vector<TimeBean>
	 * @throws NullPointerException
	 */
	public Vector<TimeBean> applyAlarms() throws NullPointerException
	{
		if (opmode != ChooserBox.ALARM_DISCARD) saveCurrentAlarm();
		return alarmsData;
	}
	
	private void updateOpacitySlider(float opacityLevel, boolean pixelTransSupport, boolean foregroundTranslucent)
	{
		transLevel.setMinimum(pixelTransSupport && !foregroundTranslucent ? 0 : 4);
		transLevel.setValue(Math.round(opacityLevel * 20));
	}
	
	
	/** 
	 * Saves the current TimeBean alarm in add or edit mode, to the data vector.
	 * This also updates the resultant data vector to the alarmList JList.
	 */
	private void saveCurrentAlarm()
	{
		if (alarmName.getText().equals("") || alarmName.getText() == null)
		{
			JOptionPane.showMessageDialog(this,"Please enter a brief non-blank alarm name","Empty alarm name",JOptionPane.INFORMATION_MESSAGE);
			alarmName.grabFocus();
			return;
		}
		if ((sndToRun.getAudioFileName().isEmpty() && runSnd.isSelected()))
		{
			JOptionPane.showMessageDialog(this,"No audio file selected to play alarm.","No Alarm Sound",JOptionPane.INFORMATION_MESSAGE);
			sndToRun.grabFocus();
			return;
		}
		TimeBean tb = new TimeBean();
		tb.setName(alarmName.getText());
		tb.setSystemStartTimeBasedAlarm(startAfterTimeIsSelected()); // must be set before setRuntime();
		tb.setAlarmTriggerTime(getSelectedAlarmTime());//if op2 then nextRuntime should be set instead of runtime;
		tb.setAlarmRepeatInterval(getSelectedRepeatInterval());
		tb.setRepeatMultiple(rept.isSelected() ? (int)countSpinner1.getValue() : 0);
		tb.setAlarmExecutionOutputType(getSelectedAlarmExecOutputOption());
		tb.setAlarmSound(sndToRun.getAudioFileName());
		if (opmode == ChooserBox.ALARM_ADD)
		{
			alarmsData.addElement(tb);
			alarmList.setListData(alarmsData);
		}
		else if (opmode == ChooserBox.ALARM_EDIT)
		{
			alarmsData.setElementAt(tb, selIndex);
			alarmList.setListData(alarmsData);
		}
		((CardLayout)(bottomCards.getLayout())).show(bottomCards, CMD_FINISH_EDIT);
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

	private void chooseImageDirectory()
	{
		JFileChooser jfilechooser = new JFileChooser(imgFileList.getDirectory());
		jfilechooser.setFileSelectionMode(1);
		jfilechooser.setMultiSelectionEnabled(false);
		int i = jfilechooser.showOpenDialog(this);
		if (i == 0) {
			try {
				imgFileList.setDirectory(jfilechooser.getSelectedFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void resetTimePreferences()
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

	private Color chooseColor(String dialogTitle, Color defaultColor)
	{
		Color newColor = JColorChooser.showDialog(new Frame(), dialogTitle, defaultColor);
		if (newColor == null) newColor = defaultColor;
		return newColor;
	}

	private void updateAnalogCheckBoxes(Object srcCheckBox)
	{
		if (srcCheckBox.equals(analogCb2)) {
			analogCb1.setSelected(analogCb2.isSelected());
			analogCb0.setSelected(analogCb2.isSelected());
		} else if (srcCheckBox.equals(analogCb1)) {
			analogCb2.setSelected(analogCb1.isSelected());
			analogCb0.setSelected(analogCb2.isSelected());
		} else if (srcCheckBox.equals(analogCb0)) {
			analogCb1.setSelected(analogCb0.isSelected());
			analogCb2.setSelected(analogCb0.isSelected());
		} else if (srcCheckBox.equals(cbLabelBdr1)) {
			cbLabelBdr2.setSelected(cbLabelBdr1.isSelected());
		} else if (srcCheckBox.equals(cbLabelBdr2)) {
			cbLabelBdr1.setSelected(cbLabelBdr2.isSelected());
		}
	}

	private void updateAnalogDialOptionCBs(int dialOptions)
	{
		switch (dialOptions) {
			case TLabel.SHOW_AM_PM:
			cbAmPm.setSelected(true);
			break;

			case TLabel.SHOW_TIMEZONE:
			cbTz.setSelected(true);
			break;

			case TLabel.SHOW_DAYMONTH:
			cbDate.setSelected(true);
			break;

			case TLabel.SHOW_WEEKDAY:
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
			cbAmPm.setSelected(true);
			cbTz.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_WEEKDAY):
			cbAmPm.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH):
			cbAmPm.setSelected(true);
			cbDate.setSelected(true);
			break;

			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
			cbTz.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
			cbTz.setSelected(true);
			cbDate.setSelected(true);
			break;

			case (TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
			cbDate.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
			cbAmPm.setSelected(true);
			cbTz.setSelected(true);
			cbDate.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
			cbAmPm.setSelected(true);
			cbTz.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
			cbAmPm.setSelected(true);
			cbDate.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
			cbTz.setSelected(true);
			cbDate.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
			cbAmPm.setSelected(true);
			cbTz.setSelected(true);
			cbDate.setSelected(true);
			cbWkDy.setSelected(true);
			break;

			default:
			break;
		}
	}

	private int getSelectedAnalogDialOptions()
	{
		int dialOptions = 0;
		if (cbTz.isSelected()) dialOptions += TLabel.SHOW_TIMEZONE;
		if (cbAmPm.isSelected()) dialOptions += TLabel.SHOW_AM_PM;
		if (cbWkDy.isSelected()) dialOptions += TLabel.SHOW_WEEKDAY;
		if (cbDate.isSelected()) dialOptions += TLabel.SHOW_DAYMONTH;
		return dialOptions;
	}

	private void updateBorderPreview(String borderTypeCmd)
	{
		switch (borderTypeCmd) {
			case BEVELED_RAISED_BORDER:
				bdrPreview.setBorder(BorderFactory.createRaisedBevelBorder());
				break;
			case BEVELED_LOWERED_BORDER:
				bdrPreview.setBorder(BorderFactory.createLoweredBevelBorder());
				break;
			case ETCHED_RAISED_BORDER:
				bdrPreview.setBorder(BorderFactory.createEtchedBorder(0));
				break;
			case ETCHED_LOWERED_BORDER:
				bdrPreview.setBorder(BorderFactory.createEtchedBorder(1));
				break;
			case LINE_BORDER:
				thickSpinner.setValue(intThick);
				bdrPreview.setBorder(BorderFactory.createLineBorder(((ButtonIcon)selLineCol.getIcon()).getEnabledColor(), intThick, roundBdr.getSelectedIndex() != 0));
				break;
			case NO_BORDER:
				bdrPreview.setBorder(null);
				break;
			default:
				break;
		}
	}

	private void updateBackImagePreview(String tileType)
	{
		switch (tileType) {
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

	private void showFormatHelp()
	{
		if (!exceptionActive)
			new FormatHelp(this);
		else
			validateTimeFormat();
	}

	private void selectMinOneAlarmExecOption()
	{
		if (!runSnd.isSelected() && !runMsg.isSelected())
			runMsg.setSelected(true);
		sndToRun.setEnabled(runSnd.isSelected());
	}

	@Override
	public void actionPerformed(ActionEvent actionevent)
	{
		String     comm = actionevent.getActionCommand();
		CardLayout cl   = (CardLayout)(bottomCards.getLayout());
		switch (comm) {
			case "Reset Font":
				fontPreview.setFont((new JLabel()).getFont());
				setSelectedFont((new JLabel()).getFont());
				break;
			case "Choose Font Color":
				Color color3 = chooseColor("Choose Font Color...", ((ButtonIcon)selFontCol.getIcon()).getEnabledColor());
				((ButtonIcon)selFontCol.getIcon()).setEnabledColor(color3);
				fontPreview.setForeground(color3);
				picLabel.setForeground(color3);
				break;
			case "Reset Color":
				((ButtonIcon)selFontCol.getIcon()).setEnabledColor(Color.BLACK);
				fontPreview.setForeground(Color.BLACK);
				picLabel.setForeground(Color.BLACK);
				break;
			case BEVELED_RAISED_BORDER:
			case BEVELED_LOWERED_BORDER:
			case ETCHED_RAISED_BORDER:
			case ETCHED_LOWERED_BORDER:
			case LINE_BORDER:
			case NO_BORDER:
				updateBorderPreview(comm);
				break;
			case "Line Border Color":
				Color color4 = chooseColor("Choose Line Color...", ((ButtonIcon)selLineCol.getIcon()).getEnabledColor());
				((ButtonIcon)selLineCol.getIcon()).setEnabledColor(color4);
				bdrPreview.setBorder(BorderFactory.createLineBorder(color4, intThick, roundBdr.getSelectedIndex() != 0));
				break;
			case "Default Line Color":
				((ButtonIcon)selLineCol.getIcon()).setEnabledColor(resLineCol.getBackground());
				bdrPreview.setBorder(BorderFactory.createLineBorder(resLineCol.getBackground(), intThick, roundBdr.getSelectedIndex() != 0));
				break;
			case CMD_ANALOG_CLOCK:
				updateAnalogCheckBoxes(actionevent.getSource());
				jLAnaPreview.setPreferredSize(analogPanel.getSize());
				updateAnalogClockTimezone(TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex())));
				updateLayoutCombo();
				break;
			case CB_DIAL_LABEL_BDR:
				JCheckBox analogLabelBdrSrc = (JCheckBox)actionevent.getSource();
				updateAnalogCheckBoxes(analogLabelBdrSrc);
				jLAnaPreview.setAnalogClockLabelBorder(analogLabelBdrSrc.isSelected());
				bdrPreview.setAnalogClockLabelBorder(analogLabelBdrSrc.isSelected());
				break;
			case CMD_CB_DIAL_OPTION:
				int selOptions = getSelectedAnalogDialOptions();
				jLAnaPreview.setAnalogClockOptions(selOptions);
				bdrPreview.setAnalogClockOptions(selOptions);
				break;
			case "LocalTz":
				comboTz.setSelectedItem(tzCb.isSelected() ? TimeZone.getDefault().getID() : information.getTimeZone());
				break;
			case "ShowDays":
				if (cbShowDays.isSelected() && information.isDayShowing())
				{
					String s1    = information.getUpTimeFormat();
					String[] arr = s1.split("\'");
					dSymbol.setSelectedItem(arr[3]);
				}
				validateTimeFormat();
				break;
			case "Reset to default values":
				resetTimePreferences();
				break;
			case "Help on date time format characters":
				showFormatHelp();
				break;
			case "Choose Image Directory":
				chooseImageDirectory();
				break;
			case "Reset to default images":
				updateImagesList(new File("."));
				break;
			case "Choose Background Color":
				Color color5 = chooseColor("Choose Background...", ((ButtonIcon)selBackCol.getIcon()).getEnabledColor());
				((ButtonIcon)selBackCol.getIcon()).setEnabledColor(color5);
				picBackPanel.setBackground(color5);
				break;
			case "Default Color":
				((ButtonIcon)selBackCol.getIcon()).setEnabledColor(resBackCol.getBackground());
				break;
			case "Blend foreground opacity":
				updateOpacitySlider(information.getOpacity(), information.isPixelAlphaSupported(), foregroundTrans.isSelected());
				break;
			case "Transparent background":
				slowUpd.setSelected(useTrans.isSelected());
				break;
			case "Add":
				cl.show(bottomCards, CMD_ADD_EDIT);
				alarmViewOnlyMode(false);
				opmode = ChooserBox.ALARM_ADD;
				alarmName.setText("");
				selectStartAfterTimeOption(false); //must be called before timeIn();
				setSelectedAlarmTime(new Date());
				setSelectedRepeatInterval(0, 1);
				selectAlarmExecOutputOption(4); // for msg;
				break;
			case "Edit":
				selIndex = alarmList.getSelectedIndex();
				if (selIndex == -1)
				{
					JOptionPane.showMessageDialog(this,"Please select an alarm to edit.","Nothing to edit",JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				cl.show(bottomCards, CMD_ADD_EDIT);
				alarmViewOnlyMode(false);
				opmode      = ChooserBox.ALARM_EDIT;
				TimeBean tb = alarmsData.elementAt(selIndex);
				alarmName.setText(tb.getName());
				selectStartAfterTimeOption(tb.isSystemStartTimeBasedAlarm()); //must be called before timeIn();
				setSelectedAlarmTime(tb.getAlarmTriggerTime());
				setSelectedRepeatInterval(tb.getAlarmRepeatInterval(), tb.getRepeatMultiple());
				selectAlarmExecOutputOption(tb.getAlarmExecutionOutputType());
				sndToRun.setAudioFileName(tb.getAlarmSound());
				break;
			case "Commit":
				saveCurrentAlarm();
				break;
			case "Discard":
				cl.show(bottomCards, CMD_FINISH_EDIT);
				alarmViewOnlyMode(true);
				opmode = ChooserBox.ALARM_DISCARD;
				break;
			case "Remove":
				selIndex = alarmList.getSelectedIndex();
				if (selIndex != -1)
				{
					alarmsData.removeElementAt(selIndex);
					alarmList.setListData(alarmsData);
					alarmList.setSelectedIndex(selIndex);
				}
				break;
			case "Test":
				selIndex = alarmList.getSelectedIndex();
				if (selIndex != -1)
				{
					ExUtils.runAlarm(alarmsData.elementAt(selIndex), this, 50);
				}
				break;
			case "and repeat every":
				period.setSelectedIndex(rept.isSelected() ? 1 : 0);
				break;
			case "Play selected sound":
			case "Show message":
			selectMinOneAlarmExecOption();
				break;
			case "POMODORO_RADIO_BUTTON":
			case "TIME_RADIO_BUTTON":
			case "UPTIME_RADIO_BUTTON":
			case CMD_POM_LEAD_LABEL:
				validateTimeFormat();
				break;
			default:
				break;
		}
		alarmStartOnOrStartAfter();
		setOneEnabled();
	}

	private void updateLayoutCombo() {
		String currSel = imgFileList.getSelectedValue().getName().toLowerCase();
		if (currSel.contains("clock") || currSel.contains("dial")) {
			comboTLayout.getModel().setSelectedItem(TLabel.H_TILE_TEXT);
		} else {
			comboTLayout.getModel().setSelectedItem(TLabel.STRETCH_TEXT);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ie)
	{
		Object source = ie.getSource();
		if (ie.getStateChange() == ItemEvent.SELECTED)
		{
			if (source.equals(comboTLayout))
			{
				String comm = ie.getItem().toString();
				updateBackImagePreview(comm);
			}
			else if (source.equals(comboTz))
			{
				SimpleDateFormat sd = new SimpleDateFormat(information.getZonedTimeFormat());
				String tzId = comboTz.getItemAt(comboTz.getSelectedIndex());
				tzCb.setSelected(tzId.equals(TimeZone.getDefault().getID()));
				sd.setTimeZone(TimeZone.getTimeZone(tzId));
				updateAnalogClockTimezone(TimeZone.getTimeZone(tzId));
				jLDigiPreview.setText(sd.format(new Date()));
			}
			else if (source.equals(comboDialBdr))
			{
				Item tempFmt = comboDialBdr.getItemAt(comboDialBdr.getSelectedIndex());
				bdrPreview.setAnalogClockOptions(tempFmt.getId());
			}
			else if (source.equals(comboDateFmt) ||
				source.equals(comboPomodoro) || source.equals(comboPomFmt) || source.equals(uSymbol) ||
				source.equals(hSymbol) || source.equals(mSymbol) || source.equals(sSymbol) || source.equals(dSymbol))
			{
				validateTimeFormat();
			}
		}
		else
		{
			rept.setSelected(!ie.getItem().equals("Never"));
			
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
				picLabel.setText(NO_PREVIEW);
			}
			else
			{
				picLabel.setBackImage((new ImageIcon(imgFileList.getSelectedValue().toString())).getImage());
				picLabel.setText(analogCb0.isSelected() ? EMPTY : PREVIEW);
				// Bugfix: Remove dial ticks showing over image by default.
				if (information.getAnalogClockOption() % 1000 != TLabel.SHOW_NONE && analogCb1.isSelected()) comboDialBdr.setSelectedItem(anaDials.get(0));
			}
		}
		else if (srcList.equals(handSize))
		{
			bdrPreview.setDialObjSize(handSize.getSelectedValue().getSizeValue());
			bdrPreview.repaint();
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
				bdrPreview.setBorder(new LineBorder(((LineBorder)currBorder).getLineColor(), intThick, roundBdr.getSelectedIndex() != 0));
			}
		} else if (cSrc.equals(transLevel)) {
			String opacityPercent = Math.round((float)transLevel.getValue() / 20 * 100) + " %";
			jlTransSlide.setText("Opacity [ " + opacityPercent + " ]");
			transLevel.setToolTipText(opacityPercent);

			float derived = (float)transLevel.getValue() / 20;
			if (useImg.isSelected()) {
				picBackPanel.setAlpha(0.0f);
				picLabel.setImageAlpha(derived);
			} else {
				picBackPanel.setAlpha(derived);
			}
			picBackPanel.repaint();
			picLabel.repaint();
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
		@Override
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

		private String getAlarmDescription()
		{
			if (alarmList.getSelectedIndex() == -1)
			{
				return "Click on an alarm list item to view its description.";
			}
			else
			{
				TimeBean         tb     = alarmsData.elementAt(alarmList.getSelectedIndex());
				String           ltext  = "<html>This alarm \"(alarm name)\", is scheduled to start on (start time) and repeat (multi) (interval).<p><p>Its next run will be (next run).<p><p>A (run type) runs at the scheduled time.</html>";
				SimpleDateFormat tmp1   = chooseDate.getFormat();
				SimpleDateFormat tmp2   = ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat();
				GregorianCalendar ccal  = new GregorianCalendar();
				String[]          tmp3  = {"never", "minute", "hour", "day", "week", "??", "year", "month", "month same weekday"};
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
	}
}