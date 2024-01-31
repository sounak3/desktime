package com.sounaks.desktime;

import java.io.File;
import javax.swing.JList;

class FileList extends JList<File>
{
	private	File dir;

	public FileList(File file)
	{
		if(file	== null)
			dir	= new File(System.getProperty("user.home"));
		else
		if(file.isDirectory())
			dir	= file;
		else
			dir	= file.getParentFile();
		setCellRenderer(new	FileCellRenderer());
		if(dir.exists())
		{
			File afile[] = dir.listFiles(new ImageFileFilter());
			setListData(afile);
		}
	}

	public File	getDirectory()
	{
		return dir;
	}

	public void	setDirectory(File file)
	{
		if(file	== null)
			dir	= new File(System.getProperty("user.home"));
		else if(file.isDirectory())
		{
			if (file.toString().equals("."))
			{
				dir = new File(Thread.currentThread().getContextClassLoader().getResource("images/").getPath());
			}
			else
			{
				dir	= file;
			}
		}
		else
			dir	= file.getParentFile();
		if(dir.exists())
		{
			File afile[] = dir.listFiles(new ImageFileFilter());
			setListData(afile);
			ensureIndexIsVisible(0);
		}
	}
}