package jp.cloudgarden.sever.model;


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

	public int getId() {
		return id;
	}
	public int getUserId() {
		return userId;
	}
	public long getDate() {
		return date;
	}
	public int getTemperature() {
		return temperature;
	}
	public int getHumid() {
		return humid;
	}
	public byte[] getPhoto() {
		return photo;
	}

}
