package jata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jata.Utils.Collections;
import jata.repository.CRUD;

class Inserter {
	
	
	
	
    static Map<String, String> insertMap = new HashMap();
    public static String getInsert(Class<?> ct) {
        if (!insertMap.containsKey(ct.getName())) {
            List<Field> fieldList = JataFields.getFields(ct);
            String tableName = DB.getTableName(ct);
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");
            sb.append(tableName);
            sb.append(" (");
            Collections.forNext(fieldList, (field, i) -> {
            	sb.append(field.getName().toLowerCase());
            	sb.append(i<fieldList.size()-1?",":"");
            });                             
            sb.append(") values (");
            Collections.forNext(fieldList, (field, i) -> {
            	sb.append(":").append(field.getName().toLowerCase());
            	sb.append(i<fieldList.size()-1?",":"");
            });        
            sb.append(")");
            System.out.println(sb.toString());
            insertMap.put(ct.getName(), sb.toString());
        }
        return insertMap.get(ct.getName());
    }        

    

    
    public static int insert(Object o) {
        return DB.getDB(o.getClass()).execute(getInsert(o.getClass()), JataFields.getValueMap(o));
    }     
	
    
	

	
	
	

}
