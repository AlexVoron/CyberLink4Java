package org.cybergarage.mediagate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.Directory;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.util.Debug;

public class PropertiesDirectoryBackend extends DirectoryBackend {
	private static final String PROPERTIES_FILENAME = "MediaServerDirectories.properties";

	@Override
	public void loadUserDirectories(MediaServer mediaServer) {
	    try {
//	        Preferences dirPref = getUserDirectoryPreferences();
	    	Properties properties = new Properties();
	    	properties.loadFromXML(new FileInputStream(PROPERTIES_FILENAME + ".xml"));

	    	Set<Object> dirName = properties.keySet();
	        int dirCnt = dirName.size();
	        Debug.message("Loadin Directories (" + dirCnt + ") ....");
	        for (Object object : dirName) {
	            String name = (String) object;
	            String path = (String) properties.get(object);
	            FileDirectory fileDir = new FileDirectory(name, path);
	            mediaServer.addContentDirectory(fileDir);
	            Debug.message(name + "=" + path);
			}
	    }
	    catch (Exception e) {
	        Debug.warning(e);
	    }
	}

	@Override
	public void saveUserDirectories(MediaServer mediaServer) {
	    ContentDirectory conDir = mediaServer.getContentDirectory();
	    try {
	    	Properties properties = new Properties();
	        int dirCnt = conDir.getNDirectories();
	        for (int n=0; n<dirCnt; n++) {
	            Directory dir = conDir.getDirectory(n);
	            if (!(dir instanceof FileDirectory))
	                continue;
	            FileDirectory fileDir = (FileDirectory)dir;
	            properties.put(fileDir.getFriendlyName(), fileDir.getPath());
	        }
	        properties.storeToXML(new FileOutputStream(PROPERTIES_FILENAME + ".xml"), "Media server directories");
	    }
	    catch (Exception e) {
	        Debug.warning(e);
	    }
	}
}
