/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author sochunyui
 */
public class QueryResult implements Closeable {
    
    
    private String[] cols;
    private ResultSet rs;
    private Transaction transaction;
    private String sql;

    
    
    public QueryResult(ResultSet rs, String sql, Transaction transaction) {
        try {
            this.rs = rs;
            this.sql = sql;     
            this.transaction = transaction;
            ResultSetMetaData data = rs.getMetaData();            
            this.cols = new String[data.getColumnCount()];
            for (int i=1;i<=this.cols.length;i++) {
                this.cols[i-1] = data.getColumnLabel(i);
            }                                      
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String getSql() {
        return sql;
    }
    
    public List<String> getColumns() {
        return Arrays.asList(cols);
    }
      
    public int columnCount() {
        return cols.length;
    }
    
    public List<String> skipFirstColumn() {
        return getColumns().subList(1, getColumns().size()-1);
    }
    
    public List<String> skipFirst2Columns() {
        return getColumns().subList(2, getColumns().size()-1);
    }    
    

    public boolean hasNext() {
        try {
            return !rs.isLast();
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean next() {
        try {
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public Object getValue(String field) {
        try {
            return rs.getObject(field);
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public List<Object> nextRecord() {                       
        try {
            if (rs != null && rs.next()) {
                List<Object> values = new LinkedList();
                for (int i=0;i<cols.length;i++) {
                    values.add(rs.getObject(cols[i]));
                }                
                return values;
            } else {
                close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Map<String, Object> nextMap() {
        try {
            if (rs != null && rs.next()) {
                Map<String, Object> map = new HashMap();
                for (int i=0;i<cols.length;i++) {                	
                    map.put(cols[i].toUpperCase(), rs.getObject(cols[i]));
                }                
                return map;
            } else {
                close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ObservableList<List<Object>> getAll() {
        ObservableList<List<Object>> list = FXCollections.observableArrayList();
        List<Object> record = nextRecord();
        while (record != null) {
            list.add(record);
            record = nextRecord();
        }        
        return list;
    }
    

    @Override
    public void close() {
        try {
            if (rs != null) {
                rs.close();
                transaction.close();
                rs = null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
