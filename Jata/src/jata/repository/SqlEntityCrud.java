package jata.repository;



import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import jata.DataSource;
import jata.Utils.Collections;
import jata.Utils.Json;
import jata.jatasql.Record;
import jata.jatasql.SqlClient;
import jata.jatasql.SqlTable;
import jata.reflections.JTClass;
import jata.reflections.JTField;
import static jata.repository.EntityCondition.*;

class SqlEntityCrud extends EntityCrud {
	
	
	

	
	SqlClient client = null;
	String dbName;
	
	protected SqlEntityCrud(String dbName) {
		this.dbName = dbName;
		
	}
	    

	private SqlTable<Record> getTable(Object condition) {
		client = SqlClient.create(DataSource.get(dbName).getUrl());
		return client.getTable(EntityCrud.getEntityName(condition.getClass()));	
	}
	

	private Record doSelectOneRecord(Object condition) {
		Record r = getTable(condition).findOne(toFilters(condition, false));
		client.close();
		return r;
	}

	@Override
	protected Map<String, Object> doSelectOneMap(Object condition) {
		return doSelectOneRecord(condition);
	}


	@Override
	protected String doSelectOneJson(Object condition) {
		return doSelectOneRecord(condition).toJson();
	}


	@Override
	protected <T> T doSelectOne(T condition) {
		T t = newEntity((Class<T>)condition.getClass(), getTable(condition).findOne(toFilters(condition, false)));
		client.close();
		return t;
	}
	
	
	private List<Record> doSelectRecordList(Object condition) {		
		List<Record> rl = getTable(condition).find(toFilters(condition, false));
		client.close();
		return rl;
	}	



	@Override
	protected List<Map<String, Object>> doSelectListMap(Object condition) {
		return doSelectRecordList(condition).stream().map(map->(Map<String,Object>)map).collect(Collectors.toList());
	}



	@Override
	protected String doSelectJson(Object condition) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		Collections.forNextLast(doSelectRecordList(condition), (r, i, last) -> {
			sb.append(r.toJson());
			if (i<last) {
				sb.append(", ");
			}
		});
		sb.append(" ]");
		return sb.toString();
	}


	@Override
	protected <T> List<T> doSelectList(T condition) {		
		List<T> tl = getTable(condition).find(toFilters(condition, false)).stream().map(r->(T)newEntity(condition.getClass(), r)).collect(Collectors.toList());
		client.close();
		return tl;
	}





	@Override
	protected long doCount(Object condition) {
		long l = getTable(condition).count(toFilters(condition, false));
		client.close();
		return l;
	}





	@Override
	protected <T> boolean doInsert(T t) {	
		boolean i = getTable(t).insert(Record.create(JTClass.getValueMap(t))) == 1;
		client.commit();
		client.close();
		return i;
	}



	@Override
	protected <T> long doInsert(List<T> list) {
		if (list == null || list.size() == 0) 
			return -1;	
		SqlTable<Record> table = getTable(list.get(0));
		long c = list.stream().filter(t->table.insert(Record.create(JTClass.getValueMap(t))) == 1).count();
		client.commit();
		client.close();
		return c;
	}




	@Override
	protected <T> boolean doUpdate(T t) {
		boolean i = getTable(t).update(toUpdateFilters(t), toSets(t)) == 1;
		client.commit();
		client.close();
		return i;
	}




	@Override
	protected <T> long doUpdate(List<T> list) {
		if (list == null || list.size() == 0) 
			return -1;		
		SqlTable<Record> table = getTable(list.get(0));
		long u = list.stream().filter(t->table.update(toUpdateFilters(t), toSets(t)) == 1).count();
		client.commit();
		client.close();
		return u;
	}

	
	
	

	@Override
	protected <T> boolean doDelete(T t) {
		boolean i = getTable(t).delete(toFilters(t, true)) == 1;
		client.commit();
		client.close();
		return i;
	}



	@Override
	protected <T> long doDelete(List<T> list) {
		if (list == null || list.size() == 0) 
			return -1;		
		SqlTable<Record> table = getTable(list.get(0));
		long d = list.stream().filter(t->table.delete(toFilters(t, true)) == 1).count();
		client.commit();
		client.close();
		return d;
	}


        
	
}
