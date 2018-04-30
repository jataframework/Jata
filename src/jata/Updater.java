package jata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jata.Utils.Collections;

class Updater {
	
	
	
	
    static Map<String, String> updateMap = new HashMap();
    public static String getUpdate(Class<?> ct) {
        if (!updateMap.containsKey(ct.getName())) {
            List<Field> fieldList = JataFields.getNonKeyFields(ct);
            System.out.println("fields for "+ct.getCanonicalName()+" size = "+fieldList.size()); 
            String tableName = DB.getTableName(ct);
            StringBuilder sb = new StringBuilder();
            sb.append("update ");
            sb.append(tableName);
            sb.append(" set ");
            Collections.forNext(fieldList, (field, i) -> {
            	sb.append(field.getName()).append(" = :"+field.getName().toLowerCase());
            	sb.append(i<fieldList.size()-1?", ":"");
            });
            sb.append(" where ");
            List<Field> keylist = JataFields.getKeyFields(ct);
            Collections.forNext(keylist, (field, i) -> {
            	sb.append(field.getName()).append(" = :").append(field.getName().toLowerCase());
            	sb.append(i<keylist.size()-1?" and ":"");
            });
            updateMap.put(ct.getName(), sb.toString());
        }
        return updateMap.get(ct.getName());
    }        

    
    

    
    public static int update(Object o) {
        return DB.getDB(o.getClass()).execute(getUpdate(o.getClass()), JataFields.getValueMap(o));
    }          
    

}
