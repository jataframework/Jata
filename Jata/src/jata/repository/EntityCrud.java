package jata.repository;



import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import jata.DataSource;
import jata.Jata;
import jata.Utils.Collections;
import jata.Utils.TwoKeyMap;
import jata.reflections.JTClass;
import jata.reflections.JTField;



public abstract class EntityCrud {
	
	
	
	
	
    static Map<String, List<JTField>> nonKeyFieldMap = new HashMap();
    static List<JTField> getNonKeyFieldList(String className) {
    	if (!nonKeyFieldMap.containsKey(className))
    		nonKeyFieldMap.put(className, new LinkedList());
    	return nonKeyFieldMap.get(className);
    }
    static Map<String, List<JTField>> keyFieldMap = new HashMap();
    static List<JTField> getKeyFieldList(String className) {
    	if (!keyFieldMap.containsKey(className))
    		keyFieldMap.put(className, new LinkedList());
    	return keyFieldMap.get(className);
    }    
    
    static void loadFields(Class<?> ct) {
        List<JTField> fieldList = JTClass.getFields(ct);
        for (JTField field : fieldList) {
            if (field.has(Id.class)) {
            	getKeyFieldList(ct.getName()).add(field);
            } else {
            	getNonKeyFieldList(ct.getName()).add(field);
            }
        }                        	
    }
    static List<JTField> getNonKeyFields(Class<?> ct) {
        if (!nonKeyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return nonKeyFieldMap.get(ct.getName());
    }    
    static List<JTField> getKeyFields(Class<?> ct) {
        if (!keyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        return keyFieldMap.get(ct.getName());
    }  
     static List<JTField> getFields(Class<?> ct) {
        if (!nonKeyFieldMap.containsKey(ct.getName())) {
        	loadFields(ct);
        }
        List<JTField> nklist = nonKeyFieldMap.get(ct.getName());
        List<JTField> klist = keyFieldMap.get(ct.getName());
        List<JTField> list = new LinkedList();
        if (klist != null && klist.size() > 0) 
        	list.addAll(klist);
        if (nklist != null && nklist.size() > 0)
        	list.addAll(nklist);        
        return list;
    }    	
	

	
	
    static String getEntityName(Class<?> ct) {
        Database database = ct.getAnnotation(Database.class);
        if (database != null && !database.entity().trim().isEmpty())
            return database.entity();
        jata.repository.Entity entity = ct.getDeclaredAnnotation(jata.repository.Entity.class);
        return entity != null ? entity.value() : ct.getSimpleName();        
    }		
	
	final static String CLASS_PATH = "/classes";
	static Map<String, Class<?>> entityClassMap = null;
	static Class<?> getEntityClass(String entityName) {
		if (entityClassMap == null) {
			entityClassMap = new HashMap();
			loadEntityClass();
		}
		return entityClassMap.get(entityName);
	}
	static void loadEntityClass() {
		File file = new File(Jata.jarFolder()+CLASS_PATH);  
		loadEntityClass(file, "");
	}
	static void loadEntityClass(File dir, String prefix) {
		File[] files = dir.listFiles();
		for (File file : files) {	
			String pp = prefix == null || prefix.isEmpty() ? "" : prefix+".";
			if (file.isDirectory()) {
				loadEntityClass(file, pp+file.getName());
			} else {
				String className = pp+file.getName().replace(".class", "");
				System.out.println(className);
				try {
					Class<?> ct = Class.forName(className);
					Database database = ct.getAnnotation(Database.class);
					if (database != null && database.entity() != null && !database.entity().isEmpty()) {
						entityClassMap.put(database.entity(), ct);
					} else if (ct.isAnnotationPresent(jata.repository.Entity.class)) {
						jata.repository.Entity entity = ct.getAnnotation(jata.repository.Entity.class);				
						entityClassMap.put(entity.value(), ct);
					} else {
						if (EntityCrud.class.isAssignableFrom(ct)) {
							entityClassMap.put(ct.getSimpleName(), ct);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	
	
	
	static TwoKeyMap<Class<?>, Class<?>, ValueCall> classMap = new TwoKeyMap();
	static {
		classMap.put(double.class, BigDecimal.class, o->((BigDecimal)o).doubleValue());
		classMap.put(Double.class, BigDecimal.class, o->((BigDecimal)o).doubleValue());
		classMap.put(int.class, BigInteger.class, o->((BigInteger)o).intValue());
		classMap.put(Integer.class, BigInteger.class, o->((BigInteger)o).intValue());
		classMap.put(Date.class, Long.class, o->new Date((long)o));
		classMap.put(Date.class, long.class, o->new Date((long)o));
	}
	
	
	
	static <T> T newEntity(Class<T> ct, Map<String, Object> params) {
		T t = (T) JTClass.newInstance(ct);
    	JTClass.getFields(ct).forEach(field-> {
    		DBField dbField = field.getAnnotation(DBField.class);
    		String name = dbField != null ? dbField.value().toUpperCase() : field.getName().toUpperCase();
    		Object value = params.get(name);
    		if (classMap.containsKey(field.getType(), value.getClass())) {
    			value = classMap.get(field.getType(), value.getClass()).value(value);
    		}
    		field.setValue(t, value);
    	});
    	return t;		
	}
	
	



    
    protected abstract Map<String, Object> doSelectOneMap(Object condition);
    
    protected abstract String doSelectOneJson(Object condition);
    
    protected abstract  <T> T doSelectOne(T condition);
    
    protected abstract <T> List<T> doSelectList(T condition);
    
    protected abstract List<Map<String, Object>> doSelectListMap(Object condition);
    
    protected abstract String doSelectJson(Object condition);    
 
    protected abstract long doCount(Object condition);
    
    protected abstract <T> long doInsert(List<T> list);
    
    protected abstract <T> boolean doInsert(T t);
    
    protected abstract <T> long doUpdate(List<T> list);
    
    protected abstract <T> boolean doUpdate(T t);
    
    protected abstract <T> long doDelete(List<T> list);
    
    protected abstract <T> boolean doDelete(T t);
    
    

    public static Map<String, Object> selectOneMap(Object condition) {
    	return getCrud(condition.getClass()).doSelectOneMap(condition);
    }
    
    public static String selectOneJson(Object condition) {
    	return getCrud(condition.getClass()).doSelectOneJson(condition);
    }
    
    public static <T> T selectOne(T condition) {
    	return getCrud(condition.getClass()).doSelectOne(condition);
    }
    
    public static <T> List<T> selectList(T condition) {
    	return getCrud(condition.getClass()).doSelectList(condition);
    }
    
    public static List<Map<String, Object>> selectListMap(Object condition) {
    	return getCrud(condition.getClass()).doSelectListMap(condition);
    }
    
    public static String selectJson(Object condition) {
    	return getCrud(condition.getClass()).doSelectJson(condition);
    }
    
    public static <T> T[] selectArray(T condition) {    	
    	return Collections.toArray(selectList(condition));
    }
 
    public static long count(Object condition) {
    	return getCrud(condition.getClass()).doCount(condition);
    }
    
    public static <T> long insert(List<T> list) {
    	if (list == null || list.size() == 0) {
    		return -1;
    	}
    	Iterator<T> it = list.iterator();
    	return getCrud(it.next().getClass()).doInsert(list);
    }
    
    public static <T> boolean insert(T t) {
    	return getCrud(t.getClass()).doInsert(t);
    }
    
    public static <T> long update(List<T> list) {
    	Iterator<T> it = list.iterator();
    	return getCrud(it.next().getClass()).doUpdate(list);    	
    }
    
    public static <T> boolean update(T t) {
    	return getCrud(t.getClass()).doUpdate(t);    	
    }
    
    public static <T> long delete(List<T> list) {
    	Iterator<T> it = list.iterator();
    	return getCrud(it.next().getClass()).doDelete(list);    	    	
    }
	
    public static <T> boolean delete(T t) {
    	return getCrud(t.getClass()).doDelete(t);    	    	
    }
	
	
    
    static EntityCrud create(DataSource ds) {
    	return ds.isMongodb() ? new MongoEntityCrud(ds.getName()) : new SqlEntityCrud(ds.getName());
    }
	
	
    static EntityCrud getCrud(Class<?> ct) {
    	Database database = ct.getAnnotation(Database.class);
    	return database != null && !database.value().isEmpty() ? EntityCrud.crud(database.value()) : EntityCrud.crud();    	
    }
    
    static EntityCrud getCrud(String table) {
    	return getCrud(EntityCrud.getEntityClass(table));
    }  	
	
	
    static Map<String, EntityCrud> dbMap = null;
    public static EntityCrud crud(String name) {
        return dbMap.get(name);
    }



    static void loadDB() {
    	if (dbMap == null) {
    		dbMap = new HashMap<String, EntityCrud>();    	
	        for (DataSource ds : DataSource.allDataSources()) {
	            dbMap.put(ds.getName(), EntityCrud.create(ds));
	        }
	        System.out.println("DB Loaded");
    	}
    }
    
    
    static {
    	loadDB();
    }
    
    public static EntityCrud crud() {
        Map.Entry<String, EntityCrud> entry = dbMap.entrySet().iterator().next();
        return entry.getValue();
    }	
	
	

}
