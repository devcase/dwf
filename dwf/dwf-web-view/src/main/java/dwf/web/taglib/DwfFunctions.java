package dwf.web.taglib;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.iterators.ArrayIterator;

public class DwfFunctions {
	
	public static boolean memberOf(Object item, Object collection) {
		if(collection == null || item == null) return false;
		
		if(item.getClass().isEnum()) item = ((Enum<?>) item).name();
		
		Iterator<?> iterator;
		if(collection instanceof Iterator<?>) {
			iterator = (Iterator<?>) collection;
		} else if(collection instanceof Collection<?>) {
			iterator = ((Collection<?>) collection).iterator();
		} else if(collection.getClass().isArray()) {
			iterator = new ArrayIterator<Object>(collection);
		} else {
			return compare(collection, item);
		}
		while(iterator.hasNext()) {
			Object o = iterator.next();
			if(compare(item, o)) return true;
		}
		return false;
	}
	
	private static boolean compare(Object itemA, Object itemB) {
		if(itemA == null || itemB == null) {
			return itemA == itemB;
		} else {
			if(itemA.getClass().isEnum()) itemA = ((Enum<?>) itemA).name();  
			if(itemB.getClass().isEnum()) itemB = ((Enum<?>) itemB).name();
			return itemA.equals(itemB);
		}
		
	}
}
