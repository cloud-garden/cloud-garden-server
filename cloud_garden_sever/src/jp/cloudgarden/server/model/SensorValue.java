package jp.cloudgarden.server.model;

import javax.xml.bind.annotation.XmlRootElement;

//for hardware.
@XmlRootElement(name="sensors")
public class SensorValue {
	public int temperature;
	public int humidity;
	public String image;
}
