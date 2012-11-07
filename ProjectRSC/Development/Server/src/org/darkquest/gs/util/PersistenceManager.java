package org.darkquest.gs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.darkquest.config.Constants;

import com.thoughtworks.xstream.XStream;

public final class PersistenceManager {

    private static final XStream xstream = new XStream();

    static {
        setupAliases();
    }

    public static Object load(String filename) {
        try {
            InputStream is = new FileInputStream(new File(Constants.GameServer.CONFIG_DIR, filename));
            if (filename.endsWith(".gz")) {
                is = new GZIPInputStream(is);
            }
            Object rv = xstream.fromXML(is);
            return rv;
        } catch (IOException ioe) {
            Logger.error(ioe);
        }
        return null;
    }

    public static void setupAliases() {
        try {
            Properties aliases = new Properties();
            FileInputStream fis = new FileInputStream(new File(Constants.GameServer.CONFIG_DIR, "aliases.xml"));
            aliases.loadFromXML(fis);
            for (Enumeration<?> e = aliases.propertyNames(); e.hasMoreElements(); ) {
                String alias = (String) e.nextElement();
                Class<?> c = Class.forName((String) aliases.get(alias));
                xstream.alias(alias, c);
            }
        } catch (Exception ioe) {
            Logger.error(ioe);
        }
    }

    public static void write(String filename, Object o) {
        try {
            OutputStream os = new FileOutputStream(new File(Constants.GameServer.CONFIG_DIR, filename));
            if (filename.endsWith(".gz")) {
                os = new GZIPOutputStream(os);
            }
            xstream.toXML(o, os);
        } catch (IOException ioe) {
            Logger.error(ioe);
        }
    }

    //TODO remove meeee -hikilaka
    /**
    public static Map<Integer, PacketHandler> loadPacketHandlers() {
        Map<Integer, PacketHandler> handlerMap = new HashMap<Integer, PacketHandler>();
        PacketHandlerDef[] handlerDefs = (PacketHandlerDef[]) PersistenceManager.load("PacketHandlers.xml");

        for (PacketHandlerDef handlerDef : handlerDefs) {
            try {
                String className = handlerDef.getClassName();
                Class<?> c = Class.forName(className);
                if (c != null) {
                    PacketHandler handler = (PacketHandler) c.newInstance();
                    for (int packetID : handlerDef.getAssociatedPackets()) {
                        handlerMap.put(packetID, handler);
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }

        return handlerMap;
    }
    */
}
