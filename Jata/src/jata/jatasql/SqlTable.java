package jata.jatasql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import jata.Utils.Collections;
import jata.reflections.JTClass;
import jata.reflections.JTField;

public class SqlTable<T> {

	
	
	
	
	String tableName;
	SqlClient client;
	Class<T> ct;
	
	

	
	SqlTable(String tableName, SqlClient client, Class<T> ct) {
		this.tableName = tableName;
		this.client = client;
		this.ct = ct;
	}	
	
	
	
	public Iterable<T> find() {
		return find(null);
	}
	
	
	<T> T newEntity(Class<T> ct, Map<String, Object> params) {
		T t = (T) JTClass.newInstance(ct);
    	JTClass.getFields(ct).forEach(field-> field.setValue(t, params.get(field.getName().toUpperCase())));
    	return t;		
	}
	
	
	public List<T> find(Condition filters) {
		String where = filters!=null?filters.getSql():"";
		Map<String, Object> params = filters!=null?filters.getParams():null;
		List<Record> list = client.select("select * from "+tableName+(!where.trim().isEmpty()?" where "+where:""), params).toList();
		if (ct == Record.class) {
			return (List<T>)list;
		} else {
			return list.stream().map(record->newEntity(ct,record)).collect(Collectors.toList());
		}
	}

	public T findOne() {
		return findOne(null);
	}		
	
	public T findOne(Condition filters) {
		String where = filters!=null?filters.getSql():"";
		Map<String, Object> params = filters!=null?filters.getParams():null;
		try (SqlQueryResult qr = client.select("select * from "+tableName+(!where.trim().isEmpty()?" where "+where:""), params)) {
			Record r = qr.nextRecord();
			return ct == Record.class ? (T)r : newEntity(ct, r);
		}
	}	
	
	
	public <E extends T> int insert(E e) {
		Map<String, Object> params = ct == Record.class ?  (Map<String, Object>)e : JTClass.getValueMap(e, ct);			
		return client.execute(getInsert(tableName, params), params);
	}
	
	public <E extends T> long insert(List<E> list) {
		List<Map<String, Object>> paramsList = (ct == Record.class ? list.stream().map(e->(Map<String, Object>)e) : list.stream().map(e->JTClass.getValueMap(e, ct))).collect(Collectors.toList());		
		return client.execute(getInsert(tableName, paramsList.get(0)), paramsList);
	}
	
	public long delete() {
		return delete(null);
	}
		
	public long delete(Condition filters) {		
		String where = filters!=null?filters.getSql():"";	
		Map<String, Object> params = filters!=null?filters.getParams():null;	 
		return client.execute("delete from "+tableName+(!where.trim().isEmpty()?" where "+where:""), params);			
	}

	
	public long update(Condition filters, Sets sets) {
		if (sets == null) {
			return -1;
		}
		String where = filters!=null?filters.getSql():"";
		String set = sets!=null?sets.getSql():"";
		Map<String, Object> params = sets.getParams();
		if (filters != null) {
			if (filters.getParams() != null)
				params.putAll(filters.getParams());
		}
		return client.execute("update "+tableName+" set "+set+(!where.trim().isEmpty()?" where "+where:""), params);		
	}
	
	
	
	public long count() {
		return count(null);
	}
	
	
	public long count(Condition filters)  {
		String where = filters!=null?filters.getSql():"";	
		Map<String, Object> params = filters!=null?filters.getParams():null;	 
		try (SqlQueryResult qr = client.select("select count(*) from "+tableName+(!where.trim().isEmpty()?" where "+where:""), params)) {
			return (long) qr.nextRecord().get(0);			
		}
	}
	
	
	static Map<String, String> insertMap = new HashMap();
    static String getInsert(String tableName, Map<String, Object> params) {
        if (!insertMap.containsKey(tableName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");
            sb.append(tableName);
            sb.append(" (");
            Set<String> keySet = params.keySet();
            Collections.forNext(keySet, (field, i) -> {
            	sb.append(field.toLowerCase());
            	sb.append(i<keySet.size()-1?", ":"");
            });                             
            sb.append(") values (");
            Collections.forNext(keySet, (field, i) -> {
            	sb.append(":").append(field.toLowerCase());
            	sb.append(i<keySet.size()-1?", ":"");
            });        
            sb.append(")");
            System.out.println(sb.toString());
            insertMap.put(tableName, sb.toString());
        }
        return insertMap.get(tableName);
    }   
	

	
	
}
