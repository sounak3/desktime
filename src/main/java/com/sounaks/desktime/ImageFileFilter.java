package com.sounaks.desktime;

import java.io.File;
import java.io.FileFilter;

class ImageFileFilter
	implements FileFilter
{

	ImageFileFilter()
	{
	}

	public boolean accept(File file)
	{
		String s = file.getName();
		return s.endsWith("jpg") ||	s.endsWith("gif") || s.endsWith("JPG") || s.endsWith("GIF")	|| s.endsWith("jpeg") || s.endsWith("JPEG")	|| s.endsWith("jpe") ||	s.endsWith("JPE");
	}
}