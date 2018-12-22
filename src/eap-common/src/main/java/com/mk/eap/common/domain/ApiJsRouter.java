package com.mk.eap.common.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mk.eap.common.utils.JarResourceUtil;

public class ApiJsRouter {
	
	private static HashMap<String,Invocable> handlers = new HashMap<>();
	
	private static boolean reload = true;
	
	public static Object Handler(Context context, String payload){
		String url = context.getPath();
		url = url.substring(0, url.lastIndexOf("/"));
		if(!handlers.containsKey(url) || reload){ 
			initJsHandler(url);
		}
		Invocable invoke = handlers.get(url);
		if(invoke != null){
			try { 
				return invoke.invokeFunction("handler", context, payload);
			} catch (NoSuchMethodException e) { 
				e.printStackTrace();
			} catch (ScriptException e) { 
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static void initJsHandler(String url){

		handlers.put(url, null);
		String fileName = url.replace("../", "/") ;
		String filePath = JarResourceUtil.JarUtil.getResourseFolder();
		String jsPath = filePath + String.join(File.separator, "..", "js",  fileName , "index.js");
		 
		File file = new File(jsPath);   
		if(!file.exists()){   
			return;
		}

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try {
			engine.eval(new FileReader(jsPath));
			Invocable invocable = (Invocable) engine;
			handlers.put(url, invocable);
			
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (ScriptException e) { 
			e.printStackTrace();
		}
		
	}

}
