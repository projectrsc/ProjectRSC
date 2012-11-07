package org.darkquest.gs.plugins;

import java.io.File;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.darkquest.gs.world.Shop;
import org.darkquest.gs.world.World;

/**
 * Initiates plug-ins that implements some listeners
 *
 * @author Peeter, design idea xEnt
 */
public final class PluginHandler {

	public static PluginHandler pluginHandler = null;

	public static PluginHandler getPluginHandler() {
		if (pluginHandler == null)
			pluginHandler = new PluginHandler();
		return pluginHandler;
	}

	private final Map<String, List<Object>> plugins_action = new HashMap<String, List<Object>>();
	
	public void initShops() {
		
	}

	private final Map<String, List<Object>> plugins_executive = new HashMap<String, List<Object>>();

	private final ExecutorService executor = Executors.newCachedThreadPool();

	public void initPlugins() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String, Object> loadedPlugins = new HashMap<String, Object>();

		ArrayList<String> pluginLocations = new ArrayList<String>();
		pluginLocations.add("org.darkquest.gs.plugins.quests");
		pluginLocations.add("org.darkquest.gs.plugins.npcs");
		pluginLocations.add("org.darkquest.gs.plugins.minigames");
		pluginLocations.add("org.darkquest.gs.plugins.skills");
		pluginLocations.add("org.darkquest.gs.plugins.commands");
		pluginLocations.add("org.darkquest.gs.plugins.shops");
		pluginLocations.add("org.darkquest.gs.plugins.misc");

		for (Class<?> interfce : loadInterfaces("org.darkquest.gs.plugins.listeners.action")) {
			String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
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
					if (plugins_action.containsKey(interfceName)) {
						List<Object> data = plugins_action.get(interfceName);
						data.add(instance);
						plugins_action.put(interfceName, data);
					} else {
						List<Object> data = new ArrayList<Object>();
						data.add(instance);
						plugins_action.put(interfceName, data);
					}
				}
			}
		}
		for (Class<?> interfce : loadInterfaces("org.darkquest.gs.plugins.listeners.executive")) {
			String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
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

					if (plugins_executive.containsKey(interfceName)) {
						List<Object> data = plugins_executive.get(interfceName);
						data.add(instance);
						plugins_executive.put(interfceName, data);
					} else {
						List<Object> data = new ArrayList<Object>();
						data.add(instance);
						plugins_executive.put(interfceName, data);
					}
				}
			}
		}
	}

	public boolean blockDefaultAction(String interfce, final Object[] data) {
		return blockDefaultAction(interfce, data, true);
	}

	public boolean blockDefaultAction(String interfce, final Object[] data, boolean callAction) {
		boolean found = false;
		if (plugins_executive.containsKey(interfce + "ExecutiveListener")) {
			for (Object c : plugins_executive.get(interfce + "ExecutiveListener")) {
				try {
					Class<?>[] dataClasses = new Class<?>[data.length];
					int i = 0;
					for (Object o : data) {
						dataClasses[i++] = o.getClass();
					}
					Method m = c.getClass().getMethod("block" + interfce, dataClasses);
					found = (Boolean) m.invoke(c, data);

					if (found) {
						if (callAction) {
							this.handleAction(interfce, data);
						}
						return true;
					}
				} catch (Exception e) {
					System.err.println("Exception at plugin handling: ");
					e.printStackTrace();
				}
			}
		}
		if (callAction) {
			this.handleAction(interfce, data);
		}
		return found;
	}

	public void handleAction(String interfce, final Object[] data) {
		if (plugins_action.containsKey(interfce + "Listener")) {
			for (final Object c : plugins_action.get(interfce + "Listener")) {
				try {
					Class<?>[] dataClasses = new Class<?>[data.length];
					int i = 0;
					for (Object o : data) {
						dataClasses[i++] = o.getClass();
					}
					final Method m = c.getClass().getMethod("on" + interfce, dataClasses);
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
					this.getExecutor().execute(task);
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

	public ExecutorService getExecutor() {
		return executor;
	}

}
