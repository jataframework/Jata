/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.repository;


import java.util.List;
import java.util.Map;


import jata.Utils.Json;






/**
 *
 * @author sochunyui
 */
public class EntityDTO {
    
    
   
    public boolean insert() {
    	return EntityCrud.insert(this);    	
    }
    
    public boolean update() {
    	return EntityCrud.update(this);
    }
    

	public <T> List<T> select() {
		return (List<T>) EntityCrud.selectList(this);
	}
	
	public String selectJson() {
		return EntityCrud.selectJson(this);
	}
	
	public List<Map<String, Object>> selectListMap() {
		return EntityCrud.selectListMap(this);
	}
	
	public <T> T selectOne() {
		return (T) EntityCrud.selectOne(this);
	}
	
	public String selectOneJson() {
		return EntityCrud.selectOneJson(this);
	}
	
	public Map<String, Object> selectOneMap() {
		return EntityCrud.selectOneMap(this);
	}
	
	public <T> T[] selects() {
		return (T[]) EntityCrud.selectArray(this);
	}
	
	public long count() {
		return EntityCrud.count(this);
	}
	
	
	
	public String toJson() {
		return Json.toJson(this);
	}

    
}
