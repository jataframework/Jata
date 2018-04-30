/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.db.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author sochunyui
 */
public class TableInfo {
    
    


    private String name;
    private List<String> primaryKeys;
    private JataTableSchema schema;


    public String getName() {
        return name;
    }
    

    public final Collection<String> getPrimaryKeys() {
        return Collections.unmodifiableCollection(primaryKeys);
    }
    
    public final JataTableSchema getSchema() {
        return schema;
    }

    public TableInfo(String name, List<String> primaryKeys, JataTableSchema schema) {
        this.name = name;
        this.primaryKeys = primaryKeys;
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "TableInfo{" + "name=" + name + ", primaryKeys=" + primaryKeys + ", schema=" + schema + '}';
    }



    
    
    
    
    
    
}
