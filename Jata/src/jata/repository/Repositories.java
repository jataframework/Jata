/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.repository;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sochunyui
 */
public class Repositories {
	
	
	
	
	
	public static <T extends Repository> T get(Class<T> ct) {
        
        Object object = Proxy.newProxyInstance(ct.getClassLoader(), new Class[]{ ct }, (proxy, method, args) -> {     
	        Class<?> mct = method.getReturnType();	        
	        CRUD sql = method.getAnnotation(CRUD.class);
	        if (sql.sql() != null && !sql.sql().trim().isEmpty()) {
		        String sqlstr = sql.sql().trim();
		        try {
			        if (sqlstr.toUpperCase().startsWith("SELECT")) {
			        	if (mct == String.class) {
			        		if (method.isAnnotationPresent(JSON.class)) {        	
			        			return MethodCrud.selectJson(method, args);
			        		} else {
			        			return MethodCrud.selectPrimitive(method, args);
			        		}
			        	} else  if (mct.isPrimitive()) {     
				        	return MethodCrud.selectPrimitive(method, args);
				        } else if (mct.isArray()) {
				            return MethodCrud.selectArray(method, args);
				        } else if (List.class.isAssignableFrom(mct)) {
				            return MethodCrud.selectList(method, args);
				        } else if (Map.class.isAssignableFrom(mct)) {
				        	return MethodCrud.selectMap(method, args);
				        } else {
				            return MethodCrud.selectOne(method, args);
				        }
			        } else {
			        	return MethodCrud.execute(method, args);
			        } 	        
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	return null;        
		        }
	        } 
	        System.err.println("No CRUD sql found.");
	        return null;
        }); 
        System.out.println(ct.getName()+" is initialized.");
        return (T) object;

	    
	    

              	      
	    		
	}
    

    
}
