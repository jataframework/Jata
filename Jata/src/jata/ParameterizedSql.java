/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata;

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

/**
 *
 * @author sochunyui
 */
class ParameterizedSql {
    
    
    static Pattern p = Pattern.compile("\\:([a-zA-Z_0-9]+)");
    
    
    
    String sql;
    String preparedSql;
    

    Map<String, List<Integer>> paramIndexMap = new HashMap();
    
    ParameterizedSql(String sql) {
        this.sql = sql;
        init();
        System.out.println("init:: preparedSql = "+preparedSql);
    }
    
    
    
    void init() {
        
        Matcher m = p.matcher(sql);    
        
        int index = 0;
        while (m.find()) {
            index++;
            String param = m.group(1);
            if (!paramIndexMap.containsKey(param))
                paramIndexMap.put(param, new LinkedList());
            paramIndexMap.get(param).add(index);          
        }
        
        preparedSql = sql;
        paramIndexMap.keySet().forEach((param) -> {
            preparedSql = preparedSql.replace(":"+param, "?");
        });
        
        
                
    }
    
    
    public PreparedStatement makePreparedStatenebt(Connection c, Map<String, Object> params) {
    	try {
	    	PreparedStatement ps = c.prepareStatement(preparedSql);
	        params.forEach((k,v) -> {
	            List<Integer> indexList = getParamIndexList(k);
	            indexList.forEach(i -> {
	                try {
	                    ps.setObject(i, v);
	                } catch (SQLException ex) {
	                    Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
	                }
	            });
	        });     
	        return ps;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
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
    
    
    
    
    
    static Map<String, ParameterizedSql> sqls = new HashMap();
    public static ParameterizedSql getParamterizedSql(String sql) {
        if (!sqls.containsKey(sql)) {
            sqls.put(sql, new ParameterizedSql(sql));
        }
        return sqls.get(sql);
    }
    
}
