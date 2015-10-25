package jp.cloudgarden.sever.jax;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.cloudgarden.sever.model.Comment;
import jp.cloudgarden.sever.model.Like;
import jp.cloudgarden.sever.model.Report;
import jp.cloudgarden.sever.model.Schedule;
import jp.cloudgarden.sever.model.State;
import jp.cloudgarden.sever.util.DBUtils;

import org.bson.types.ObjectId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class CloudController {
	private final String stateCollectionName = "state";
	private final String pastCollectionName = "past";
	private final String scheduleCollectionName = "schedule";
	private final String photoCollectionName = "photo";

	private DBCollection state_collection;
	private DBCollection past_schedule_collection;
	private DBCollection active_schedule_collection;
	private DBCollection photo_collection;

	private final String HARD_URL = "";

	public CloudController() {
		this.state_collection = DBUtils.getInstance().getDb().getCollection(stateCollectionName);
		this.past_schedule_collection = DBUtils.getInstance().getDb().getCollection(pastCollectionName);
		this.active_schedule_collection = DBUtils.getInstance().getDb().getCollection(scheduleCollectionName);
		this.photo_collection = DBUtils.getInstance().getDb().getCollection(photoCollectionName);
	}

	public void createActiveSchedule(Schedule sc){
		insertScheduleDB(active_schedule_collection, sc);
	}

	public List<Schedule> getPastPreviousScheduleList(String user,long date){
		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		Calendar cal = null,target_cal = null;
		target_cal.setTimeInMillis(date);
		query.put("user", user);
		DBCursor cusor = past_schedule_collection.find(query);
		for(DBObject o : cusor){
			Schedule sc = new Schedule(o);
			//カレンダー形式へ変換
			cal.setTimeInMillis(sc.getDate());
			//一日の一番最初の情報が対象の情報であると仮定
			if((cal.YEAR==target_cal.YEAR)&&(cal.MONTH==target_cal.MONTH)&&(cal.DATE==(target_cal.DATE-1))){
				list.add(sc);
			}
		}
		//データがまったくなかった場合
		if(list.size()==0){
			return null;
		}
		return list;
	}

	public List<Schedule> getPastNextScheduleList(String user,long date){
		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		Calendar cal = null,target_cal = null;
		target_cal.setTimeInMillis(date);
		query.put("user", user);
		DBCursor cusor = past_schedule_collection.find(query);
		for(DBObject o : cusor){
			Schedule sc = new Schedule(o);
			//カレンダー形式へ変換
			cal.setTimeInMillis(sc.getDate());
			//一日の一番最初の情報が対象の情報であると仮定
			if((cal.YEAR==target_cal.YEAR)&&(cal.MONTH==target_cal.MONTH)&&(cal.DATE==(target_cal.DATE+1))){
				list.add(sc);
			}
		}

		//データがまったくなかった場合
		if(list.size()==0){
			return null;
		}

		return list;
	}

	public State getPastPreviousState(String user,long date){
		DBObject query = new BasicDBObject();
		Calendar cal = null,target_cal = null;
		target_cal.setTimeInMillis(date);
		query.put("user", user);
		DBCursor cusor = state_collection.find(query);
		for(DBObject o : cusor){
			State st = new State(o);
			//カレンダー形式へ変換
			cal.setTimeInMillis(st.getDate());
			//指定の日付の状態があれば返す
			//一日の一番最初の情報が対象の情報であると仮定
			if((cal.YEAR==target_cal.YEAR)&&(cal.MONTH==target_cal.MONTH)&&(cal.DATE==(target_cal.DATE-1))){
				return st;
			}
		}
		//指定の日付が存在しない場合
		return null;
	}

	public State getPastNextState(String user,long date){
		DBObject query = new BasicDBObject();
		Calendar cal = null,target_cal = null;
		target_cal.setTimeInMillis(date);
		query.put("user", user);
		DBCursor cusor = state_collection.find(query);
		for(DBObject o : cusor){
			State st = new State(o);
			//カレンダー形式へ変換
			cal.setTimeInMillis(st.getDate());
			//指定の日付の状態があれば返す
			//一日の一番最初の情報が対象の情報であると仮定
			if((cal.YEAR==target_cal.YEAR)&&(cal.MONTH==target_cal.MONTH)&&(cal.DATE==(target_cal.DATE+1))){
				return st;
			}
		}
		//指定の日付が存在しない場合
		return null;
	}


	public List<Schedule> getActiveScheduleList(String user){
		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cusor = active_schedule_collection.find(query);
		for(DBObject o : cusor){
			Schedule sc = new Schedule(o);
			list.add(sc);
		}
		Collections.sort(list);
		return list;
	}

	public boolean deleteActiveSchedule(String id){
		if(!ObjectId.isValid(id)) return false;
		ObjectId objid = new ObjectId(id);
		DBObject query = new BasicDBObject();
		query.put("_id", objid);
		active_schedule_collection.remove(query);
		return true;
	}

	public void checkSchedules(){
		Calendar currentTime = Calendar.getInstance();

		DBCursor cusor = active_schedule_collection.find();
		for(DBObject o : cusor){
			long date = (long) o.get("date");
			boolean isRoutine = (boolean) o.get("isRoutine");
			Calendar scheduledTime = Calendar.getInstance();
			scheduledTime.setTimeInMillis(date);
			System.err.println("isRoutine = "+ isRoutine );
			System.err.println("Tscheduled "+scheduledTime.getTime());
			System.err.println("Tcurrent   "+currentTime.getTime());
			if(isRoutine){
				if(scheduledTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY)
						&& scheduledTime.get(Calendar.MINUTE) == currentTime.get(Calendar.MINUTE) ){
					System.err.println("routine watering");
					//						executeWatering();
					Schedule sc = new Schedule(o);
					sc.setDate(currentTime.getTime().getTime());
					insertScheduleDB(past_schedule_collection, sc);
				}
			}else{
				if(scheduledTime.compareTo(currentTime) > 0){
					System.err.println("\tFuture");
					continue;
				}
				System.err.println("not routine watering");
				//If d is a past time, execute watering.
				//					executeWatering();
				Schedule sc = new Schedule(o);
				deleteActiveSchedule(sc.getId());
				sc.setDate(currentTime.getTime().getTime());
				insertScheduleDB(past_schedule_collection, sc);
			}
			//			} catch (WateringErrorException e) {
			//				e.printStackTrace();
			//			}
		}
	}

	public State getCurrentState(String user){
		return updateCurrentState(user)	;
	}

	public void updateAllCurrentStates(){
		//ほんとはすべてのユーザIDに対して行う．今回は固定なのでuser1にしている．
		updateCurrentState("user1");
	}

	private State updateCurrentState(String user){
		try {
			State current = getStateFromHardwareServer(user);
			String photo = getPhotoFromHardwareServer(user);
			DBObject o = new BasicDBObject();
			o.put("data", photo);
			photo_collection.save(o);
			String photoId = o.get("_id").toString();
			current.setPhotoId(photoId);
			insertStateDB(current);
			return current;
		} catch (MonitorErrorException e) {
			e.printStackTrace();
			return new State();
		}
	}

	private void executeWatering() throws WateringErrorException{
		Client client = Client.create();
		WebResource webResource = client.resource(HARD_URL).path("");
		//受け取る型を指定できるらしい．対応するBeanを作成するのもアリ．
		String json = webResource.accept(MediaType.APPLICATION_XML).get(String.class);
		//具体的な返り値の実装がまだ分からないので，空けておく．
		//エラーが起こったら例外を投げる
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(json);
			Element root = document.getDocumentElement();
			//	root.getAttribute("status").equals("OK");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private void insertStateDB(State s){
		DBObject o = new BasicDBObject();
		o.put("user", s.getUser());
		o.put("date", s.getDate());
		o.put("temp",s.getTemperature());
		o.put("humid",s.getHumid());
		o.put("photoId", s.getPhotoId());
		state_collection.save(o);
	}

	private void insertScheduleDB(DBCollection col , Schedule sc){
		DBObject o = new BasicDBObject();
		o.put("user", sc.getUser());
		o.put("date", sc.getDate());
		o.put("isRoutine", sc.isRoutine());
		col.save(o);
	}


	private State getStateFromHardwareServer(String user) throws MonitorErrorException{
		State ret = new State();
		Calendar current = Calendar.getInstance();
		ret.setDate(current.getTimeInMillis());
		ret.setUser(user);
		//get from HWS
		//set the other values other than "photo"
		int humid = 78;
		int temp = 23;
		ret.setHumid(humid);
		ret.setTemperature(temp);
		return ret;
	}

	private String getPhotoFromHardwareServer(String user) throws MonitorErrorException{

		return "photo data";
	}

	private class WateringErrorException extends Exception {
		private static final long serialVersionUID = 1234L;
	}

	private class MonitorErrorException extends Exception {
		private static final long serialVersionUID = 1234L;
	}

	//以下，アルパカ．参考用．
	public void like() {
		DBObject like = new BasicDBObject();
		like.put("date", new Date());
		state_collection.save(like);
	}

	public void comment(String message) {
		DBObject comment = new BasicDBObject();
		comment.put("date", new Date());
		comment.put("message", message);
		past_schedule_collection.save(comment);
	}

	public Report getReport(int n) {
		DBObject query = new BasicDBObject();

		Report report = new Report();
		List<Like> likes = new ArrayList<Like>();
		List<Comment> comments = new ArrayList<Comment>();

		DBCursor cursor = state_collection.find(query);
		/*
		for (DBObject like : cursor) {
			likes.add(new Like((Date)like.get("date")));
		}
		 */
		report.setTotalLike(cursor.count());
		DBObject sort = new BasicDBObject("_id", -1);
		cursor = past_schedule_collection.find(query).sort(sort).limit(n);
		for (DBObject comment : cursor) {
			comments.add(new Comment(
					(Date)comment.get("date"), (String)comment.get("message")));
		}

		report.setLikes(likes);
		report.setComments(comments);
		return report;
	}


	public String savePhoto(String photoData) {
		DBObject dbo = new BasicDBObject("src", photoData);
		active_schedule_collection.save(dbo);
		String id = dbo.get("_id").toString();
		return id;
	}


	public ByteArrayOutputStream getPhoto(String id) {
		DBObject query = new BasicDBObject("_id", new ObjectId(id));
		DBObject o = active_schedule_collection.findOne(query);
		if (o == null) {
			return null;
		}
		String src = (String)o.get("src");
		src = src.split(",")[1];
		byte[] bytes = Base64.decode(src);
		try {
			BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();;
			ImageIO.write(bImage, "png", baos);
			return baos;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getPhotoList(int n) {
		List<String> list = new ArrayList<>();
		DBObject orderBy = new BasicDBObject("$natural", -1);
		DBCursor cursor = active_schedule_collection.find().sort(orderBy).limit(n);
		for (DBObject o : cursor) {
			list.add(o.get("_id").toString());
		}
		return list;
	}

}
