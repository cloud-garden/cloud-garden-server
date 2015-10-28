package jp.cloudgarden.server.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/api/ws")
public class WebSocket {

	private Session session;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	// 切断時の処理
	@OnClose
	public void onClose(Session session) {
		this.session = null;
	}

	// テキストメッセージ受信時の処理
	@OnMessage
	public void onMessage(String msg) {
		System.out.println("send:" + msg);
	}

	public void setdMessage(String msg){
		try {
			this.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
		}
	}
}
