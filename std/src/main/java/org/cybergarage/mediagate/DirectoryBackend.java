package org.cybergarage.mediagate;

import org.cybergarage.upnp.std.av.server.MediaServer;

public abstract class DirectoryBackend {
	private static final DirectoryBackend BACKEND = new PropertiesDirectoryBackend(); 
	
	public static DirectoryBackend getBackend() {
		return BACKEND;
	}

	public abstract void loadUserDirectories(MediaServer mediaServer);
	public abstract void saveUserDirectories(MediaServer mediaServer);
}
