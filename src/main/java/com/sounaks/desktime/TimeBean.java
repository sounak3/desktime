package com.sounaks.desktime;

import java.io.*;
import java.util.*;

public class TimeBean implements Serializable
{
	public  static final long serialVersionUID = 913566255917L;
	private String name                        = "";
	private Date runtime                       = new Date();
	private Date nextRuntime                   = new Date();
	private Integer interval                   = 0;
	private Integer runType                    = 4;
	private String command                     = "";
	private Boolean timeType                   = false;

	public TimeBean()
	{
	}
	
	private Date oneStepPropell(Date toPropell)
	{
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(toPropell);
		switch(getInterval())
		{
			case 0:
			return gcal.getTime();
			
			case 1:
			gcal.add(Calendar.MINUTE,1);
			return gcal.getTime();
			
			case 2:
			gcal.add(Calendar.HOUR_OF_DAY,1);
			return gcal.getTime();
			
			case 3:
			gcal.add(Calendar.DATE,1);
			return gcal.getTime();
			
			case 4:
			gcal.add(Calendar.WEEK_OF_MONTH,1);
			return gcal.getTime();
			
			case 6:
			gcal.add(Calendar.YEAR,1);
			return gcal.getTime();
			
			case 7:
			gcal.add(Calendar.MONTH,1);
			return gcal.getTime();
			
			case 8:
			int w  = gcal.get(Calendar.WEEK_OF_MONTH);
			int wd = gcal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
			gcal.add(Calendar.MONTH,1);
			gcal.set(Calendar.WEEK_OF_MONTH,w);
			gcal.set(Calendar.DAY_OF_WEEK_IN_MONTH,wd);
			return gcal.getTime();
		}
		return gcal.getTime();
	}
	
	public Date getNextRuntime(Date now)
	{
		Date temp                      = new Date();
		if  (runtime.before(now)) temp = oneStepPropell(runtime);
		else temp                      = runtime;
		while(temp.before(now))
		{
			temp=oneStepPropell(temp);
		}
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(temp);
		gcal.add(Calendar.SECOND, -1);
		temp=gcal.getTime();
		return temp.after(nextRuntime) ? temp : nextRuntime; //return the newer;
	}
	
	public void setNextRuntime(Date nn)
	{
		nextRuntime=nn;
	}
	
	public String getName()
	{
		return name;
	}

	public void	setName(String nn)
	{
		name=nn;
	}
	
	public Date getRuntime()
	{
		return runtime;
	}

	public void	setRuntime(Date nn)
	{
		runtime=nn;
	}
	
	public Integer getInterval()
	{
		return interval;
	}

	public void	setInterval(Integer nn)
	{
		interval=nn;
	}
	
	public Integer getRunType()
	{
		return runType;
	}

	public void	setRunType(Integer nn)
	{
		runType=nn;
	}
	
	public Boolean getTimeType()
	{
		return timeType;
	}
	
	public void setTimeType(Boolean nn)
	{
		timeType=nn;
	}
	
	public String getCommand()
	{
		return command;
	}

	public void	setCommand(String nn)
	{
		command=nn;
	}
}