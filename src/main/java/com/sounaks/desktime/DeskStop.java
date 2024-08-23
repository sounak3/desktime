package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import javazoom.jl.player.Player;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class DeskStop extends JFrame implements MouseInputListener, ActionListener, ComponentListener, ChangeListener
{
	protected transient ClockThread clockThread; 
	protected transient Refresher refreshThread;
	private String time, lastPomFormat, lastPomTask;
	private Date date;
	private int locX, locY, locW, locH, cursorX, cursorY, cursorW, cursorH;
	private TLabel tLabel;
	private TwilightPanel mainPane;
	private SimpleDateFormat sd, clk;
	private FontMetrics metrics;
	private InitInfo info;
	private Vector <TimeBean>alarms;
	private JPopupMenu pMenu;
	private JMenu addPanel, mFormat, timeMode, timeZone, mSize, mOpacityLevel, mRoundCorners;
	private JSlider sizer, opacityLevel;
	private JMenuItem miAnaTime, miDigTime, miUptime, miPomo, miSeltz, miDeftz, timSet, zonSet, mAllOpacity;
	private JMenuItem fore, back, alm, bdr, exit, about, newItem, dupItem, removePanel, miMovable, ontop, sysClk;
	private JMenuItem[] impZon;
	private JMenuItem[] tMenuItem;
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
	private static final int[] fontSizes                       = {6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72, 80};
	private static final GraphicsDevice gd                     = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public  static final String DISPLAY_MODE_CURRENT_TIMEZONE  = "CURTZ";
	public  static final String DISPLAY_MODE_SELECTED_TIMEZONE = "GMTTZ";
	public  static final String DISPLAY_MODE_SYSTEM_UPTIME     = "UPTIME";
	public  static final String DISPLAY_MODE_POMODORO_TIMER    = "POMODORO";
	public  static final String WINDOW_SHADOW_PROPERTY         = "Window.shadow";
	public  static final String PREFERENCES_TITLE              = "Preferences...";
	private static final String ABOUT_STRING                   = "<html>Created and Developed by : Sounak Choudhury<p>E-mail Address : <a href='mailto:sounak_s@rediffmail.com'>sounak_s@rediffmail.com</a><p>The software, information and documentation<p>is provided \"AS IS\" without warranty of any<p>kind, either express or implied. The Readme.txt<p>file containing EULA must be read before use.<p>Suggestions and credits are Welcomed.</html>";
	private static ArrayList<InitInfo> deskstops;
	private final ImageIcon plusPng  = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/plus-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon minusPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/minus-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon checkPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/checked-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon clearPng = new ImageIcon((new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/unchecked-icon.png"))).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
	private final ImageIcon aboutGif = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/duke.gif"));

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
		setTitle("DeskStop");
		setType(Type.UTILITY);
		pack();
		setSize(300, 50);
		setLocation((scsize.width - 200) / 2, (scsize.height - 200) / 2);
		if(info.getID() == 0) setVisible(true);
		try
		{
			if(info.getID() == 0) Thread.sleep(3000L);
		} 
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
			Thread.currentThread().interrupt();
		}
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
		clk   = new SimpleDateFormat("zzz':'hh':'mm':'ss':'a");
		date  = new Date();
		time  = sd.format(date);
		UIManager.put("PopupMenu.background", Color.WHITE);
		UIManager.put("MenuItem.background", Color.WHITE);
		UIManager.put("CheckBoxMenuItem.background", Color.WHITE);
		pMenu = new JPopupMenu("DeskTime Menu");
		mFormat = new JMenu("Format");
		mRoundCorners = new JMenu("Rounded corners");
		tMenuItem = new JMenuItem[ExUtils.ROUND_CORNERS.values().length];
		int mCnt = 0;
		for (ExUtils.ROUND_CORNERS corner : ExUtils.ROUND_CORNERS.values()) {
			ImageIcon selectedIco = info.getRoundCorners() == corner.getRoundType() ? checkPng : clearPng;
			tMenuItem[mCnt] = new JMenuItem(ExUtils.toCamelCase(corner.name()), selectedIco);
			tMenuItem[mCnt].setActionCommand(corner.name());
			tMenuItem[mCnt].addActionListener(this);
			mRoundCorners.add(tMenuItem[mCnt]);
			mCnt++;
		}
		mOpacityLevel = new JMenu("Opacity level");
		opacityLevel  = new JSlider(SwingConstants.VERTICAL, 4, 20, 10);
		opacityLevel.setPreferredSize(new Dimension(opacityLevel.getPreferredSize().width + 40, opacityLevel.getPreferredSize().height + 40));
		opacityLevel.setMajorTickSpacing(4);
		opacityLevel.setMinorTickSpacing(1);
		opacityLevel.setPaintTicks(true);
		opacityLevel.setSnapToTicks(true);
		opacityLevel.addChangeListener(this);
		mOpacityLevel.add(opacityLevel);
		mAllOpacity = new JMenuItem("Blend foreground opacity");
		mAllOpacity.setIcon(info.isForegroundTranslucent() ? checkPng : clearPng);
		mAllOpacity.addActionListener(this);
		mAllOpacity.setEnabled(pixelTranslucency);
		fore  = new JMenuItem("Font & Foreground...");
		fore.addActionListener(this);
		back  = new JMenuItem("Background & Dials...");
		back.addActionListener(this);
		bdr   = new JMenuItem("Borders & Clock UI...");
		bdr.addActionListener(this);
		mFormat.add(mRoundCorners);
		mFormat.add(mOpacityLevel);
		mFormat.add(mAllOpacity);
		mFormat.addSeparator();
		mFormat.add(fore);
		mFormat.add(back);
		mFormat.add(bdr);
		timeMode = new JMenu("Time Mode");
		miDigTime   = new JMenuItem("Digital clock");
		miDigTime.addActionListener(this);
		miAnaTime   = new JMenuItem("Analog clock");
		miAnaTime.addActionListener(this);
		miUptime = new JMenuItem("System Up-time");
		miUptime.addActionListener(this);
		miPomo   = new JMenuItem("Pomodoro Timer");
		miPomo.addActionListener(this);
		timSet   = new JMenuItem("More settings...");
		timSet.addActionListener(this);
		timeMode.add(miDigTime);
		timeMode.add(miAnaTime);
		timeMode.add(miUptime);
		timeMode.add(miPomo);
		timeMode.addSeparator();
		timeMode.add(timSet);
		timeZone = new JMenu("Timezone");
		miSeltz  = new JMenuItem("Last selected");
		miSeltz.addActionListener(this);
		miDeftz  = new JMenuItem("System");
		miDeftz.addActionListener(this);
		timeZone.add(miSeltz);
		timeZone.add(miDeftz);
		timeZone.addSeparator();
		impZon = new JMenuItem[impZoneList.length];
		for (int i = 0; i < impZoneList.length; i++) {
			impZon[i] = new JMenuItem(impZoneList[i]);
			impZon[i].addActionListener(this);
			impZon[i].setActionCommand("TZ-" + impZoneList[i].split("[\\(|\\)]")[1]);
			timeZone.add(impZon[i]);
		}
		zonSet   = new JMenuItem("More Timezones...");
		zonSet.addActionListener(this);
		timeZone.addSeparator();
		timeZone.add(zonSet);
		sysClk = new JMenuItem("System Clock");
		alm    = new JMenuItem("Set Alarm...");
		alm.addActionListener(this);
		sysClk.addActionListener(this);
		Hashtable<Integer, JLabel> ht = new Hashtable<>();
		for (int i = 0; i < fontSizes.length; i++)
			ht.put(i, new JLabel(String.valueOf(fontSizes[i])));
		sizer = new JSlider(SwingConstants.VERTICAL, 0, fontSizes.length - 1, 5);
		sizer.setLabelTable(ht);
		sizer.setMajorTickSpacing(4);
		sizer.setMinorTickSpacing(1);
		sizer.setPaintLabels(true);
		sizer.setPaintTicks(true);
		sizer.setSnapToTicks(true);
		sizer.addChangeListener(this);
		mSize = new JMenu("Resize");
		sizer.setPreferredSize(new Dimension(sizer.getPreferredSize().width + 20, sizer.getPreferredSize().height + 40));
		mSize.add(sizer);
		miMovable  = new JMenuItem("Movable");
		miMovable.setIcon(info.isFixed() ? clearPng : checkPng);
		miMovable.addActionListener(this);
		ontop = new JMenuItem("Always on top");
		ontop.setIcon(info.getOnTop() ? checkPng : clearPng);
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
		about = new JMenuItem("About...");
		about.addActionListener(this);
		exit  = new JMenuItem("Exit");
		exit.addActionListener(this);
		pMenu.add(timeMode);
		pMenu.add(timeZone);
		pMenu.add(mFormat);
		pMenu.add(alm);
		switch (ExUtils.getOS()) {
			case WINDOWS:
			case LINUX:
				pMenu.add(sysClk);
				break;
			case MAC:
				// to be included later
				break;
			default:
				break;
		}
		pMenu.addSeparator();
		pMenu.add(mSize);
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
		tLabel.setAnalogClockOptions(info.getAnalogClockOption());
		miSeltz.setText("Last selected (" + info.getTimeZone() + ")");
		miDeftz.setText("System (" + TimeZone.getDefault().getID() + ")");
		timeZone.setEnabled(info.getDisplayMethod().endsWith("TZ"));
		Rectangle screen = new Rectangle(scsize);
		Point savedLocation = info.getLocation();
		setLocation(screen.contains(savedLocation) ? savedLocation : new Point(10, 10));
		if (info.isUsingImage())
		{
			tLabel.setText(time);
			tLabel.setTransparency(false);
			opacityMethod(info.isUsingImage());
			stopRefresh();
			boolean lastClockMode = tLabel.isClockMode();
			boolean newClockMode  = info.isAnalogClock();
			tLabel.setBackImage((new ImageIcon(info.getImageFile().toString())).getImage());
			// Bugfix: set image style as tile-horizontal when changing from analog to others and stretch when vice versa.
			if (lastClockMode == newClockMode) tLabel.setImageLayout(info.getImageStyle());
			else if (lastClockMode) tLabel.setImageLayout(TLabel.H_TILE);
			else tLabel.setImageLayout(TLabel.STRETCH);

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
			if (info.hasGlassEffect()) info.setGlassEffect(false);
			opacityMethod(info.isUsingImage());
			tLabel.setText(time);
			tLabel.setBackImage(null);
			tLabel.setTransparency(false);
			getRootPane().putClientProperty(WINDOW_SHADOW_PROPERTY, Boolean.TRUE);
			stopRefresh();
		}
		if(info.getID() != 0) setVisible(true);
		setRoundedCorners(info.getRoundCorners());
		miMovable.setIcon(info.isFixed() ? clearPng : checkPng);
		ontop.setIcon(info.getOnTop() ? checkPng : clearPng);
		if (info.getDisplayMethod().endsWith("TZ"))
		{
			miDigTime.setIcon(!info.isAnalogClock() ? checkPng : clearPng);
			miAnaTime.setIcon(info.isAnalogClock() ? checkPng : clearPng);
		}
		miUptime.setIcon(info.getDisplayMethod().equals(DISPLAY_MODE_SYSTEM_UPTIME) ? checkPng : clearPng);
		miPomo.setIcon(info.getDisplayMethod().equals(DISPLAY_MODE_POMODORO_TIMER) ? checkPng : clearPng);
		mAllOpacity.setIcon(info.isForegroundTranslucent() ? checkPng : clearPng);
		opacityLevel.setMinimum(info.isPixelAlphaSupported() && !info.isForegroundTranslucent() ? 0 : 4);
		opacityLevel.setValue(Math.round(info.getOpacity() * 20));
		lastPomTask = info.getPomodoroTask();
		lastPomFormat = info.getPomodoroFormat();
		timeDisplayConfig();
		validate();
	}

	public static final int[] getFontSizes()
	{
		return fontSizes;
	}

	private void timeDisplayConfig()
	{
		String dispString = info.getDisplayMethod();
		if (dispString.equals(DISPLAY_MODE_SELECTED_TIMEZONE) || dispString.equals(DISPLAY_MODE_CURRENT_TIMEZONE))
		{
			sd   = new SimpleDateFormat(info.getZonedTimeFormat());
			sd.setTimeZone(dispString.equals(DISPLAY_MODE_SELECTED_TIMEZONE) ? TimeZone.getTimeZone(info.getTimeZone()) : TimeZone.getDefault());
			date = new Date();
			time = sd.format(date);
			if (info.isAnalogClock())
			{
				tLabel.setText("");
				tLabel.setClockMode(true);
				resizingMethod("");
			}
			else
			{
				tLabel.setText(time);
				tLabel.setClockMode(false);
				resizingMethod(time);
			}
			if (info.hasTooltip())
				tLabel.setToolTipText(tipCur.replace("Time of your location", info.getTimeZone()));
			else
				tLabel.setToolTipText(null);
		}
		else if (dispString.equals(DISPLAY_MODE_POMODORO_TIMER))
		{
			// Only create new pomodoro object if earlier was not present, else refer existing one.
			tLabel.setClockMode(false);
			if (pom == null || !lastPomTask.equals(info.getPomodoroTask()) || !lastPomFormat.equals(info.getPomodoroFormat()))
				pom  = new Pomodoro(info.getPomodoroTask());
			time = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(info.isPomodoroCountdown()), info.getPomodoroFormat(), pom.getRunningLabel(), info.isPomodoroLeadingLabel());

			String lstr1 = info.getPomodoroFormat() + " [" + pom.getWorkLabel() + "]";
			String lstr2 = info.getPomodoroFormat() + " [" + pom.getBreakLabel() + "]";
			String lstr3 = pom.checkCanRest() ? info.getPomodoroFormat() + " [" + pom.getRestLabel() + "]" : "";
			if (lstr1.length() >= lstr2.length() && lstr1.length() >= lstr3.length())
				resizingMethod(lstr1);
			else if (lstr2.length() >= lstr1.length() && lstr2.length() >= lstr3.length())
				resizingMethod(lstr2);
			else
				resizingMethod(lstr3);

			if (info.hasTooltip())
				tLabel.setToolTipText(tipPom.replace("pomodoro task", info.getPomodoroTask()));
			else
				tLabel.setToolTipText(null);
		}
		else if (dispString.equals(DISPLAY_MODE_SYSTEM_UPTIME))
		{
			tLabel.setClockMode(false);
			time = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), info.getUpTimeFormat());
			resizingMethod(time);
			if (info.hasTooltip())
				tLabel.setToolTipText(tipUpt);
			else
				tLabel.setToolTipText(null);
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

	@Override
	public void actionPerformed(ActionEvent actionevent)
	{
		lastPomTask   = info.getPomodoroTask();   //Since pomodoro change can be triggered with time tab,
		lastPomFormat = info.getPomodoroFormat(); //which in turn can be navigated from any of the tabs.

		Object obj = actionevent.getSource();
		InfoTracker trackChanges;
		if (obj.equals(bdr))
		{
			trackChanges = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.BORDER_TAB, info, alarms);
			info         = trackChanges.getSelectedInformation();
			alarms       = trackChanges.getSelectedAlarms();
			ExUtils.saveAlarms(alarms);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(fore))
		{
			trackChanges = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.FONT_TAB, info, alarms);
			info         = trackChanges.getSelectedInformation();
			alarms       = trackChanges.getSelectedAlarms();
			ExUtils.saveAlarms(alarms);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(back))
		{
			trackChanges = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.BACKGROUND_TAB, info, alarms);
			info         = trackChanges.getSelectedInformation();
			alarms       = trackChanges.getSelectedAlarms();
			ExUtils.saveAlarms(alarms);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miSeltz))
		{
			info.setDisplayMethod(DISPLAY_MODE_SELECTED_TIMEZONE);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miDeftz))
		{
			info.setDisplayMethod(DISPLAY_MODE_CURRENT_TIMEZONE);
			info.setTimeZone(TimeZone.getDefault().getID());
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miDigTime) || obj.equals(miAnaTime))
		{
			info.setDisplayMethod(info.getTimeZone().equals(TimeZone.getDefault().getID()) ? DISPLAY_MODE_CURRENT_TIMEZONE : DISPLAY_MODE_SELECTED_TIMEZONE);
			info.setAnalogClock(obj.equals(miAnaTime));
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj instanceof JMenuItem && actionevent.getActionCommand().startsWith("TZ-"))
		{
			String tzId = actionevent.getActionCommand().split("-")[1];
			info.setDisplayMethod(TimeZone.getTimeZone(tzId).hasSameRules(TimeZone.getDefault()) ? DISPLAY_MODE_CURRENT_TIMEZONE : DISPLAY_MODE_SELECTED_TIMEZONE);
			info.setTimeZone(tzId);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miUptime))
		{
			info.setDisplayMethod(DISPLAY_MODE_SYSTEM_UPTIME);
			info.setAnalogClock(false);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miPomo))
		{
			info.setDisplayMethod(DISPLAY_MODE_POMODORO_TIMER);
			info.setAnalogClock(false);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(timSet) || obj.equals(zonSet))
		{
			trackChanges = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.TIMES_TAB, info, alarms);
			info         = trackChanges.getSelectedInformation();
			alarms       = trackChanges.getSelectedAlarms();
			ExUtils.saveAlarms(alarms);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(alm))
		{
			trackChanges = ChooserBox.showDialog(PREFERENCES_TITLE, ChooserBox.ALARMS_TAB, info, alarms);
			info         = trackChanges.getSelectedInformation();
			alarms       = trackChanges.getSelectedAlarms();
			ExUtils.saveAlarms(alarms);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(sysClk))
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
					// to be included later
					break;
				default:
					break;
			}
		}
		else if (obj.equals(mAllOpacity))
		{
			info.setForegroundTranslucent(!info.isForegroundTranslucent());
			mAllOpacity.setIcon(info.isForegroundTranslucent() ? checkPng : clearPng);
			ExUtils.saveDeskStops(info, deskstops);
			reInitialize();
		}
		else if (obj.equals(miMovable))
		{
			info.setFixed(!info.isFixed());
			miMovable.setIcon(info.isFixed() ? clearPng : checkPng);
			tLabel.setCursor(info.isFixed() ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			ExUtils.saveDeskStops(info, deskstops);
		}
		else if (obj.equals(ontop))
		{
			info.setOnTop(!info.getOnTop());
			ontop.setIcon(info.getOnTop() ? checkPng : clearPng);
			ExUtils.saveDeskStops(info, deskstops);
			setAlwaysOnTop(info.getOnTop());
		}
		else if (obj.equals(about))
		{
			JOptionPane.showMessageDialog(new Frame(), ABOUT_STRING, "About DeskStop...", 1, aboutGif);
		}
		else if (obj.equals(exit))
		{
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
		}
		else if (obj.equals(newItem))
		{
			DeskStop.createInstance(info, false);
		}
		else if (obj.equals(dupItem))
		{
			DeskStop.createInstance(info, true);
		}
		else if (obj.equals(removePanel))
		{
			DeskStop.removeInstance(this, info);
		}
		else if (obj instanceof JMenuItem)
		{
			int mCnt = 0;
			for (ExUtils.ROUND_CORNERS corner : ExUtils.ROUND_CORNERS.values()) {
				if (corner.name().equals(actionevent.getActionCommand())) {
					tMenuItem[mCnt].setIcon(checkPng);
					setRoundedCorners(corner.getRoundType());
					info.setRoundCorners(corner.getRoundType());
					ExUtils.saveDeskStops(info, deskstops);
					reInitialize();
				} else {
					tMenuItem[mCnt].setIcon(clearPng);
				}
				mCnt++;
			}
		}
		info.setPixelAlphaSupport(pixelTranslucency);
		info.setWindowAlphaSupport(wholeTranslucency);
		info.setScreenshotSupport(robotSupport);
		refreshNow = true;
		startRefresh();
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
	}

	@Override
	public void mouseEntered(MouseEvent mouseevent)
	{
		if (!pMenu.isShowing()) {
			refreshNow = true;
		}
		startRefresh();
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
		} else if (srcObject.equals(opacityLevel)) {
			float derived = (float)opacityLevel.getValue() / 20;
			if (derived != info.getOpacity()) {
				info.setOpacity(derived);
				opacityMethod(info.isUsingImage());
				ExUtils.saveDeskStops(info, deskstops);
			}
		}
	}

	class ClockThread extends Thread
	{
		private volatile boolean timerun = true;

		ClockThread()
		{
			super("TimeView");
		}

		@Override
		public void run()
		{
			Calendar gcal        = Calendar.getInstance();
			Date     startTime   = ExUtils.getSystemStartTime();
			String   curLabel    = "";
			Player   runPlayer   = null;
			int      soundRunSec = 60;
			String[] clockTime;
			if(pom != null) 
				curLabel = pom.getRunningLabel();
			for (Thread thread = Thread.currentThread(); clockThread == thread && timerun;)
			{
				String method = info.getDisplayMethod();
				date = new Date();
				try
				{
					if (method.equals(DISPLAY_MODE_SYSTEM_UPTIME))
					{
						Duration uptimeNow = Duration.ofNanos(System.nanoTime());
						time = ExUtils.formatUptime(uptimeNow, info.getUpTimeFormat());
						tLabel.setText(time);
						gcal.setTime(startTime);
						gcal.add(Calendar.SECOND, (int)uptimeNow.getSeconds());
						date = gcal.getTime();
						int curMin = Math.toIntExact(uptimeNow.getSeconds() % 3600 / 60);
						int curSec = Math.toIntExact(uptimeNow.getSeconds() % 3600 % 60);
						if (curMin == 0 && curSec == 0)
						{
							if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
							runPlayer = SoundPlayer.playAudio(info.getUptimeHourSound(), soundRunSec);
						}
					}
					else if (method.equals(DISPLAY_MODE_SELECTED_TIMEZONE) || method.equals(DISPLAY_MODE_CURRENT_TIMEZONE))
					{
						time = sd.format(date);
						gcal.setTime(date);
						int curMin = gcal.get(Calendar.MINUTE);
						int curSec = gcal.get(Calendar.SECOND);
						if (info.isAnalogClock())
						{
							clk.setTimeZone(sd.getTimeZone());
							clockTime = clk.format(date).split(":");
							tLabel.setText("");
							tLabel.setTime(clockTime[1], clockTime[2], clockTime[3], clockTime[4], clockTime[0]);
						}
						else
							tLabel.setText(time);
						if (curMin == 0 && curSec == 0)
						{
							if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
							runPlayer = SoundPlayer.playAudio(info.getHourSound(), soundRunSec);
						}
					}
					else
					{
						// Below 2 line is under timeDisplayConfig(), but added here to avoid NullPointerException in case of race condition.
						if (pom == null || !lastPomTask.equals(info.getPomodoroTask()) || !lastPomFormat.equals(info.getPomodoroFormat()))
							pom  = new Pomodoro(info.getPomodoroTask());
						time = ExUtils.formatPomodoroTime(pom.getRunningLabelDuration(info.isPomodoroCountdown()), info.getPomodoroFormat(), pom.getRunningLabel(), info.isPomodoroLeadingLabel());
						tLabel.setText(time);
						if (!curLabel.equals(pom.getRunningLabel())) // compare with stored label as below
						{
							if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
							if (pom.getRunningLabel().equals(pom.getWorkLabel()))
							{
								runPlayer = SoundPlayer.playAudio(info.getPomodoroWorkSound(), soundRunSec);
							}
							else if (pom.getRunningLabel().equals(pom.getBreakLabel()))
							{
								runPlayer = SoundPlayer.playAudio(info.getPomodoroBreakSound(), soundRunSec);
							}
							else if (pom.getRunningLabel().equals(pom.getRestLabel()))
							{
								runPlayer = SoundPlayer.playAudio(info.getPomodoroRestSound(), soundRunSec);
							}
						}
						curLabel = pom.getRunningLabel();
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
			if (tmpArray.length() == (w*h))
			{
				for (int j = y; j < (y + h); j++)
				{
					for (int i = x; i < (x + w); i++)
					{
						int ov = tmpArray.get(cp);
						int nv = img2compare.getRGB(i, j);
						tmpArray.set(cp, nv);
						if (ov != nv) compInt += 1;
						cp += 1;
					}
				}
			}
			else
			{
				tmpArray = new AtomicIntegerArray(w*h);
				for (int j = y; j < h; j++)
					for (int i = x; i < w; i++)
					{
						int nv = img2compare.getRGB(i, j);
						tmpArray.set(cp, nv);
						cp += 1;
					}
				compInt = 1;  // 0=all matching, >0=mismatch;
			}
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
		Vector<TimeBean> allTimeBeans = ExUtils.loadAlarms();
		DeskStop deskstop = new DeskStop(initInfo, allTimeBeans);
		deskstop.start();
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
				deskstops.add(initInfo);
				ExUtils.saveDeskStops(initInfo, deskstops);
			} else {
				initInfo = deskstops.get(count);
			}
			DeskStop deskstop = new DeskStop(initInfo, allTimeBeans);
			deskstop.start();
			count++;
		} while (count < deskstops.size());
	}
}
