package jata.reflections;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JTClass {
	
	
	
	
	Class<?> ct;
	List<Field> fieldList = new LinkedList();
	List<Method> methodList = new LinkedList();
	
	
	public List<Field> getFieldList() {
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
		fieldList.addAll(Arrays.asList(ct.getFields()));
		methodList.addAll(Arrays.asList(ct.getMethods()));
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
	

}
