package jata.Utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TwoKeyMap<K1,K2,V> {
	
	
	Map<K1, V> map1 = new LinkedHashMap<K1, V>();
	Map<K2, V> map2 = new LinkedHashMap<K2, V>();
	
	
	
	
	public void put(K1 k1, K2 k2, V v) {
		map1.put(k1, v);
		map2.put(k2, v);
	}
	
	public V get(K1 k1, K2 k2) {
		if (containsKey(k1,k2)) {
			return map1.get(k1);
		}
		return null;
	}
	
	public boolean containsKey(K1 k1, K2 k2) {
		return map1.containsKey(k1) && map2.containsKey(k2);
	}
	
	public Set<K1> key1Sets() {
		return map1.keySet();
	}
	
	public Set<K2> key2Sets() {
		return map2.keySet();
	}
	
	public Collection<V> valueSets() {
		return map1.values();
	}

}
