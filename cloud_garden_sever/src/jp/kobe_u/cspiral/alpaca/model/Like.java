package jp.kobe_u.cspiral.alpaca.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="like")
public class Like {
	private Date date;
	private String user;
	
	public Like() {
		this(new Date(0), "");
	}
	public Like(Date date) {
		this(date, "");
	}
	public Like(Date date, String user) {
		this.date = date;
		this.user = user;
	}

	@XmlElement(name="date")
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDate() {
		return date;
	}
	
	@XmlElement(name="user")
	public String getuser() {
		return user;
	}
}
