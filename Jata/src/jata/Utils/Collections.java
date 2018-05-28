package jata.Utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class Collections<T, K, V> {

	
	public static <T> void forNext(Iterable<T> list, ForCall<T> forCall) {
		int count = 0;
		Iterator<T> it = list.iterator();
		while (it.hasNext()) {
			forCall.foreach(it.next(), count);
			count++;
		}
	}
	
	public static <T> void forNextLast(List<T> list, ForNextLastCall<T> call) {
		int last = list.size()-1;
		for (int i=0;i<list.size();i++) {
			call.foreach(list.get(i), i, last);
		}
	}
	
	public static <T> void forLast(List<T> list, Call<T> call, Call<T> last) {
		for (int i=0;i<list.size();i++) {
			T t = list.get(i);
			if (i<list.size()-1) {
				call.call(t);
			} else {
				last.call(t);
			}
		}
	}	
	
	
	public static <T> T[] toArray(Collection<T> tlist) {
		if (tlist != null || tlist.size() > 0) {
			Iterator<T> it = tlist.iterator();
	        T[] ts = (T[]) Array.newInstance(it.next().getClass(), tlist.size());
	        return tlist.toArray(ts);		
		} else {
			return null;
		}
	}
	
	
//	public static <T> List<T> asList(Iterable<T> iterable) {
//		Iterator<T> it = iterable.iterator();
//		List<T> list = new LinkedList<T>();
//		while (it.hasNext()) {
//			list.add(it.next());
//		}
//		return list;
//	}
	
	public static <T> List<T> asList(Iterable<? extends T> iterable) {
		Iterator<? extends T> it = iterable.iterator();
		List<T> list = new LinkedList<T>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}	
	
	
	public static String toString(Map map) {		
        StringBuilder sb = new StringBuilder();
        Iterator<Entry> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();		
	}
	
}
