package com.sounaks.desktime;

import java.io.*;
import java.util.*;

public class TimeBean implements Serializable
{
	public  static final long serialVersionUID = 913566255917L;
	private String name;
	private Date alarmTriggerTime;
	private Date nextAlarmTriggerTime;
	private Integer alarmRepeatInterval;
	private Integer repeatMultiple;
	private Integer alarmExecutionOutputType;
	private String command;
	private Boolean systemStartTimeBasedAlarm;

	public TimeBean()
	{
		this.name                      = "";
		this.alarmTriggerTime          = new Date();
		this.nextAlarmTriggerTime      = new Date();
		this.alarmRepeatInterval       = 0;
		this.repeatMultiple            = 1;
		this.alarmExecutionOutputType  = 4;
		this.command                   = "";
		this.systemStartTimeBasedAlarm = false;
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
			gcal.add(Calendar.MINUTE, repeatMultiple);
			return gcal.getTime();
			
			case 2:
			gcal.add(Calendar.HOUR_OF_DAY, repeatMultiple);
			return gcal.getTime();
			
			case 3:
			gcal.add(Calendar.DATE, repeatMultiple);
			return gcal.getTime();
			
			case 4:
			gcal.add(Calendar.WEEK_OF_MONTH, repeatMultiple);
			return gcal.getTime();
			
			case 6:
			gcal.add(Calendar.YEAR, repeatMultiple);
			return gcal.getTime();
			
			case 7:
			gcal.add(Calendar.MONTH, repeatMultiple);
			return gcal.getTime();
			
			case 8:
			int w  = gcal.get(Calendar.WEEK_OF_MONTH);
			int wd = gcal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
			gcal.add(Calendar.MONTH, repeatMultiple);
			gcal.set(Calendar.WEEK_OF_MONTH,w);
			gcal.set(Calendar.DAY_OF_WEEK_IN_MONTH,wd);
			return gcal.getTime();
		}
		return gcal.getTime();
	}
	
	public Date getNextAlarmTriggerTime()
	{
		Date temp, now = new Date();
		if (alarmTriggerTime.before(now) && alarmRepeatInterval != 0)
			temp = oneStepPropell(alarmTriggerTime);
		else
			temp = alarmTriggerTime;

		if (alarmRepeatInterval == 0)
			nextAlarmTriggerTime = alarmTriggerTime;

		while(alarmTriggerTime.before(temp) && temp.before(now) && !temp.equals(alarmTriggerTime) && alarmRepeatInterval != 0)
		{
			temp = oneStepPropell(temp);
		}
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(temp);
		gcal.add(Calendar.SECOND, -1);
		temp = gcal.getTime();
		if (temp.after(nextAlarmTriggerTime)) 
		{
			this.nextAlarmTriggerTime = temp; //return the newer;
		}
		return nextAlarmTriggerTime;
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

	public String toString()
	{
		return name + ": " + alarmTriggerTime.toString() + ", " + nextAlarmTriggerTime.toString(); 
	}

	public Integer getRepeatMultiple()
	{
		return repeatMultiple;
	}

	public void setRepeatMultiple(Integer repeatMultiple)
	{
		this.repeatMultiple = repeatMultiple;
	}
}