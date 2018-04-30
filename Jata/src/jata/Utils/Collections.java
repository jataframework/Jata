package jata.Utils;

import java.lang.reflect.Array;
import java.util.List;

import jata.reflections.JTClass;

public class Collections<T> {

	
	public static <T> void forNext(List<T> list, ForCall<T> forCall) {
		for (int i=0;i<list.size();i++) {
			forCall.foreach(list.get(i), i);
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
	
	
	public static <T> T[] toArray(List<T> tlist) {
		if (tlist != null || tlist.size() > 0) {
	        T[] ts = (T[]) Array.newInstance(tlist.get(0).getClass(), tlist.size());
	        return tlist.toArray(ts);		
		} else {
			return null;
		}
	}
	
}
