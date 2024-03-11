package com.sounaks.desktime;

import java.io.File;
import java.net.URISyntaxException;
import javax.swing.JList;

class ImageFileList extends JList<File>
{
	private	File dir;
	public String defaultImagesDir;

	public String getDefaultImagesDir()
	{
		return defaultImagesDir;
	}

	public void setDefaultImagesDir(String defaultImagesDir)
	{
		this.defaultImagesDir = defaultImagesDir;
	}

	public ImageFileList(File file)
	{
		if(file	== null && defaultImagesDir == null)
			dir	= new File(System.getProperty("user.home"));
		else if(file == null)
			dir = new File(defaultImagesDir);
		else {
			if (file.isDirectory()) dir = file;
			else dir = file.getParentFile();
		}
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

	public void	setDirectory(File file) throws URISyntaxException
	{
		if(file	== null) {
			dir	= new File(System.getProperty("user.home"));
		} else if(file.isDirectory()) {
			if (file.toString().equals(".")) {
				dir = new File(Thread.currentThread().getContextClassLoader().getResource("images/").getPath());
				if (!dir.exists())
					dir = new File(defaultImagesDir);
			} else {
				dir	= file;
			}
		} else {
			dir	= file.getParentFile();
		}
		if(dir.exists()) {
			File afile[] = dir.listFiles(new ImageFileFilter());
			setListData(afile);
			ensureIndexIsVisible(0);
		}
	}
}