package com.sounaks.desktime;

import java.io.*;
import java.util.*;

public class TimeBean implements Serializable
{
	public  static final long serialVersionUID = 913566255917L;
	private String name                        = "";
	private Date alarmTriggerTime              = new Date();
	private Date nextAlarmTriggerTime          = new Date();
	private Integer alarmRepeatInterval        = 0;
	private Integer alarmExecutionOutputType   = 4;
	private String command                     = "";
	private Boolean systemStartTimeBasedAlarm  = false;

	public TimeBean()
	{
	}
	
	private Date oneStepPropell(Date toPropell)
	{
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(toPropell);
		switch (getAlarmRepeatInterval())
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
	
	public Date getNextAlarmTriggerTime(Date now)
	{
		Date temp                                = new Date();
		if   (alarmTriggerTime.before(now)) temp = oneStepPropell(alarmTriggerTime);
		else temp                                = alarmTriggerTime;
		while(temp.before(now))
		{
			temp=oneStepPropell(temp);
		}
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(temp);
		gcal.add(Calendar.SECOND, -1);
		temp=gcal.getTime();
		return temp.after(nextAlarmTriggerTime) ? temp : nextAlarmTriggerTime; //return the newer;
	}
	
	public void setNextAlarmTriggerTime(Date nextAlarmTriggerTime)
	{
		this.nextAlarmTriggerTime = nextAlarmTriggerTime;
	}
	
	public String getName()
	{
		return name;
	}

	public void	setName(String name)
	{
		this.name = name;
	}
	
	public Date getAlarmTriggerTime()
	{
		return alarmTriggerTime;
	}

	public void	setAlarmTriggerTime(Date alarmTriggerTime)
	{
		this.alarmTriggerTime = alarmTriggerTime;
	}
	
	public Integer getAlarmRepeatInterval()
	{
		return alarmRepeatInterval;
	}

	public void	setAlarmRepeatInterval(Integer alarmRepeatInterval)
	{
		this.alarmRepeatInterval = alarmRepeatInterval;
	}
	
	public Integer getAlarmExecutionOutputType()
	{
		return alarmExecutionOutputType;
	}

	public void	setAlarmExecutionOutputType(Integer alarmExecutionOutputType)
	{
		this.alarmExecutionOutputType = alarmExecutionOutputType;
	}
	
	public Boolean isSystemStartTimeBasedAlarm()
	{
		return systemStartTimeBasedAlarm;
	}
	
	public void setSystemStartTimeBasedAlarm(Boolean systemStartTimeBasedAlarm)
	{
		this.systemStartTimeBasedAlarm = systemStartTimeBasedAlarm;
	}
	
	public String getCommand()
	{
		return command;
	}

	public void	setCommand(String command)
	{
		this.command = command;
	}
}