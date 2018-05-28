package jata.repository;

import jata.jatasql.Record;

public interface RowMapper<T> {
	
	
	T map(Record record);
	

}
