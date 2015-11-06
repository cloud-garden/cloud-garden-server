package jp.cloudgarden.server.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//for hardware.
@XmlRootElement(name="sensors")
public class SensorValue {
	@XmlElement(name="temperature")
	public int temperature;
	@XmlElement(name="humidity")
	public int humidity;
	@XmlElement(name="photo")
	public String image;
}
