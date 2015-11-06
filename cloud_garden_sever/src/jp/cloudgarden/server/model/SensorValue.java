package jp.cloudgarden.server.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//for hardware.
@XmlRootElement
public class SensorValue {
	@XmlElement(name="temperature")
	public String temperature;
	@XmlElement(name="humidity")
	public String humidity;
	@XmlElement(name="image")
	public String image;

	public int getTemperature() {
		return Integer.parseInt(temperature.substring(0,2));
	}

	public int getHumidity() {
		return Integer.parseInt(humidity.substring(0,2));
	}

	public String getImage() {
		return image;
	}
}
