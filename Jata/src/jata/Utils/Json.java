package jata.Utils;

import java.lang.reflect.Type;
import java.util.Map;

import org.bson.Document;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Json {
	
	
	
	static Gson gson = new Gson(); 
	static Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
	
	
	
	public static String toJson(Object o) {
		return gson.toJson(o);		
	}
	
	public static <T> T fromJson(String json, Class<T> ct) {
		return gson.fromJson(json, ct);
	}
	
	public static int[] toInts(String json) {
		return gson.fromJson(json, int[].class);
	}
	
	public static String[] toStrings(String json) {
		return gson.fromJson(json, String[].class);
	}	
	
	
	public static Map<String, Object> toMap(String json) {
		return gson.fromJson(json, mapType);
	}

	
	public static String merge(String... json) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int i=0;i<json.length;i++) {
			sb.append(json[i]);
			sb.append(i<json.length-1?", ":"");
		}
		sb.append(" ]");
		return sb.toString();		
	}
	
	public static String merge(Iterable<String> iterableJson) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		Iterator<String> it = iterableJson.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(", ");
		}
		sb.append(" ]");		
		return sb.toString();		
	}	
	
}
