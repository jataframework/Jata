/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata.exception;

/**
 *
 * @author sochunyui
 */
public class DuplicatedBeanNameException extends Exception{
    
    
    public DuplicatedBeanNameException(String name, String oldClassName) {
        super(getMessage(name, oldClassName));
    }
    
    
    public static String getMessage(String name, String oldClassName) {
        return name +" is already mapped to "+oldClassName;
    }
    
}
