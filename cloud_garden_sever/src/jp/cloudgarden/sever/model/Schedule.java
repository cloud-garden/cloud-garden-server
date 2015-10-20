package jp.cloudgarden.sever.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Schedule implements Comparable<Schedule>{
	private String id;
	private String user;
	private Date date;
	private boolean isRoutine;

	public Schedule(String user, Date date, boolean isRoutine) {
		this.user = user;
		this.date = date;
		this.isRoutine = isRoutine;
	}
	public Schedule(String id ,String user, Date date, boolean isRoutine) {
		this(user, date, isRoutine);
		this.id = id;
	}
	public Schedule() {

	}

	@XmlElement(name="user")
	public String getUser() {
		return user;
	}
	@XmlElement(name="date")
	public Date getDate() {
		return date;
	}
	@XmlElement(name="isRoutine")
	public boolean isRoutine() {
		return isRoutine;
	}
	@XmlElement(name="id")
	public String getId() {
		return id;
	}

	@Override
	public int compareTo(Schedule o) {
		return this.date.compareTo(o.getDate());
	}

}
