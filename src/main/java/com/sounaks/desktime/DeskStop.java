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
import java.beans.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import javazoom.jl.player.Player;

public class DeskStop extends JWindow implements MouseInputListener, ActionListener, ComponentListener
{
	protected ClockThread clockThread; 
	protected Refresher refreshThread;
	private String time, lastPomFormat, lastPomTask;
	private Date date;
	private int locX,locY,locW,locH,cursorX,cursorY,cursorW,cursorH;
	private TLabel tLabel;
	private SimpleDateFormat sd;
	private FontMetrics metrics;
	private InitInfo info;
	private Vector <TimeBean>alarms;
	private JPopupMenu pMenu;
	private JMenuItem fore,back,tim,alm,bdr,exit,about;
	private JCheckBoxMenuItem fix1,mhelp,ontop;
	private Point windowLoc;
	private Dimension scsize;
	protected Robot robot;
	private   boolean refreshNow  = true;
	private Pomodoro pom;
	private Container contentPane;
	protected final String tipGmt = "<html><b>Currently Displaying:</b> Greenwich Mean Time (GMT) <p>This time is reffered as the world standard time.</html>";
	protected final String tipCur = "<html><b>Currently Displaying:</b> Time of your location (Time-Zone) <p>The system time should be set correctly according <p>to your time zone.</html>";
	protected final String tipUpt = "<html><b>Currently Displaying:</b> System Up-Time <p>This time shows for how long your computer is running <p>without a shut-down or log-off.</html>";
	protected final String tipPom = "<html><b>Currently Displaying:</b> Pomodoro Timer <p>This shows pomodoro timer slots of 25 and 5 minutes or as <p>selected. A bigger break slot of 30 minutes after 4 regular slots.</html>";

	public DeskStop()
	{
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		clockThread   = null;
		refreshThread = null;
		scsize        = Toolkit.getDefaultToolkit().getScreenSize();
		tLabel        = new TLabel("Welcome " + System.getProperty("user.name"));
		tLabel.setBackground(Color.white);
		tLabel.setForeground(Color.black);
		tLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(tLabel, "Center");
		pack();
		setSize(300, 50);
		setLocation((scsize.width - 200) / 2, (scsize.height - 200) / 2);
		setVisible(true);
		try
		{
			Thread.sleep(3000L);
		} 
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		info   = loadProperties();
		alarms = loadAlarms();
		addComponentListener(this);
		windowLoc    = new Point(0, 0);
		locX  = (int)info.getLocation().getX();
		locY  = (int)info.getLocation().getY();
		cursorX  = cursorY = 0;
		cursorW  = cursorH = 0;
		sd    = new SimpleDateFormat(info.getZonedTimeFormat());
		date  = new Date();
		time  = sd.format(date);
		pMenu = new JPopupMenu("DeskTime Menu");
		fore  = new JMenuItem("Font Format...");
		fore.setBackground(Color.white);
		fore.addActionListener(this);
		back  = new JMenuItem("Background...");
		back.setBackground(Color.white);
		back.addActionListener(this);
		bdr   = new JMenuItem("Borders & UI...");
		bdr.setBackground(Color.white);
		bdr.addActionListener(this);
		tim   = new JMenuItem("Time Format...");
		tim.setBackground(Color.white);
		tim.addActionListener(this);
		alm   = new JMenuItem("Set Alarm...");
		alm.setBackground(Color.white);
		alm.addActionListener(this);
		fix1  = new JCheckBoxMenuItem("Unmovable");
		fix1.setBackground(Color.white);
		fix1.addActionListener(this);
		mhelp = new JCheckBoxMenuItem("Mouse-Over Help");
		mhelp.setBackground(Color.white);
		mhelp.addActionListener(this);
		ontop = new JCheckBoxMenuItem("Always on top");
		ontop.setBackground(Color.white);
		ontop.addActionListener(this);
		about = new JMenuItem("About...");
		about.setBackground(Color.white);
		about.addActionListener(this);
		exit  = new JMenuItem("Exit");
		exit.setBackground(Color.white);
		exit.addActionListener(this);
		pMenu.add(fore);
		pMenu.add(back);
		pMenu.add(bdr);
		pMenu.add(tim);
		pMenu.add(alm);
		pMenu.addSeparator();
		pMenu.add(fix1);
		pMenu.add(ontop);
		pMenu.add(mhelp);
		pMenu.addSeparator();
		pMenu.add(about);
		pMenu.add(exit);
		pMenu.pack();
		pMenu.setBackground(Color.white);
		re_init();
		tLabel.addMouseListener(this);
	}

