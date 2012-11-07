package org.darkquest.gs.plugins.lang.python;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darkquest.gs.plugins.NpcInterface;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.QuestInterface;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.lang.GenericFactory;
import org.darkquest.gs.world.World;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class PythonScriptFactory implements GenericFactory {
	
	protected PluginHandler handler;
	protected PythonInterpreter interpreter;
	protected PyObject pythonClass;
	protected List<Object> backedScripts; 
	
	public PythonScriptFactory(PluginHandler handler) {
		this.handler = handler;
		this.interpreter = new PythonInterpreter();
		this.backedScripts = new ArrayList<Object>();
	}
	
	public boolean canReload() { 
		if(backedScripts.isEmpty()) {
			return false;
		}
		
		Map<String, Set<Object>> exePlugs =  handler.getExecutivePlugins();
		Map<String, Set<Object>> actionPlugs =  handler.getActionPlugins();
		
		for(Class<?> interfce : handler.getKnownInterfaces()) {
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
		for(QuestInterface q : quests) {
			for(Class<?> cl : q.getClass().getInterfaces()) {
				String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
				if(cl.getName().equals(interfce.getName())){
					if(handler.getExecutivePlugins().containsKey(interfceName)) {
						//System.out.println("Adding to executive plugs " + interfce.getSimpleName());
						Set<Object> data = handler.getExecutivePlugins().get(interfceName);
			        	data.add(q);
			        	handler.getExecutivePlugins().put(interfceName, data);
					}
					if(handler.getActionPlugins().containsKey(interfceName)) {
						//System.out.println("Adding to action plugs" + interfce.getSimpleName());
						Set<Object> data = handler.getActionPlugins().get(interfceName);
			        	data.add(q);
			        	handler.getActionPlugins().put(interfceName, data);
					}
				}
			}
		}
	}
	
	/**
	 * TODO: Remove redundant call (should be able to cross check against all entities in one go)
	 * @param npcHandlers
	 * @param interfce
	 */
	
	public void crossCheckNpcs(List<NpcInterface> npcHandlers, Class<?> interfce) {
		for(NpcInterface npcHandler : npcHandlers) {
			for(Class<?> cl : npcHandler.getClass().getInterfaces()) {
				String interfceName = interfce.getName().substring(interfce.getName().lastIndexOf(".") + 1);
				if(cl.getName().equals(interfce.getName())){
					if(handler.getExecutivePlugins().containsKey(interfceName)) {
						//System.out.println("Adding to executive plugs " + interfce.getSimpleName());
						Set<Object> data = handler.getExecutivePlugins().get(interfceName);
			        	data.add(npcHandler);
			        	handler.getExecutivePlugins().put(interfceName, data);
					}
					if(handler.getActionPlugins().containsKey(interfceName)) {
						//System.out.println("Adding to action plugs" + interfce.getSimpleName());
						Set<Object> data = handler.getActionPlugins().get(interfceName);
			        	data.add(npcHandler);
			        	handler.getActionPlugins().put(interfceName, data);
					}
				}
			}
		}
	} 
	
	@Override
    public List<QuestInterface> buildQuests(File directory) throws Exception {
		List<QuestInterface> loadedPythonQuests = new ArrayList<QuestInterface>();
		
		for(File file : directory.listFiles()) {
			if(file.getAbsoluteFile().isFile() && file.getName().endsWith(".py")) {
				String pyFile = directory.getAbsolutePath() + File.separatorChar + file.getName();
				interpreter.execfile(pyFile);
				pythonClass = interpreter.get(file.getName().replace(".py", "").trim());
				if(pythonClass != null) {
					PyObject pObj = pythonClass.__call__();
					QuestInterface q = (QuestInterface) pObj.__tojava__(QuestInterface.class);
					loadedPythonQuests.add((Quest)q);
					backedScripts.add(q);
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
	public List<NpcInterface> buildNpcs(File pyNpcsDir) throws Exception {
		List<NpcInterface> loadedPythonNpcs = new ArrayList<NpcInterface>();
		
		for(File file : pyNpcsDir.listFiles()) {
			if(file.getAbsoluteFile().isFile() && file.getName().endsWith(".py")) {
				String pyFile = pyNpcsDir.getAbsolutePath() + File.separatorChar + file.getName();
				interpreter.execfile(pyFile);
				if(pythonClass != null) {
					pythonClass = interpreter.get(file.getName().replace(".py", "").trim());
					PyObject pObj = pythonClass.__call__();
					NpcInterface npcHandler = (NpcInterface) pObj.__tojava__(NpcInterface.class);
					loadedPythonNpcs.add(npcHandler);
					backedScripts.add(npcHandler);
				} else {
					throw new Exception("Syntax error found, unable to convert " + file.getName().replace(".py", "").trim());
				}
			}
		}
		return loadedPythonNpcs;
	} 

}
