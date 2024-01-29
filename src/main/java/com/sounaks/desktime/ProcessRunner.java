package com.sounaks.desktime;

import java.awt.*;
import java.util.*;

class ProcessRunner extends Thread
{
	private Vector <TimeBean>alarms;
	private Date now;
	private Thread procRunner;
	private Component parent;
	
	public ProcessRunner(Vector <TimeBean>alarms, Date now, Component parent)
	{
		this.alarms=alarms;
		this.now=now;
		this.parent=parent;
		procRunner=new Thread(this,"Runner");
		procRunner.start();
	}
	
	public void run()
	{
		runSubProcess(now);
		procRunner=null;
	}
	
	private void runSubProcess(Date current)
	{
		for(int indx=0;indx<alarms.size();indx++)
		{
			TimeBean tmpb=alarms.elementAt(indx);
			if(ExUtils.dateCompareUptoSecond(current,tmpb.getNextRuntime(current)))
			{
				ExUtils.runProgram(tmpb,parent);
			}
		}
	}
	
	protected void finalize()
	{
		alarms=null;
		now=null;
		procRunner=null;
	}
}
	