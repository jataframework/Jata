package jata.reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jata.repository.DBField;

public class JTField {
	
	private Field field;
	
	JTField(Field field) {
		this.field = field;
		field.setAccessible(true);
	}
	
	static JTField create(Field field) {
		return new JTField(field);
	}
	
	
	
	
	public Object get(Object o) throws IllegalArgumentException, IllegalAccessException {
		return field.get(o);
	}
	
	public Object getValue(Object o) {
		try {
			return field.get(o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
	public void set(Object o, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(o, value);
	}
	
	public boolean setValue(Object o, Object value) {
		try {
			field.set(o, value);
			return true;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getName() {
		return field.getName();
	}
	
	public <T extends Annotation> T getAnnotation(Class<T> ct) {
		return field.getAnnotation(ct);
	}
	
	public boolean has(Class<? extends Annotation> ct) {
		return field.isAnnotationPresent(ct);
	}

	
	public Class<?> getType() {
		return field.getType();
	}
	
	


}
