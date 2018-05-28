package jata.reflections;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jata.repository.DBField;

public class JTClass {
	
	
	static Map<Class<?>, Object> ctMap = new HashMap();
	public static <T> T get(Class<T> ct) {
		if (!ctMap.containsKey(ct)) {
			ctMap.put(ct, newInstance(ct));
		}
		return (T) ctMap.get(ct);
	}
	
	
	Class<?> ct;
	List<JTField> fieldList = null;
	List<Method> methodList = null;
	
	
	public List<JTField> getFieldList() {
		return fieldList;
	}
	
	public List<Method> getMethodList() {
		return methodList;
	}
	
	
	public JTClass(Class<?> ct) {
		this.ct = ct;
		init();
	}
	
	
	
	void init() {
		fieldList = getFields(ct);
		methodList.addAll(Arrays.asList(ct.getMethods()));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	static Map<Class<?>, List<JTField>> fieldMap = new HashMap();
	public static List<JTField> getFields(Class<?> ct) {
		if (!fieldMap.containsKey(ct))
			fieldMap.put(ct, Arrays.asList(ct.getDeclaredFields()).stream().map(field->JTField.create(field)).collect(Collectors.toList()));
		return fieldMap.get(ct);
	}
	

    public static Class<?> getParaClassFromMethod(Method method) {
        ParameterizedType pt = castToParaType(method.getGenericReturnType());
        if (pt != null) {
            return (Class<?>) pt.getActualTypeArguments()[0];
        } 
        return null;
    }  	
    public static Class<?> getParaClassFromParaType(Type type) {
        ParameterizedType pt = castToParaType(type);
        if (pt != null) {
            return (Class<?>) pt.getActualTypeArguments()[0];
        } 
        return null;
    }  	
    public static ParameterizedType castToParaType(Type type) {
        return cast(type, ParameterizedType.class);
    }    	
	
    public static <T> T cast(Object o, Class<T> ct) {
        try {
            return ct.cast(o);
        } catch (Exception e) {
            return null;
        }
    }    
	
    
    
    static Map<String, Constructor> constructorMap = new HashMap();
    public static Constructor getConstructor(Class<?> ct) {
        if (!constructorMap.containsKey(ct.getName())) {
            for (Constructor c : ct.getDeclaredConstructors()) {
                if (c.getGenericParameterTypes().length == 0) {
                    constructorMap.put(ct.getName(), c);
                    break;
                }
            }
        }
        return constructorMap.get(ct.getName());
    }
	

	
	
    public static <T> T newInstance(Class<T> ct) {
		try {
			return (T)getConstructor(ct).newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
    }	    

    

    
    
    
    public static Map<String, Object> getValueMap(Object o) {
    	List<JTField> fieldList = getFields(o.getClass());
    	return fieldList.stream().collect(Collectors.toMap(field->field.getName(), field->field.getValue(o)));
    }
    
    
    public static Map<String, Object> getValueMap(Object o, Class<?> ct) {
    	List<JTField> fieldList = getFields(ct);
    	return fieldList.stream().collect(Collectors.toMap(field->field.getName(), field->field.getValue(o)));
    }
    
    
   
    
}
