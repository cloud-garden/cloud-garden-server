package jp.kobe_u.cspiral.alpaca.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * コメントBean + Jersey Object
 * @author shin
 *
 */
@XmlRootElement(name="comment")
public class Comment {

	private Date date;
	private String message;
	
	// default constructor for jaxb
	public Comment() {
		this(new Date(0), "");
	}
		
	public Comment(Date date, String message) {
		this.date = date;
		this.message = message;
	}
	
	@XmlElement(name="date")
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDate() {
		return date;
	}
	@XmlElement(name="message")
	public String getMessage() {
		return message;
	}

}
