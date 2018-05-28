package jata.Utils;

public class Counter {

	
	int count = 0;
	
	private int scale = 1;
	

	
	public int getScale() {
		return scale;
	}


	public void setScale(int scale) {
		this.scale = scale;
	}




	public void up() {
		count += scale;
	}
	
	public void increase(int amount) {
		count += amount;
	}
	
	public void down() {
		count -= scale;
	}
	
	public void descrease(int amount) {
		count -= amount;
	}
	
	public int value() {
		return count;
	}
	
}
