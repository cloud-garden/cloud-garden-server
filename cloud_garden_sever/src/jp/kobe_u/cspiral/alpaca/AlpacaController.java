package jp.kobe_u.cspiral.alpaca;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import jp.kobe_u.cspiral.alpaca.model.Comment;
import jp.kobe_u.cspiral.alpaca.model.Like;
import jp.kobe_u.cspiral.alpaca.model.Report;
import jp.kobe_u.cspiral.alpaca.util.DBUtils;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.jersey.core.util.Base64;

public class AlpacaController {
	private final String LIKE_COLLECTION_NAME = "like";
	private final String COMMENT_COLLECTION_NAME = "comment";
	private final String PHOTO_COLLECTION_NAME = "photo";

	private DBCollection LIKE_COLLECTION;
	private DBCollection COMMENT_COLLECTION;
	private DBCollection PHOTO_COLLECTION;

	public AlpacaController() {
		this.LIKE_COLLECTION = DBUtils.getInstance().getDb().getCollection(LIKE_COLLECTION_NAME);
		this.COMMENT_COLLECTION = DBUtils.getInstance().getDb().getCollection(COMMENT_COLLECTION_NAME);
		this.PHOTO_COLLECTION = DBUtils.getInstance().getDb().getCollection(PHOTO_COLLECTION_NAME);
	}


	public void like() {
		DBObject like = new BasicDBObject();
		like.put("date", new Date());
		LIKE_COLLECTION.save(like);
	}

	public void comment(String message) {
		DBObject comment = new BasicDBObject();
		comment.put("date", new Date());
		comment.put("message", message);
		COMMENT_COLLECTION.save(comment);
	}


	public Report getReport(int n) {
		DBObject query = new BasicDBObject();

		Report report = new Report();
		List<Like> likes = new ArrayList<Like>();
		List<Comment> comments = new ArrayList<Comment>();

		DBCursor cursor = LIKE_COLLECTION.find(query);
		/*
		for (DBObject like : cursor) {
			likes.add(new Like((Date)like.get("date")));
		}
		*/
		report.setTotalLike(cursor.count());
		DBObject sort = new BasicDBObject("_id", -1);
		cursor = COMMENT_COLLECTION.find(query).sort(sort).limit(n);
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
		PHOTO_COLLECTION.save(dbo);
		String id = dbo.get("_id").toString();
		return id;
	}


	public ByteArrayOutputStream getPhoto(String id) {
		DBObject query = new BasicDBObject("_id", new ObjectId(id));
		DBObject o = PHOTO_COLLECTION.findOne(query);
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
		DBCursor cursor = PHOTO_COLLECTION.find().sort(orderBy).limit(n);
		for (DBObject o : cursor) {
			list.add(o.get("_id").toString());
		}
		return list;
	}

}
