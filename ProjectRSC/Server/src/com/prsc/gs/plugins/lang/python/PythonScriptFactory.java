package com.prsc.gs.plugins.lang.python;

import java.io.File;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PlugInterface;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.Quest;
import com.prsc.gs.plugins.QuestInterface;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.lang.GenericFactory;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.registrar.PortRegistrar;
import com.prsc.gs.registrar.impl.PacketHandlers;

public class PythonScriptFactory implements GenericFactory {
	
	private final PythonInterpreter interpreter = new PythonInterpreter();
	private List<Object> backedScripts; 
	private List<String[]> errors;
	
	public PythonScriptFactory() {
		this.backedScripts = new ArrayList<Object>();
		this.errors = new ArrayList<String[]>();
	}
	
	public List<String[]> getErrorLog() {
		return errors;
	}
	
	public boolean canReload() { 
		if(backedScripts.isEmpty()) {
			return false;
		}
		
		for(Object o : backedScripts) {
			if(o instanceof PacketHandler) {
				PacketHandler handler = (PacketHandler) o;
				PortRegistrar.lookup(PacketHandlers.class).unregisterGameHandler(handler);
			}
		}
		
		Map<String, Set<Object>> exePlugs =  PluginHandler.getPluginHandler().getExecutivePlugins();
		Map<String, Set<Object>> actionPlugs =  PluginHandler.getPluginHandler().getActionPlugins();
		
		for(Class<?> interfce : PluginHandler.getPluginHandler().getKnownInterfaces()) {
			String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);

			if(exePlugs.containsKey(interfceName)) {
				Set<Object> exeData = exePlugs.get(interfceName);
				Iterator<Object> plugs = exeData.iterator();
				
				while(plugs.hasNext()) {
					Object plug = plugs.next();
					//System.out.println("Binded to " + plug.getClass().getName());
					String classNme = plug.getClass().getName();
					for(Object script : backedScripts) {
						String scriptNme = script.getClass().getName();
						if(classNme.equals(scriptNme)) {
							//System.out.println("Removed " + plug.getClass().getName());
							if(plug instanceof QuestInterface) {
								World.getWorld().unregisterQuest((QuestInterface)plug); // need to specify
							}
							plugs.remove();
						}
					} 
				} 
			}
			
			if(actionPlugs.containsKey(interfceName)) {
				Set<Object> actionData = actionPlugs.get(interfceName);
				Iterator<Object> plugs = actionData.iterator();
				
				while(plugs.hasNext()) {
					Object plug = plugs.next();
					//System.out.println("Binded to " + plug.getClass().getName());
					String classNme = plug.getClass().getName();
					for(Object script : backedScripts) {
						String scriptNme = script.getClass().getName();
						if(classNme.equals(scriptNme)) {
							//System.out.println("Removed " + plug.getClass().getName());
							if(plug instanceof QuestInterface) {
								World.getWorld().unregisterQuest((QuestInterface)plug); // need to specify
							}
							plugs.remove();
						}
					} 
				}
			}
		}
		backedScripts.clear();
		return true;
	}
	
	public void crossCheckQuests(List<QuestInterface> quests, Class<?> interfce) {
		try {
			for(QuestInterface q : quests) { // for executive
				List<Class<?>> interfces = Arrays.asList(q.getClass().getInterfaces());
				if(interfces != null && interfces.contains(interfce)) {
					String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
					if(PluginHandler.getPluginHandler().getExecutivePlugins().containsKey(interfceName)) {
						//System.out.println("Adding to executive plugs " + interfce.getSimpleName());
						Set<Object> data = PluginHandler.getPluginHandler().getExecutivePlugins().get(interfceName);
						data.add(q);
						PluginHandler.getPluginHandler().getExecutivePlugins().put(interfceName, data);
					} else if(interfceName.endsWith("ExecutiveListener")) {
						//System.out.println("(EXEC) We should add " + interfceName);
						Set<Object> data = new HashSet<Object>();
						data.add(q);
						PluginHandler.getPluginHandler().getExecutivePlugins().put(interfceName, data);
					}
				}
			}
			for(QuestInterface q : quests) { // for action
				List<Class<?>> interfces = Arrays.asList(q.getClass().getInterfaces());
				if(interfces != null && interfces.contains(interfce)) {
					String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
					if(PluginHandler.getPluginHandler().getActionPlugins().containsKey(interfceName)) {
						//System.out.println("Adding to action plugs " + interfce.getSimpleName());
						Set<Object> data = PluginHandler.getPluginHandler().getActionPlugins().get(interfceName);
		        		data.add(q);
		        		PluginHandler.getPluginHandler().getActionPlugins().put(interfceName, data);
					} else if(!interfceName.endsWith("ExecutiveListener")) {
						//System.out.println("(ACTION) We should add " + interfceName);
						Set<Object> data = new HashSet<Object>();
                    	data.add(q);
                    	PluginHandler.getPluginHandler().getActionPlugins().put(interfceName, data);
					}
				}
			}
		} catch(Exception e) {
			//e.printStackTrace()
		}
	}
	
	public void crossCheckPlugs(List<PlugInterface> handlers, Class<?> interfce) {
		try {
			for(PlugInterface p : handlers) { // for executive
				List<Class<?>> interfces = Arrays.asList(p.getClass().getInterfaces());
				if(interfces != null && interfces.contains(interfce)) {
					String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
					if(PluginHandler.getPluginHandler().getExecutivePlugins().containsKey(interfceName)) {
						//System.out.println("Adding to executive plugs " + interfce.getSimpleName());
						Set<Object> data = PluginHandler.getPluginHandler().getExecutivePlugins().get(interfceName);
						data.add(p);
						PluginHandler.getPluginHandler().getExecutivePlugins().put(interfceName, data);
					} else if(interfceName.endsWith("ExecutiveListener")) {
						//System.out.println("(EXEC) We should add " + interfceName);
						Set<Object> data = new HashSet<Object>();
						data.add(p);
						PluginHandler.getPluginHandler().getExecutivePlugins().put(interfceName, data);
					}
				}
			}
			for(PlugInterface p : handlers) { // for action
				List<Class<?>> interfces = Arrays.asList(p.getClass().getInterfaces());
				if(interfces != null && interfces.contains(interfce)) {
					String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
					if(PluginHandler.getPluginHandler().getActionPlugins().containsKey(interfceName)) {
						//System.out.println("Adding to action plugs " + interfce.getSimpleName());
						Set<Object> data = PluginHandler.getPluginHandler().getActionPlugins().get(interfceName);
		        		data.add(p);
		        		PluginHandler.getPluginHandler().getActionPlugins().put(interfceName, data);
					} else if(!interfceName.endsWith("ExecutiveListener")) {
						//System.out.println("(ACTION) We should add " + interfceName);
						Set<Object> data = new HashSet<Object>();
                    	data.add(p);
                    	PluginHandler.getPluginHandler().getActionPlugins().put(interfceName, data);
					}
				}
			}
		} catch(Exception e) {
			//
		}
	} 
	
	@Override
    public List<QuestInterface> buildQuests(File directory) throws Exception {
		List<QuestInterface> loadedPythonQuests = new ArrayList<QuestInterface>();
		PyObject pythonClass = null; 
		for(File file : directory.listFiles()) {
			if(file.getAbsoluteFile().isFile() && file.getName().endsWith(".py")) {
				String pyFile = directory.getAbsolutePath() + File.separatorChar + file.getName();
				if(pythonClass == null) {
					PyObject pObj = null;
					QuestInterface q = null;
					try {
						interpreter.execfile(pyFile);
						pythonClass = interpreter.get(file.getName().replace(".py", "").trim());
						pObj = pythonClass.__call__();
						q = (QuestInterface) pObj.__tojava__(QuestInterface.class);
						pythonClass = null;
					} catch(PyException py) {
						errors.add(ReadableError.toReadable(py.toString()));
						py.printStackTrace();
					} finally {
						loadedPythonQuests.add((Quest)q);
						backedScripts.add(q);
					}
				} else {
					throw new Exception("[PYTHON]: Syntax error found, unable to convert " + file.getName().replace(".py", "").trim());
				}
			}
		}
		return loadedPythonQuests; 
    }

	@Override
	public List<ShopInterface> buildShops(File pyShopsDir) {
		// TODO Auto-generated method stub
		return null; 
	}

	@Override
	public List<PlugInterface> buildPlugs(File pyNpcsDir) throws Exception {
		List<PlugInterface> loadedPythonNpcs = new ArrayList<PlugInterface>();
		PyObject pythonClass = null; 
		for(File file : pyNpcsDir.listFiles()) {
			if(file.getAbsoluteFile().isFile() && file.getName().endsWith(".py")) {
				String pyFile = pyNpcsDir.getAbsolutePath() + File.separatorChar + file.getName();
				if(pythonClass == null) {
					PyObject pObj = null;
					PlugInterface handler = null;
					try {
						interpreter.execfile(pyFile);
						pythonClass = interpreter.get(file.getName().replace(".py", "").trim());
						pObj = pythonClass.__call__();
						handler = (PlugInterface) pObj.__tojava__(PlugInterface.class);
						pythonClass = null;
					} catch(PyException py) {
						errors.add(ReadableError.toReadable(py.toString()));
						py.printStackTrace();
					} finally {
						loadedPythonNpcs.add(handler);
						backedScripts.add(handler);
					}
				} else {
					throw new Exception("Syntax error found, unable to convert " + file.getName().replace(".py", "").trim());
				}
			}
		}
		return loadedPythonNpcs;
	}

	@Override
	public List<PacketHandler> buildHandler(File handlerDir) throws Exception {
		List<PacketHandler> loadedPythonHandlers = new ArrayList<PacketHandler>();
		PyObject pythonClass = null; 
		for(File file : handlerDir.listFiles()) {
			if(file.getAbsoluteFile().isFile() && file.getName().endsWith(".py")) {
				String pyFile = handlerDir.getAbsolutePath() + File.separatorChar + file.getName();
				if(pythonClass == null) {
					PyObject pObj = null;
					PacketHandler handler = null;
					try {
						interpreter.execfile(pyFile);
						pythonClass = interpreter.get(file.getName().replace(".py", "").trim());
						pObj = pythonClass.__call__();
						handler = (PacketHandler) pObj.__tojava__(PacketHandler.class);
						pythonClass = null;
					} catch(PyException py) {
						errors.add(ReadableError.toReadable(py.toString()));
						py.printStackTrace();
					} finally {
						loadedPythonHandlers.add(handler);
						backedScripts.add(handler);
					}
				} else {
					throw new Exception("Syntax error found, unable to convert " + file.getName().replace(".py", "").trim());
				}
			}
		}
		return loadedPythonHandlers;
	}
}
