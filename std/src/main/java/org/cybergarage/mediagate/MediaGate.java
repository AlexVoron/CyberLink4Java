/******************************************************************
*
*	MediaGate for CyberLink
*
*	Copyright (C) Satoshi Konno 2004
*
*	File : MediaServer.java
*
*	10/22/03
*		- first revision.
*
******************************************************************/

package org.cybergarage.mediagate;

import java.util.prefs.*;

import org.cybergarage.util.*;
import org.cybergarage.upnp.std.av.server.*;
//import org.cybergarage.upnp.std.av.object.*;
import org.cybergarage.upnp.std.av.server.directory.file.*;
import org.cybergarage.upnp.std.av.server.directory.mythtv.*;
import org.cybergarage.mediagate.frame.*;
import org.cybergarage.mediagate.frame.swing.*;
import org.cybergarage.upnp.std.av.server.object.format.*;

public class MediaGate
{
	private DirectoryBackend directoryBackend;
	
    ////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////

    /**** Mode Option ****/
    private final static int MODE_OPT_MASK = 0x00FF;
    private final static int FILESYS_MODE = 0x0000;
    private final static int MYTHTV_MODE = 0x0001;

    /**** Support Option ****/
    private final static int SUPPORT_OPT_MASK = 0xFF00;
    private final static int FLASH_SUPPORT = 0x0100;

    private final static String MYTHTV_OPT_STRING_OLD = "-mythtv";
    private final static String MYTHTV_OPT_STRING = "--mythtv";
    private final static String VERBOSE_OPT_STRING = "-v";
    private final static String FLASH_OPT_STRING = "--flash";
    private static final String CONSOLE_OPT_STRING = "-console";

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    public MediaGate(int option, boolean need_gui)
    {
        try {
        	directoryBackend = DirectoryBackend.getBackend();
//            mediaServ = new MediaServer(MediaServer.DESCRIPTION, ContentDirectory.SCPD, ConnectionManager.SCPD);
            mediaServ = new MediaServer(MediaServer.DESCRIPTION_FILE_NAME);
            setOption(option);

            switch (getModeOption()) {
            case FILESYS_MODE:
                {
                    mediaServ.addPlugIn(new ID3Format());
                    mediaServ.addPlugIn(new GIFFormat());
                    mediaServ.addPlugIn(new JPEGFormat());
                    mediaServ.addPlugIn(new PNGFormat());
                    mediaServ.addPlugIn(new MPEGFormat());
                    mediaServ.addPlugIn(new AVIFormat());
                    mediaServ.addPlugIn(new MKVFormat());
                    directoryBackend.loadUserDirectories(getMediaServer());
                }
                break;
            case MYTHTV_MODE:
                {
                    mediaServ.addPlugIn(new DefaultFormat());
                    MythDirectory mythDir = new MythDirectory();
                    mediaServ.addContentDirectory(mythDir);
                }
                break;
            }

            if ( need_gui ) {
                mediaFrame = new SwingFrame(this, isFileSystemMode());
            } else if ( Debug.isOn() ) {
                Debug.message("Starting in console mode");
                for (int n=0; n < mediaServ.getNContentDirectories(); n++) {
                    Directory dir = mediaServ.getContentDirectory(n);
                    Debug.message("serving content dir: " + dir.getFriendlyName());
                    int nItems = dir.getNContentNodes();
                    for (int x = 0; x < nItems; x++) {
                        Debug.message("\n" + dir.getNode(x));
                    }
                }

            }
        }
        catch (Exception e) {
            Debug.warning(e);
        }
    }

    ////////////////////////////////////////////////
    // Mode
    ////////////////////////////////////////////////

    private int option;

    private void setOption(int value)
    {
        option = value;
    }

    private int getOption()
    {
        return option;
    }

    private int getModeOption()
    {
        return (option & MODE_OPT_MASK);
    }

    private boolean isFileSystemMode()
    {
        return ((getModeOption() & MODE_OPT_MASK) == FILESYS_MODE) ? true : false;
    }

    private int getSupportOption()
    {
        return (option & SUPPORT_OPT_MASK);
    }

    ////////////////////////////////////////////////
    // MediaServer
    ////////////////////////////////////////////////

    private MediaServer mediaServ;

    public MediaServer getMediaServer()
    {
        return mediaServ;
    }

    public ContentDirectory getContentDirectory()
    {
        return mediaServ.getContentDirectory();
    }

    ////////////////////////////////////////////////
    // MediaServer
    ////////////////////////////////////////////////

    private MediaFrame mediaFrame;

    public MediaFrame getMediaFrame()
    {
        return mediaFrame;
    }

    ////////////////////////////////////////////////
    // start/stop
    ////////////////////////////////////////////////

    public void start()
    {
        getMediaServer().start();
    }

    public void stop()
    {
        getMediaServer().stop();
        if (getOption() == FILESYS_MODE)
        	directoryBackend.saveUserDirectories(getMediaServer());
    }

    ////////////////////////////////////////////////
    // Debug
    ////////////////////////////////////////////////

    public static void debug(MediaGate mgate)
    {
        /*
          String sortCriteria = "+dc:date,+dc:title,+upnp:class";
          mgate.getContentDirectory().sortContentNodeList(new ContentNodeList(), sortCriteria);
          */
    }

    ////////////////////////////////////////////////
    // main
    ////////////////////////////////////////////////

    public static void main(String args[])
    {
        Debug.off();

        boolean need_gui = true;
        int mode = FILESYS_MODE;
        Debug.message("args = " + args.length);
        for (int n=0; n<args.length; n++) {
            Debug.message("  [" + n + "] = " + args[n]);
            if (MYTHTV_OPT_STRING.compareTo(args[n]) == 0)
                mode = MYTHTV_MODE;
            if (MYTHTV_OPT_STRING_OLD.compareTo(args[n]) == 0)
                mode = MYTHTV_MODE;
            if (VERBOSE_OPT_STRING.compareTo(args[n]) == 0)
                Debug.on();
            if (CONSOLE_OPT_STRING.compareTo(args[n]) == 0)
                need_gui = false;
        }

        MediaGate mediaGate = new MediaGate(mode, need_gui);
        debug(mediaGate);
        mediaGate.start();
    }

}

