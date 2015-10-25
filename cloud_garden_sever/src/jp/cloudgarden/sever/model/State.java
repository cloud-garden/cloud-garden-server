package jp.cloudgarden.sever.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import org.bson.types.ObjectId;

import com.mongodb.DBObject;

@XmlRootElement
public class State {
	private int id;
	private int userId;
	private long date;
	private int temperature;
	private int humid;
	private String photoId;//ここでは写真のIDだけ保持してればいいかもしれない．



	public State(int userId, long date, int temperature, int humid, String photoId) {
		this.userId = userId;
		this.date = date;
		this.temperature = temperature;
		this.humid = humid;
		this.photoId = photoId;
	}
	public State(DBObject o){
		this.id = (int) o.get("id");
		this.userId = (int) o.get("user");
		this.date = (long) o.get("date");
		this.temperature =(int) o.get("temp");
		this.humid =(int) o.get("humid");


		this.photoId = (String) o.get("photoId");
	}
	public State(int id, int userId, long date, int temperature, int humid,
			String photoId) {
		super();
		this.id = id;
		this.userId = userId;
		this.date = date;
		this.temperature = temperature;
		this.humid = humid;
		this.photoId = photoId;
	}
	public State(){

	}
	@XmlElement(name="id")
	public int getId() {
		return id;
	}
	@XmlElement(name="user")
	public int getUserId() {
		return userId;
	}
	@XmlElement(name="date")
	public long getDate() {
		return date;
	}
	@XmlElement(name="temp")
	public int getTemperature() {
		return temperature;
	}
	@XmlElement(name="humid")
	public int getHumid() {
		return humid;
	}
	@XmlElement(name="photoId")
	public String getPhotoId() {
		return photoId;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public void setHumid(int humid) {
		this.humid = humid;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getJsonString(){
		StringBuffer bf = new StringBuffer();
		bf.append("{\"id\":\"").append(id).append("\",")//Tue Feb 01 14:33:27 JST 2022
		.append("\"userId\":\"").append(userId).append("\",")
		.append("\"date\":\"").append(date).append("\",")
		.append("\"temperature\":\"").append(temperature).append("\",")
		.append("\"humid\":\"").append(humid).append("\",")
		.append("\"photoId\":\"").append(photoId).append("\"}");
		return bf.toString();
	}
}
