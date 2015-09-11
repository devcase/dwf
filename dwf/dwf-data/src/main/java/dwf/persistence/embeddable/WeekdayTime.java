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
public class WeekdayTime implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8260105961479479940L;
	private LocalTime time;
	private DayOfWeek weekday;
	
	//TODO - 08/2015: Hibernate ainda não dá suporte a LocalTime ou OffsetTime.
	// quando possível, retirar esse campo date
	private Date date;
	
	public WeekdayTime(){}
	
	public WeekdayTime(String string){
		setWeekdayTime(string);
	}
	
	public WeekdayTime(LocalTime time, DayOfWeek weekday) {
		super();
		setTime(time);
		this.weekday = weekday;
	}

	@JsonView(View.Summary.class)
	@Transient
	public LocalTime getTime() {
		if(time == null){
			time = LocalTime.of(date.getHours(), date.getMinutes());
		}
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
		if(this.date == null){
			setDate(new Date(0, 0, 0, time.getHour(), time.getMinute()));
		}
	}
	
	@JsonIgnore
	@Temporal(TemporalType.TIME)
	@Column(name="time")
	private Date getDate(){
		return date;
	}
	
	private void setDate(Date date){
		this.date = date;
		if(this.time == null){
			setTime(LocalTime.of(date.getHours(), date.getMinutes()));
		}
	}
	
	@JsonView(View.Summary.class)
	public DayOfWeek getWeekday() {
		return weekday;
	}
	public void setWeekday(DayOfWeek weekday) {
		this.weekday = weekday;
	}
	
	/*
	 * Espera o formato (dia da semana (inglês))-(horário no formato HH:MM)
	 */
	public void setWeekdayTime(String weekdayTime){
		String[] weekdayTimeSplitted = weekdayTime.split("-");
		String weekday = weekdayTimeSplitted[0];
		String time = weekdayTimeSplitted[1];
		String[] timeSplitted = time.split(":");
		
		int hours = Integer.valueOf(timeSplitted[0]);
		int minutes = Integer.valueOf(timeSplitted[1]);
		
		setWeekday(DayOfWeek.valueOf(weekday.toUpperCase()));
		setTime(LocalTime.of(hours, minutes));
	}
	
	@Transient
	public String format(){
		return weekday.toString().toLowerCase() + "-" + time.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	
	@Override
	public boolean equals(Object compared) {
		if(compared instanceof String){
			return this.format().equals(compared);
		}else if(compared instanceof WeekdayTime){
			return this.format().equals(((WeekdayTime) compared).format());
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
