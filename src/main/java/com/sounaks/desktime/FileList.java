package com.sounaks.desktime;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.swing.JList;

class FileList extends JList<File>
{
	private	File dir;

	public FileList(File file)
	{
		if(file	== null)
			dir	= new File(System.getProperty("user.home"));
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
				if (!dir.exists()) {
					CodeSource codeSrc = FileList.class.getProtectionDomain().getCodeSource();
					File sourceJar = new File(codeSrc.getLocation().toURI());
					File destDir = ExUtils.getJarExtractedDirectory(sourceJar);
					dir = new File(destDir, "images");
				}
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