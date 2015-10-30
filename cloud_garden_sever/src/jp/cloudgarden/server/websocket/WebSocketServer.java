package jp.cloudgarden.server.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/api/ws")
public class WebSocketServer {

	private Session session;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnClose
	public void onClose(Session session) {
		this.session = null;
	}

	// テキストメッセージ受信時の処理
	@OnMessage
	public void onMessage(String msg) {
		System.out.println("send:" + msg);
		sendMessage("Sever gets your message! " + msg);
	}

	public void getTempAndHumid(){
		String msg = "{ method: \"getTemperatureAndHumidty\"}";
		sendMessage(msg);
	}

	public void executeWatering(){
		String msg = "{ method: \"executeWatering\"}";
		sendMessage(msg);
	}

	public void getImage(){
		String msg = "{ method: \"getImage\"}";
		sendMessage(msg);
	}

	private void sendMessage(String msg){
		try {
			this.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
		}
	}

}
