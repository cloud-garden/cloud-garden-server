package jp.cloudgarden.sever.jax;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import jp.cloudgarden.sever.model.Comment;
import jp.cloudgarden.sever.model.Like;
import jp.cloudgarden.sever.model.Report;
import jp.cloudgarden.sever.model.Schedule;
import jp.cloudgarden.sever.util.DBUtils;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.jersey.core.util.Base64;

public class CloudController {
	private final String stateCollectionName = "state";
	private final String pastCollectionName = "past";
	private final String scheduleCollectionName = "schedule";

	private DBCollection state_collection;
	private DBCollection past_collection;
	private DBCollection schedule_collection;

	public CloudController() {
		this.state_collection = DBUtils.getInstance().getDb().getCollection(stateCollectionName);
		this.past_collection = DBUtils.getInstance().getDb().getCollection(pastCollectionName);
		this.schedule_collection = DBUtils.getInstance().getDb().getCollection(scheduleCollectionName);
	}

	public void addSchedule(Schedule sc){
		DBObject o = new BasicDBObject();
		o.put("user", sc.getUser());
		o.put("Date", sc.getDate());
		o.put("isRoutine", sc.isRoutine());
		schedule_collection.save(o);
	}

	public void like() {
		DBObject like = new BasicDBObject();
		like.put("date", new Date());
		state_collection.save(like);
	}

	public void comment(String message) {
		DBObject comment = new BasicDBObject();
		comment.put("date", new Date());
		comment.put("message", message);
		past_collection.save(comment);
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
		cursor = past_collection.find(query).sort(sort).limit(n);
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
		schedule_collection.save(dbo);
		String id = dbo.get("_id").toString();
		return id;
	}


	public ByteArrayOutputStream getPhoto(String id) {
		DBObject query = new BasicDBObject("_id", new ObjectId(id));
		DBObject o = schedule_collection.findOne(query);
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
		DBCursor cursor = schedule_collection.find().sort(orderBy).limit(n);
		for (DBObject o : cursor) {
			list.add(o.get("_id").toString());
		}
		return list;
	}

}
