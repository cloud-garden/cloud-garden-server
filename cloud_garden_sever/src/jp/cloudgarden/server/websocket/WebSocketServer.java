package jp.cloudgarden.server.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/api/ws")
public class WebSocketServer {
	private static Session session;
	public static boolean isConnected = false;
	public static String latestMessage = "none";
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("WebSocket Open Success!");
		isConnected = true;
		WebSocketServer.session = session;
	}

	@OnClose
	public void onClose(Session session) {
		isConnected = false;
		WebSocketServer.session = null;
	}

	@OnMessage
	public static void onMessage(String msg) {
		System.out.println("send:" + msg);
		latestMessage = msg;
		sendMessage(msg);
	}

	public static void getTempAndHumid(){
		String msg = "{ method: \"getTemperatureAndHumidty\"}";
		sendMessage(msg);
	}

	public static void executeWatering(){
		String msg = "{ method: \"executeWatering\"}";
		sendMessage(msg);
	}

	public static void getImage(){
		String msg = "{ method: \"getImage\"}";
		sendMessage(msg);
	}

	private static void sendMessage(String msg){
		try {
			session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
		}
	}

}
