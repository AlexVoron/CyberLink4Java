/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003-2004
*
*	File : MPEGPlugIn.java
*
*	Revision:
*
*	02/02/04
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server.object.format;

import java.io.*;

import org.cybergarage.xml.*;
import org.cybergarage.util.*;
import org.cybergarage.upnp.std.av.server.object.*;
import org.cybergarage.upnp.std.av.server.object.item.*;

public class AVIFormat implements Format, FormatObject
{
	////////////////////////////////////////////////
	// Member
	////////////////////////////////////////////////

	private File aviFile;
		
	////////////////////////////////////////////////
	// Constroctor
	////////////////////////////////////////////////
	
	public AVIFormat()
	{
	}
	
	public AVIFormat(File file)
	{
		aviFile = file;
	}

	////////////////////////////////////////////////
	// Abstract Methods
	////////////////////////////////////////////////
	
	public boolean equals(File file)
	{
		String ext = Header.getSuffix(file);
		if (ext == null)
			return false;
		if (ext.startsWith("avi"))
			return true;
		return false;
	}
	
	public FormatObject createObject(File file)
	{
		return new AVIFormat(file);
	}
	
	public String getMimeType()
	{
		return "video/msvideo";
	}

	public String getMediaClass()
	{
		return "object.item.videoItem.movie";
	}
	
	public AttributeList getAttributeList()
	{
		AttributeList attrList = new AttributeList();
		
		try {
			// Size 
			long fsize = aviFile.length();
			Attribute sizeStr = new Attribute(ItemNode.SIZE, Long.toString(fsize));
			attrList.add(sizeStr);
		}
		catch (Exception e) {
			Debug.warning(e);
		}
		
		return attrList;	
	}
	
	public String getTitle()
	{
		String fname = aviFile.getName();
		int idx = fname.lastIndexOf(".");
		if (idx < 0)
			return "";
		String title = fname.substring(0, idx);
		return title;
	}
	
	public String getCreator()
	{
		return "";
	}
}

