package com.sounaks.desktime;

import java.io.File;
import java.io.FileFilter;

class ImageFileFilter implements FileFilter {

	ImageFileFilter() {
	}

	/**
	 * Accept jpg, png and hpe images as file name filter in background pane display
	 * list.
	 * 
	 * @param file Is the file to be checked with conditional filter.
	 * @return boolean Is either true or false based on filter match.
	 */
	public boolean accept(File file) {
		String s = file.getName();
		if (s.endsWith("jpg") || s.endsWith("JPG") ||
				s.endsWith("png") || s.endsWith("PNG") ||
				s.endsWith("jpeg") || s.endsWith("JPEG") ||
				s.endsWith("jpe") || s.endsWith("JPE")) {
			return !s.substring(0, s.lastIndexOf('.')).endsWith("-icon");
		} else
			return false;
	}
}