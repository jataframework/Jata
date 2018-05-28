package jata.jatasql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jata.Utils.Counter;





public class SqlClient {

	
	String url;
	Connection connection; 
	
	

	
	SqlClient(String url) {
		this.url = url;
		try {			
			connection = DriverManager.getConnection(url);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	
	public SqlTable<Record> getTable(String tableName) {
		return new SqlTable<Record>(tableName, this, Record.class);
	}
	
	public <T> SqlTable<T> getTable(String tableName, Class<T> ct) {
		return new SqlTable<T>(tableName, this, ct);
	}
	
	
	public boolean commit() {
		try {
			connection.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean close() {
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	


	public SqlQueryResult select(String sql, Map<String, Object> params) {
		try {
			ParamSql psql = ParamSql.getParamterizedSql(sql);
			PreparedStatement ps = psql.makePreparedStatement(connection, params);
			ResultSet rs = ps.executeQuery();
	        ps.closeOnCompletion();
	        return new SqlQueryResult(rs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int execute(String sql, Map<String, Object> params) {
		try {
			ParamSql psql = ParamSql.getParamterizedSql(sql);
			PreparedStatement ps = psql.makePreparedStatement(connection, params);
			int r = ps.executeUpdate();
			ps.closeOnCompletion();
	        return r;		    
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}	
	
	public int execute(String sql, List<Map<String, Object>> paramsList) {
		ParamSql psql = ParamSql.getParamterizedSql(sql);
		psql.prepareStatement(connection);
		Counter counter = new Counter();
		paramsList.forEach(params-> counter.increase(psql.execute(params)));
		psql.closeOnCompletion();
        return counter.value();
	}	
	
	public static SqlClient create(String url) {
		return new SqlClient(url);
	}
	
}
