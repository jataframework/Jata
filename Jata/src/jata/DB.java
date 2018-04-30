package jata;

import jata.Utils.Collections;
import jata.reflections.JTClass;
import jata.repository.Database;
import jata.repository.Entity;
import jata.repository.Id;

import jata.repository.CRUD;
import jata.repository.Table;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


class DB {
	
	final static String CLASS_PATH = "/classes";
	static Map<String, Class<?>> tableClassMap = null;
	public static Class<?> getTableClass(String tableName) {
		if (tableClassMap == null) {
			tableClassMap = new HashMap();
			loadTableClass();
		}
		return tableClassMap.get(tableName);
	}
	static void loadTableClass() {
		File file = new File(Jata.jarFolder()+CLASS_PATH);  
		loadTableClass(file, "");
	}
	static void loadTableClass(File dir, String prefix) {
		File[] files = dir.listFiles();
		for (File file : files) {	
			String pp = prefix == null || prefix.isEmpty() ? "" : prefix+".";
			if (file.isDirectory()) {
				loadTableClass(file, pp+file.getName());
			} else {
				String className = pp+file.getName().replace(".class", "");
				System.out.println(className);
				try {
					Class<?> ct = Class.forName(className);
					Database database = ct.getAnnotation(Database.class);
					if (database != null && database.table() != null && !database.table().isEmpty()) {
						tableClassMap.put(database.table(), ct);
					} else if (ct.isAnnotationPresent(Table.class)) {
						Table table = ct.getAnnotation(Table.class);				
						tableClassMap.put(table.value(), ct);
					} else {
						if (Entity.class.isAssignableFrom(ct)) {
							tableClassMap.put(ct.getSimpleName(), ct);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    
    public static String getTableName(Class<?> ctt) {
        Database database = ctt.getAnnotation(Database.class);
        if (database != null && !database.table().trim().isEmpty())
            return database.table();
        Table table = ctt.getDeclaredAnnotation(Table.class);
        return table != null ? table.value() : ctt.getSimpleName();        
    }		
    
    public static JataDB getDB(Class<?> ct) {
    	Database database = ct.getAnnotation(Database.class);
    	return database != null && !database.value().isEmpty() ? JataDB.db(database.value()) : JataDB.db();    	
    }
 

    static JataDB getDB(Method method) {
        Database database = method.getAnnotation(Database.class);               // DB definition in Method?        
        if (database == null) { 
            if (method.isAnnotationPresent(CRUD.class)) {
                CRUD sql = method.getAnnotation(CRUD.class);                // DB definition in Query?
                String db = sql.db();
                if (!db.trim().isEmpty()) {
                    return JataDB.db(db);
                } 
            } 
            Class<?> returnClass =  method.getReturnType();
            database = returnClass.getDeclaredAnnotation(Database.class);       // single entity?
            if (database == null) {
                Class<?> paraClass = JTClass.getParaClassFromMethod(method);                 
                if (paraClass != null && paraClass.isAnnotationPresent(Database.class)) {               
                    database = paraClass.getDeclaredAnnotation(Database.class); // List of entity?
                } else {
                    Class<?> dc = method.getDeclaringClass();
                    database = dc.getDeclaredAnnotation(Database.class);        // DB definition in the repository class?                  
                }
            }
        }
        return database != null ? JataDB.db(database.value()) : JataDB.db();     // otherwise, default db
    } 
    
    
    

    
    

    
    

    


}
    
    

    
    
    
  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 
    
    
    
    
	
	

