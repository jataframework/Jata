package jata.jatasql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Sets {

	
	
	String sql;
	Map<String, Object> params = new HashMap();
	
	Sets(String sql, String field, Object value) {
		this.sql = sql;
		this.params.put(field, value);
	}
	
	String getSql() {
		return sql;
	}
	
	Map<String, Object> getParams() {
		return params;
	}
	
	
	public Sets add(String field, Object value) {
		params.put(field, value);
		sql = sql + ", "+field+" = :"+field.toLowerCase();
		return this;
	}
	
	
	public static Sets set(String field, Object value) {
		return new Sets(field+" = :"+field.toLowerCase(), field.toLowerCase(), value);
	}

	

	
	
	
}
