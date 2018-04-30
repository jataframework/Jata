package jata;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jata.Utils.Collections;



class Selector {
	
	
	static Map<String, String> selectMap = new HashMap();
    public static String getSelect(Class<?> ct) {
        if (!selectMap.containsKey(ct.getName())) {
            String tableName = DB.getTableName(ct);
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(tableName);
            selectMap.put(ct.getName(), sb.toString());
        }
        return selectMap.get(ct.getName());
    }       
    
	static Map<String, String> countMap = new HashMap();
    public static String getCount(Class<?> ct) {
        if (!countMap.containsKey(ct.getName())) {
            String tableName = DB.getTableName(ct);
            StringBuilder sb = new StringBuilder();
            sb.append("select count(*) from ");
            sb.append(tableName);
            countMap.put(ct.getName(), sb.toString());
        }
        return countMap.get(ct.getName());
    }     
 

    

    static QueryResult select(Object condition) {
        String sql = getSelect(condition.getClass());
        Map<String, Object> params = new HashMap();
        String where = Condition.makeWhereClause(condition, params);
        return DB.getDB(condition.getClass()).select(sql+where, params);
    }   
    
    

    
    public static <T> T selectOne(T condition) {
        try (QueryResult qr = select(condition)) {        
            return (T) JataFields.setValues(JataBeans.newInstance(condition.getClass()), qr.nextMap()); 
        }      
    }
    
  

    public static <T> T[] selectArray(T condition) {    	
    	return Collections.toArray(selectList(condition));
    }
    
  
    
    public static <T> List<T> selectList(T condition) {    
        List<T> list = new LinkedList<>();
        try (QueryResult qr = select(condition)) {
            while (qr.hasNext()) {
            	list.add((T) JataFields.setValues(JataBeans.newInstance(condition.getClass()), qr.nextMap()));           
            }
            return list;  
        }  
    }
        
    
    

    public static <T> List<T> select(Class<T> ct) {
    	List<T> list = new LinkedList<>();
    	try (QueryResult qr = DB.getDB(ct).select(getSelect(ct), null)) {
            while (qr.hasNext()) {
            	list.add((T) JataFields.setValues(JataBeans.newInstance(ct), qr.nextMap()));           
            }
            return list;      		
    	}
    }   
    
    
    
    public static long count(Object condition) {
        String sql = getCount(condition.getClass());
        Map<String, Object> params = new HashMap();
        String where = Condition.makeWhereClause(condition, params);
        try (QueryResult qr = DB.getDB(condition.getClass()).select(sql+where, params)) {
        	return Long.class.cast(qr.nextRecord().get(0)).longValue();
        }
    }
    
    
    
    public static long count(Class<?> ct) {
        String sql = getCount(ct);
        Map<String, Object> params = new HashMap();
        try (QueryResult qr = DB.getDB(ct).select(sql, null)) {
        	return Long.class.cast(qr.nextRecord().get(0)).longValue();
        }
    } 
    
 
    
    
    
    

}
