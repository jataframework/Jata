/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.db.structure;

/**
 *
 * @author sochunyui
 */
public class JataTableField {
    
    
    
    private String name;
    private String type;
    private boolean key;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "JataDBField{" + "name=" + name + ", type=" + type + ", key=" + key + ", description=" + description + '}';
    }
    
    
    
    
    
    
    
}
