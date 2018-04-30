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
public class JataTableSchema {
    
    
    
    
    List<JataTableField> fieldList = null;

    public final List<JataTableField> getFieldList() {
        return Collections.unmodifiableList(fieldList);
    }
    


    public JataTableSchema(List<JataTableField> fieldList) {
        this.fieldList = fieldList;
    }
    
    
    
    
    
    
    
    
}