	private void re_init()
	{
		setAlwaysOnTop(info.getOnTop());
		tLabel.setFont(info.getFont());
		metrics = tLabel.getFontMetrics(info.getFont());
		tLabel.setForeground(info.getForeground());
		tLabel.setBorder(info.getBorder());
		Rectangle screen = new Rectangle(scsize);
		Point savedLocation = info.getLocation();
		if (screen.contains(savedLocation))
			setLocation(savedLocation);
		else
			setLocation(5, 5);
		if (info.isUsingImage())
		{
			tLabel.setText(time);
			tLabel.setTransparency(false);
			setOpacity(1.0f);
			stopRefresh();
			tLabel.setBackImage((new ImageIcon(info.getImageFile().toString())).getImage());
			tLabel.setImagePosition(info.getImageStyle());
			tLabel.setBackground(null);
		}
		else if (info.hasGlassEffect())
		{
			try
			{
				if (robot == null)
					robot = new Robot();
				tLabel.setTransparency(true);
				setOpacity(1.0f);
			}
			catch (Exception e)
			{
				System.err.println("Can't process robot task due to - ");
				e.printStackTrace();
				tLabel.setTransparency(false);
				tLabel.setBackImage(null);
				tLabel.setBackground(SystemColor.desktop);
			}
			tLabel.setText(time);
			startRefresh();
		}
		else
		{
			tLabel.setText(time);
			tLabel.setBackImage(null);
			tLabel.setBackground(info.getBackground());
			setOpacity(info.getOpacity());
			stopRefresh();
		}
		resizingMethod(time);
		setRoundedCorners(info.hasRoundedCorners());
		fix1.setSelected(info.isFixed());
		ontop.setSelected(info.getOnTop());
		mhelp.setSelected(info.hasTooltip());
		lastPomTask = info.getPomodoroTask();
		lastPomFormat = info.getPomodoroFormat();
		timeDisplayConfig();
		validate();
	}

