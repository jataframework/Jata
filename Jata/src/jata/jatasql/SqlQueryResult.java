/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.jatasql;


import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Arrays;


import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author sochunyui
 */
public class SqlQueryResult implements Closeable {
    
    
    private String[] cols;
    private ResultSet rs;


 
    
    SqlQueryResult(ResultSet rs) {
        try {
            this.rs = rs;   
            ResultSetMetaData data = rs.getMetaData();            
            this.cols = new String[data.getColumnCount()];
            for (int i=1;i<=this.cols.length;i++) {
                this.cols[i-1] = data.getColumnLabel(i);
            }         
        } catch (SQLException ex) {
            Logger.getLogger(SqlQueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
 
    
    
    public List<String> getColumns() {
        return Arrays.asList(cols);
    }
      

 
    
 

    

    
  
    public Record nextRecord() {
        try {
            if (rs != null && rs.next()) {
            	return getRecord();
            } else {
                close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SqlQueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    


    public Record getRecord() {
        try {
            Record map = new Record();
            for (int i=0;i<cols.length;i++) {                	
                map.put(cols[i].toUpperCase(), rs.getObject(cols[i]));
            }                
            return map;
        } catch (SQLException ex) {
            Logger.getLogger(SqlQueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    
    public void close() {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SqlQueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


	
	public List<Record> toList() {
		List<Record> list = new LinkedList();
		try {
			while (rs.next()) {
				list.add(getRecord());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return list;
	}    
    
}
