package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class DeskStop extends JFrame implements MouseInputListener, ActionListener, ComponentListener, ChangeListener
{
	protected transient ClockThread clockThread; 
	protected transient Refresher refreshThread;
	protected transient TrayIcon trayIcon;
	protected transient SystemTray thisTray;
	private String time, lastPomFormat, lastPomTask;
	private Date date;
	private int locX, locY, locW, locH, cursorX, cursorY, cursorW, cursorH;
	private static int recentIconType;
	private TLabel tLabel;
	private TwilightPanel mainPane;
	private SimpleDateFormat sd, clk;
	private FontMetrics metrics;
	private InitInfo info;
	private Vector <TimeBean>alarms;
	private JPopupMenu pMenu;
	private JMenu addPanel, mFormat, timeMode, timeZone, mSize, mOpacityLevel, mRoundCorners, mDialMarks, mHandSize, mDigiFormat;
	private JSlider sizer, miOpacitySlider;
	private JMenuItem miAnaTime, miDigTime, miUptime, miPomo, miSeltz, miDeftz, timSet, zonSet, mAllOpacity, miLabBorder;
	private JMenuItem fore, back, alm, bdr, exit, about, newItem, dupItem, removePanel, miMovable, ontop, sysClk;
	private JCheckBoxMenuItem miAmPmMark, miTzMark, miWkDyMark, miDateMark, miSelectAll;
	private JRadioButtonMenuItem[] impZon, miDigiFormats;
	private JRadioButtonMenuItem[] tMenuItem;
	private JRadioButtonMenuItem[] miDialObjSize;
	private transient PointerInfo pi;
	private Point pointerLoc;
	private Dimension scsize;
	protected transient Robot robot;
	private boolean refreshNow = true;
	private boolean pixelTranslucency, wholeTranslucency, robotSupport, allowMoveOutOfScreen, alreadyOutOfScreen;
	private transient Pomodoro pom;
	private Container contentPane;
	private String tipCur = "<html><b>Currently Displaying:</b> Time of your location (Time-Zone) time. <p>(This is with reference to system time and not internet).</html>";
	private String tipUpt = "<html><b>Currently Displaying:</b> System Up-Time <p>The time your computer is running <p>without a shut-down or log-off.</html>";
	private String tipPom = "<html><b>Currently Displaying:</b> Pomodoro Timer <p>Timer slots of pomodoro task. <p>One rest slot after 4 repetations of regular slots.</html>";
	protected final String[] impZoneList = new String[]{
		"Australia Eastern Standard Time (AET)",
		"British Summer Time (BST)",
		"Central European Time (CET)",
		"Central Standard Time (CST6CDT)",
		"China Standard Time (CTT)",
		"Eastern Standard Time (EST5EDT)",
		"Eastern European Time (EET)",
		"Pacific Standard Time (PST8PDT)",
		"India Standard Time (IST)",
		"Japan Standard Time (JST)",
		"Coordinated Universal Time (UTC)"};
	private static final int[] fontSizes                       = {6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72, 80, 96, 112, 128, 144, 160, 192, 224, 256, 288, 320};
	private static final GraphicsDevice gd                     = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public  static final String DISPLAY_MODE_CURRENT_TIMEZONE  = "CURTZ";
	public  static final String DISPLAY_MODE_SELECTED_TIMEZONE = "GMTTZ";
	public  static final String DISPLAY_MODE_SYSTEM_UPTIME     = "UPTIME";
	public  static final String DISPLAY_MODE_POMODORO_TIMER    = "POMODORO";
	public  static final String WINDOW_SHADOW_PROPERTY         = "Window.shadow";
	public  static final String PREFERENCES_TITLE              = "Preferences...";
	private static final String ANALOG_CLOCK_USE_PATTERN       = "zzz'|'hh'|'mm'|'ss'|'a'|'dd'|'MMM'|'EEE";
	private static final String ABOUT_STRING                   = "<html>Made by : Sounak Choudhury<p>E-mail : <a href='mailto:sounak_s@rediffmail.com'>sounak_s@rediffmail.com</a><p><p>The software, information and documentation is provided \"AS IS\" without<p>warranty of any kind, either express or implied. By downloading, installing<p>or using this software, you signify acceptance of and agree to the terms<p>and conditions mentioned in LICENSE.txt. Suggestions and credits are<p>Welcomed. Thank you for using DeskStop!</html>";
	private static final String CMD_TIME_SETTINGS              = "Time Settings";
	private static final String CMD_ABOUT					   = "About DeskStop...";
	private static final String CMD_ANALOG_DIAL_LABEL		   = "AnalogDialLabel";
	private static final String CMD_HAND_STRING_COMPLEMENT     = "_HANDS";
	private static final String CMD_TIMEFORMAT                 = "TIMEFORMAT";
	private static final String TITLE_STRING			       = "DeskStop";
	private static ArrayList<InitInfo> deskstops;
	private final ImageIcon plusPng  = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/plus-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon minusPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/minus-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon checkPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/checked-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon clearPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/unchecked-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	// private final ImageIcon aboutGif = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/duke.gif"));
	private final ImageIcon mainIcon = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/DeskStop-icon.png"))).getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH));

	public DeskStop(InitInfo info, Vector<TimeBean> alarms)
	{
		super(gd.getDefaultConfiguration());
		this.info         = info;
		this.alarms       = alarms;
		pixelTranslucency = gd.isWindowTranslucencySupported(PERPIXEL_TRANSLUCENT);
		// pixelTranslucency = false;
		wholeTranslucency = gd.isWindowTranslucencySupported(TRANSLUCENT);
		robotSupport      = ExUtils.checkAWTPermission("createRobot") && ExUtils.checkAWTPermission("readDisplayPixels");
		clockThread       = null;
		refreshThread     = null;
		scsize            = Toolkit.getDefaultToolkit().getScreenSize();
		tLabel            = new TLabel("Welcome " + System.getProperty("user.name"));
		tLabel.setForeground(Color.black);
		tLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		mainPane = new TwilightPanel(new BorderLayout());
		mainPane.setOpaque(false);
		mainPane.setBackground(Color.white);
		mainPane.add(tLabel, BorderLayout.CENTER);
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPane, BorderLayout.CENTER);
		setUndecorated(true);
		setTitle(TITLE_STRING);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(mainIcon.getImage());
		thisTray = SystemTray.getSystemTray();
		updateIconType(); // must be called before visibility
		pack();
		info.setPixelAlphaSupport(pixelTranslucency);
		info.setWindowAlphaSupport(wholeTranslucency);
		info.setScreenshotSupport(robotSupport);
		addComponentListener(this);
		pointerLoc    = new Point(0, 0);
		locX  = (int)info.getLocation().getX();
		locY  = (int)info.getLocation().getY();
		cursorX  = cursorY = 0;
		cursorW  = cursorH = 0;
		sd    = new SimpleDateFormat(info.getZonedTimeFormat());
		clk   = new SimpleDateFormat(ANALOG_CLOCK_USE_PATTERN);
		date  = new Date();
		time  = sd.format(date);
		UIManager.put("PopupMenu.background", Color.WHITE);
		UIManager.put("Menu.background", Color.WHITE);
		UIManager.put("MenuItem.background", Color.WHITE);
		UIManager.put("CheckBoxMenuItem.background", Color.WHITE);
		UIManager.put("RadioButtonMenuItem.background", Color.WHITE);
		UIManager.put("Menu.opaque", true);
		UIManager.put("MenuItem.opaque", true);
		pMenu = new JPopupMenu("DeskTime Menu");
		mFormat = new JMenu("Preferences");
		int mCnt = 0;
		mDigiFormat = new JMenu("Digital clock format");
		String[] formatsList = ChooserBox.getDigitalTimeFormats();
		ButtonGroup digiFmtButtonGroup = new ButtonGroup();
		miDigiFormats = new JRadioButtonMenuItem[formatsList.length];
		for (int i = 0; i < formatsList.length; i++) {
			miDigiFormats[i] = new JRadioButtonMenuItem(formatsList[i]);
			miDigiFormats[i].addActionListener(this);
			miDigiFormats[i].setActionCommand(CMD_TIMEFORMAT + formatsList[i]);
			digiFmtButtonGroup.add(miDigiFormats[i]);
			mDigiFormat.add(miDigiFormats[i]);
		}
		mHandSize = new JMenu("Clock Hand and Label size");
		ButtonGroup dialObjButtonGroup = new ButtonGroup();
		miDialObjSize = new JRadioButtonMenuItem[TLabel.DIAL_OBJECTS_SIZE.values().length];
		for (TLabel.DIAL_OBJECTS_SIZE sizes : TLabel.DIAL_OBJECTS_SIZE.values()) {
			miDialObjSize[mCnt] = new JRadioButtonMenuItem(ExUtils.toCamelCase(sizes.name()));
			miDialObjSize[mCnt].setSelected(info.getDialObjectsSize() == sizes.getSizeValue());
			miDialObjSize[mCnt].setActionCommand(sizes.name() + CMD_HAND_STRING_COMPLEMENT);
			miDialObjSize[mCnt].addActionListener(this);
			dialObjButtonGroup.add(miDialObjSize[mCnt]);
			mHandSize.add(miDialObjSize[mCnt]);
			mCnt++;
		}
		mCnt = 0;
		mRoundCorners = new JMenu("Rounded corners");
		ButtonGroup rCornersButtonGroup = new ButtonGroup();
		tMenuItem = new JRadioButtonMenuItem[ExUtils.ROUND_CORNERS.values().length];
		for (ExUtils.ROUND_CORNERS corner : ExUtils.ROUND_CORNERS.values()) {
			tMenuItem[mCnt] = new JRadioButtonMenuItem(ExUtils.toCamelCase(corner.name()));
			tMenuItem[mCnt].setSelected(info.getRoundCorners() == corner.getRoundType());
			tMenuItem[mCnt].setActionCommand(corner.name());
			tMenuItem[mCnt].addActionListener(this);
			rCornersButtonGroup.add(tMenuItem[mCnt]);
			mRoundCorners.add(tMenuItem[mCnt]);
			mCnt++;
		}
		mOpacityLevel = new JMenu("Opacity level %");
		Hashtable<Integer, JLabel> hto = new Hashtable<>();
		for (int i = 4; i <= 20; i += 4)
			hto.put(i, new JLabel(String.valueOf(i * 5)));
		miOpacitySlider  = new JSlider(SwingConstants.VERTICAL, 4, 20, 10);
		miOpacitySlider.setPreferredSize(new Dimension(miOpacitySlider.getPreferredSize().width + 40, miOpacitySlider.getPreferredSize().height + 40));
		miOpacitySlider.setMajorTickSpacing(4);
		miOpacitySlider.setMinorTickSpacing(1);
		miOpacitySlider.setLabelTable(hto);
		miOpacitySlider.setPaintLabels(true);
		miOpacitySlider.setPaintTicks(true);
		miOpacitySlider.setSnapToTicks(true);
		miOpacitySlider.addChangeListener(this);
		mOpacityLevel.add(miOpacitySlider);
		mAllOpacity = new JMenuItem("Blend foreground opacity");
		mAllOpacity.addActionListener(this);
		fore  = new JMenuItem("Font & Foreground...");
		fore.addActionListener(this);
		back  = new JMenuItem("Background & Dials...");
		back.addActionListener(this);
		bdr   = new JMenuItem("Borders & Clock UI...");
		bdr.addActionListener(this);
		timeMode = new JMenu(CMD_TIME_SETTINGS);
		miDigTime   = new JMenuItem("Show Digital clock");
		miDigTime.addActionListener(this);
		miAnaTime   = new JMenuItem("Show Analog clock");
		miAnaTime.addActionListener(this);
		miUptime = new JMenuItem("Show System Up-time");
		miUptime.addActionListener(this);
		miUptime.setActionCommand(DISPLAY_MODE_SYSTEM_UPTIME);
		miPomo   = new JMenuItem("Show Pomodoro Timer");
		miPomo.addActionListener(this);
		miPomo.setActionCommand(DISPLAY_MODE_POMODORO_TIMER);
		timSet   = new JMenuItem("More settings...");
		timSet.addActionListener(this);
		timSet.setActionCommand(CMD_TIME_SETTINGS);
		timeZone = new JMenu("Timezone");
		miSeltz  = new JMenuItem("Last selected");
		miSeltz.addActionListener(this);
		miSeltz.setActionCommand(DISPLAY_MODE_SELECTED_TIMEZONE);
		miDeftz  = new JMenuItem("System");
		miDeftz.addActionListener(this);
		miDeftz.setActionCommand(DISPLAY_MODE_CURRENT_TIMEZONE);
		timeZone.add(miSeltz);
		timeZone.add(miDeftz);
		timeZone.addSeparator();
		ButtonGroup zonesButtonGroup = new ButtonGroup();
		impZon = new JRadioButtonMenuItem[impZoneList.length];
		for (int i = 0; i < impZoneList.length; i++) {
			impZon[i] = new JRadioButtonMenuItem(impZoneList[i]);
			impZon[i].addActionListener(this);
			impZon[i].setActionCommand("TZ-" + impZoneList[i].split("[\\(|\\)]")[1]);
			zonesButtonGroup.add(impZon[i]);
			timeZone.add(impZon[i]);
		}
		zonSet   = new JMenuItem("More Timezones...");
		zonSet.addActionListener(this);
		zonSet.setActionCommand(CMD_TIME_SETTINGS);
		timeZone.addSeparator();
		timeZone.add(zonSet);
		mDialMarks = new JMenu("Dial marker labels");
		miSelectAll = new JCheckBoxMenuItem("Select All");
		miSelectAll.setActionCommand(CMD_ANALOG_DIAL_LABEL);
		miSelectAll.addActionListener(this);
		miAmPmMark = new JCheckBoxMenuItem("AM-PM label");
		miAmPmMark.setActionCommand(CMD_ANALOG_DIAL_LABEL);
		miAmPmMark.addActionListener(this);
		miTzMark   = new JCheckBoxMenuItem("Time zone label");
		miTzMark.setActionCommand(CMD_ANALOG_DIAL_LABEL);
		miTzMark.addActionListener(this);
		miWkDyMark   = new JCheckBoxMenuItem("Weekday label");
		miWkDyMark.setActionCommand(CMD_ANALOG_DIAL_LABEL);
		miWkDyMark.addActionListener(this);
		miDateMark   = new JCheckBoxMenuItem("Date label");
		miDateMark.setActionCommand(CMD_ANALOG_DIAL_LABEL);
		miDateMark.addActionListener(this);
		mDialMarks.add(miSelectAll);
		mDialMarks.addSeparator();
		mDialMarks.add(miAmPmMark);
		mDialMarks.add(miTzMark);
		mDialMarks.add(miWkDyMark);
		mDialMarks.add(miDateMark);
		miLabBorder  = new JMenuItem("Dial marker labels border");
		miLabBorder.addActionListener(this);
		sysClk = new JMenuItem("System Clock");
		alm    = new JMenuItem("Alarm & sounds");
		alm.addActionListener(this);
		sysClk.addActionListener(this);
		Hashtable<Integer, JLabel> ht = new Hashtable<>();
		for (int i = 0; i < fontSizes.length; i++)
			ht.put(i, new JLabel(String.valueOf(fontSizes[i])));
		sizer = new JSlider(SwingConstants.VERTICAL, 0, fontSizes.length - 1, 5);
		sizer.setLabelTable(ht);
		sizer.setMinorTickSpacing(1);
		sizer.setPaintLabels(true);
		sizer.setPaintTicks(true);
		sizer.setSnapToTicks(true);
		sizer.addChangeListener(this);
		mSize = new JMenu("Resize");
		int sliderheight = getFontMetrics(getFont()).getHeight() * fontSizes.length;
		sizer.setPreferredSize(new Dimension(sizer.getPreferredSize().width + 20, sliderheight));
		mSize.add(sizer);
		miMovable  = new JMenuItem("Movable");
		miMovable.addActionListener(this);
		ontop = new JMenuItem("Always on top");
		ontop.addActionListener(this);
		addPanel = new JMenu("Add clock panel");
		addPanel.setIcon(plusPng);
		newItem = new JMenuItem("New");
		newItem.addActionListener(this);
		dupItem = new JMenuItem("Duplicate");
		dupItem.addActionListener(this);
		addPanel.add(newItem);
		addPanel.add(dupItem);
		removePanel = new JMenuItem("Remove this panel", minusPng);
		removePanel.addActionListener(this);
		removePanel.setEnabled(info.getID() != 0);
		about = new JMenuItem(CMD_ABOUT);
		about.addActionListener(this);
		exit  = new JMenuItem("Exit");
		exit.addActionListener(this);
		timeMode.add(timeZone);
		timeMode.addSeparator();
		timeMode.add(miDigTime);
		timeMode.add(miAnaTime);
		timeMode.add(miUptime);
		timeMode.add(miPomo);
		timeMode.addSeparator();
		timeMode.add(timSet);
		mFormat.add(fore);
		mFormat.add(back);
		mFormat.add(bdr);
		mFormat.addSeparator();
		mFormat.add(mDigiFormat);
		mFormat.add(mHandSize);
		mFormat.add(mDialMarks);
		mFormat.add(miLabBorder);
		mFormat.add(mRoundCorners);
		mFormat.add(mAllOpacity);
		pMenu.add(timeMode);
		pMenu.add(mFormat);
		pMenu.add(alm);
		pMenu.add(sysClk);
		pMenu.addSeparator();
		pMenu.add(mSize);
		pMenu.add(mOpacityLevel);
		pMenu.add(miMovable);
		pMenu.add(ontop);
		pMenu.addSeparator();
		pMenu.add(addPanel);
		pMenu.add(removePanel);
		pMenu.addSeparator();
		pMenu.add(about);
		pMenu.add(exit);
		pMenu.pack();
		reInitialize();
		tLabel.addMouseListener(this);
	}

	private void reInitialize()
	{
		setAlwaysOnTop(info.getOnTop());
		tLabel.setFont(info.getFont());
		tLabel.setCursor(info.isFixed() ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		metrics = tLabel.getFontMetrics(info.getFont());
		tLabel.setForeground(info.getForeground());
		tLabel.setBorder(info.getBorder());
		tLabel.setAnalogClockLabelBorder(info.isAnalogClockLabelBorderShowing());
		tLabel.setAnalogClockOptions(info.getAnalogClockOption());
		tLabel.setDialObjSize(info.getDialObjectsSize());
		checkUpdateRoundedCorners(ExUtils.ROUND_CORNERS.nameOfValue(info.getRoundCorners()));
		checkUpdateDialObjectsSizes(TLabel.DIAL_OBJECTS_SIZE.nameOfValue(info.getDialObjectsSize()) + CMD_HAND_STRING_COMPLEMENT);
		mHandSize.setEnabled(info.isAnalogClock());
		mDigiFormat.setEnabled(info.getDisplayMethod().endsWith("TZ") && !info.isAnalogClock());
		miSeltz.setText("Last selected (" + info.getTimeZone() + ")");
		miDeftz.setText("System (" + TimeZone.getDefault().getID() + ")");
		timeZone.setEnabled(info.getDisplayMethod().endsWith("TZ"));
		mDialMarks.setEnabled(info.isAnalogClock());
		miLabBorder.setEnabled(info.isAnalogClock());
		mAllOpacity.setEnabled(pixelTranslucency && info.getOpacity() != 1.0f);
		Rectangle screen = new Rectangle(scsize);
		Point savedLocation = info.getLocation();
		setLocation(screen.contains(savedLocation) ? savedLocation : new Point(10, 10));
		if (info.isUsingImage())
		{
			mainPane.setBackground(Color.white);
			tLabel.setTransparency(false);
			opacityMethod(info.isUsingImage());
			stopRefresh();
			boolean lastWasClockMode = tLabel.isClockMode();
			boolean newIsClockMode   = info.isAnalogClock();
			// Bugfix: set image style as tile-horizontal when changing from analog to others and stretch when vice versa.
			// Bugfix: set non-analog-clock image when changing from analog to others and analog-clock image when vice versa.
			updateImageAndStyleOnModeChange(lastWasClockMode, newIsClockMode);

			info.setImageStyle(tLabel.getImageLayout());
			tLabel.setBackground(null);
			getRootPane().putClientProperty(WINDOW_SHADOW_PROPERTY, Boolean.TRUE);
		}
		else if (info.hasGlassEffect() && !pixelTranslucency)
		{
			try
			{
				if (robot == null)
					robot = new Robot();
				tLabel.setTransparency(true);
				setOpacity(1.0f);
				getRootPane().putClientProperty(WINDOW_SHADOW_PROPERTY, Boolean.FALSE);
			}
			catch (Exception e)
			{
				System.err.println("Can't process robot task due to - ");
				e.printStackTrace();
				tLabel.setTransparency(false);
				tLabel.setBackImage(null);
				mainPane.setBackground(SystemColor.desktop);
			}
			tLabel.setText(time);
			startRefresh();
		}
		else
		{
			mainPane.setBackground(info.getBackground());
			info.setGlassEffect(false);
			opacityMethod(info.isUsingImage());
			tLabel.setText(time);
			tLabel.setBackImage(null);
			tLabel.setTransparency(false);
			getRootPane().putClientProperty(WINDOW_SHADOW_PROPERTY, Boolean.TRUE);
			stopRefresh();
		}
		timeDisplayConfig();
		setVisible(true);
		setRoundedCorners(info.getRoundCorners());
		updatePopupMenuIcons();
		updateOpacitySlider(info.getOpacity(), info.isPixelAlphaSupported(), info.isForegroundTranslucent());
		int anaClkOpts = Math.floorDiv(info.getAnalogClockOption(), 1000) * 1000;
		updateDialMarksMenu(anaClkOpts);
		validate();
		updateAllPanelIconTypes(info.getTrayIconType());
	}

	public static void updateAllPanelIconTypes(int trayIconType) {
		recentIconType = trayIconType;
		for (int cnt = 0; cnt < deskstops.size(); cnt++) {
			InitInfo tmp = deskstops.get(cnt);
			tmp.setTrayIconType(trayIconType);
			deskstops.set(cnt, tmp);
		}
		ExUtils.saveDeskStops(deskstops);
	}

	private void updateIconType() {
		if (!SystemTray.isSupported()) return;
		trayIcon = new TrayIcon(mainIcon.getImage(), TITLE_STRING);
		trayIcon.setImageAutoSize(true);
		PopupMenu sysPMenu = new PopupMenu();
		MenuItem toFront = new MenuItem("Bring to front");
		MenuItem exitItm = new MenuItem("Exit");
		toFront.addActionListener(this);
		exitItm.addActionListener(this);
		sysPMenu.add(toFront);
		sysPMenu.add(exitItm);
		trayIcon.setPopupMenu(sysPMenu);
		switch (info.getTrayIconType()) {
			case ChooserBox.SYS_TRAY_ICON:
				TrayIcon[] allIcons = thisTray.getTrayIcons();
				int x = 0;
				for (TrayIcon currTrayIcon : allIcons) {
					if (currTrayIcon.getToolTip().equals(TITLE_STRING)) x += 1;
				}
				try {
					if (x == 0) thisTray.add(trayIcon);
					setType(Type.UTILITY);
				} catch (AWTException e) {
					e.printStackTrace();
				}
				break;
			case ChooserBox.NO_APP_ICON:
				if (trayIcon != null) thisTray.remove(trayIcon);
				setType(Type.UTILITY);
				break;
			case ChooserBox.TASK_BAR_ICON:
			default:
				if (trayIcon != null) thisTray.remove(trayIcon);
				setType(Type.NORMAL);
				break;
		}
	}

	private void updateImageAndStyleOnModeChange(boolean oldModeIsClock, boolean newModeIsClock) {
		boolean isClockImage = info.getImageFile().getName().toLowerCase().contains("dial") || info.getImageFile().getName().toLowerCase().contains("clock");
		int     newStyle     = 0;
		File newBkImageFile  = info.getImageFile();
		ImageIcon newBkImage = new ImageIcon(newBkImageFile.getAbsolutePath());
		if (oldModeIsClock == newModeIsClock) {  // old and new mode is analog clock
			newStyle     = info.getImageStyle();
		} else if (oldModeIsClock && !isClockImage) {  // old is analog, new is digital, image not analog clock
			newStyle = TLabel.H_TILE;
		} else if (oldModeIsClock && isClockImage) {    // old is analog, new is digital, image is analog clock
			newBkImageFile = info.getPreviousBlockImageFile();
			newBkImage = new ImageIcon(newBkImageFile.getAbsolutePath()); // mk image to digital clock
			newStyle  = TLabel.H_TILE;
		} else if (!oldModeIsClock && !isClockImage) {  // old is digital, new is analog, image is not analog clock
			newBkImageFile = info.getNextClockImageFile();
			newBkImage = new ImageIcon(newBkImageFile.getAbsolutePath());  // make image to analog clock
			newStyle  = TLabel.STRETCH;
		} else {  // old is digital, new is analog, image is analog clock and every other condition
			newStyle  = TLabel.STRETCH;
		}
		tLabel.setBackImage(newBkImage.getImage());
		tLabel.setImageLayout(newStyle);
		info.setImageFile(newBkImageFile.getAbsolutePath());
		info.setImageStyle(newStyle);
		ExUtils.saveDeskStops(info, deskstops);
	}

	private void updateDialMarksMenu(int analogDialMarks) {
		switch (analogDialMarks) {
			case TLabel.SHOW_AM_PM:
				miAmPmMark.setSelected(true);
				break;

			case TLabel.SHOW_TIMEZONE:
				miTzMark.setSelected(true);
				break;
	
			case TLabel.SHOW_DAYMONTH:
				miDateMark.setSelected(true);
				break;
	
			case TLabel.SHOW_WEEKDAY:
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE):
				miAmPmMark.setSelected(true);
				miTzMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_WEEKDAY):
				miAmPmMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH):
				miAmPmMark.setSelected(true);
				miDateMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
				miTzMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
				miTzMark.setSelected(true);
				miDateMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				miDateMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH):
				miAmPmMark.setSelected(true);
				miTzMark.setSelected(true);
				miDateMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_WEEKDAY):
				miAmPmMark.setSelected(true);
				miTzMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				miAmPmMark.setSelected(true);
				miDateMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				miTzMark.setSelected(true);
				miDateMark.setSelected(true);
				miWkDyMark.setSelected(true);
				break;
	
			case (TLabel.SHOW_AM_PM + TLabel.SHOW_TIMEZONE + TLabel.SHOW_DAYMONTH + TLabel.SHOW_WEEKDAY):
				miAmPmMark.setSelected(true);
				miTzMark.setSelected(true);
				miDateMark.setSelected(true);
				miWkDyMark.setSelected(true);
				miSelectAll.setSelected(true);
				break;
	
			default:
				break;
		}
	}

	private void updatePopupMenuIcons()
	{
		miMovable.setIcon(info.isFixed() ? clearPng : checkPng);
		ontop.setIcon(info.getOnTop() ? checkPng : clearPng);
		miDigTime.setIcon(info.getDisplayMethod().endsWith("TZ") && !info.isAnalogClock() ? checkPng : clearPng);
		miAnaTime.setIcon(info.getDisplayMethod().endsWith("TZ") && info.isAnalogClock() ? checkPng : clearPng);
		miUptime.setIcon(info.getDisplayMethod().equals(DISPLAY_MODE_SYSTEM_UPTIME) ? checkPng : clearPng);
		miPomo.setIcon(info.getDisplayMethod().equals(DISPLAY_MODE_POMODORO_TIMER) ? checkPng : clearPng);
		mAllOpacity.setIcon(info.isForegroundTranslucent() ? checkPng : clearPng);
		miLabBorder.setIcon(info.isAnalogClockLabelBorderShowing() ? checkPng : clearPng);

		TimeZone curTz = TimeZone.getTimeZone(info.getTimeZone());
		for (JRadioButtonMenuItem miZButtonMenuItem : impZon) {
			TimeZone tmpZone = TimeZone.getTimeZone(miZButtonMenuItem.getText().split("[\\(|\\)]")[1]);
			miZButtonMenuItem.setSelected(tmpZone.equals(curTz));
		}

		String curFmt = info.getZonedTimeFormat();
		for (JRadioButtonMenuItem miFButtonMenuItem : miDigiFormats) {
			String tmpFmt = miFButtonMenuItem.getText();
			miFButtonMenuItem.setSelected(tmpFmt.equals(curFmt));
		}
	}

	private void updateOpacitySlider(float opacityLevel, boolean pixelTransSupport, boolean foregroundTranslucent)
	{
		miOpacitySlider.setMinimum(pixelTransSupport && !foregroundTranslucent ? 0 : 4);
		miOpacitySlider.setValue(Math.round(opacityLevel * 20));
	}

	public static final int[] getFontSizes()
	{
		return fontSizes;
	}

	private synchronized void timeDisplayConfig()
	{
		String dispString = info.getDisplayMethod();
		if (lastPomTask == null) lastPomTask = info.getPomodoroTask();
		if (lastPomFormat == null) lastPomFormat = info.getPomodoroFormat();
		switch (dispString) {
			case DISPLAY_MODE_POMODORO_TIMER:
				// Only create new pomodoro object if earlier was not present, else refer existing one.
				tLabel.setClockMode(false);
				if (pom == null || !lastPomTask.equals(info.getPomodoroTask()) || !lastPomFormat.equals(info.getPomodoroFormat())) {
					pom  = new Pomodoro(info.getPomodoroTask());
					// notify waiting clockThread that pom is created
					notifyAll();
				}
				time = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(info.isPomodoroCountdown()), info.getPomodoroFormat(), pom.getRunningLabel(), info.isPomodoroLeadingLabel());
				resizePomodoroLabel();
				tLabel.setToolTipText(info.hasTooltip() ? tipPom.replace("pomodoro task", info.getPomodoroTask()) : null);
				break;
			case DISPLAY_MODE_SYSTEM_UPTIME:
				tLabel.setClockMode(false);
				time = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), info.getUpTimeFormat());
				resizingMethod(time);
				tLabel.setToolTipText(info.hasTooltip() ? tipUpt : null);
				break;
			case DISPLAY_MODE_SELECTED_TIMEZONE:
			case DISPLAY_MODE_CURRENT_TIMEZONE:
			default:
				sd   = new SimpleDateFormat(info.getZonedTimeFormat());
				sd.setTimeZone(dispString.equals(DISPLAY_MODE_SELECTED_TIMEZONE) ? TimeZone.getTimeZone(info.getTimeZone()) : TimeZone.getDefault());
				date = new Date();
				time = sd.format(date);
				resizeTimeLabel(time);
				tLabel.setToolTipText(info.hasTooltip() ? tipCur.replace("Time of your location", info.getTimeZone()) : null);
				break;
		}
	}

	private void resizePomodoroLabel()
	{
		String lstr1 = info.getPomodoroFormat() + " [" + pom.getWorkLabel() + "]";
		String lstr2 = info.getPomodoroFormat() + " [" + pom.getBreakLabel() + "]";
		String lstr3 = pom.checkCanRest() ? info.getPomodoroFormat() + " [" + pom.getRestLabel() + "]" : "";
		int maxLength = Math.max(lstr1.length(), Math.max(lstr2.length(), lstr3.length()));
		if (maxLength == lstr1.length()) {
			resizingMethod(lstr1);
		} else if (maxLength == lstr2.length()) {
			resizingMethod(lstr2);
		} else {
			resizingMethod(lstr3);
		}
	}

	private void resizeTimeLabel(String text)
	{
		if (info.isAnalogClock())
		{
			tLabel.setText("");
			tLabel.setClockMode(true);
			resizingMethod("");
		}
		else
		{
			tLabel.setText(text);
			tLabel.setClockMode(false);
			resizingMethod(text);
		}
	}

	private void resizingMethod(String lstr)
	{
		int dialToFontRatio = 15;
		int strLength = 0;
		// if (lstr == null || lstr.isEmpty())
		// 	lstr = time;
		if (info.isAnalogClock() && info.getDisplayMethod().endsWith("TZ")) strLength = dialToFontRatio;
		else strLength = lstr.length();
		char[] mChar = new char[strLength];
		for (int k = 0; k < strLength; k++)
			mChar[k] ='8'; // Coz its the medium char generally :)
		int k = metrics.stringWidth(new String(mChar));
		int i = metrics.stringWidth(lstr) + 10;
		int j = metrics.getHeight() + 5;
		if (info.isAnalogClock() && info.getDisplayMethod().endsWith("TZ")) j = i > k ? i : k;
		tLabel.setSize(i > k ? i : k, j);  // normal string or string of '8'
		setSize(i > k ? i : k, j);  // whichever gr8er;
	}

	private void opacityMethod(boolean containsImage)
	{
		if (pixelTranslucency && !info.isForegroundTranslucent())
		{
			setOpacity(1.0f);
			setBackground(new Color(0, 0, 0, 0.00f));
			if (containsImage) {
				mainPane.setAlpha(0.0f); // required in glass effect / robot screenshot
				tLabel.setImageAlpha(info.getOpacity());
			} else {
				mainPane.setAlpha(info.getOpacity());
			}
		}
		else
		{
			if (wholeTranslucency) {
				// if foreground is translucent or pixel translucency is unsupported,
				// then opacity shouldn't be below 0.2, else, it'll be almost invisible.
				if (info.getOpacity() < 0.2f && !containsImage) {
					setOpacity(0.2f);
					info.setOpacity(0.2f);
				} else {
					setOpacity(info.getOpacity());
				}
			} else {
				setOpacity(1.0f);
				info.setOpacity(1.0f);
			}
			setBackground(info.getBackground());
			tLabel.setImageAlpha(1.0f);
			mainPane.setAlpha(1.0f);
		}
		repaint();
	}

	private void setRoundedCorners(int roundness)
	{
		Border thisBorder = info.getBorder();
		if (roundness != 0 && (!info.hasGlassEffect() || thisBorder instanceof EtchedBorder || thisBorder instanceof LineBorder || thisBorder instanceof BevelBorder))
		{
			int arc = Math.floorDiv(Math.min(getWidth(), getHeight()), 5);
			setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 0.25f * roundness * arc, 0.25f * roundness * arc));
		}
		else
		{
			setShape(null);
		}
	}

	private void trackChangesAndSave(InfoTracker trackChanges)
	{
		info   = trackChanges.getSelectedInformation();
		alarms = trackChanges.getSelectedAlarms();
		ExUtils.saveAlarms(alarms);
		ExUtils.saveDeskStops(info, deskstops);
	}

	@Override
	public void actionPerformed(ActionEvent actionevent)
	{
		lastPomTask   = info.getPomodoroTask();   //Since pomodoro change can be triggered with time tab,
		lastPomFormat = info.getPomodoroFormat(); //which in turn can be navigated from any of the tabs.
		String comm = actionevent.getActionCommand();
		Object obj = actionevent.getSource();
		InfoTracker track;
		switch (comm) {
			case "Borders & Clock UI...":
				track = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.BORDER_TAB, info, alarms);
				trackChangesAndSave(track);
				reInitialize();				
				break;
			case "Font & Foreground...":
				track = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.FONT_TAB, info, alarms);
				trackChangesAndSave(track);
				reInitialize();
				break;
			case "Background & Dials...":
				track = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.BACKGROUND_TAB, info, alarms);
				trackChangesAndSave(track);
				reInitialize();
				break;
			case DISPLAY_MODE_SELECTED_TIMEZONE:
				info.setDisplayMethod(DISPLAY_MODE_SELECTED_TIMEZONE);
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case DISPLAY_MODE_CURRENT_TIMEZONE:
				info.setDisplayMethod(DISPLAY_MODE_CURRENT_TIMEZONE);
				info.setTimeZone(TimeZone.getDefault().getID());
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case "Show Digital clock":
			case "Show Analog clock":
				info.setDisplayMethod(getDisplayMethodBasedOnTZ(info.getTimeZone()));
				info.setAnalogClock(obj.equals(miAnaTime));
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case DISPLAY_MODE_SYSTEM_UPTIME:
				info.setDisplayMethod(DISPLAY_MODE_SYSTEM_UPTIME);
				info.setAnalogClock(false);
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case DISPLAY_MODE_POMODORO_TIMER:
				info.setDisplayMethod(DISPLAY_MODE_POMODORO_TIMER);
				info.setAnalogClock(false);
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case CMD_TIME_SETTINGS:
				track = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.TIMES_TAB, info, alarms);
				trackChangesAndSave(track);
				reInitialize();
				break;
			case "Alarm & sounds":
				track = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.ALARMS_TAB, info, alarms);
				trackChangesAndSave(track);
				reInitialize();
				break;
			case "System Clock":
				runSystemClock();
				break;
			case "Dial marker labels border":
				info.setAnalogClockLabelBorder(!info.isAnalogClockLabelBorderShowing());
				tLabel.setAnalogClockLabelBorder(info.isAnalogClockLabelBorderShowing());
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case "Blend foreground opacity":
				info.setForegroundTranslucent(!info.isForegroundTranslucent());
				ExUtils.saveDeskStops(info, deskstops);
				reInitialize();
				break;
			case "Movable":
				info.setFixed(!info.isFixed());
				miMovable.setIcon(info.isFixed() ? clearPng : checkPng);
				tLabel.setCursor(info.isFixed() ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				ExUtils.saveDeskStops(info, deskstops);
				break;
			case "Always on top":
				info.setOnTop(!info.getOnTop());
				ontop.setIcon(info.getOnTop() ? checkPng : clearPng);
				ExUtils.saveDeskStops(info, deskstops);
				setAlwaysOnTop(info.getOnTop());
				break;
			case CMD_ABOUT:
				Object[] options = {"Show LICENSE.txt", "OK"};
				int choice = JOptionPane.showOptionDialog(new Frame(), ABOUT_STRING, CMD_ABOUT, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, mainIcon, options, JOptionPane.NO_OPTION);
				if (choice == 0)
					openLicense();
				break;
			case "Exit":
				clockThread.terminate();
				try
				{
					clockThread.join();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
				System.exit(0);
				break;
			case "New":
				DeskStop.createInstance(info, false);
				break;
			case "Duplicate":
				DeskStop.createInstance(info, true);
				break;
			case "Remove this panel":
				DeskStop.removeInstance(this, info);
				break;
			case CMD_ANALOG_DIAL_LABEL:
				setAnalogClockDialMarks(obj.equals(miSelectAll));
				break;
			case "Bring to front":
				toFront();
				break;
			default: {
					if (comm.startsWith("TZ-")) {
						info.setDisplayMethod(getDisplayMethodBasedOnTZ(comm.split("-")[1]));
						info.setTimeZone(comm.split("-")[1]);
						ExUtils.saveDeskStops(info, deskstops);
					} else if (comm.startsWith(CMD_TIMEFORMAT)) {
						info.setZonedTimeFormat(comm.split(CMD_TIMEFORMAT)[1]);
						ExUtils.saveDeskStops(info, deskstops);
					} else {
						checkUpdateRoundedCorners(comm);
						checkUpdateDialObjectsSizes(comm);
					}
					reInitialize();
				}
				break;
		}
		info.setPixelAlphaSupport(pixelTranslucency);
		info.setWindowAlphaSupport(wholeTranslucency);
		info.setScreenshotSupport(robotSupport);
		refreshNow = true;
		startRefresh();
		updateCursor();
	}

	private void openLicense() {
		File jarDir;
		try {
			jarDir = ExUtils.getJarDir();
		} catch (Exception e) {
			jarDir = new File(System.getProperty("user.home"));
			JOptionPane.showMessageDialog(this, "Unable to locate jar file. License file not found. Exiting!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		File licFile     = new File(jarDir, "LICENSE.md");       // in build
		File licFileTxt  = new File(jarDir, "LICENSE.txt");      // in deploy
		File internalLic = new File(jarDir, "app/LICENSE.txt");  // in deploy
		try {
			File tmpfile = File.createTempFile("License", ".txt");
			FileInputStream fis;
			FileOutputStream fos = new FileOutputStream(tmpfile);
			if (!licFile.exists() && internalLic.exists()) {
				fis = new FileInputStream(internalLic);
			} else if (!licFile.exists() && licFileTxt.exists()) {
				fis = new FileInputStream(licFileTxt);
			} else {
				fis = new FileInputStream(licFile);
			}
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
			fis.close();
			fos.close();
			Desktop.getDesktop().open(tmpfile);
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(this, "License file not found. Exiting!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void updateCursor() {
		if (pMenu.isShowing()) {
			tLabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			tLabel.setCursor(info.isFixed() ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	}

	private void setAnalogClockDialMarks(boolean selectAllTrigger) {
		if (selectAllTrigger) {
			miAmPmMark.setSelected(miSelectAll.isSelected());
			miTzMark.setSelected(miSelectAll.isSelected());
			miWkDyMark.setSelected(miSelectAll.isSelected());
			miDateMark.setSelected(miSelectAll.isSelected());
		} else {
			miSelectAll.setSelected(miAmPmMark.isSelected() && miTzMark.isSelected() && miWkDyMark.isSelected() && miDateMark.isSelected());
		}
		int anaClkOpts = 0;
		if (miAmPmMark.isSelected()) anaClkOpts += TLabel.SHOW_AM_PM;
		if (miTzMark.isSelected())   anaClkOpts += TLabel.SHOW_TIMEZONE;
		if (miWkDyMark.isSelected()) anaClkOpts += TLabel.SHOW_WEEKDAY;
		if (miDateMark.isSelected()) anaClkOpts += TLabel.SHOW_DAYMONTH;
		info.setAnalogClockOption(anaClkOpts);
		ExUtils.saveDeskStops(info, deskstops);
		reInitialize();
	}

	protected void runSystemClock()
	{
		switch (ExUtils.getOS()) {
			case WINDOWS:
				String[] sysClkCmd = {"explorer.exe", "shell:Appsfolder\\Microsoft.WindowsAlarms_8wekyb3d8bbwe!App"};
				ExUtils.runProgram(sysClkCmd, this);
				break;
			case LINUX:
				sysClkCmd = new String[]{"xclock"};
				ExUtils.runProgram(sysClkCmd, this);
				break;
			case MAC:
				sysClkCmd = new String[]{"open", "/System/Applications/Clock.app"};
				ExUtils.runProgram(sysClkCmd, this);
				break;
			default:
				break;
		}
	}

	private String getDisplayMethodBasedOnTZ(String tzString)
	{
		TimeZone inputTZ = TimeZone.getTimeZone(tzString);
		if (inputTZ.hasSameRules(TimeZone.getDefault())) return DISPLAY_MODE_CURRENT_TIMEZONE;
		else return DISPLAY_MODE_SELECTED_TIMEZONE;
	}

	private void checkUpdateRoundedCorners(String cornerType)
	{
		int mCnt = 0;
		for (ExUtils.ROUND_CORNERS corner : ExUtils.ROUND_CORNERS.values()) {
			if (corner.name().equals(cornerType)) {
				tMenuItem[mCnt].setSelected(true);
				setRoundedCorners(corner.getRoundType());
				info.setRoundCorners(corner.getRoundType());
				ExUtils.saveDeskStops(info, deskstops);
			} else {
				tMenuItem[mCnt].setSelected(false);
			}
			mCnt++;
		}
	}

	private void checkUpdateDialObjectsSizes(String sizeCmdString)
	{
		int mCnt = 0;
		for (TLabel.DIAL_OBJECTS_SIZE handSz : TLabel.DIAL_OBJECTS_SIZE.values()) {
			if (sizeCmdString.startsWith(handSz.name()) && sizeCmdString.endsWith(CMD_HAND_STRING_COMPLEMENT)) {
				miDialObjSize[mCnt].setSelected(true);
				tLabel.setDialObjSize(handSz.getSizeValue());
				info.setDialObjectsSize(handSz.getSizeValue());
				ExUtils.saveDeskStops(info, deskstops);
			} else {
				miDialObjSize[mCnt].setSelected(false);
			}
			mCnt++;
		}
	}

	@Override
	public void mouseClicked(MouseEvent mouseevent)
	{
		if (SwingUtilities.isRightMouseButton(mouseevent) || mouseevent.getClickCount() == 2)
		{ //Whenever this menu appears a mouseExited event occurs calling method
			refreshNow = false; // refreshThreadTransparency which forces popup to disappear. So refreshNow=false.
			int x = info.getFont().getSize();
			for (int i = 0; i < fontSizes.length; i++)
			{
				if (fontSizes[i] == x) sizer.setValue(i);
			}
			ExUtils.showPopup(pMenu, this, (Component)mouseevent.getSource(), mouseevent.getPoint(), scsize);
		}
		updateCursor();
	}

	@Override
	public void mouseEntered(MouseEvent mouseevent)
	{
		if (!pMenu.isShowing()) {
			refreshNow = true;
		}
		startRefresh();
		updateCursor();
	}

	@Override
	public void mouseExited(MouseEvent mouseevent)
	{
		// Not required to implement.
	}

	@Override
	public void mouseMoved(MouseEvent mouseevent)
	{
		// Not required to implement.
	}

	@Override
	public void mousePressed(MouseEvent mouseevent)
	{
		if (!SwingUtilities.isRightMouseButton(mouseevent))
		{
			pi = MouseInfo.getPointerInfo();
			Point currPoint = pi.getLocation();
			SwingUtilities.convertPointFromScreen(currPoint, this);
			cursorX = ((int)currPoint.getX());
			cursorY = ((int)currPoint.getY());
			cursorW = getWidth() - ((int)currPoint.getX());
			cursorH = getHeight() - ((int)currPoint.getY());
			// As sometimes unremoved listenenrs cause buggy movement.
			for (MouseMotionListener mListener : tLabel.getMouseMotionListeners())
			{
				if (!(mListener instanceof ToolTipManager)) tLabel.removeMouseMotionListener(mListener);
			}
			if (!info.isFixed()) tLabel.addMouseMotionListener(this);
			if (cursorX <= scsize.width && cursorY <= scsize.height)
			{
				locX = getLocation().x;
				locY = getLocation().y;
				locW = scsize.width - (locX + getWidth());
				locH = scsize.height - (locY + getHeight());
				alreadyOutOfScreen = locX < 0 || locY < 0 || locW < 0 || locH < 0;
			}	
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseevent)
	{
		if (!SwingUtilities.isRightMouseButton(mouseevent))
		{
			for (MouseMotionListener mListener : tLabel.getMouseMotionListeners()) {
				if (!(mListener instanceof ToolTipManager) && !info.isFixed())
					tLabel.removeMouseMotionListener(mListener);
			}
			Point newLocation = getLocation();
			cursorX = 0;
			cursorY = 0;
			cursorW = 0;
			cursorH = 0;
			refreshNow = true;
			if (!info.getLocation().equals(newLocation))
			{
				info.setLocation(newLocation);
				ExUtils.saveDeskStops(info, deskstops);
				refreshThread.refreshTransparency();
			}
			startRefresh();
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseevent)
	{
		pi = MouseInfo.getPointerInfo();
		pointerLoc = pi.getLocation();
		if (Math.abs(pointerLoc.getX()) <= scsize.width && Math.abs(pointerLoc.getY()) <= scsize.height)
		{
			locX = (int)pointerLoc.getX() - cursorX;
			locY = (int)pointerLoc.getY() - cursorY;
			locW = scsize.width - ((int)pointerLoc.getX() + cursorW);
			locH = scsize.height - ((int)pointerLoc.getY() + cursorH);
			if (allowMoveOutOfScreen || locX >= 0 && locY >= 0 && locW >= 0 && locH >= 0)
			{
				setLocation(locX, locY);
				alreadyOutOfScreen = false;
			}
			else if (alreadyOutOfScreen) setLocation(locX, locY);
		}
		refreshNow = false;
		stopRefresh();
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		setRoundedCorners(info.getRoundCorners());
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		setRoundedCorners(info.getRoundCorners());
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		// Not required to implement.
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		// Not required to implement.
	}

	@Override
	public void stateChanged(ChangeEvent ce)
	{
		Object srcObject = ce.getSource();
		if (srcObject.equals(sizer)) {
			Font derived = info.getFont().deriveFont((float)fontSizes[sizer.getValue()]);
			if (derived.getSize() != info.getFont().getSize()) {
				tLabel.setFont(derived);
				metrics = tLabel.getFontMetrics(derived);
				resizingMethod(info.isAnalogClock() ? "" : time);
				info.setFont(derived);
				ExUtils.saveDeskStops(info, deskstops);
			}
		} else if (srcObject.equals(miOpacitySlider)) {
			float derived = (float)miOpacitySlider.getValue() / 20;
			if (derived != info.getOpacity()) {
				info.setOpacity(derived);
				mAllOpacity.setEnabled(pixelTranslucency && derived != 1.0f);
				opacityMethod(info.isUsingImage());
				ExUtils.saveDeskStops(info, deskstops);
			}
		}
	}

	class ClockThread extends Thread
	{
		private volatile boolean timerun = true;
		private int      soundRunSec = 60;
		private Player   runPlayer;
		private Calendar gcal;
		private String   curLabel;

		ClockThread()
		{
			super("TimeView");
		}

		@Override
		public void run()
		{
			Date startTime   = ExUtils.getSystemStartTime();
			gcal        = Calendar.getInstance();
			curLabel    = "";
			runPlayer   = null;
			if(pom != null) 
				curLabel = pom.getRunningLabel();
			for (Thread thread = Thread.currentThread(); clockThread == thread && timerun;)
			{
				String method = info.getDisplayMethod();
				date = new Date();
				try
				{
					switch (method) {
						case DISPLAY_MODE_SYSTEM_UPTIME:
							Duration uptimeNow = Duration.ofNanos(System.nanoTime());
							time = ExUtils.formatUptime(uptimeNow, info.getUpTimeFormat());
							tLabel.setText(time);
							gcal.setTime(startTime);
							gcal.add(Calendar.SECOND, (int)uptimeNow.getSeconds());
							date = gcal.getTime(); // this date is used by checkTimeAndRunAlarm(date) when actual date varies due to uptime selected.
							checkUptimeAndRunHourSound(uptimeNow);
							break;
						case DISPLAY_MODE_POMODORO_TIMER:
							synchronized(pom) {
								while (pom == null) {
									wait(); // wait for first time pom to be created in timeDisplayConfig
								}
							}
							time = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(info.isPomodoroCountdown()), info.getPomodoroFormat(), pom.getRunningLabel(), info.isPomodoroLeadingLabel());
							tLabel.setText(time);
							checkPomodoroAndRunSound(pom);
							break;
						case DISPLAY_MODE_SELECTED_TIMEZONE:
						case DISPLAY_MODE_CURRENT_TIMEZONE:
						default:
							setClockTime(date);
							checkTimeAndRunHourSound(date);
							break;
					}
					checkTimeAndRunAlarm(date);
					Thread.sleep(1000L);
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
					Thread.currentThread().interrupt();
				}
				catch (Exception otherException)
				{
					System.out.println("An error occurred while displaying time.");
					otherException.printStackTrace();
					timerun = false;
				}
			}
		}

		public void terminate()
		{
			timerun = false;
		}

		private void setClockTime(Date date)
		{
			String[] clockTime;
			time = sd.format(date);
			if (info.isAnalogClock())
			{
				clk.setTimeZone(sd.getTimeZone());
				clockTime = clk.format(date).split("\\|");
				tLabel.setText("");
				// bugfix for os where zzz gives timezone as GMT-8:00 for PST. Taking the timezone ID instead and splitting if contains a number. Ex. PST8PDT
				if (clockTime[0].length() >= 5 && clk.getTimeZone().getID().split("\\d").length == 2) {
					if (clk.getTimeZone().inDaylightTime(date)) {
						clockTime[0] = clk.getTimeZone().getID().split("\\d")[1];
					} else {
						clockTime[0] = clk.getTimeZone().getID().split("\\d")[0];
					}
				}
				//          clockTime hour, clockTime min, clockTime sec, clockTime ampm, clockTime daymonth,                     clockTime weekday, clockTime timeZone
				tLabel.setTime(clockTime[1], clockTime[2], clockTime[3], clockTime[4], clockTime[6].toUpperCase() + clockTime[5], clockTime[7].toUpperCase(), clockTime[0]);
			}
			else
				tLabel.setText(time);
		}

		private void checkUptimeAndRunHourSound(Duration uptimeNow) throws JavaLayerException, FileNotFoundException
		{
			int curMin = Math.toIntExact(uptimeNow.getSeconds() % 3600 / 60);
			int curSec = Math.toIntExact(uptimeNow.getSeconds() % 3600 % 60);
			if (curMin == 0 && curSec == 0)
			{
				if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
				runPlayer = SoundPlayer.playAudio(info.getUptimeHourSound(), soundRunSec);
			}
		}

		private void checkTimeAndRunHourSound(Date date) throws JavaLayerException, FileNotFoundException
		{
			gcal.setTime(date);
			int curMin = gcal.get(Calendar.MINUTE);
			int curSec = gcal.get(Calendar.SECOND);

			if (curMin == 0 && curSec == 0)
			{
				if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
				runPlayer = SoundPlayer.playAudio(info.getHourSound(), soundRunSec);
			}
		}

		private void checkPomodoroAndRunSound(Pomodoro thisPomodoro) throws JavaLayerException, FileNotFoundException
		{
			if (!curLabel.equals(thisPomodoro.getRunningLabel())) // compare with stored label as below
			{
				if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
				if (thisPomodoro.getRunningLabel().equals(thisPomodoro.getWorkLabel()))
				{
					runPlayer = SoundPlayer.playAudio(info.getPomodoroWorkSound(), soundRunSec);
				}
				else if (thisPomodoro.getRunningLabel().equals(thisPomodoro.getBreakLabel()))
				{
					runPlayer = SoundPlayer.playAudio(info.getPomodoroBreakSound(), soundRunSec);
				}
				else if (thisPomodoro.getRunningLabel().equals(thisPomodoro.getRestLabel()))
				{
					runPlayer = SoundPlayer.playAudio(info.getPomodoroRestSound(), soundRunSec);
				}
			}
			curLabel = thisPomodoro.getRunningLabel();
		}

		private void checkTimeAndRunAlarm(Date current)
		{
			for (int indx = 0; indx < alarms.size(); indx++)
			{
				TimeBean tmpb = alarms.elementAt(indx);
				if (ExUtils.dateCompareUptoSecond(current, tmpb.getNextAlarmTriggerTime()))
				{
					Thread newProc = new Thread("DeskTime-Alarm-" + tmpb.getName()) {
						@Override
						public void run()
						{
							ExUtils.runAlarm(tmpb, contentPane, 50);
						}
					};
					newProc.start();
				}
			}
		}
	}

	public void start()
	{
		if (clockThread == null)
		{
			clockThread = new ClockThread();
			clockThread.start();
		}
	}

	class Refresher extends Thread
	{
		private AtomicIntegerArray top2pixelRows, left2pixelColumns, bottom2pixelRows, right2pixelColumns;
		private int compInt;
		private Rectangle newBounds;
		private volatile boolean running = true;
		
		Refresher()
		{
			super("BG-Refresher");
			top2pixelRows      = new AtomicIntegerArray(10);
			left2pixelColumns  = new AtomicIntegerArray(10);
			bottom2pixelRows   = new AtomicIntegerArray(10);
			right2pixelColumns = new AtomicIntegerArray(10);
			compInt            = 0;
			newBounds          = getBounds();
		}

		public synchronized void refreshTransparency()
		{
			if (info.hasGlassEffect() && refreshNow)
			{
				DisplayMode thisDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
				int refreshRate        = thisDevice.getRefreshRate() <= 0 ? 60 : thisDevice.getRefreshRate(); // In Mac refresh rate comes as 0.
				Dimension thisScreen   = Toolkit.getDefaultToolkit().getScreenSize();
				float scaleRatio = Math.min(thisDevice.getWidth()/(float)thisScreen.getWidth(), thisDevice.getHeight()/(float)thisScreen.getHeight());
				newBounds = new Rectangle(Math.round(scaleRatio*getX()), Math.round(scaleRatio*getY()), Math.round(scaleRatio*getWidth()), Math.round(scaleRatio*getHeight()));
				refreshNow = false;
				setVisible(false);
				try
				{
					long waitBeforeRefresh = Math.round(2*1000.0f/refreshRate);
					Thread.sleep(waitBeforeRefresh);
					BufferedImage capture = robot.createScreenCapture(newBounds);
					BufferedImage scaled  = new BufferedImage(getBounds().width, getBounds().height, BufferedImage.TYPE_INT_ARGB);
					AffineTransform trans = new AffineTransform();
					trans.scale(1/scaleRatio, 1/scaleRatio);
					AffineTransformOp scaleOp = new AffineTransformOp(trans, AffineTransformOp.TYPE_BILINEAR);
					scaled = scaleOp.filter(capture, scaled);
					tLabel.setBackImage(scaled);
				}
				catch (InterruptedException ie)
				{
					System.out.println("Can't process robot task due to - ");
					ie.printStackTrace();
					Thread.currentThread().interrupt();
				}
				setVisible(true);
				refreshNow = true;
			}
		}

		@Override
		public void run()
		{
			while(info.hasGlassEffect() && refreshThread!=null)
			{
				if (backgroundEqualsOld() || !running)
				{
					Thread.yield();
				}
				else
				{
					try
					{
						if (info.isSlowTransUpdating()) Thread.sleep(1000L);
					}
					catch (InterruptedException ie)
					{
						ie.printStackTrace();
						Thread.currentThread().interrupt();
					}
					refreshTransparency();
				}
			}
		}
		
		public void pause()
		{
			running = false;
		}

		public void play()
		{
			running = true;
		}

		public synchronized boolean backgroundEqualsOld()
		{
			Rectangle boundWith2pxFence = new Rectangle();
			boundWith2pxFence.setRect(newBounds.getX()-2,newBounds.getY()-2,newBounds.getWidth()+4,newBounds.getHeight()+4);
			BufferedImage currImage     = robot.createScreenCapture(boundWith2pxFence);

			compInt             = 0;
			top2pixelRows       = copyAndComparePixelToArray(currImage,currImage.getMinX(), currImage.getMinY(), currImage.getWidth(), 2.0, top2pixelRows);
			left2pixelColumns   = copyAndComparePixelToArray(currImage,currImage.getMinX(), currImage.getMinY(), 2.0, currImage.getHeight(), left2pixelColumns);
			bottom2pixelRows    = copyAndComparePixelToArray(currImage,currImage.getMinX(), currImage.getHeight() - 2.00d, currImage.getWidth(), 2.0, bottom2pixelRows);
			right2pixelColumns  = copyAndComparePixelToArray(currImage,currImage.getWidth() - 2.00d, currImage.getMinY(), 2.0, currImage.getHeight(), right2pixelColumns);

			return compInt == 0;
		}
		
		private AtomicIntegerArray copyAndComparePixelToArray(BufferedImage img2compare, double rectx, double recty, double rectw, double recth, AtomicIntegerArray tmpArray)
		{
			int x  = (int)Math.round(rectx);
			int y  = (int)Math.round(recty);
			int w  = (int)Math.round(rectw);
			int h  = (int)Math.round(recth);
			int cp = 0;
			boolean equalLength = tmpArray.length() == (w*h);
			int loopH = equalLength ? y + h : h;
			int loopW = equalLength ? x + w : w;
			if (!equalLength) tmpArray = new AtomicIntegerArray(w*h);

			for (int j = y; j < loopH; j++)
			{
				for (int i = x; i < loopW; i++)
				{
					int ov = tmpArray.get(cp);
					int nv = img2compare.getRGB(i, j);
					tmpArray.set(cp, nv);
					if (equalLength && ov != nv) compInt += 1;
					cp += 1;
				}
			}

			if (!equalLength) compInt = 1; // 0=all matching, >0=mismatch;

			return tmpArray;
		}
	}

	public void startRefresh()
	{
		if (refreshThread == null || !refreshThread.isAlive())
		{
			refreshThread = new Refresher();
			refreshThread.start();
		}

		if (info.hasGlassEffect() && refreshNow) 
		{
			refreshThread.play();
		}
	}
	
	public void stopRefresh()
	{
		if (refreshThread != null && refreshThread.isAlive()) {
			refreshThread.pause();
		}
	}

	public static void createInstance(InitInfo reference, boolean duplicate)
	{
		int[] ids = new int[deskstops.size()];
		int newid = 0;
		for (int cnt = 0; cnt < deskstops.size(); cnt++) {
			ids[cnt] = deskstops.get(cnt).getID();
		}
		Arrays.sort(ids);
		for (int cnt = 0; cnt < deskstops.size(); cnt++) {
			if (cnt != ids[cnt]) {
				newid = cnt;
				break;
			}
		}
		InitInfo initInfo = duplicate ? (InitInfo)reference.clone() : new InitInfo();
		initInfo.setID(newid > 0 ? newid : deskstops.size());
		Point refLocation = reference.getLocation();
		initInfo.setLocation(new Point(refLocation.x + 10, refLocation.y + 10));
		initInfo.setTrayIconType(recentIconType);
		if (!duplicate) {
			initInfo.setTimeZone(TimeZone.getDefault().getID());
		}
		Vector<TimeBean> allTimeBeans = ExUtils.loadAlarms();
		SwingUtilities.invokeLater(() -> {
			DeskStop deskstop = new DeskStop(initInfo, allTimeBeans);
			deskstop.start();
		});
		deskstops.add(initInfo);
		ExUtils.saveDeskStops(deskstops);
	}

	public static void removeInstance(DeskStop deskStop, InitInfo initInfo)
	{
		deskStop.clockThread.terminate();
		for (int cnt = 0; cnt < deskstops.size(); cnt++)
		{
			if (deskstops.get(cnt).getID() == initInfo.getID() && initInfo.getID() != 0)
			{
				deskstops.remove(cnt);
				ExUtils.saveDeskStops(deskstops);
			}
		}
		try
		{
			deskStop.clockThread.join();
		} 
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
			Thread.currentThread().interrupt();
		}
		deskStop.dispose();
	}

	public static void main(String[] args)
	{
		if (ExUtils.lockInstance()) {
			java.util.List<InitInfo> readList = ExUtils.loadDeskStops();
			if (readList instanceof ArrayList) {
				deskstops = (ArrayList<InitInfo>)readList;
			} else {
				throw new ArrayStoreException("DeskTime.xml is not in ArrayList format.");
			}
			
			Vector<TimeBean> allTimeBeans = ExUtils.loadAlarms();
			int count = 0;
			do {
				InitInfo initInfo;
				if (deskstops.isEmpty()) {
					initInfo = new InitInfo();
					initInfo.setTimeZone(TimeZone.getDefault().getID());
					deskstops.add(initInfo);
					ExUtils.saveDeskStops(initInfo, deskstops);
				} else {
					initInfo = deskstops.get(count);
				}
				SwingUtilities.invokeLater(() -> {
					DeskStop deskstop = new DeskStop(initInfo, allTimeBeans);
					deskstop.start();
				});
				count++;
			} while (count < deskstops.size());
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "<html>DeskStop is already running. To add additional clock panel, please<p>Right click on it and click \"Add clock panel\" sub-menu options.</html>", TITLE_STRING, JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}
