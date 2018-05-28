package jata.jatasql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jata.Utils.SqlCommon;

public class Condition {
	
	
	String sql;
	Map<String, Object> params;
	

	Condition(String sql) {
		this.sql = sql;			
	}
	
	Condition(String sql, Map<String, Object> params) {
		this.sql = sql;		
		this.params = params;
	}	
	
	Condition(String sql, String field, Object value) {
		this(sql);
		this.params = new HashMap();
		this.params.put(field, value);		
	}
	
	String getSql() {
		return sql;
	}
	
	
	Map<String, Object> getParams() {
		return params;
	}

	
	
	
	public static Condition eq(String field, Object value) {
		return new Condition(field+" = :"+field.toLowerCase(), field.toLowerCase(), value);
	}	
	public static Condition gt(String field, Object value) {
		return new Condition(field+" > :"+field.toLowerCase(), field.toLowerCase(), value);
	}		
	public static Condition gte(String field, Object value) {
		return new Condition(field+" >= :"+field.toLowerCase(), field.toLowerCase(), value);
	}
	public static Condition lt(String field, Object value) {
		return new Condition(field+" < :"+field.toLowerCase(), field.toLowerCase(), value);
	}		
	public static Condition lte(String field, Object value) {
		return new Condition(field+" <= :"+field.toLowerCase(), field.toLowerCase(), value);
	}
	public static Condition like(String field, String value) {
		return new Condition(field+" like '"+value+"'");
	}
	public static Condition in(String field, Object... value) {
		return new Condition(field+" in ("+SqlCommon.join(value, ","));
	}
	public static Condition notIn(String field, Object... value) {
		return new Condition(field+" in ("+SqlCommon.join(value, ","));
	}		
	
	static Condition combine(String del, List<Condition> conditions) {
		if (conditions == null)
			return null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> params = new HashMap();
		for (int i=0;i<conditions.size();i++) {
			sb.append(conditions.get(i).getSql());
			if (conditions.get(i).getParams() != null)
				params.putAll(conditions.get(i).getParams());
			if (i<conditions.size()-1) {
				sb.append(" ").append(del).append(" ");
			}
		}
		return new Condition(sb.toString(), params);
	}
	
	public static Condition and(Condition... conditions) {
		return combine("and", Arrays.asList(conditions));
	}
	
	public static Condition and(List<Condition> conditions) {
		return combine("and", conditions);
	}
	
	public static Condition or(Condition... conditions) {
		return combine("or", Arrays.asList(conditions));
	}
	
	public static Condition or(List<Condition> conditions) {
		return combine("or", conditions);
	}	
	
	public static Condition not(Condition condition) {
		return new Condition("not ("+condition.getSql()+")", condition.getParams());
	}
	
	
	
	public Condition and(Condition condition) {
		if (condition == null)
			return null;
		return Condition.and(this, condition);
	}
	
	public Condition or(Condition condition) {
		if (condition == null)
			return null;
		return Condition.or(this, condition);
	}		

}