	private void timeDisplayConfig()
	{
		String dispString = info.getDisplayMethod();
		if (dispString.equals("GMTTZ") || dispString.equals("CURTZ"))
		{
			sd   = new SimpleDateFormat(info.getZonedTimeFormat());
			sd.setTimeZone(dispString.equals("GMTTZ") ? TimeZone.getTimeZone(info.getTimeZone()) : TimeZone.getDefault());
			time = sd.format(date = new Date());
			resizingMethod(time);
			if (info.hasTooltip())
				tLabel.setToolTipText(dispString.equals("GMTTZ") ? tipGmt : tipCur);
			else
				tLabel.setToolTipText(null);
		}
		else if (dispString.equals("POMODORO"))
		{
			// Only create new pomodoro object if earlier was not present, else refer existing one.
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
				tLabel.setToolTipText(tipPom);
			else
				tLabel.setToolTipText(null);
		}
		else if (dispString.equals("UPTIME"))
		{
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
		if (lstr == null || lstr.isEmpty())
			lstr = time;
		char mChar[] = new char[lstr.length()];
		for (int k = 0; k < lstr.length(); k++)
			mChar[k] ='8'; // Coz its the medium char generally :)
		int k = Math.round(metrics.stringWidth(new String(mChar)));
		int i = Math.round(metrics.stringWidth(lstr)) + 10;
		int j = Math.round(metrics.getHeight()) + 5;
		tLabel.setSize(i > k ? i : k, j);  // normal string or string of '8'
		setSize(i > k ? i : k, j);  // whichever gr8er;
	}

	private void setRoundedCorners(boolean isRound)
	{
		Border thisBorder = info.getBorder();
		if (isRound && (!info.hasGlassEffect() || thisBorder instanceof EtchedBorder || thisBorder instanceof LineBorder || thisBorder instanceof BevelBorder))
		{
			setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 5, 5));
		}
		else
		{
			setShape(null);
		}
	}

	private InitInfo loadProperties()
	{
		InitInfo data = new InitInfo();
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("DeskTime.xml")));
			data = (InitInfo)decoder.readObject();
			decoder.close();
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("File missing-\"DeskTime.xml\": " + exclusive.toString());
			exclusive.printStackTrace();
		}
		return data;
	}
	
	private void saveProperties(InitInfo status)
	{
		try
		{
			XMLEncoder xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("DeskTime.xml")));
			xencode.writeObject(status);
			xencode.close();
		}
		catch (FileNotFoundException fne)
		{
			System.out.println("Exception while saving properties file-\"DeskTime.xml\": " + fne.toString());
			fne.printStackTrace();
		}
	}
	
	private Vector<TimeBean> loadAlarms()
	{
		Vector<TimeBean> data = new Vector<TimeBean>();
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("Smrala.xml")));
			Object settingsObj = decoder.readObject();
			decoder.close();
			if (settingsObj instanceof Vector) {
				Vector<?> tmpVec = (Vector<?>)settingsObj;
				for (int cnt = 0; cnt < tmpVec.size(); cnt++) {
					if (tmpVec.elementAt(cnt) instanceof TimeBean) data.add((TimeBean)tmpVec.elementAt(cnt));
				}
			}
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("Exception while loading properties file-\"Smarla.xml\": " + exclusive.getMessage());
			exclusive.printStackTrace();
		}
		return data;
	}
	
	private void saveAlarms(Vector <TimeBean>data)
	{
		try
		{
			XMLEncoder xencode = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("Smrala.xml")));
			xencode.writeObject(data);
			xencode.close();
		}
		catch (FileNotFoundException fne)
		{
			System.out.println("Exception while saving alarms file \"Smarla.xml\": " + fne.toString());
			fne.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent actionevent)
	{
		lastPomTask   = info.getPomodoroTask();   //Since pomodoro change can be triggered with time tab,
		lastPomFormat = info.getPomodoroFormat(); //which in turn can be navigated from any of the tabs.

		Object obj = actionevent.getSource();
		InfoTracker trackChanges;
		if (obj.equals(bdr))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", ChooserBox.BORDER_TAB, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(fore))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", ChooserBox.FONT_TAB, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(back))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", ChooserBox.BACKGROUND_TAB, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(tim))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", ChooserBox.TIMES_TAB, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(alm))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", ChooserBox.ALARMS_TAB, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(fix1))
		{
			info.setFixed(fix1.isSelected());
			saveProperties(info);
		}
		else if (obj.equals(ontop))
		{
			info.setOnTop(ontop.isSelected());
			saveProperties(info);
			setAlwaysOnTop(info.getOnTop());
		}
		else if (obj.equals(mhelp))
		{
			info.setTooltip(mhelp.isSelected());
			saveProperties(info);
			timeDisplayConfig();
		}
		else if (obj.equals(about))
		{
			String    s1        = "<html>Created and Developed by : Sounak Choudhury<p>E-mail Address : <a href='mailto:sounak_s@rediffmail.com'>sounak_s@rediffmail.com</a><p>The software, information and documentation<p>is provided \"AS IS\" without warranty of any<p>kind, either express or implied. The Readme.txt<p>file containing EULA must be read before use.<p>Suggestions and credits are Welcomed.</html>";
			ImageIcon imageicon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/duke.gif"));
			JOptionPane.showMessageDialog(new Frame(), s1, "About DeskStop...", 1, imageicon);
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
			}
			System.exit(0);
		}
		refreshNow = true;
		startRefresh();
	}

	public void mouseClicked(MouseEvent mouseevent)
	{
		if (SwingUtilities.isRightMouseButton(mouseevent) || mouseevent.getClickCount() == 2)
		{ //Whenever this menu appears a mouseExited event occurs calling method
			refreshNow = false; // refreshThreadTransparency which forces popup to disappear. So refreshNow=false.
			ExUtils.showPopup(pMenu, this, (Component)mouseevent.getSource(), mouseevent.getPoint(), scsize);
		}
	}

	public void mouseEntered(MouseEvent mouseevent)
	{
		if (!pMenu.isShowing()) {
			refreshNow = true;
		}
		startRefresh();
	}

	public void mouseExited(MouseEvent mouseevent)
	{
	}

	public void mouseMoved(MouseEvent mouseevent)
	{
	}

	public void mousePressed(MouseEvent mouseevent)
	{
		if (!SwingUtilities.isRightMouseButton(mouseevent))
		{
			cursorX = mouseevent.getX();
			cursorY = mouseevent.getY();
			cursorW = getWidth() - mouseevent.getX();
			cursorH = getHeight() - mouseevent.getY();
			// As sometimes unremoved listenenrs cause buggy movement.
			for (MouseMotionListener mListener : tLabel.getMouseMotionListeners()) {
				if (!(mListener instanceof ToolTipManager))
					tLabel.removeMouseMotionListener(mListener);
			}
			if (!info.isFixed())
				tLabel.addMouseMotionListener(this);
		}
	}

	public void mouseReleased(MouseEvent mouseevent)
	{
		if (!SwingUtilities.isRightMouseButton(mouseevent))
		{
			if (!info.isFixed())
				tLabel.removeMouseMotionListener(this);
			Point newLocation = getLocation();
			cursorX = 0;
			cursorY = 0;
			cursorW = 0;
			cursorH = 0;
			refreshNow = true;
			if (!info.getLocation().equals(newLocation))
			{
				info.setLocation(newLocation);
				saveProperties(info);
				refreshThread.refreshTransparency();
			}
			startRefresh();
		}
	}

	public void mouseDragged(MouseEvent mouseevent)
	{
		windowLoc.setLocation(mouseevent.getPoint());
		SwingUtilities.convertPointToScreen(windowLoc, this);
		if (Math.abs(windowLoc.getX()) <= (double)scsize.width && Math.abs(windowLoc.getY()) <= (double)scsize.height)
		{
			locX = (int)windowLoc.getX() - cursorX;
			locY = (int)windowLoc.getY() - cursorY;
			locW = (int)scsize.width - ((int)windowLoc.getX() + cursorW);
			locH = (int)scsize.height - ((int)windowLoc.getY() + cursorH);
			if (locX >= 0 && locY >= 0 && locW >= 0 && locH >= 0)
				setLocation(locX, locY);
		}
		refreshNow = false;
		stopRefresh();
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		setRoundedCorners(info.hasRoundedCorners());
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		setRoundedCorners(info.hasRoundedCorners());
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
	}

	class ClockThread extends Thread
	{
		private volatile boolean timerun = true;

		ClockThread()
		{
			super("TimeView");
		}

		public void run()
		{
			Calendar gcal        = Calendar.getInstance();
			Date     startTime   = ExUtils.getSystemStartTime();
			String   curLabel    = "";
			Player   runPlayer   = null;
			int      soundRunSec = 60;
			if(pom != null) 
				curLabel = pom.getRunningLabel();
			for (Thread thread = Thread.currentThread(); clockThread == thread && timerun;)
			{
				String method = info.getDisplayMethod();
				date = new Date();
				try
				{
					if (method.equals("UPTIME"))
					{
						Duration uptimeNow = Duration.ofNanos(System.nanoTime());
						time = ExUtils.formatUptime(uptimeNow, info.getUpTimeFormat());
						gcal.setTime(startTime);
						gcal.add(Calendar.SECOND, (int)uptimeNow.getSeconds());
						date = gcal.getTime();
						int curMin = Math.round(uptimeNow.getSeconds() % 3600 / 60);
						int curSec = Math.round(uptimeNow.getSeconds() % 3600 % 60);
						if (curMin == 0 && curSec == 0)
						{
							if (runPlayer != null) SoundPlayer.stopAudio(runPlayer);
							runPlayer = SoundPlayer.playAudio(info.getUptimeHourSound(), soundRunSec);
						}
					}
					else if (method.equals("GMTTZ") || method.equals("CURTZ"))
					{
						time = sd.format(date);
						gcal.setTime(date);
						int curMin = gcal.get(Calendar.MINUTE);
						int curSec = gcal.get(Calendar.SECOND);
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
					tLabel.setText(time);
					checkTimeAndRunAlarm(date);
					Thread.sleep(1000L);
				}
				catch (Exception exception)
				{
					System.out.println("An error occurred while displaying time.");
					exception.printStackTrace();
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
		private volatile Rectangle newBounds;
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
				int refreshRate        = thisDevice.getRefreshRate();
				Dimension thisScreen   = Toolkit.getDefaultToolkit().getScreenSize();
				float scaleRatio = Math.min(thisDevice.getWidth()/(float)thisScreen.getWidth(), thisDevice.getHeight()/(float)thisScreen.getHeight());
				newBounds = new Rectangle(Math.round(scaleRatio*getX()), Math.round(scaleRatio*getY()), Math.round(scaleRatio*getWidth()), Math.round(scaleRatio*getHeight()));
				refreshNow = false;
				setVisible(false);
				try
				{
					long waitBeforeRefresh = Math.round(2*1000/refreshRate);
					Thread.sleep(waitBeforeRefresh);
					BufferedImage capture = robot.createScreenCapture(newBounds);
					BufferedImage scaled  = new BufferedImage(getBounds().width, getBounds().height, BufferedImage.TYPE_INT_ARGB);
					AffineTransform trans = new AffineTransform();
					trans.scale(1/scaleRatio, 1/scaleRatio);
					AffineTransformOp scaleOp = new AffineTransformOp(trans, AffineTransformOp.TYPE_BILINEAR);
					scaled = scaleOp.filter(capture, scaled);
					tLabel.setBackImage(scaled);
				}
				catch (Exception e)
				{
					System.out.println("Can't process robot task due to - ");
					e.printStackTrace();
				}
				setVisible(true);
				refreshNow = true;
			}
		}

		public void run()
		{
			while(info.hasGlassEffect() && refreshThread!=null)
			{
				if (backgroundEqualsOld() || !running)
				{
					yield();
				}
				else
				{
					try
					{
						if (info.isSlowTransUpdating()) Thread.sleep(1000L);
					}
					catch (Exception e)
					{
						e.printStackTrace();
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
			bottom2pixelRows    = copyAndComparePixelToArray(currImage,currImage.getMinX(), currImage.getHeight()-2, currImage.getWidth(), 2.0, bottom2pixelRows);
			right2pixelColumns  = copyAndComparePixelToArray(currImage,currImage.getWidth()-2, currImage.getMinY(), 2.0, currImage.getHeight(), right2pixelColumns);

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

	public static void main(String args[])
	{
		DeskStop deskstop = new DeskStop();
		deskstop.start();
	}
}
