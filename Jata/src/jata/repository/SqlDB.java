package jata.repository;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jata.DataSource;
import jata.jatasql.SqlClient;
import jata.reflections.JTClass;

class SqlDB {
	
	
	
	
	
	static Map<String, SqlClient> sqlClientMap = new HashMap();
	static SqlClient getSqlClient(String name) {
		if (!sqlClientMap.containsKey(name)) {
			DataSource ds = DataSource.get(name);		
			sqlClientMap.put(name, SqlClient.create(ds.getUrl()));
		}
		return sqlClientMap.get(name);
	}
	static SqlClient getSqlClient() {
		if (sqlClientMap.size() == 0) {
			DataSource.allSqlDataSources().forEach(ds->getSqlClient(ds.getName()));		// load all data source to sqlclient
		}
		return sqlClientMap.values().iterator().next();
	}

	
    public static SqlClient getSqlClient(Method method) {
        Database database = method.getAnnotation(Database.class);               // DB definition in Method?        
        if (database == null) { 
            if (method.isAnnotationPresent(CRUD.class)) {
                CRUD sql = method.getAnnotation(CRUD.class);                // DB definition in Query?
                String db = sql.db();
                if (!db.trim().isEmpty()) {
                    return getSqlClient(db);
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
        return database != null ? getSqlClient(database.value()) : getSqlClient();     // otherwise, default db
    } 	

}
