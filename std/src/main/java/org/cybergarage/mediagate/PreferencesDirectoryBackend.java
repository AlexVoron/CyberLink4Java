package org.cybergarage.mediagate;

import java.util.prefs.Preferences;

import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.Directory;
import org.cybergarage.upnp.std.av.server.MediaServer;
import org.cybergarage.upnp.std.av.server.directory.file.FileDirectory;
import org.cybergarage.util.Debug;

public class PreferencesDirectoryBackend extends DirectoryBackend {
	protected final static String DIRECTORY_PREFS_NAME = "directory";
	private Preferences prefs = null;

	PreferencesDirectoryBackend() {}

	private Preferences getUserPreferences() {
	    if (prefs == null)
	        prefs =	Preferences.userNodeForPackage(this.getClass());
	    return prefs;
	}

	private Preferences getUserDirectoryPreferences() {
	    return getUserPreferences().node(DIRECTORY_PREFS_NAME);
	}

	private void clearUserDirectoryPreferences() {
	    try {
	        Preferences dirPref = getUserDirectoryPreferences();
	        String dirName[] = dirPref.keys();
	        int dirCnt = dirName.length;
	        for (int n=0; n<dirCnt; n++)
	            dirPref.remove(dirName[n]);
	    }
	    catch (Exception e) {
	        Debug.warning(e);
	    }
	}

	@Override
	public void loadUserDirectories(MediaServer mediaServer) {
	    try {
	        Preferences dirPref = getUserDirectoryPreferences();
	        String dirName[] = dirPref.keys();
	        int dirCnt = dirName.length;
	        Debug.message("Loadin Directories (" + dirCnt + ") ....");
	        for (int n=0; n<dirCnt; n++) {
	            String name = dirName[n];
	            String path = dirPref.get(name, "");
	            FileDirectory fileDir = new FileDirectory(name, path);
	            mediaServer.addContentDirectory(fileDir);
	            Debug.message("[" + n + "] = " + name + "," + path);
	        }
	    }
	    catch (Exception e) {
	        Debug.warning(e);
	    }
	}

	@Override
	public void saveUserDirectories(MediaServer mediaServer) {
	    clearUserDirectoryPreferences();
	
	    ContentDirectory conDir = mediaServer.getContentDirectory();
	    try {
	        Preferences dirPref = getUserDirectoryPreferences();
	        int dirCnt = conDir.getNDirectories();
	        for (int n=0; n<dirCnt; n++) {
	            Directory dir = conDir.getDirectory(n);
	            if (!(dir instanceof FileDirectory))
	                continue;
	            FileDirectory fileDir = (FileDirectory)dir;
	            dirPref.put(fileDir.getFriendlyName(), fileDir.getPath());
	        }
	    }
	    catch (Exception e) {
	        Debug.warning(e);
	    }
	};
}
