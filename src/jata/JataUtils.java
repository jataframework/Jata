/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata;


import jata.reflections.JTClass;
import jata.repository.Database;

import jata.repository.Repository;
import jata.repository.CRUD;
import jata.repository.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sochunyui
 */
class JataUtils {
    
    
  



    
 

    

    static <T> T makeRepository(Class<T> ct) {         
        Object object = Proxy.newProxyInstance(ct.getClassLoader(), new Class[]{ ct }, (proxy, method, args) -> {              
            return implementSqlMethod(method, args);
        }); 
        System.out.println(ct.getName()+" is initialized.");
        return (T) object;
    }
    
    
    static Object implementSqlMethod(Method method, Object[] args) {
        CRUD sql = method.getAnnotation(CRUD.class);
        if (sql.sql() == null || sql.sql().trim().isEmpty()) {
        	System.err.println("No such method: "+method.getName());
        	return null;
        }
        Class<?> mct = method.getReturnType();
        String sqlstr = sql.sql().trim();
        if (sqlstr.toUpperCase().startsWith("SELECT")) {
	        if (mct.isPrimitive()) {                
	            return MethodCrud.selectPrimitive(method, args);
	        } else if (mct.isArray()) {
	            return MethodCrud.selectArray(method, args);
	        } else if (List.class.isAssignableFrom(mct)) {
	            return MethodCrud.selectList(method, args);
	        } else {
	            return MethodCrud.selectOne(method, args);
	        }
        } else {
        	return MethodCrud.execute(method, args);
        }              	      
    }
    

    
    
    
    static void initializeSingletonBean(Object o) {
    	List<Field> fieldList = JataFields.getSingleFields(o.getClass());
    	fieldList.forEach(field -> {
    		try {
				field.set(o, Jata.get(field.getType()));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    }
    
    static void initializeNewBean(Object o) {
    	List<Field> fieldList = JataFields.getNewFields(o.getClass());
    	fieldList.forEach(field -> {
    		try {
				field.set(o, makeBean(field.getType()));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    }    
    

    
    
    static <T> T makeGeneralBean(Class<T> ct) {
    	
    	try {
    		
    		
    		T t = JataBeans.newInstance(ct);
    		    		
    		initializeSingletonBean(t);
    		initializeNewBean(t);
  
    		
    		return t;
    	
    	
    	} catch (Exception e) {
    		return null;
    	}
    	
    }
    
    

    
    
    public static <T> T makeBean(Class<T> ct) {
        if (Repository.class.isAssignableFrom(ct)) {
            return makeRepository(ct);        
        } else {
        	return makeGeneralBean(ct);
        }
    }
    
}
