package jp.cloudgarden.server.jax;

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

import jp.cloudgarden.server.model.Comment;
import jp.cloudgarden.server.model.Like;
import jp.cloudgarden.server.model.Report;
import jp.cloudgarden.server.model.Schedule;
import jp.cloudgarden.server.model.SensorValue;
import jp.cloudgarden.server.model.State;
import jp.cloudgarden.server.util.DBUtils;

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

	private static State latestCurrentState = new State();
	private static String latestPhotoData = new String();
	public static boolean needWatering = false;

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

	public List<Schedule> getScheduleList(String user,long date){
		Calendar givenDate = Calendar.getInstance();
		givenDate.setTimeInMillis(date);

		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = past_schedule_collection.find(query);

		for(DBObject o : cursor){
			Schedule sc = new Schedule(o);
			Calendar target = Calendar.getInstance();
			target.setTimeInMillis(sc.getDate());
			if(isSameDate(target, givenDate)){
				list.add(sc);
			}
		}
		Collections.sort(list);
		return list;
	}

	public List<Schedule> getPastPreviousScheduleList(String user,long date){
		Calendar givenDate = Calendar.getInstance();
		givenDate.setTimeInMillis(date);

		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = past_schedule_collection.find(query);

		for(DBObject o : cursor){
			Schedule sc = new Schedule(o);
			Calendar target = Calendar.getInstance();
			target.setTimeInMillis(sc.getDate());
			target.add(Calendar.DAY_OF_MONTH, 1);
			if(isSameDate(target, givenDate)){
				list.add(sc);
			}
		}
		Collections.sort(list);
		return list;
	}

	public List<Schedule> getPastNextScheduleList(String user,long date){
		Calendar givenDate = Calendar.getInstance();
		givenDate.setTimeInMillis(date);

		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = past_schedule_collection.find(query);

		for(DBObject o : cursor){
			Schedule sc = new Schedule(o);
			Calendar target = Calendar.getInstance();
			target.setTimeInMillis(sc.getDate());
			target.add(Calendar.DAY_OF_MONTH, -1);
			if(isSameDate(target, givenDate)){
				list.add(sc);
			}
		}
		Collections.sort(list);
		return list;
	}

	public List<Schedule> getPastScheduleList(String user){
		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = past_schedule_collection.find(query);
		for(DBObject o : cursor){
			Schedule sc = new Schedule(o);
			list.add(sc);
		}
		Collections.reverse(list);
		return list;
	}

	public void addState(String user,long date){
		State state = new State();
		state.setDate(date);
		state.setUser(user);
		state.setHumid(22);
		state.setTemperature(32);
		state.setPhotoId("hogehoge");
		insertStateDB(state);
	}

	public State getPastPreviousState(String user,long date){
		Calendar givenDate = Calendar.getInstance();
		givenDate.setTimeInMillis(date);
		System.out.println("check" + new Date(date).toString());
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = state_collection.find(query);
		for(DBObject o : cursor){
			State st = new State(o);
			Calendar target = Calendar.getInstance();
			target.setTimeInMillis(st.getDate());
			target.add(Calendar.DAY_OF_MONTH, 1);
			if(isSameDate(target, givenDate)){
				return st;
			}
		}
		return null;
	}


	/**
	 * @param user
	 * @param date
	 * @return State of the day which is the same as param .<br>
	 *         null if the corresponding state is not found.
	 */
	public State getPastNextState(String user,long date){
		Calendar givenDate = Calendar.getInstance();
		givenDate.setTimeInMillis(date);

		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = state_collection.find(query);
		for(DBObject o : cursor){
			State st = new State(o);
			Calendar target = Calendar.getInstance();
			target.setTimeInMillis(st.getDate());
			target.add(Calendar.DAY_OF_MONTH, -1);
			if(isSameDate(target, givenDate)){
				return st;
			}
		}
		return null;
	}

	private boolean isSameDate(Calendar cal1 , Calendar cal2){
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
				&& cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
	}

	public List<Schedule> getActiveScheduleList(String user){
		List<Schedule> list = new ArrayList<Schedule>();
		DBObject query = new BasicDBObject();
		query.put("user", user);
		DBCursor cursor = active_schedule_collection.find(query);
		for(DBObject o : cursor){
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

		DBCursor cursor = active_schedule_collection.find();
		for(DBObject o : cursor){
			long date = (long) o.get("date");
			boolean isRoutine = (boolean) o.get("isRoutine");
			Calendar scheduledTime = Calendar.getInstance();
			scheduledTime.setTimeInMillis(date);
			if(isRoutine){
				if(scheduledTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY)
						&& scheduledTime.get(Calendar.MINUTE) == currentTime.get(Calendar.MINUTE) ){
					System.err.println(Calendar.getInstance().toString() +" routine watering ");
					needWatering = true;
					Schedule sc = new Schedule(o);
					sc.setDate(currentTime.getTime().getTime());
					insertScheduleDB(past_schedule_collection, sc);
				}
			}else{
				if(scheduledTime.compareTo(currentTime) > 0){
					continue;
				}
				System.err.println(Calendar.getInstance().toString() +" not-routine watering ");
				//If d is a past time, execute watering.
				needWatering = true;
				Schedule sc = new Schedule(o);
				deleteActiveSchedule(sc.getId());
				sc.setDate(currentTime.getTime().getTime());
				insertScheduleDB(past_schedule_collection, sc);
			}
		}
	}

	public State getCurrentState(String user){
		//ここはただcurrentStateを返すだけにする．
		return updateCurrentState(user)	;
	}

	//for hardware.
	public void updateState(SensorValue sensor){
		latestCurrentState.setHumid(sensor.getHumidity());
		latestCurrentState.setTemperature(sensor.getTemperature());
		latestPhotoData = new String(sensor.image);
	}

	public void updateAllCurrentStates(){
		//ほんとはすべてのユーザIDに対して行う．今回は固定なのでuser1にしている．
		updateCurrentState("user1");
	}

	private State updateCurrentState(String user){
		State current = latestCurrentState;
		String photo = latestPhotoData;
		DBObject o = new BasicDBObject();
		o.put("data", photo);
		photo_collection.save(o);
		String photoId = o.get("_id").toString();
		current.setPhotoId(photoId);
		insertStateDB(current);
		return current;
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

	public ByteArrayOutputStream getPhoto(String id) {
		if(!ObjectId.isValid(id)) return null;
		DBObject query = new BasicDBObject("_id", new ObjectId(id));
		DBObject o = photo_collection.findOne(query);
		if (o == null) {
			return null;
		}
		String src = (String)o.get("data");
//		src = src.split(",")[1];
		byte[] bytes = Base64.decode(src);
		try {
			BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();;
			ImageIO.write(bImage, "png", baos);
			return baos;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
