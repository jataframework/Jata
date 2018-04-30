package jata;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class JataBeans {
	
	
	
	
	
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

}
