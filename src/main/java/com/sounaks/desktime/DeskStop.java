package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class DeskStop extends JWindow implements MouseInputListener, ActionListener
{
	protected ClockThread clockThread; 
	protected Refresher refreshThread;
	private String time;
	private Date date;
	private int locX,locY,curX,curY;
	private TLabel tLabel;
	private SimpleDateFormat sd;
	private FontMetrics metrics;
	private InitInfo info;
	private Vector <TimeBean>alarms;
	private JPopupMenu pMenu;
	private JMenuItem fore,back,fmt,alm,opt,exit,about;
	private JCheckBoxMenuItem fix1,mhelp,ontop;
	private Point pp;
	private Dimension scsize;
	protected Robot robot;
	private   boolean refreshNow  = true;
	protected final String tipGmt = "Currently Displaying: Greenwich Mean Time (GMT)\nThis time is reffered as the world standard time.";
	protected final String tipCur = "Currently Displaying: Time of your location (Time-Zone)\nThe system time should be set correctly according\nto your time zone.";
	protected final String tipUpt = "Currently Displaying: System Up-Time\nThis time shows for how long your computer is running\nwithout a shut-down or log-off.\n(Requires this program to be running from system startup).";

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
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tLabel, "Center");
		setSize(300, 50);
		setLocation((scsize.width - 200) / 2, (scsize.height - 200) / 2);
		setVisible(true);
		info   = loadProperties();
		alarms = loadAlarms();
		setSystemStartTime(alarms);
		pp    = new Point(0, 0);
		locX  = (int)info.getLocation().getX();
		locY  = (int)info.getLocation().getY();
		curX  = curY = 0;
		sd    = new SimpleDateFormat(info.getThisTimeZoneFormat());
		date  = new Date();
		time  = sd.format(date);
		pMenu = new JPopupMenu("DeskTime Menu");
		fore  = new JMenuItem("Font Format...");
		fore.setBackground(Color.white);
		fore.addActionListener(this);
		back  = new JMenuItem("Background...");
		back.setBackground(Color.white);
		back.addActionListener(this);
		opt   = new JMenuItem("Borders & UI...");
		opt.setBackground(Color.white);
		opt.addActionListener(this);
		fmt   = new JMenuItem("Time Format...");
		fmt.setBackground(Color.white);
		fmt.addActionListener(this);
		alm   = new JMenuItem("Set Alarm...");
		alm.setBackground(Color.white);
		alm.addActionListener(this);
		fix1  = new JCheckBoxMenuItem("Fix To Place");
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
		pMenu.add(opt);
		pMenu.add(fmt);
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
		setLocation(info.getLocation());
		if (info.isUsingImage())
		{
			tLabel.setText(time);
			tLabel.setTransparency(false);
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
			}
			catch (Exception e)
			{
				System.err.println("Can't process robot task due to -");
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
			stopRefresh();
		}
		resizingMethod();
		fix1.setSelected(info.isFixed());
		ontop.setSelected(info.getOnTop());
		mhelp.setSelected(info.hasTooltip());
		timeDisplayConfig();
		validate();
	}

	private void timeDisplayConfig()
	{
		if (info.getDisplayMethod().equals("GMTTZ"))
		{
			sd   = new SimpleDateFormat(info.getGMTZoneFormat());
			sd.setTimeZone(TimeZone.getTimeZone("GMT"));
			time = sd.format(date = new Date());
			resizingMethod();
			if (info.hasTooltip())
				tLabel.setToolTipText(tipGmt);
			else
				tLabel.setToolTipText(null);
		}
		else if (info.getDisplayMethod().equals("CURTZ"))
		{
			sd   = new SimpleDateFormat(info.getThisTimeZoneFormat());
			sd.setTimeZone(TimeZone.getDefault());
			time = sd.format(date = new Date());
			resizingMethod();
			if (info.hasTooltip())
				tLabel.setToolTipText(tipCur);
			else
				tLabel.setToolTipText(null);
		}
		else if (info.getDisplayMethod().equals("UPTIME"))
		{
			time = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), info.getUpTimeFormat());
			resizingMethod();
			if (info.hasTooltip())
				tLabel.setToolTipText(tipUpt);
			else
				tLabel.setToolTipText(null);
		}
	}

	private void resizingMethod()
	{
		char mChar[] = new char[time.length()];
		for (int k = 0; k < time.length(); k++)
			mChar[k] ='8'; // Coz its the medium char generally :)
		int k = Math.round(metrics.stringWidth(new String(mChar)));
		int i = Math.round(metrics.stringWidth(time)) + 10;
		int j = Math.round(metrics.getHeight()) + 5;
		tLabel.setSize(i > k ? i : k, j);  // normal string or string of '8'
		setSize(i > k ? i : k, j);  // whichever gr8er;
	}
	
	// Used for setting run time of up-time alarms;
	private void setSystemStartTime(Vector <TimeBean>vec)
	{
		long nano    = Math.abs(System.nanoTime()/1000000000);
		int  days    = -1*(int)nano/86400;
		int  hours   = -1*(int)(nano%86400)/3600;
		int  minutes = -1*(int)(nano%3600)/60;
		//int seconds=-1*(int)(nano%3600)%60; // Not used coz we need 0;
		GregorianCalendar gcal=new GregorianCalendar();
		gcal.add(Calendar.DATE,days);
		gcal.add(Calendar.HOUR_OF_DAY,hours);
		gcal.add(Calendar.MINUTE,minutes);
		gcal.add(Calendar.SECOND,0);  // Second 0 for a match;
		for (TimeBean tb : vec)
		{
			if (tb.getTimeType().booleanValue()) tb.setRuntime(gcal.getTime());
		}
	}

	private InitInfo loadProperties()
	{
		InitInfo data=new InitInfo();
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("DeskTime.xml")));
			data = (InitInfo)decoder.readObject();
			decoder.close();
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("File missing-\"DeskTime.xml\": "+exclusive.toString());
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
			System.out.println("Exception while saving properties file-\"DeskTime.xml\": "+fne.toString());
			fne.printStackTrace();
		}
	}
	
	private Vector<TimeBean> loadAlarms()
	{
		Vector <TimeBean>data=new Vector<TimeBean>();
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("Smrala.xml")));
			data = (Vector<TimeBean>)decoder.readObject();
			decoder.close();
		}
		catch (Exception exclusive)
		{// Ignoring missing file...
			System.out.println("Exception while loading properties file-\"Smarla.xml\": "+exclusive.toString());
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
			System.out.println("Exception while saving alarms file \"Smarla.xml\": "+fne.toString());
			fne.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent actionevent)
	{
		Object obj = actionevent.getSource();
		InfoTracker trackChanges;
		if (obj.equals(opt))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", 2, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			setSystemStartTime(alarms);
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(fore))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", 0, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			setSystemStartTime(alarms);
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(back))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", 1, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			setSystemStartTime(alarms);
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(fmt))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", 3, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			setSystemStartTime(alarms);
			saveAlarms(alarms);
			saveProperties(info);
			re_init();
		}
		else if (obj.equals(alm))
		{
			trackChanges = ChooserBox.showDialog("Preferences...", 4, info, alarms);
			info         = trackChanges.INFORMATION;
			alarms       = trackChanges.ALARMS;
			setSystemStartTime(alarms);
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
			ImageIcon imageicon = new ImageIcon("duke.gif");
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
			refreshNow = false; // refreshThreadransparency which forces popup to disappear. So refreshNow=false.
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
			curX = mouseevent.getX();
			curY = mouseevent.getY();
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
			info.setLocation(new Point(locX, locY));
			curX = 0;
			curY = 0;
			saveProperties(info);
			refreshNow = true;
			refreshThread.refreshThreadransparency();
			startRefresh();
		}
	}

	public void mouseDragged(MouseEvent mouseevent)
	{
		pp.setLocation(mouseevent.getPoint());
		SwingUtilities.convertPointToScreen(pp, this);
		if (Math.abs(pp.getX()) <= (double)scsize.width && Math.abs(pp.getY()) <= (double)scsize.height)
		{
			locX = Math.abs((int)pp.getX() - curX);
			locY = Math.abs((int)pp.getY() - curY);
			setLocation(locX, locY);
		}
		refreshNow = false;
		stopRefresh();
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
			for (Thread thread = Thread.currentThread(); clockThread == thread && timerun;)
			{
				try
				{
					if (info.getDisplayMethod().equals("UPTIME"))
					{
						time = ExUtils.formatUptime(Duration.ofNanos(System.nanoTime()), info.getUpTimeFormat());
					}
					else
					{
						date = new Date();
						time = sd.format(date);
					}
					tLabel.setText(time);
					Thread.sleep(1000L);
				}
				catch (Exception exception)
				{
					System.out.println(exception);
					timerun = false;
				}
			}
		}

		public void terminate()
		{
			timerun = false;
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
		private volatile boolean running = true;
		
		Refresher()
		{
			super("BG-Refresher");
			top2pixelRows      = new AtomicIntegerArray(10);
			left2pixelColumns  = new AtomicIntegerArray(10);
			bottom2pixelRows   = new AtomicIntegerArray(10);
			right2pixelColumns = new AtomicIntegerArray(10);
			compInt            = 0;
			setPriority(6);
		}

		public synchronized void refreshThreadransparency()
		{
			int refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
			if (info.hasGlassEffect() && refreshNow)
			{
				refreshNow = false;
				setVisible(false);
				try
				{
					long waitBeforeRefresh = Math.round(2*1000/refreshRate);
					Thread.sleep(waitBeforeRefresh);
					tLabel.setBackImage(robot.createScreenCapture(getBounds()));
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
					refreshThreadransparency();
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

		public boolean backgroundEqualsOld()
		{
			Rectangle bound             = getBounds();
			Rectangle boundWith2pxFence = new Rectangle();
			boundWith2pxFence.setRect(bound.getX()-2,bound.getY()-2,bound.getWidth()+4,bound.getHeight()+4);
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
		if ((refreshThread == null || !refreshThread.isAlive()) && info.hasGlassEffect() && refreshNow) 
		{
			refreshThread = new Refresher();
			refreshThread.start();
		}
		else
		{
			refreshThread.play();
		}
	}
	
	public void stopRefresh()
	{
		refreshThread.pause();
	}

	public static void main(String args[])
	{
		DeskStop deskstop = new DeskStop();
		deskstop.start();
	}
}
