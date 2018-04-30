package jata;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jata.Utils.Collections;
import jata.reflections.JTClass;
import jata.repository.CRUD;



class MethodCrud {
	

	

    
    
    
    
    
    public static int execute(Method method, Object[] args) {    	
    	return DB.getDB(method).execute(method.getAnnotation(CRUD.class).sql(), Condition.getParams(method, args));   
    }
    
    
    static <T> List<T> selectList(Method method, Object[] args, Class<T> ct) {    
        List<T> list = new LinkedList<>();
        try (QueryResult qr = select(method, args)) {
            while (qr.hasNext()) {
            	list.add(JataFields.setValues(JataBeans.newInstance(ct), qr.nextMap()));           
            }
            return list;  
        }  
    }    
    
    
    
    static QueryResult select(Method method, Object[] args) {    	
    	 return DB.getDB(method).select(method.getAnnotation(CRUD.class).sql(), Condition.getParams(method, args));   
    }
    
    
    
    public static <T> T selectOne(Method method, Object[] args) {    	
        try (QueryResult qr = select(method, args)) {        
            return (T) JataFields.setValues(JataBeans.newInstance(method.getReturnType()), qr.nextMap()); 
        }      
    }
    
  

    public static <T> T[] selectArray(Method method, Object[] args) {    	
    	return (T[]) Collections.toArray(selectList(method, args, method.getReturnType().getComponentType()));
    }
    
    public static <T> List<T> selectList(Method method, Object[] args) {    
    	return (List<T>) selectList(method, args, JTClass.getParaClassFromMethod(method));
    }    
    
    public static Object selectPrimitive(Method method, Object[] args) {
        try (QueryResult qr = select(method, args)) {        
            return method.getReturnType().cast(qr.nextRecord().get(0));
        }         	
    }
    
   
    
  
    

    
    

}
