package jata;

import java.util.LinkedList;
import java.util.List;

public class JataTransaction {
	
	
	
	JataTransactionCall call;
	List<Transaction> transactionList = new LinkedList();
	
	
	public JataTransaction(JataTransactionCall call) {
		this.call = call;
	}
	
	
	
	void AddTransaction(Transaction transaction) {
		transactionList.add(transaction);
	}
	
	void rollbackAll() {
		transactionList.forEach(t->t.rollback());
	}
	
	void commitAll() {
		transactionList.forEach(t->t.commit());
	}	
	
	public void start() {
		try {
			call.call();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

}
