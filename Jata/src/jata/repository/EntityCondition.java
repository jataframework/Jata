package jata.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;


import jata.dependency.New;
import jata.dependency.Singleton;
import jata.jatasql.Condition;
import jata.jatasql.Sets;
import jata.reflections.JTClass;
import jata.reflections.JTField;

class EntityCondition {
	
  
	

	
	
	
	public static Condition toFilters(Object o, boolean nullable) {
		Class<?> ct = o.getClass();
		List<JTField> fieldList = EntityCrud.getFields(o.getClass());
		List<Condition> conditionList = fieldList.stream().map(field -> {
			Object value = field.getValue(o);
			return value != null || nullable ? Condition.eq(field.getName(), value) : null;			
		}).filter(Objects::nonNull).collect(Collectors.toList());
		if (conditionList.size() > 0)
			return Condition.and(conditionList);
		return null;
	}
	
	public static Condition toUpdateFilters(Object o) {
		List<JTField> fieldList = EntityCrud.getKeyFields(o.getClass());
		List<Condition> conditionList = fieldList.stream().map(field -> {
			Object value = field.getValue(o);
			return value != null ? Condition.eq(field.getName(), value) : null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		if (conditionList.size() > 0)
			return Condition.and(conditionList);
		return null;
	}	
	
	public static Sets toSets(Object o) {
		List<JTField> fieldList = EntityCrud.getFields(o.getClass());		
		Sets sets = null;
		for (JTField field : fieldList) {
			Object value = field.getValue(o);
			if (value != null) {
				if (sets == null) {
					sets = Sets.set(field.getName(), value);
				}
				sets.add(field.getName(), value);
			}
		};
		return sets;
		
	}
	
	
	

    
    public static Bson makeFilters(Object condition, boolean nullable) {
    	List<JTField> fieldList = JTClass.getFields(condition.getClass());
    	List<Bson> filterList = fieldList.stream().map(field -> {
			Object value = field.getValue(condition);
			return nullable || value != null ? Filters.eq(field.getName(), value) : null;			
    	}).filter(Objects::nonNull).collect(Collectors.toList());
    	return Filters.and(filterList);
    	
    }



    public static Bson makeUpdate(Object condition)  {
    	List<JTField> fieldList = JTClass.getFields(condition.getClass());
    	List<Bson> bsonList = fieldList
    			.stream()
    			.map(field-> Updates.set(field.getName(), field.getValue(condition)))
    			.filter(Objects::nonNull)
    			.collect(Collectors.toList());	    	
    	return Updates.combine(bsonList);    	
    }



    public static Bson makeKeyBson(Object condition)  {
    	List<JTField> fieldList = EntityCrud.getKeyFields(condition.getClass());
    	List<Bson> bsonList = fieldList
    			.stream()
    			.map(field-> Updates.set(field.getName(), field.getValue(condition)))
    			.filter(Objects::nonNull)
    			.collect(Collectors.toList());	    	
    	return Updates.combine(bsonList);    	
    }	

}
