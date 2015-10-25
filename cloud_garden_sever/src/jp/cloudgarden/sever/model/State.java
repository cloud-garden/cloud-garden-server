package jp.cloudgarden.sever.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sun.jersey.core.util.Base64;

@XmlRootElement
public class State {
	private String id;
	private String user;
	private long date;
	private int temperature;
	private int humid;
	private String photoId;

	public State(DBObject o){
		ObjectId objId = (ObjectId) o.get("_id");
		this.id = objId.toString();
		this.user = (String) o.get("user");
		this.date = (long) o.get("date");
		this.temperature = (int)o.get("temp");
		this.humid = (int)o.get("humid");
		this.photoId = (String)o.get("photo");
	}
	public State(){

	}
	@XmlElement(name="id")
	public String getId() {
		return id;
	}
	@XmlElement(name="user")
	public String getUser() {
		return user;
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
	@XmlElement(name="photo")
	public String getPhotoId() {
		return photoId;
	}

	public void setId(String id) {
		this.id = id;
	}
	public void setUser(String userId) {
		this.user = userId;
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

}
