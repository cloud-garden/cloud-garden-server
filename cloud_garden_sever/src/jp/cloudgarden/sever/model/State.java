package jp.cloudgarden.sever.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class State {
	private int id;
	private int userId;
	private long date;
	private int temperature;
	private int humid;
	private byte[] photo;//ここでは写真のIDだけ保持してればいいかもしれない．

	public State(int id, int userId, long date, int temperature, int humid,
			byte[] photo) {
		super();
		this.id = id;
		this.userId = userId;
		this.date = date;
		this.temperature = temperature;
		this.humid = humid;
		this.photo = photo;
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
	@XmlElement(name="photo")
	public byte[] getPhoto() {
		return photo;
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
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
}
