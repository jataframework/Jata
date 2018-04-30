/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.repository;


import java.util.List;

import jata.EntityCrud;





/**
 *
 * @author sochunyui
 */
public class Entity {
    
    
	

    
    
    

    
  
    
    
    
    public boolean insert() {
    	return EntityCrud.insert(this) > 0;    	
    }
    
    public boolean update() {
    	return EntityCrud.update(this) > 0;
    }
    

	public <T> List<T> select() {
		return (List<T>) EntityCrud.selectList(this);
	}
	
	public <T> T selectOne() {
		return (T) EntityCrud.selectOne(this);
	}
	
	public <T> T[] selects() {
		return (T[]) EntityCrud.selectArray(this);
	}
	
	public long count() {
		return EntityCrud.count(this);
	}
	
	
	
	

    
}
