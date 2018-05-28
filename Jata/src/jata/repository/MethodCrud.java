package jata.repository;


import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jata.jatasql.*;
import jata.Utils.Collections;
import jata.Utils.Json;
import jata.reflections.JTClass;




class MethodCrud {
	

	

    
    
    
    
    
    public static int execute(Method method, Object[] args) {    	
    	return SqlDB.getSqlClient(method).execute(method.getAnnotation(CRUD.class).sql(), getParams(method, args));   
    }
    
    
    static <T> List<T> selectList(Method method, Object[] args, Class<T> ct) throws IOException {    
        try (SqlQueryResult qr = select(method, args)) {
        	List<Record> recordList = qr.toList();
        	if (!method.isAnnotationPresent(Mapper.class)) {
        		return recordList.stream().map(r->EntityCrud.newEntity(ct, r)).collect(Collectors.toList());
        	} else {
        		return recordList.stream().map(r->(T)JTClass.get(method.getAnnotation(Mapper.class).value()).map(r)).collect(Collectors.toList());
        	}
        }  
    }    
    
    
    
    static SqlQueryResult select(Method method, Object[] args) {    	
    	 return SqlDB.getSqlClient(method).select(method.getAnnotation(CRUD.class).sql(), getParams(method, args));   
    }
    
    
    
    public static <T> T selectOne(Method method, Object[] args) {    	
        try (SqlQueryResult qr = select(method, args)) {      
        	if (!method.isAnnotationPresent(Mapper.class)) {
        		return (T) EntityCrud.newEntity(method.getReturnType(), qr.nextRecord());
        	} else {
        		return (T) JTClass.get(method.getAnnotation(Mapper.class).value()).map(qr.nextRecord());
        	}
        }
    }
    
  

    public static <T> T[] selectArray(Method method, Object[] args) throws IOException {    	
    	return (T[]) Collections.toArray(selectList(method, args, method.getReturnType().getComponentType()));
    }
    
    public static <T> List<T> selectList(Method method, Object[] args) throws IOException {    
    	return (List<T>) selectList(method, args, JTClass.getParaClassFromMethod(method));
    }    
    
    public static String selectJson(Method method, Object[] args) throws IOException {
        try (SqlQueryResult qr = select(method, args)) {
        	List<Record> list = qr.toList();        	          
	        if (list.size() == 1) {
	        	return Json.toJson(list.get(0));
	        } else {
	        	return Json.toJson(list);
	        }
        }
    }
    
    public static Object selectPrimitive(Method method, Object[] args) throws IOException {
        try (SqlQueryResult qr = select(method, args)) {        
            return method.getReturnType().cast(qr.nextRecord().get(0));
        }         	
    }
    
    public static <K,V> Map<K, V> selectMap(Method method, Object[] args) throws IOException {    	
    	 Mapping mapping = method.getAnnotation(Mapping.class);
    	 Mapper mm = method.getAnnotation(Mapper.class);
		 RowMapper<Entry<K,V>> mmr = mm != null ? JTClass.newInstance(mm.value()) :  null;
    	 Map<K, V> result = new HashMap();
    	 try (SqlQueryResult qr = select(method, args)) {
    		 List<Record> list = qr.toList();
    		 list.forEach(record -> {
	    		 if (mmr != null) {
	    			 Entry<K,V> entry = mmr.map(record);
	    			 result.put(entry.getKey(), entry.getValue());
	    		 } else if (mapping != null){
	    			 result.put((K)record.get(mapping.key().toUpperCase()), (V)record.get(mapping.value().toUpperCase()));
	    		 } 
    		 });
    	 }
    	 return result;
    }
   
    
  
    
    static Map<String, Object> getParams(Method method, Object[] args) {
        if (method.getParameterCount() > 0) {
            Map<String, Object> params = new HashMap();
            Parameter[] parameters = method.getParameters();
            for (int i=0;i<parameters.length;i++) {   
            	String name = parameters[i].isAnnotationPresent(Param.class) ? parameters[i].getAnnotation(Param.class).value() : parameters[i].getName();
                params.put(name, args[i]);
            }                
            return params;
        }
        return null;
    }   
    

    
    

}
