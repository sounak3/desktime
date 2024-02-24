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
	private JButton setFont,resetFont,selFontCol,resFontCol;
	private TLabel fontPreview;
	private BorderPreview borderPreview;
	private JRadioButton bRaised,bLowered,eRaised,eLowered,lBorder;
	private JCheckBox nBorder,fixPlace,enTooltip,nativeLook,roundBdr;
	private JButton selLineCol,resLineCol;
	private JComboBox<String> comboTz, comboDateFmt, comboPomodoro, comboPomFmt;
	private JRadioButton selTimeZone,sysUpTime,pomodoroTime;
	private JLabel jLUptimeTxt, jLHourTxt, jLMinTxt, jLSecTxt, tzLabel;
	private JComboBox<String> uSymbol,hSymbol,mSymbol,sSymbol;
	private JButton resetDefs,helpFormat;
	private JLabel transSlide,previewLabel,jLDateFormat,jLPomFormat;
	private JSlider transLevel;
	private JCheckBox tzCb,useImg,useCol,useTrans,slowUpd;
	private FileList fileList;
	private JButton selectDir,selBackCol,resBackCol;
	private TLabel picLabel;
	private JRadioButton rbHtile,rbTile,rbVtile,rbCenter,rbFit,rbStretch;
	private JList<TimeBean> alarmList;
	private JLabel jLAlmAbout, jLAlmSame;
	private JButton add,remove,edit,test,browse;
	private JPanel bottomCards;
	private JComboBox<String> period, dateOrWeek;
	private JCheckBox rept,runCom,runMsg,runSnd;
	private JTextField cmdToRun, alarmName;
	private JRadioButton optStartOn, optStartAfter;
	private JSpinner timeSpinner1, timeSpinner2, countSpinner1;
	private DateChooser choosefrom;
	private int opmode   = 0;
	private int selIndex = -1;
	private boolean exceptionActive = false;
	private JButton ok,cancel;
	private Pomodoro pom;
	public Vector <TimeBean>data;
	public InitInfo information;

	public static final int FONT_TAB       = 0;
	public static final int BACKGROUND_TAB = 1;
	public static final int BORDER_TAB     = 2;
	public static final int TIMES_TAB      = 3;
	public static final int ALARMS_TAB     = 4;

	public ChooserBox(InitInfo initinfo, Vector <TimeBean>alarminfo)
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
		cFontList         = new ListChooser(as, "Font");
		cFontStyleList        = new ListChooser(as1, "Style");
		cFontSizeList         = new ListChooser(ainteger, "Size");
		selFontCol   = new JButton("Choose Font Color");
		resFontCol   = new JButton("Reset Font Color");
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
		resLineCol = new JButton("Reset");
		resLineCol.setActionCommand("Default Line Color");
		JSeparator jseparator1 = new JSeparator();
		fixPlace   = new JCheckBox("Fix to Place");
		roundBdr   = new JCheckBox("Round Corners");
		enTooltip  = new JCheckBox("Mouse-over Time info");
		nativeLook = new JCheckBox("Check to view this dialog box in system's look and feel");
		jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Looks and Borders"));
		//All configurations for Time Conf. Panel as follows:
		JPanel    jpanel2      = new JPanel(new GridBagLayout());
		Component component1   = Box.createHorizontalStrut(160);
		pomodoroTime = new JRadioButton("Pomodoro tasks");
		pomodoroTime.setActionCommand("POMODORO_RADIO_BUTTON");
		pomodoroTime.setToolTipText("<html>Select from well known pomodoro timer schedules (in minutes).<p>The schedules repeats itself once completed.</html>");
		selTimeZone = new JRadioButton("Current Time");
		selTimeZone.setActionCommand("TIME_RADIO_BUTTON");
		selTimeZone.setToolTipText("<html>This setting displays the current time in selected time zone<p>in the date-time format selected below.</html>");
		JPanel toolPane1 = getToolbarPanel();
		sysUpTime   = new JRadioButton("System Up-Time");
		sysUpTime.setActionCommand("UPTIME_RADIO_BUTTON");
		sysUpTime.setToolTipText("<html>This setting displays the time elapsed since your system<p>had started without a log off or shut down.</html>");
		JPanel toolPane2 = getToolbarPanel();
		ButtonGroup buttongroup1 = new ButtonGroup();
		buttongroup1.add(pomodoroTime);
		buttongroup1.add(selTimeZone);
		buttongroup1.add(sysUpTime);
		Component timePaneRadSpace = Box.createHorizontalStrut(17);
		previewLabel = new JLabel("", JLabel.CENTER);
		previewLabel.setVerticalAlignment(JLabel.CENTER);
		previewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		tzLabel = new JLabel("Select Timezone");
		tzCb    = new JCheckBox("Use local timezone");
		tzCb.setActionCommand("LocalTz");
		tzLabel.setLabelFor(tzCb);
		comboTz   = new JComboBox<String>(new DefaultComboBoxModel<String>(TimeZone.getAvailableIDs()));
		comboTz.setRenderer(new TimezoneCellRenderer());
		comboTz.addItemListener(this);
		jLDateFormat = new JLabel("Date/Time Format");
		String formats[] = {
			"yyyy.MM.dd G 'at' HH:mm:ss z",
			"zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy",
			"yyyy.MM.dd G 'at' hh:mm:ss z",
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
		comboDateFmt  = new JComboBox<String>(formats);
		comboDateFmt.setEditable(true);
		comboDateFmt.addItemListener(this);
		jLDateFormat.setLabelFor(comboDateFmt);
		JSeparator jseparator3 = new JSeparator();
		jLUptimeTxt = new JLabel("Up-time Label");
		jLHourTxt = new JLabel("Hour Label");
		jLMinTxt = new JLabel("Minute Label");
		jLSecTxt = new JLabel("Second Label");
		uSymbol = new JComboBox<String>(new String[]{"Up-Time: ", "", "Run time: ", "Uptime: ", "System uptime: "});
		hSymbol = new JComboBox<String>(new String[]{"-hour(s), ", "", " hour(s), ", " hrs. ", " hr(s), "});
		mSymbol = new JComboBox<String>(new String[]{"-minute(s), ", "", " minute(s), ", " mins. ", " min(s), "});
		sSymbol = new JComboBox<String>(new String[]{"-second(s)", "", " second(s)", " secs.", " sec(s)"});
		uSymbol.setEditable(true);
		uSymbol.addItemListener(this);
		hSymbol.setEditable(true);
		hSymbol.addItemListener(this);
		mSymbol.setEditable(true);
		mSymbol.addItemListener(this);
		sSymbol.setEditable(true);
		sSymbol.addItemListener(this);
		JSeparator jseparator4 = new JSeparator();
		String pomtech[] = {
			"25 min. work, 5 min. break, 30 min. rest",
			"45 minutes work, 15 minutes break",
			"50 minutes work, 10 minutes break",
			"52 minutes work, 17 minutes break",
			"90 minutes work, 20 minutes break",
			"112 minutes work, 26 minutes break"
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
		jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Time Display Options"));
		//All configurations for Background Panel as follows:
		JPanel jpanel3 = new JPanel(new GridBagLayout());
		useImg   = new JCheckBox("<html>Use background image. Use image settings from the following:</html>");
		useCol   = new JCheckBox("<html>Use Background Color. Use color settings from the following:</html>");
		useTrans = new JCheckBox("<html>Use transparent background (may cause flickers)</html>");
		useTrans.setActionCommand("Transparent background");
		slowUpd  = new JCheckBox("<html>Reduce flicker (slower transparent background updates)</html>");
		slowUpd.setActionCommand("Reduce flicker");
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
		Component component4 = Box.createHorizontalStrut(140);
		selectDir = new JButton("<< Choose Image Directory");
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
		alarmList = new JList<TimeBean>(data);
		alarmList.setCellRenderer(new AlarmListCellRenderer());
		alarmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jsp.setViewportView(alarmList);
		alarmList.addListSelectionListener(this);
		add         = new JButton("Add");
		edit        = new JButton("Edit");
		remove      = new JButton("Remove");
		test        = new JButton("Test >>");
		bottomCards = new JPanel(new CardLayout());
		jLAlmAbout  = new JLabel("");
		jLAlmAbout.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
		setDescriptionText();
		JPanel almSet = new JPanel(new GridBagLayout());
		JLabel nameLabel    = new JLabel("Alarm Name",JLabel.CENTER);
		       alarmName    = new JTextField();
		nameLabel.setLabelFor(alarmName);
		alarmName.setToolTipText("<html>Please enter a brief description of the alarm such as:<p> &quot;John's Birthday&quot;</html>");
		ButtonGroup bgr = new ButtonGroup();
		optStartOn 		= new JRadioButton("Start on");
		bgr.add(optStartOn);
		choosefrom   = new DateChooser();
		timeSpinner1 = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor spinModel = new JSpinner.DateEditor(timeSpinner1,"hh:mm a");
		timeSpinner1.setEditor(spinModel);
		       rept   = new JCheckBox("and repeat every");
		countSpinner1 = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
		String pds[]  = new String[]{"Never", "Minute", "Hour", "Day", "Week", "Month", "Year"};
		       period = new JComboBox<String>(pds);
		period.addItemListener(this);
		jLAlmSame = new JLabel("same");
		String dow[]  = new String[]{"Date", "Weekday"};
		dateOrWeek    = new JComboBox<String>(dow);
		jLAlmSame.setEnabled(false);
		dateOrWeek.setEnabled(false);
		optStartAfter = new JRadioButton("Start after");
		bgr.add(optStartAfter);
		timeSpinner2             = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dit2 = new JSpinner.DateEditor(timeSpinner2,"HH:mm");
		timeSpinner2.setEditor(dit2);
		JLabel jLAlmHrs = new JLabel(" hours of program start-up");
		JLabel jLAlmAct = new JLabel("On Alarm",JLabel.CENTER);
		runCom        = new JCheckBox("Start Command");
		runSnd        = new JCheckBox("Just Beep");
		runMsg        = new JCheckBox("Message");
		cmdToRun      = new JTextField(10);
		browse        = new JButton("Browse...");
		bottomCards.add(jLAlmAbout, "FinishEdit");
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
			ExUtils.addComponent(jpanel,  cFontList, 		0, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontStyleList, 	1, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  cFontSizeList, 	2, 0, 1, 1, 0.33D, 0.4D, this);
			ExUtils.addComponent(jpanel,  setFont, 			0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resetFont, 		1, 1, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  selFontCol, 		0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  resFontCol, 		1, 2, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel,  jscrollpane, 		0, 3, 3, 1, 1.0D, 0.6D, this);
			ExUtils.addComponent(jpanel1, nBorder, 			0, 0, 4, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bRaised, 			0, 2, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, borderPreview, 	2, 2, 4, 4, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, bLowered, 		0, 3, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eRaised, 			0, 4, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, eLowered, 		0, 5, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator, 		0, 6, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, lBorder, 			0, 7, 2, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, component, 		0, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, selLineCol, 		1, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, resLineCol, 		2, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel1, jseparator1, 		0, 9, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel1, enTooltip, 		0, 10, 2, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, fixPlace, 		2, 10, 1, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, roundBdr, 		3, 10, 2, 1, 0.33D, 0.0D, this);
			ExUtils.addComponent(jpanel1, nativeLook, 		0, 11, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, component1,	 	1, 1, 1, 1, 1.0D, 1.0D, this);
			ExUtils.addComponent(jpanel2, previewLabel, 	0, 2, 5, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, selTimeZone, 		0, 3, 2, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, tzCb,		 		2, 3, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, toolPane1, 		4, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, timePaneRadSpace, 0, 2, 1, 1, 0.0D, 0.2D, this);
			ExUtils.addComponent(jpanel2, tzLabel, 			1, 4, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboTz, 			2, 4, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLDateFormat, 	1, 5, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboDateFmt, 	2, 5, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator3, 		1, 6, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sysUpTime, 		0, 7, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, toolPane2, 		4, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLUptimeTxt, 		1, 8, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, uSymbol, 			2, 8, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLHourTxt, 		3, 8, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, hSymbol, 			4, 8, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLMinTxt, 		1, 9, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, mSymbol, 			2, 9, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLSecTxt, 		3, 9, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, sSymbol, 			4, 9, 1, 1, 0.5D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jseparator4, 		1, 10, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, pomodoroTime, 	0, 11, 2, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomodoro, 	2, 11, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, jLPomFormat, 		1, 12, 1, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel2, comboPomFmt,		2, 12, 3, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useImg, 			0, 0, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component2, 		0, 1, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, jscrollpane1, 	1, 1, 1, 2, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, selectDir, 		2, 1, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component3,		0, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, component4,		1, 2, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, picLabel, 		2, 2, 2, 1, 0.0D, 0.8D, this);
			ExUtils.addComponent(jpanel3, rbTile, 			1, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbHtile, 			2, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbVtile, 			3, 3, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbCenter, 		1, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbFit, 			2, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, rbStretch, 		3, 4, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useCol, 			0, 5, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, selBackCol, 		1, 6, 2, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, resBackCol, 		3, 6, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, transSlide, 		1, 7, 1, 1, 0.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, transLevel, 		2, 7, 3, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, useTrans, 		0, 8, 4, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(jpanel3, slowUpd,			1, 9, 7, 1, 1.0D, 0.0D, this);
			ExUtils.addComponent(topList, jsp,				0, 0, 1,	5, 1.0D, 1.0D, this);
			ExUtils.addComponent(topList, add,				1, 0, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, edit,				1, 1, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, remove,			1, 2, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(topList, test,				1, 3, 1,	1, 0.0D, 0.25D, this);
			ExUtils.addComponent(almSet,  nameLabel,		0, 0, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  alarmName,		1, 0, 6,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartOn,		0, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  choosefrom,		1, 1, 5,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner1,		6, 1, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  optStartAfter,	0, 2, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  timeSpinner2,		1, 2, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmHrs,			3, 2, 4,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  rept,				0, 3, 2,	1, 0.4D, 0.0D, this);
			ExUtils.addComponent(almSet,  countSpinner1,	2, 3, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  period,			3, 3, 2,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmSame,		5, 3, 1,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  dateOrWeek,		6, 3, 1,	1, 0.2D, 0.0D, this);
			ExUtils.addComponent(almSet,  jLAlmAct,			0, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runCom,			1, 4, 3,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runSnd,			4, 4, 2,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  runMsg,			6, 4, 1,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  cmdToRun,			0, 5, 6,	1, 0.0D, 0.0D, this);
			ExUtils.addComponent(almSet,  browse,			6, 5, 1,	1, 0.0D, 0.0D, this);
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
		chooserbox.setSize(450, 470);
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
		slowUpd.setSelected(initinfo.isSlowTransUpdating());
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
		roundBdr.setSelected(initinfo.hasRoundedCorners());
		enTooltip.setSelected(initinfo.hasTooltip());
		nativeLook.setSelected(initinfo.hasNativeLook());
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
			sd.setTimeZone(TimeZone.getTimeZone(comboTz.getItemAt(comboTz.getSelectedIndex())));
			privT = sd.format(new Date());
		}
		else if (dispM.equals("UPTIME")) {
			sysUpTime.setSelected(true);
			privT = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), initinfo.getUpTimeFormat());
		}
		else if (dispM.equals("POMODORO")) {
			pomodoroTime.setSelected(true);
			privT = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(), initinfo.getPomodoroFormat(), pom.getRunningLabel(), false);
		}
		previewLabel.setText(privT);
		String s1    = initinfo.getUpTimeFormat();
		String arr[] = s1.split("\'");
		uSymbol.setSelectedItem(arr[1]);
		hSymbol.setSelectedItem(arr[3]);
		mSymbol.setSelectedItem(arr[5]);
		sSymbol.setSelectedItem(arr.length == 8 ? arr[7] : " ");
		comboPomodoro.setSelectedItem(initinfo.getPomodoroTask());
		comboPomFmt.setSelectedItem(initinfo.getPomodoroFormat());
		data = alarmInit;
		alarmList.setListData(data);
		setDescriptionText();
		setOneEnabled();
	}

	private final JPanel getToolbarPanel()
	{
		JPanel smallPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		Image defaultPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/default.png"))).getImage();
		resetDefs  = new JButton(new ImageIcon(defaultPng.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		resetDefs.setActionCommand("Reset To Defaults");
		resetDefs.setToolTipText("Reset to default values");
		resetDefs.setMargin(new Insets(2, 5, 2, 5));
		resetDefs.addActionListener(this);
		Image helpPng = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/help.png"))).getImage();
		helpFormat  = new JButton(new ImageIcon(helpPng.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		helpFormat.setActionCommand("Help On Format");
		helpFormat.setToolTipText("Help on date time format characters.");
		helpFormat.setMargin(new Insets(2, 5, 2, 5));
		helpFormat.addActionListener(this);
		smallPane.add(resetDefs);
		smallPane.add(helpFormat);
		return smallPane;
	}

	private void setDescriptionText()
	{
		if (alarmList.getSelectedIndex() == -1)
		{
			jLAlmAbout.setText("Click on an alarm list item to view its description.");
		}
		else
		{
			TimeBean         tb     = (TimeBean)data.elementAt(alarmList.getSelectedIndex());
			String           ltext  = "<html>This alarm \"(alarm name)\", is scheduled to start on (start time) and repeat (multi) (interval).<p><p>Its next run will be (next run).<p><p>A (run type) runs at the scheduled time.</html>";
			SimpleDateFormat tmp1   = choosefrom.getFormat();
			SimpleDateFormat tmp2   = ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat();
			GregorianCalendar ccal  = new GregorianCalendar();
			String           tmp3[] = {"never", "minute", "hour", "day", "week", "??", "year", "month", "month same weekday"};
			int              tmp4   = 0;
			ltext  = ltext.replace("(alarm name)", tb.getName());
			ltext  = ltext.replace("(start time)", tmp1.format(tb.getAlarmTriggerTime()) + " at " + tmp2.format(tb.getAlarmTriggerTime()));
			tmp4   = tb.getAlarmRepeatInterval().intValue();
			ltext  = ltext.replace("(multi)", tmp4 == 0 ? "" : "every " + tb.getRepeatMultiple().toString());
			ltext  = ltext.replace("(interval)", tmp4 == 0 ? tmp3[tmp4] : tmp3[tmp4] + "(s)");
			ccal.setTime(tb.getNextAlarmTriggerTime());
			ccal.add(Calendar.SECOND, 1);
			ltext  = ltext.replace("(next run)", tmp4 == 0 ? "never" : "on " + tmp1.format(ccal.getTime()) + " at " + tmp2.format(ccal.getTime()));
			tmp4   = tb.getAlarmExecutionOutputType().intValue();
			ltext  = ltext.replace("(run type)", ((tmp4 % 2 != 0) ? " command," : "") + (((tmp4 % 3 == 0) || (tmp4 == 2)) ? " sound," : "") + ((tmp4 > 3) ? " message" : ""));
			jLAlmAbout.setText(ltext);
		}
	}

	private void setOneEnabled()
	{
		comboPomodoro.setEnabled(pomodoroTime.isSelected());
		jLPomFormat.setEnabled(pomodoroTime.isSelected());
		comboPomFmt.setEnabled(pomodoroTime.isSelected());
		jLDateFormat.setEnabled(selTimeZone.isSelected());
		tzLabel.setEnabled(selTimeZone.isSelected());
		tzCb.setEnabled(selTimeZone.isSelected());
		comboTz.setEnabled(selTimeZone.isSelected());
		comboDateFmt.setEnabled(selTimeZone.isSelected());
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
		if (optStartOn.isSelected())
		{
			timeSpinner1.setEnabled(true);
			timeSpinner2.setEnabled(false);
			choosefrom.setEnabled(true);
		}
		else if (optStartAfter.isSelected())
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

	private void setSelectedFont(Font font1)
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
			i = Integer.parseInt(cFontSizeList.getText());
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
		Date temp=new Date();
		if (optStartOn.isSelected())
		{
			String           pttn    = choosefrom.getFormat().toPattern() + ((JSpinner.DateEditor)timeSpinner1.getEditor()).getFormat().toPattern();
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
		else if (optStartAfter.isSelected())
		{
			temp = ((JSpinner.DateEditor)timeSpinner2.getEditor()).getModel().getDate();
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
			choosefrom.setDate(date);
			timeSpinner1.setValue(date);
		}
		else if (optStartAfter.isSelected())
		{
			timeSpinner2.setValue(date);
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
		enadesa();
	}
	
	public boolean startAfterTimeIsSelected()
	{
		enadesa();
		return optStartAfter.isSelected();
	}
	
	public void selectAlarmExecOutputOption(int type)
	{
		runCom.setSelected(type % 2 != 0); //for 1;
		runMsg.setSelected(type > 3); //for 4;
		runSnd.setSelected((type % 3 == 0) || (type == 2)); //for 2;
		cmdToRun.setEnabled(type % 2 != 0);
		browse.setEnabled(type % 2 != 0);
	}
	
	public int getSelectedAlarmExecOutputOption()
	{
		int totaltmp = 0;
		if  (runCom.isSelected()) totaltmp += 1;
		if  (runSnd.isSelected()) totaltmp += 2;
		if  (runMsg.isSelected()) totaltmp += 4;
		return totaltmp;
	}

	public InitInfo applySettings() throws Exception
	{
		information.setFont(getSelectedFont());
		information.setForeground(selFontCol.getBackground());
		information.setUsingImage(useImg.isSelected());
		information.setGlassEffect(useTrans.isSelected());
		information.setSlowTransUpdating(slowUpd.isSelected());
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
		information.setRoundCorners(roundBdr.isSelected());
		information.setNativeLook(nativeLook.isSelected());
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
			information.setUpTimeFormat("'" + uSymbol.getSelectedItem().toString() + "'HH'" + hSymbol.getSelectedItem().toString() + "'mm'" + mSymbol.getSelectedItem().toString() + "'ss'" + sSymbol.getSelectedItem().toString() + "'");
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
			slowUpd.setSelected(false);
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
		tb.setSystemStartTimeBasedAlarm(startAfterTimeIsSelected()); // must be set before setRuntime();
		if (startAfterTimeIsSelected()) tb.setNextAlarmTriggerTime(getSelectedAlarmTime());//if op2 then nextRuntime
		else tb.setAlarmTriggerTime(getSelectedAlarmTime()); // should be set instead of runtime;
		tb.setAlarmRepeatInterval(getSelectedRepeatInterval());
		tb.setRepeatMultiple(rept.isSelected() ? (int)countSpinner1.getValue() : 0);
		tb.setAlarmExecutionOutputType(getSelectedAlarmExecOutputOption());
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
				privT = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(), pttn, pom.getRunningLabel(), false);
			}
			previewLabel.setText(privT);
			exceptionActive = false;
		}
		catch (Exception exception)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ok.doClick();
					if (selTimeZone.isSelected())
						comboDateFmt.requestFocus();
					else if (sysUpTime.isSelected())
						uSymbol.requestFocus();
					else if (pomodoroTime.isSelected())
						comboPomFmt.requestFocus();
				}
			});
			exceptionActive = true;
		}
	}

	public void actionPerformed(ActionEvent actionevent)
	{
		String     comm = actionevent.getActionCommand();
		CardLayout cl   = (CardLayout)(bottomCards.getLayout());
		if (comm.equals("Preview Font"))
			fontPreview.setFont(getSelectedFont());
		else if (comm.equals("Reset Font"))
		{
			fontPreview.setFont((new JLabel()).getFont());
			setSelectedFont((new JLabel()).getFont());
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
		else if (comm.equals("Reset Font Color"))
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
		else if (comm.equals("LocalTz"))
		{
			if (tzCb.isSelected())	comboTz.setSelectedItem(TimeZone.getDefault().getID());
			else comboTz.setSelectedItem(information.getTimeZone());
		}
		else if (comm.equals("Reset To Defaults"))
		{
			if (selTimeZone.isSelected())
			{
				tzCb.setSelected(true);
				comboTz.setSelectedItem(TimeZone.getDefault().getID());
				comboDateFmt.setSelectedItem("zzz':' hh:mm:ss a',' EEEE',' dd-MMM-yyyy");
			}
			else if (sysUpTime.isSelected())
			{
				uSymbol.setSelectedItem("Up-Time: ");
				hSymbol.setSelectedItem("-hour(s), ");
				mSymbol.setSelectedItem("-minute(s), ");
				sSymbol.setSelectedItem("-second(s)");
			} else if (pomodoroTime.isSelected())
			{
				comboPomodoro.setSelectedItem("25 min. work, 5 min. break, 30 min. rest");
				comboPomFmt.setSelectedItem("mm:ss");
			}
		}
		else if (comm.equals("Help On Format"))
		{
			if (!exceptionActive)
				new FormatHelp(this);
			else
				validateTimeFormat();
		}
		else if (comm.equals("<< Choose Image Directory"))
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
				enadesa2(false);
				opmode      = 2;
				TimeBean tb = (TimeBean)data.elementAt(selIndex);
				alarmName.setText(tb.getName());
				selectStartAfterTimeOption(tb.isSystemStartTimeBasedAlarm().booleanValue()); //must be called before timeIn();
				setSelectedAlarmTime(tb.getAlarmTriggerTime());
				setSelectedRepeatInterval(tb.getAlarmRepeatInterval(), tb.getRepeatMultiple());
				selectAlarmExecOutputOption(tb.getAlarmExecutionOutputType().intValue());
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
		else if (comm.equals("and repeat every"))
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
		else if (comm.equals("POMODORO_RADIO_BUTTON") || comm.equals("TIME_RADIO_BUTTON") || comm.equals("UPTIME_RADIO_BUTTON"))
		{
			validateTimeFormat();
		}
		enadesa();
		setOneEnabled();
	}

	public void itemStateChanged(ItemEvent ie)
	{
		Object source = ie.getSource();
		if (ie.getStateChange() == ItemEvent.SELECTED && source.equals(comboTz))
		{
			SimpleDateFormat sd = new SimpleDateFormat(information.getZonedTimeFormat());
			String tzId = comboTz.getItemAt(comboTz.getSelectedIndex());
			tzCb.setSelected(tzId.equals(TimeZone.getDefault().getID()));
			sd.setTimeZone(TimeZone.getTimeZone(tzId));
			previewLabel.setText(sd.format(new Date()));
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED && (source.equals(comboDateFmt) ||
			source.equals(comboPomodoro) || source.equals(comboPomFmt) || source.equals(uSymbol) ||
			source.equals(hSymbol) || source.equals(mSymbol) || source.equals(sSymbol)))
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