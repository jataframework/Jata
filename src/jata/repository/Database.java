/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.repository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author sochunyui
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Database {
    
    String value();
    String table() default "";
}
