package org.darkquest.gs.plugins;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.darkquest.config.Constants;
import org.darkquest.gs.plugins.lang.python.PythonScriptFactory;
import org.darkquest.gs.world.Shop;
import org.darkquest.gs.world.World;

/**
 * Initiates plug-ins that implements some listeners
 *
 * @author Peeter, design idea xEnt, revised by GORF
 */
public final class PluginHandler {

    public static PluginHandler pluginHandler = null;

    private final Map<String, Set<Object>> actionPlugins = new HashMap<String, Set<Object>>();
    private final Map<String, Set<Object>> executivePlugins = new HashMap<String, Set<Object>>();
    //private final Map<String, Class<?>> queue = new HashMap<String, Class<?>>();
    private final List<Class<?>> knownInterfaces = new ArrayList<Class<?>>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final PythonScriptFactory psf = new PythonScriptFactory(this);

    public static PluginHandler getPluginHandler() {
        if (pluginHandler == null)
            pluginHandler = new PluginHandler();
        return pluginHandler;
    }

    public void initPlugins() throws Exception {
        Map<String, Object> loadedPlugins = new HashMap<String, Object>();
        ArrayList<String> pluginLocations = new ArrayList<String>();

        pluginLocations.add("org.darkquest.gs.plugins.quests");
        pluginLocations.add("org.darkquest.gs.plugins.npcs");
        pluginLocations.add("org.darkquest.gs.plugins.minigames");
        pluginLocations.add("org.darkquest.gs.plugins.skills");
        pluginLocations.add("org.darkquest.gs.plugins.commands");
        pluginLocations.add("org.darkquest.gs.plugins.shops");
        pluginLocations.add("org.darkquest.gs.plugins.misc");

        // Java class loading
        for (Class<?> interfce : loadInterfaces("org.darkquest.gs.plugins.listeners.action")) {
            String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
            knownInterfaces.add(interfce);

            for (String packge : pluginLocations) {
                for (Class<?> plugin : loadClasses(packge, interfce)) {
                    Object instance = plugin.newInstance();

                    if(instance instanceof ShopInterface) {
                        ShopInterface it = (ShopInterface)instance;

                        for(Shop s : it.getShops()) {
                            World.getWorld().getShops().add(s);
                        }
                    }

                    if (loadedPlugins.containsKey(instance.getClass().getName())) {
                        instance = loadedPlugins.get(instance.getClass().getName());
                    } else {
                        loadedPlugins.put(instance.getClass().getName(), instance);
                        if (instance instanceof Quest) {
                            QuestInterface q = (QuestInterface) instance;
                            try {
                                World.getWorld().registerQuest(q);
                            } catch (Exception e) {
                                System.out.println("Error registering quest " + q.getQuestName());
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }

                    if (actionPlugins.containsKey(interfceName)) {
                        Set<Object> data = actionPlugins.get(interfceName);
                        data.add(instance);
                        actionPlugins.put(interfceName, data);
                    } else {
                        Set<Object> data = new HashSet<Object>();
                        data.add(instance);
                        actionPlugins.put(interfceName, data);
                    }
                }
            }
        }
        for (Class<?> interfce : loadInterfaces("org.darkquest.gs.plugins.listeners.executive")) {
            String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
            knownInterfaces.add(interfce);

            for (String packge : pluginLocations) {
                for (Class<?> plugin : loadClasses(packge, interfce)) {
                    Object instance = plugin.newInstance();

                    if (loadedPlugins.containsKey(instance.getClass().getName())) {
                        instance = loadedPlugins.get(instance.getClass().getName());
                    } else {
                        loadedPlugins.put(instance.getClass().getName(), instance);

                        if (Arrays.asList(instance.getClass().getInterfaces()).contains(Quest.class)) {
                            Quest q = (Quest) instance;
                            try {
                                World.getWorld().registerQuest((Quest) instance);
                            } catch (Exception e) {
                                System.out.println("Error registering quest " + q.getQuestName());
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }

                    if (executivePlugins.containsKey(interfceName)) {
                        Set<Object> data = executivePlugins.get(interfceName);
                        data.add(instance);
                        executivePlugins.put(interfceName, data);
                    } else {
                        Set<Object> data = new HashSet<Object>();
                        data.add(instance);
                        executivePlugins.put(interfceName, data);
                    }
                }
            }
        }
        // Python - Region Start
        loadPythonScripts();
    }

    public List<Class<?>> getKnownInterfaces() {
        return knownInterfaces;
    }

    public Map<String, Set<Object>> getExecutivePlugins() {
        return executivePlugins;
    }

    public Map<String, Set<Object>> getActionPlugins() {
        return actionPlugins;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public PythonScriptFactory getPythonScriptFactory() {
        return psf;
    }

    public void loadPythonScripts() throws Exception {
        File pyQuestsDir = new File(Constants.GameServer.SCRIPTS_DIR +  "/python/quests/");

        if(!pyQuestsDir.exists()) {
            try {
                throw new FileNotFoundException("Python quests directory not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        List<QuestInterface> pyQuests = psf.buildQuests(pyQuestsDir);

        File pyNpcsDir = new File(Constants.GameServer.SCRIPTS_DIR + "/python/npcs/");

        if(!pyNpcsDir.exists()) {
            try {
                throw new FileNotFoundException("Python npcs directory not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        List<NpcInterface> pyNpcs = psf.buildNpcs(pyNpcsDir);

        for(Class<?> interfce : knownInterfaces) {
            if(!pyQuests.isEmpty())
                psf.crossCheckQuests(pyQuests, interfce); // will check, register plugin
            if(!pyNpcs.isEmpty())
                psf.crossCheckNpcs(pyNpcs, interfce);
        }

        for(QuestInterface q : pyQuests) { // since we need to register them to the world...
            try {
                //System.out.println("[PYTHON]: Registering quest " + q.getQuestName());
                World.getWorld().registerQuest(q);
            } catch (Exception e) {
                System.out.println("Error registering quest " + q.getQuestName());
                e.printStackTrace();
            }
        }
    }

    public boolean blockDefaultAction(String interfce, final Object[] data) {
        return blockDefaultAction(interfce, data, true);
    }

    /**
     * TODO: Assign a default action to override (hard to determine WHO is default when more than one is available)
     * @param interfce
     * @param data
     * @param callAction
     * @return
     */

    public boolean blockDefaultAction(String interfce, final Object[] data, boolean callAction) {
        boolean shouldBlock = false;

        //queue.clear();
        if (executivePlugins.containsKey(interfce + "ExecutiveListener")) {
            for (Object c : executivePlugins.get(interfce + "ExecutiveListener")) {
                try {
                    Class<?>[] dataClasses = new Class<?>[data.length];
                    int i = 0;
                    for (Object o : data) {
                        dataClasses[i++] = o.getClass();
                    }
                    
                    Method m = c.getClass().getMethod("block" + interfce, dataClasses); // invoke
                    shouldBlock = (Boolean) m.invoke(c, data); // return where to block or not
                    
                    // we should block npc talktonpc if the ^ returns true
                    //if(shouldBlock) {
                        //System.out.println("Has blocking for this npc " + c.getClass().getName());
                       // queue.put(interfce, c.getClass());
                   // } // we can assume the rest is fair game
                } catch (Exception e) {
                    System.err.println("Exception at plugin handling: ");
                    e.printStackTrace();
                }
            }
        }

        if (callAction) // call action no matter what
            handleAction(interfce, data);
        //if(!queue.isEmpty())
        //    return true;
        return false; // will return false for now (action: block future reqs if found)
    }

    public void handleAction(String interfce, final Object[] data) {
        if (actionPlugins.containsKey(interfce + "Listener")) {
            for (final Object c : actionPlugins.get(interfce + "Listener")) { // change to hashset, we dont need multiple instances
                try {
                    Class<?>[] dataClasses = new Class<?>[data.length];
                    int i = 0;
                    for (Object o : data) {
                        dataClasses[i++] = o.getClass();
                    }

                    final Method m = c.getClass().getMethod("on" + interfce, dataClasses);
                    //boolean go = true;
                    //Class<?>[] interfcs = c.getClass().getInterfaces();
                    //System.out.println("Interface " + interfce);
                    //System.out.println("Class " + c.getClass().getName());
                    /*
                    if(queue.containsKey(interfce)) {
                        //System.out.println("Triggered from interface " + interfce);
                        for(Class<?> clz : queue.values()) {
                            if(clz.getName().equalsIgnoreCase(c.getClass().getName())) {
                                go = true;
                                break;
                            }
                        }
                    } else {
                    	go = true;
                    } */
                    
                    //if(go) {
                        //System.out.println("INVOKING " + c.getClass().getName());
                        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {

                            @Override
                            public Integer call() throws Exception {
                                try {
                                    m.invoke(c, data);
                                } catch (java.util.ConcurrentModificationException cme) {
                                    cme.printStackTrace();
                                }
                                return 1;
                            }
                        });
                        getExecutor().execute(task);
                   // }
                } catch (Exception e) {
                    System.err.println("Exception at plugin handling: ");
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<Class<?>> loadClasses(String pckgname) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ArrayList<File> directories = new ArrayList<File>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL res = resources.nextElement();
                if (res.getProtocol().equalsIgnoreCase("jar")) {
                    JarURLConnection conn = (JarURLConnection) res.openConnection();
                    JarFile jar = conn.getJarFile();
                    for (JarEntry e : Collections.list(jar.entries())) {
                        if (e.getName().startsWith(pckgname.replace('.', '/')) && e.getName().endsWith(".class") && !e.getName().contains("$")) {
                            String className = e.getName().replace("/", ".").substring(0, e.getName().length() - 6);
                            classes.add(Class.forName(className));
                        }
                    }
                } else
                    directories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }

        for (File directory : directories) {
            if (directory.exists()) {
                String[] files = directory.list();
                for (String file : files) {
                    if (file.endsWith(".class")) {
                        classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }

    private List<Class<?>> loadClasses(String thePackage, Class<?> theInterface) throws ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        for (Class<?> discovered : loadClasses(thePackage)) {
            if (Arrays.asList(discovered.getInterfaces()).contains(theInterface)) {
                classList.add(discovered);
            }
        }
        return classList;
    }

    private List<Class<?>> loadInterfaces(String thePackage) throws ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        for (Class<?> discovered : loadClasses(thePackage)) {
            if (discovered.isInterface()) {
                classList.add(discovered);
            }
        }
        return classList;
    }
}