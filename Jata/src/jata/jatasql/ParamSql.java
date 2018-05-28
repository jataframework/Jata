/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.jatasql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author sochunyui
 */
class ParamSql {
    
    
    static Pattern p = Pattern.compile("\\:([a-zA-Z_0-9]+)");
    
    
    
    String sql;
    String preparedSql;
    PreparedStatement ps;
    

    Map<String, List<Integer>> paramIndexMap = new HashMap();
    
    ParamSql(String sql) {
        this.sql = sql;
        init();
        System.out.println("init:: preparedSql = "+preparedSql);
    }
    
    
    
    void init() {
        
        Matcher m = p.matcher(sql);    
        
        int index = 0;
        while (m.find()) {
            index++;
            String param = m.group(1).toLowerCase();
            if (!paramIndexMap.containsKey(param))
                paramIndexMap.put(param, new LinkedList());
            paramIndexMap.get(param).add(index);          
        }
        
        preparedSql = sql;
        List<String> keys = paramIndexMap.keySet().stream().collect(Collectors.toList());        
        Collections.sort(keys, (a,b)->a.length() < b.length() ? 1 : a.length() == b.length() ? 0 : -1);		// descending order of the length => name will not substitute names
        keys.forEach(param -> {
            preparedSql = preparedSql.replace(":"+param, "?");
        });
        
        
                
    }
    
    
    public PreparedStatement makePreparedStatement(Connection c, Map<String, Object> params) {
    	try {
	    	PreparedStatement ps = c.prepareStatement(preparedSql);
	    	setValues(ps, params);
	        return ps;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public boolean prepareStatement(Connection c) {
    	try {
	    	this.ps = c.prepareStatement(preparedSql);
	    	return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}    	
    }
    
    public int execute(Map<String, Object> params) {
    	if (this.ps == null) {
    		return -1;   		
    	}    	
    	try {
        	this.ps.clearParameters();
        	setValues(ps, params);
			return ps.executeUpdate();
		} catch (SQLException e) {			
			e.printStackTrace();
			return -1;
		}
    }
    
    public boolean closeOnCompletion() {
    	try {
    		this.ps.clearParameters();
			this.ps.closeOnCompletion();
			this.ps = null;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    private void setValues(PreparedStatement ps, Map<String, Object> params) {
    	if (params != null) {
	        params.forEach((k,v) -> {
	            List<Integer> indexList = getParamIndexList(k.toLowerCase());
	            indexList.forEach(i -> {
	                try {
	                    ps.setObject(i, v);
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });
	        });     
    	}    	
    }
    
    
    public final Set<String> getParamSet() {
        return Collections.unmodifiableSet(paramIndexMap.keySet());
    }
    
    public final List<Integer> getParamIndexList(String param) {
        return Collections.unmodifiableList(paramIndexMap.get(param));
    }
    
    public final String getPreparedSql() {
        return preparedSql;
    }
    
    
    
    
    
    static Map<String, ParamSql> sqls = new HashMap();
    public static ParamSql getParamterizedSql(String sql) {
        if (!sqls.containsKey(sql)) {
            sqls.put(sql, new ParamSql(sql));
        }
        return sqls.get(sql);
    }
    
}
