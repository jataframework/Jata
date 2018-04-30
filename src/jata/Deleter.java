package jata;


import java.lang.reflect.Method;
import java.util.HashMap;

import java.util.Map;

import jata.repository.CRUD;



class Deleter {
	
	
	static Map<String, String> deleteMap = new HashMap();
    public static String getDelete(Class<?> ct) {
        if (!deleteMap.containsKey(ct.getName())) {
            String tableName = DB.getTableName(ct);
            StringBuilder sb = new StringBuilder();
            sb.append("delete from ");
            sb.append(tableName);
            deleteMap.put(ct.getName(), sb.toString());
        }
        return deleteMap.get(ct.getName());
    }        
 
    
    

    public static int delete(Object condition) {
        String sql = getDelete(condition.getClass());
        Map<String, Object> params = new HashMap();
        String where = Condition.makeWhereClause(condition, params);
        return DB.getDB(condition.getClass()).execute(sql+where, params);
    }   
    
    
    public static int delete(Class<?> ct) {
    	return DB.getDB(ct).execute(getDelete(ct), null);
    }
    
    
    
    
    

    

    
    

}
