/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.jatasql;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import jata.Utils.Collections;
import jata.Utils.Json;
import jata.reflections.JTClass;

/**
 *
 * @author sochunyui
 */

public class Record extends HashMap<String, Object> {
    
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6423475620794748848L;



	public static Record create(Map<String, Object> map) {
		return new Record(map);
	}

	
	
	Record(Map<String, Object> map) {
		super.putAll(map);
	}
	
	public Record() {
		
	}

	public Record append(String field, Object value) {
		put(field, value);
		return this;
	}


	
	

	public int getInt(String field) {
		return Integer.parseInt(get(field.toUpperCase()).toString());
	}
	
	public long getLong(String field) {
		return Long.parseLong(get(field.toUpperCase()).toString());
	}	
	
	public short getShort(String field) {
		return Short.parseShort(get(field.toUpperCase()).toString());
	}	
	
	public double getDouble(String field) {
		return Double.parseDouble(get(field.toUpperCase()).toString());
	}	
	
	public float getFloat(String field) {
		return Float.parseFloat(get(field.toUpperCase()).toString());
	}	
	
	public char getChar(String field) {
		return get(field.toUpperCase()).toString().charAt(0);
	}	
	
	public Date getDate(String field) {
		return (Date)get(field.toUpperCase());
	}
	
	public BigDecimal getBigDecimal(String field) {
		return (BigDecimal)get(field.toUpperCase());
	}
	
	public BigInteger getBigInteger(String field) {
		return (BigInteger)get(field.toUpperCase());
	}	
	
	public String getString(String field) {
		return get(field.toUpperCase()).toString();
	}	
	

	public String toJson() {
		return Json.toJson(this);
	}
	
	
	
	
	
	
	/*String toSql() {
		StringBuilder sb = new StringBuilder();
		if (this.size() > 0) {
			sb.append(" where ");
			Collections.forNext(keySet(), (field, i) -> {						
				sb.append(field).append(" = ").append(":").append(field.toLowerCase());
				if (i<this.size()-1)
					sb.append(", ");					
			});
			sb.append(")");
		}
		return sb.toString();			
	}	*/
	
}
