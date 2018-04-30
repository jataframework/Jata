/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jata;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author sochunyui
 */
public class TransactionManager {
	
	
	final static int DEFAULT_POOL_SIZE = 10;
    
    
    int poolSize;
    DataSource dataSource;
    
    
    List<Transaction> transactionlist = new LinkedList();
    
    
    public TransactionManager(DataSource dataSource, int poolSize) {    
        this.dataSource = dataSource;
        this.poolSize = poolSize;
    }
    
    public TransactionManager(DataSource dataSource) {
        this(dataSource, DEFAULT_POOL_SIZE);
    }    
    
    public void dispose(Transaction transaction) {
    	transactionlist.remove(transaction);
    }

    public Transaction mustCreate() {
        while (true) {
            Transaction t = create();
            if (t == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                return t;
            }
        }
    }
    
    public Transaction create() {
    	if (transactionlist.size() < poolSize) {
    		Transaction transaction = new Transaction(dataSource, this);
    		transactionlist.add(transaction);
    		return transaction;
    	}
    	return null;
    }
    

    
	static Map<String, TransactionManager> managerMap = new HashMap();
	public static TransactionManager get(String name) {
		return get(DataSource.get(name));
	}    
	public static TransactionManager get(DataSource ds) {
		if (!managerMap.containsKey(ds.getName())) {
			managerMap.put(ds.getName(), new TransactionManager(ds, DEFAULT_POOL_SIZE));
		}
		return managerMap.get(ds.getName());
	}  	

    
    
}
