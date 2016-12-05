package dwf.web.taglib;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormat;

import com.github.slugify.Slugify;

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
	
	public static String periodFormat(Object obj, Locale locale) {
		if(locale == null) {
			locale = Locale.getDefault();
		}
		ReadablePeriod period;
		LocalDate inicio = new LocalDate();
		LocalDate fim ;
		if(obj instanceof java.util.Date) {
			fim = LocalDate.fromDateFields((java.util.Date) obj);
		} else if(obj instanceof Calendar) {
			fim = LocalDate.fromCalendarFields((Calendar) obj);
		} else {
			fim = new LocalDate(obj);
		}
		period = org.joda.time.Period.fieldDifference(inicio, fim);
		return PeriodFormat.wordBased(locale).print(period);
	}
	
	public static String escapeUrlPathSegment(String x) {
		return com.google.common.net.UrlEscapers.urlPathSegmentEscaper().escape(x);
	}
	public static String escapeUrlFormParameter(String x) {
		return com.google.common.net.UrlEscapers.urlFormParameterEscaper().escape(x);
	}
	public static String escapeUrlFragment(String x) {
		return com.google.common.net.UrlEscapers.urlFragmentEscaper().escape(x);
	}
	
	private static final Slugify slugify = new Slugify();

	public static String slugify(String x) {
		return slugify.slugify(x);
	}
}
