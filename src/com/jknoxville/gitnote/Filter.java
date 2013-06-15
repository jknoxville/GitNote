package com.jknoxville.gitnote;

import java.io.File;
import java.io.FilenameFilter;

public class Filter implements FilenameFilter {
	
	String name;
	
	public Filter(String name) {
		this.name = name;
	}

	@Override
	public boolean accept(File arg0, String arg1) {
		return arg1.equals(name);
	}

}
