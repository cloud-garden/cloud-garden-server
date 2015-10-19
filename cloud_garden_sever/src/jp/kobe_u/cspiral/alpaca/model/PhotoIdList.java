package jp.kobe_u.cspiral.alpaca.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="photo")
public class PhotoIdList {
	private List<String> ids;

	public PhotoIdList() {
		setIds(new ArrayList<String>());
	}

	public PhotoIdList(List<String> list) {
		setIds(list);
	}

	@XmlElement(name="id")
	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
