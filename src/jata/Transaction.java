package jata;


import jata.exception.DisposedTransactionException;
import jata.ParameterizedSql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class Transaction {
	
	
	
 
    private TransactionManager manager;
    private DataSource dataSource;

    private boolean disposed;
    
    
  


    Connection connection = null;
    Statement stmt = null;
    


    Transaction(DataSource dataSource, TransactionManager manager) {        
    	this.dataSource = dataSource;
    	this.manager = manager;
        openConnection();
    }


    void openConnection() {
        try {
            connection = DriverManager.getConnection(dataSource.getUrl());
            connection.setAutoCommit(false); 
            stmt = connection.createStatement();
        } catch ( SQLException e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }   
	
    
    void checkDisposed() throws Exception {
    	if (disposed) {
    		throw new DisposedTransactionException();
    	}
    }
    
	
    int execute(String sql) throws Exception  {
        checkDisposed();
  
        try {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            return -1;
        }
    }
    
    
    int execute(String sql, Map<String, Object> params) throws Exception {
    	checkDisposed();
        try {
        	System.out.println("DB url: "+dataSource.getUrl());
            ParameterizedSql psql = ParameterizedSql.getParamterizedSql(sql);
            PreparedStatement ps = connection.prepareStatement(psql.getPreparedSql());
            params.forEach((k,v) -> {
                List<Integer> indexList = psql.getParamIndexList(k);
                indexList.forEach(i -> {
                    try {
                        ps.setObject(i, v);
                    } catch (SQLException ex) {
                        Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });            
            int count = ps.executeUpdate();
            ps.close();           
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }    
    

    
    DatabaseMetaData selectDataBaseMetaData()  {
        try {
            return connection.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    
    ResultSet select(String sql, Map<String, Object> params) throws Exception {
    	checkDisposed();       
        try {
            ParameterizedSql psql = ParameterizedSql.getParamterizedSql(sql);
            PreparedStatement ps = connection.prepareStatement(psql.getPreparedSql());
            params.forEach((k,v) -> {
                List<Integer> indexList = psql.getParamIndexList(k);
                indexList.forEach(i -> {
                    try {
                        ps.setObject(i, v);
                    } catch (SQLException ex) {
                        Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });            
            ResultSet rs = ps.executeQuery();
            ps.closeOnCompletion();
            return rs;
        } catch ( IllegalArgumentException | SQLException e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
            System.err.println(sql);           
           return null;
        }        
    }     
    
    ResultSet select(String sql) throws Exception {
    	checkDisposed();    
        try {
            return stmt.executeQuery(sql);    
        } catch ( IllegalArgumentException | SQLException e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
            System.err.println(sql);           
           return null;
        }        
    } 
    
    public boolean commit() {     
    	try {
            connection.commit();
            return dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean rollback() {
    	try {
            connection.rollback();
            return dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean close() {
    	return dispose();
    }
    
    boolean dispose() {
    	try {
			stmt.close();
	    	connection.close();
	        if (manager != null) {
	            manager.dispose(this);
	            this.disposed = true;
	        }	    	
	        return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
    

 

}
