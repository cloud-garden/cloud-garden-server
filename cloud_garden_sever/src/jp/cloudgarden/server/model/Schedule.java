package jp.cloudgarden.server.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
//データ形式が間違っていたら修正
@XmlRootElement
public class Schedule implements Comparable<Schedule>{
	private String id;
	private String user;
	private long date; //milli sec from 1/1/1970
	private boolean isRoutine;

	public Schedule(String user, long date, boolean isRoutine) {
		this.user = user;
		this.date = date;
		this.isRoutine = isRoutine;
	}
	public Schedule(String id ,String user, long date, boolean isRoutine) {
		this(user, date, isRoutine);
		this.id = id;
	}
	public Schedule(DBObject o){
		ObjectId objId = (ObjectId) o.get("_id");
		this.id = objId.toString();
		this.user = (String) o.get("user");
		this.date = (long) o.get("date");
		this.isRoutine = (Boolean) o.get("isRoutine");
	}
	public Schedule() {

	}

	@XmlElement(name="user")
	public String getUser() {
		return user;
	}
	@XmlElement(name="date")
	public long getDate() {
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
	public void setId(String id) {
		this.id = id;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public void setRoutine(boolean isRoutine) {
		this.isRoutine = isRoutine;
	}

	@Override
	public int compareTo(Schedule o) {
		return (int) (this.date - o.getDate());
	}

	public String getJsonString(){
		StringBuffer bf = new StringBuffer();
		bf.append("{\"date\":\"").append(date).append("\",")//Tue Feb 01 14:33:27 JST 2022
		.append("\"id\":\"").append(id).append("\",")
		.append("\"isRoutine\":\"").append(isRoutine).append("\",")
		.append("\"user\":\"").append(user).append("\"}");
		return bf.toString();
	}

}
