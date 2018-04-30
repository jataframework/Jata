/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata;




import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author sochunyui
 */
public class Jata {
	
	
    static String jarfolder=null;            
    public static String jarFolder() {
        try {
            if (jarfolder == null)
                jarfolder = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile().getCanonicalPath();
            return jarfolder;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }	
    
    


    
    static Map<String, Object> singletonMap = new HashMap();
    public static <T> T get(Class<T> ct) {
        if (!singletonMap.containsKey(ct.getName())) {
            singletonMap.put(ct.getName(), JataUtils.makeBean(ct));            
        }
        return (T)singletonMap.get(ct.getName());
    }


    
    
    
    
 
    
}
