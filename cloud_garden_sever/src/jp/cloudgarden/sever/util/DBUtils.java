package jp.cloudgarden.sever.util;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Mongodbのコネクション管理クラス
 * DB接続へのシングルトン管理を行う．
 *
 * @author shin
 *
 */
public class DBUtils {
	public static DBUtils instance = new DBUtils();

	private DB db;

//	private final String host = new String("133.30.159.8");
//	private final String host = new String("192.168.10.2");
//	private final String host = new String("localhost");
	private final String host = new String("");
	private final String dbName = new String("cloud");

	private DBUtils() {
		try {
			Mongo m = new Mongo(host, 27017);
			db = m.getDB(dbName);
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		}
	}

	public static DBUtils getInstance() {
		return DBUtils.instance;
	}

	public DB getDb() {
		return this.db;
	}

}
