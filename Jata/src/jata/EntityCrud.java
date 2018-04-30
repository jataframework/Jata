package jata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jata.Utils.Collections;

public class EntityCrud {
	
	
	
	
	  
    public static <T> T selectOne(T condition) {
    	return Selector.selectOne(condition);
    }
    
  

    public static <T> T[] selectArray(T condition) {    	
    	return Selector.selectArray(condition);
    }
    
  
    
    public static <T> List<T> selectList(T condition) {    
    	return Selector.selectList(condition);
    }
        
    
    

    public static <T> List<T> select(Class<T> ct) {
    	return Selector.select(ct);
    }   
    
    
    
    public static long count(Object condition) {
    	return Selector.count(condition);
    }
    
    
    
    public static long count(Class<?> ct) {
    	return Selector.count(ct);
    } 
    	
	
	
    public static int insert(Object o) {
        return Inserter.insert(o);
    } 
	
    public static int delete(Object o) {
    	return Deleter.delete(o);
    }
    
    public static int delete(Class<?> ct) {
    	return Deleter.delete(ct);
    }
    
    public static int update(Object o) {
    	return Updater.update(o); 
    }

}
