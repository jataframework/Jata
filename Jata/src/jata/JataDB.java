package jata;






import jata.Utils.Collections;
import jata.db.structure.JataTableField;
import jata.db.structure.JataTableSchema;
import jata.db.structure.TableInfo;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



class JataDB {
	
	


    private TransactionManager manager;
    
 
      
    JataDB(DataSource dataSource) {
        manager = TransactionManager.get(dataSource);
    }
    


    public int execute(String sql, Map<String, Object> params) {       
    	Transaction transaction = manager.mustCreate();
    	try {
    		int r = transaction.execute(sql, params);
    		transaction.commit();
    		return r;
    	} catch (Exception e) {
    		transaction.rollback();
    		return -1;
    	}    			
    }       
    

    
    public QueryResult select(String sql, Map<String, Object> params) {
    	Transaction transaction = manager.mustCreate();    
    	try {
	    	if (params == null) {
	    		return new QueryResult(transaction.select(sql), sql, transaction);
	    	} else {
	    		return new QueryResult(transaction.select(sql, params), sql, transaction);
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		transaction.close();
    		return null;
    	}
    }    
    


    
    public List<String> selectAllTables() {
        List<String> list = new LinkedList();
        Transaction transaction = manager.mustCreate();         
        DatabaseMetaData meta = transaction.selectDataBaseMetaData();
        try (ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }            
        } catch (SQLException ex) {
            Logger.getLogger(JataDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        transaction.close();
        return list;
    }    

    public List<TableInfo> selectAllTableInfos() {
        List<TableInfo> list = new LinkedList();
        Transaction transaction = manager.mustCreate();          
        DatabaseMetaData meta = transaction.selectDataBaseMetaData();
        try (ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String catalog = rs.getString("TABLE_CAT");
                String schema = rs.getString("TABLE_SCHEM");
                String tableName = rs.getString("TABLE_NAME");
                List<String> pklist = new LinkedList();
                try (ResultSet primaryKeys = meta.getPrimaryKeys(catalog, schema, tableName)) {                    
                    while (primaryKeys.next()) {
                        pklist.add(primaryKeys.getString("COLUMN_NAME"));
                    }
                }
                JataTableSchema tableSchema = getSchema(tableName, pklist);
                list.add(new TableInfo(tableName, pklist, tableSchema));
            }            
        } catch (SQLException ex) {
            Logger.getLogger(JataDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        transaction.close();
        return list;
    }
    

    
    private JataTableSchema getSchema(String table, List<String> pklist) {
        Transaction transaction = manager.mustCreate(); 
        try (ResultSet rs = transaction.select("select * from "+table)) {
            ResultSetMetaData metaData = rs.getMetaData(); 
            List<JataTableField> fieldList = new LinkedList();
            for (int i=1;i<=metaData.getColumnCount();i++) {
                JataTableField field = new JataTableField();
                field.setName(metaData.getColumnLabel(i));
                field.setType(metaData.getColumnTypeName(i));   
                field.setKey(pklist.contains(field.getName()));                   
                fieldList.add(field);
            }            
            return new JataTableSchema(fieldList);
        } catch (Exception ex) {
            Logger.getLogger(JataDB.class.getName()).log(Level.SEVERE, null, ex);
        } 
        transaction.close();
        return null;
    }
    
    
  
    
    
    
	
    
    static Map<String, JataDB> dbMap = null;
    public static JataDB db(String name) {    
        return dbMap.get(name);
    }



    static void loadDB() {
    	if (dbMap == null) {
    		dbMap = new HashMap<String, JataDB>();    	
	        for (DataSource ds : DataSource.allDataSources()) {
	            dbMap.put(ds.getName(), new JataDB(ds));
	        }
	        System.out.println("DB Loaded");
    	}
    }
    
    
    static {
    	loadDB();
    }
    
    public static JataDB db() {
        Map.Entry<String, JataDB> entry = dbMap.entrySet().iterator().next();
        return entry.getValue();
    }
    
    


    
  
	
	
	
    
    
	

	
	
	

}
