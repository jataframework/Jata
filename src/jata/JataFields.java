package jata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import jata.dependency.New;
import jata.dependency.Singleton;
import jata.repository.Id;

public class JataFields {

	
	
    static Map<String, List<Field>> nonKeyFieldMap = new HashMap();
    static List<Field> getNonKeyFieldList(String className) {
    	if (!nonKeyFieldMap.containsKey(className))
    		nonKeyFieldMap.put(className, new LinkedList());
    	return nonKeyFieldMap.get(className);
    }
    static Map<String, List<Field>> keyFieldMap = new HashMap();
    static List<Field> getKeyFieldList(String className) {
    	if (!keyFieldMap.containsKey(className))
    		keyFieldMap.put(className, new LinkedList());
    	return keyFieldMap.get(className);
    }    
    static Map<String, List<Field>> singleFieldMap = new HashMap();
    static List<Field> getSingleFieldList(String className) {
    	if (!singleFieldMap.containsKey(className))
    		singleFieldMap.put(className, new LinkedList());
    	return singleFieldMap.get(className);
    }        
    static Map<String, List<Field>> newFieldMap = new HashMap();
    static List<Field> getNewFieldList(String className) {
    	if (!newFieldMap.containsKey(className))
    		newFieldMap.put(className, new LinkedList());
    	return newFieldMap.get(className);
    }          
    static void loadFields(Class<?> ct) {
        Field[] fields = ct.getDeclaredFields();
        List<Field> fieldList = new LinkedList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
            	getKeyFieldList(ct.getName()).add(field);
            } else {
            	getNonKeyFieldList(ct.getName()).add(field);
            }
            if (field.isAnnotationPresent(Singleton.class)) {
            	singleFieldMap.get(ct.getName()).add(field);
            } else if (field.isAnnotationPresent(New.class)){
            	newFieldMap.get(ct.getName()).add(field);            
            }
        }                        	
    }
    public static List<Field> getNonKeyFields(Class<?> ct) {
        if (!nonKeyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return nonKeyFieldMap.get(ct.getName());
    }    
    public static List<Field> getKeyFields(Class<?> ct) {
        if (!keyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return keyFieldMap.get(ct.getName());
    }  
    public static List<Field> getFields(Class<?> ct) {
        if (!nonKeyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        List<Field> nklist = nonKeyFieldMap.get(ct.getName());
        List<Field> klist = keyFieldMap.get(ct.getName());
        List<Field> list = new LinkedList();
        if (klist != null && klist.size() > 0) 
        	list.addAll(klist);
        if (nklist != null && nklist.size() > 0)
        	list.addAll(nklist);        
        return list;
    }    
    public static List<Field> getSingleFields(Class<?> ct) {
        if (!singleFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return singleFieldMap.get(ct.getName());
    }     
    public static List<Field> getNewFields(Class<?> ct) {
        if (!newFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return newFieldMap.get(ct.getName());
    }    	
	
    
    
    public static Map<String, Object> getValueMap(Object o) {
    	List<Field> fieldList = getFields(o.getClass());
    	Map<String, Object> map = new HashMap();
    	fieldList.forEach(field -> {
			try {
				map.put(field.getName(), field.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
			}
		});
    	return map;
    }
    
    
    
    public static <T> T setValues(T t, Map<String, Object> valueMap) {
    	List<Field> fieldList = getFields(t.getClass());
    	fieldList.forEach(field -> {
			try {
				field.set(t, valueMap.get(field.getName().toUpperCase()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
    	return t;
    }
	
}
