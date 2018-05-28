package jata.repository;



import static jata.repository.EntityCondition.toFilters;
import static jata.repository.EntityCondition.toSets;
import static jata.repository.EntityCondition.toUpdateFilters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jata.DataSource;
import jata.Utils.Collections;
import jata.jatasql.Record;
import jata.jatasql.SqlClient;
import jata.jatasql.SqlTable;
import jata.reflections.JTClass;

import static jata.repository.EntityCondition.*;


class MongoEntityCrud extends EntityCrud {



	MongoClient client = null;
	MongoDatabase db = null;
	
	protected MongoEntityCrud(String dbName) {
		DataSource ds = DataSource.get(dbName);
		client = MongoClients.create(ds.getUrl());
		db = client.getDatabase(ds.getName());
	}
	    

	private MongoCollection<Document> getCollection(Object condition) {		
		return db.getCollection(EntityCrud.getEntityName(condition.getClass()));
	}
	



	private Iterable<Document> doSelectDocuments(Object condition) {
		return getCollection(condition).find(makeFilters(condition, false));
	}
	


	@Override
	protected Map<String, Object> doSelectOneMap(Object condition) {
		Iterator<Document> it = doSelectDocuments(condition).iterator();
		return it.next();
	}


	@Override
	protected String doSelectOneJson(Object condition) {
		Iterator<Document> it = doSelectDocuments(condition).iterator();
		return it.next().toJson();
	}


	@Override
	protected <T> T doSelectOne(T condition) {
		return newEntity((Class<T>)condition.getClass(), doSelectOneMap(condition));
	}
	
	



	@Override
	protected List<Map<String, Object>> doSelectListMap(Object condition) {
		return StreamSupport.stream(doSelectDocuments(condition).spliterator(), false).collect(Collectors.toList());
	}



	@Override
	protected String doSelectJson(Object condition) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		Iterator<Document> it = doSelectDocuments(condition).iterator();
		while (it.hasNext()) {
			sb.append(it.next().toJson());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}


	@Override
	protected <T> List<T> doSelectList(T condition) {		
		return doSelectListMap(condition).stream().map(d->newEntity((Class<T>)condition.getClass(),d)).collect(Collectors.toList());
	}



	@Override
	protected long doCount(Object condition) {
		return getCollection(condition).count(makeFilters(condition, false));
	}


	@Override
	protected <T> boolean doInsert(T t) {
		try {
			Document d = new Document();
			d.putAll(JTClass.getValueMap(t));
			getCollection(t).insertOne(d);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	


	@Override
	protected <T> long doInsert(List<T> list) {
		if (list == null || list.size() <= 0) 
			return -1;
		return list.stream().filter(t->doInsert(t)).count();
	}




	@Override
	protected <T> boolean doUpdate(T t) {
		return getCollection(t).updateOne(makeKeyBson(t), makeUpdate(t)).getModifiedCount() == 1;
	}
	

	@Override
	protected <T> long doUpdate(List<T> list) {
		if (list == null || list.size() <= 0) 
			return -1;
		return list.stream().filter(t->doUpdate(t)).count();
	}



	@Override
	protected <T> boolean doDelete(T t) {
		return getCollection(t).deleteOne(makeFilters(t, true)).getDeletedCount() == 1;
	}


	@Override
	protected <T> long doDelete(List<T> list) {
		if (list == null || list.size() <= 0) 
			return -1;
		return list.stream().filter(t->doDelete(t)).count();
	}
        
	
}
