package jata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jata.repository.Param;

class Condition {
	
	
	
    public static String makeWhereClause(Object condition, Map<String, Object> params) {
    	List<Field>	fieldList = JataFields.getFields(condition.getClass());
    	fieldList.forEach(field -> {
    		try {
				Object value = field.get(condition);
				if (value != null) {
					params.put(field.getName(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    	StringBuilder sb = new StringBuilder();
    	if (params.size() > 0) {
    		sb.append(" where ");
		    Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
		    Map.Entry<String, Object> pair;
		    while (iterator.hasNext()) {
		        pair = iterator.next();
				sb.append(pair.getKey()).append(" = :").append(pair.getKey().toLowerCase());
				if (iterator.hasNext()) {
					sb.append(" and ");
				}
		    }    		
    	}	    	
    	return sb.toString();
    }	
    
    public static Map<String, Object> getParams(Method method, Object[] args) {
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
