package dwf.persistence.embeddable;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

@Embeddable
public class DayOfWeekTime implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1769401502213894810L;
	
	private DayOfWeek dayOfWeek;
	private LocalTime time;
	private Long seconds; //duração em segundos
	
	
	public DayOfWeekTime() {
		super();
	}

	public DayOfWeekTime(String string){
		String[] weekdayTimeSplitted = string.split("-");
		String weekday = weekdayTimeSplitted[0];
		String time = weekdayTimeSplitted[1];
		String[] timeSplitted = time.split(":");
		
		int hours = Integer.valueOf(timeSplitted[0]);
		int minutes = Integer.valueOf(timeSplitted[1]);
		
		this.dayOfWeek = DayOfWeek.valueOf(weekday.toUpperCase());
		this.time = LocalTime.of(hours, minutes);
		this.seconds = 1800L;
	}
	
	public DayOfWeekTime(LocalTime time, DayOfWeek weekday) {
		super();
		setTime(time);
		this.dayOfWeek = weekday;
	}

	@JsonView({View.Rest.class, View.Mongo.class})
	@Transient
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	
	@JsonView({View.Rest.class, View.Mongo.class})
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@JsonView({View.Rest.class, View.Mongo.class})
	public Long getSeconds() {
		return seconds;
	}

	public void setSeconds(Long seconds) {
		this.seconds = seconds;
	}
	
	

	/**
	 * Time as Date
	 */
	@JsonIgnore
	@Temporal(TemporalType.TIME)
	@Column(name="time")
	public Date getTimeAsDate(){
		if(time == null) return null;
		return new Date(time.toSecondOfDay() * 1000);
	}
	public void setTimeAsDate(Date date){
		if(date == null) time = null;
		else {
			time = LocalTime.ofSecondOfDay((date.getTime()/ 1000) % 86400); //hibernate returns 00:00 as 86400x10^3, and not 0
		}
	}
	
	@Transient
	@JsonIgnore
	public String format(){
		return dayOfWeek.toString().toLowerCase() + "-" + time.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	
	@Override
	public boolean equals(Object compared) {
		if(compared instanceof String){
			return this.format().equals(compared);
		}else if(compared instanceof DayOfWeekTime){
			return this.format().equals(((DayOfWeekTime) compared).format());
		}else{
			return super.equals(compared);
		}
	}
	
	@Override
	public int hashCode() {
		return this.format().hashCode();
	}
	
	public static String formatOf(String weekday, String time){
		return weekday + "-" + time;
	}
	
	@Transient
	@Override
	public String toString(){
		return format();
	}
}
