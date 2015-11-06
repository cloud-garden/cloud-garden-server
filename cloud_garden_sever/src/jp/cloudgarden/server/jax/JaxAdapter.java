package jp.cloudgarden.server.jax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jp.cloudgarden.server.model.PhotoIdList;
import jp.cloudgarden.server.model.Schedule;
import jp.cloudgarden.server.model.SensorValue;
import jp.cloudgarden.server.model.State;
import jp.cloudgarden.server.threads.ScheduleCheckTread;
import jp.cloudgarden.server.websocket.WebSocketServer;


@Path("/")
public class JaxAdapter {

	private final CloudController controller = new CloudController();
	private static ScheduleCheckTread scheduleCheckTread;
	private static ScheduleCheckTread stateCheckTread;;
	public static final String OK_STATUS = "{\"status\" : \"OK\"}";
	public static final String ERR_STATUS = "{\"status\" : \"ERROR\"}";
	private static int MILLSEC_OF_DAY = 8400000;

	/**
	 * 水やりスケジュールを作成し，DBに登録する
	 * @param user ユーザID
	 * @param isRoutine ルーチンであるかどうか
	 * @param date 時刻（1970年からの経過時間のミリ秒）
	 * @return ok
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/createSchedule")
	public Response createSchedule(@QueryParam("user") String user,@QueryParam("isRoutine") boolean isRoutine,
			@QueryParam("date") long date){
		Schedule sc = new Schedule(user,date, isRoutine);
		controller.createActiveSchedule(sc);
		return Response.status(200).entity(OK_STATUS).build();
	}

	/**
	 * アクティブなスケジュールの配列を返す．
	 * @param user ユーザID
	 * @return Scheduleの配列．時刻が若い順にソートしている．
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getActiveScheduleList")
	public Response getActiveScheduleList(@QueryParam("user") String user){
		List<Schedule> list = controller.getActiveScheduleList(user);
		if(list.size() > 1){
			Schedule[] ret = list.toArray(new Schedule[0]);;
			return Response.status(200).entity(ret).build();
		}else if(list.size() == 1){
			String ret = "{\"schedule\":["+list.get(0).getJsonString()+"]}";
			return Response.status(200).entity(ret).build();
		}else{
			String ret = "{\"schedule\":[]}";
			return Response.status(200).entity(ret).build();
		}
	}

	/**
	 * アクティブな水やりスケジュールを削除する．
	 * @param id DBから削除するオブジェクトID
	 * @return OK
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/deleteSchedule")
	public Response deleteSchedule(@QueryParam("id") String id){
		boolean isSuccess = controller.deleteActiveSchedule(id);
		if(isSuccess){
			return Response.status(200).entity(OK_STATUS).build();
		}else{
			return Response.status(403).entity(ERR_STATUS).build();
		}
	}

	/**
	 * 最近の作物状態を取得し，返す．このとき，DBの更新も行う．
	 * @param userId ユーザID
	 * @return 作物状態のJSON
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getCurrentState")
	public Response getCurrentState(@QueryParam("user") String user){
		State current = controller.getCurrentState(user);
		return Response.status(200).entity(current).build();
	}

	/**
	 * 指定日の前の日の作物状態を返す
	 * @param userId ユーザID
	 * @param long    日付
	 * @return 指定日から先日へさかのぼっていき，もっとも近いときの作物状態を返す．
	 * 一週間さかのぼってもない場合は，dateに-1が入ったJSONを返す．
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getPastPreviousState")
	public Response getPastPreviousState(@QueryParam("user") String userId,@QueryParam("date") long date){
		State past = controller.getPastPreviousState(userId,date);
		for(int i=0; i<7 ;i++){
			date = date - MILLSEC_OF_DAY;
			past = controller.getPastNextState(userId,date);
			if(past != null) break;
		}
		if(past != null){
			return Response.status(200).entity(past).build();
		}else{
			State nullState = new State("no", -1, 0, 0, "");
			return Response.status(200).entity(nullState).build();
		}
	}

	/**
	 * 指定日の後の日の作物状態を返す
	 * @param userId ユーザID
	 * @param long    日付
	 * @return 過去の作物状態の配列．ない場合はnull
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getPastNextState")
	public Response getPastNextState(@QueryParam("user") String userId,@QueryParam("date") long date){
		State past = controller.getPastNextState(userId,date);
		for(int i=0; i<7 ;i++){
			date = date + MILLSEC_OF_DAY;
			past = controller.getPastNextState(userId,date);
			if(past != null) break;
		}
		if(past != null){
			return Response.status(200).entity(past).build();
		}else{
			State nullState = new State("no", -1, 0, 0, "");
			return Response.status(200).entity(nullState).build();
		}
	}

	/**
	 * 過去の処理履歴(指定日の)配列を返す．
	 * @param userId  ユーザID
	 * @param long    日付
	 * @return 過去の処理履歴の配列．
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getScheduleList")
	public Response getScheduleList(@QueryParam("user") String userId,@QueryParam("date") long date){
		List<Schedule> list = controller.getScheduleList(userId,date);
		if(list.size() > 1){
			Schedule[] ret = list.toArray(new Schedule[0]);;
			return Response.status(200).entity(ret).build();
		}else if(list.size() == 1){
			String ret = "{\"schedule\":["+list.get(0).getJsonString()+"]}";
			return Response.status(200).entity(ret).build();
		}else{
			String ret = "{\"schedule\":[]}";
			return Response.status(200).entity(ret).build();
		}
	}

	/**
	 * 過去の処理履歴の配列を返す．
	 * @param userId  ユーザID
	 * @return 過去の処理履歴の配列．日付が現在に近い順にソート済み．
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getPastScheduleList")
	public Response getPastScheduleList(@QueryParam("user") String userId){
		List<Schedule> list = controller.getPastScheduleList(userId);
		if(list.size() > 1){
			Schedule[] ret = list.toArray(new Schedule[0]);;
			return Response.status(200).entity(ret).build();
		}else if(list.size() == 1){
			String ret = "{\"schedule\":["+list.get(0).getJsonString()+"]}";
			return Response.status(200).entity(ret).build();
		}else{
			String ret = "{\"schedule\":[]}";
			return Response.status(200).entity(ret).build();
		}
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/addState")
	public Response addState(@QueryParam("user") String userId,@QueryParam("date") long date){
		controller.addState(userId, date);
		return Response.status(200).entity(OK_STATUS).build();
	}

	/*
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getws")
	public Response getwsvalue() {
		StringBuffer bf = new StringBuffer();
		bf.append("connected = " + WebSocketServer.isConnected +",");
		bf.append("message = " + WebSocketServer.latestMessage );
		return Response.status(200).entity(bf.toString()).build();
	}
	*/

	//for hardware.
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/updateState")
	public Response updateState(SensorValue sensor) {
		controller.updateState(sensor);
		return Response.status(200).entity(OK_STATUS).build();
	}

	/**
	 * 撮影した画像ファイルを返す
	 * @param 写真のid
	 * @return png画像
	 */
	@GET
	@Produces("image/png")
	@Path("/photo/{id}.png")
	public Response getPhoto(@PathParam("id") String id) {
		ByteArrayOutputStream baos = controller.getPhoto(id);
		if (baos == null) {
			return Response.status(403).entity("<error>photo not found</error>").build();
		}
		byte[] photoData = baos.toByteArray();
		return Response.ok(new ByteArrayInputStream(photoData)).build();
	}

	/**
	 * start schedule check.
	 * @return OK
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/start")
	public Response startChecking(){
		if(scheduleCheckTread != null && scheduleCheckTread.isRunning())
			return Response.status(200).entity(OK_STATUS).build();
		scheduleCheckTread = new ScheduleCheckTread(controller);
		scheduleCheckTread.start();

		if(stateCheckTread != null && stateCheckTread.isRunning())
			return Response.status(200).entity(OK_STATUS).build();
		stateCheckTread = new ScheduleCheckTread(controller);
		stateCheckTread.start();

		return Response.status(200).entity(OK_STATUS).build();
	}

	/**
	 * stop schedule check.
	 * @return OK
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/stop")
	public Response stopChecking(){
		if(scheduleCheckTread != null)
			scheduleCheckTread.stopThread();
		if(stateCheckTread != null)
			stateCheckTread.stopThread();
		return Response.status(200).entity(OK_STATUS).build();
	}

	/**
	 * ./api/ へのアクセスを ./api/application.wadl（APIの仕様書） にリダイレクトする
	 * @return
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/")
	public Response redirect() throws URISyntaxException{
		URI uri = new URI("application.wadl");
		return Response.seeOther(uri).build();
	}



	//以下，アルパカ．参考用．------------------------------------------------------------------------------------------
	/**
	 * いいねを投稿する
	 * @return okだけ
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/like")
	public Response like() {
		controller.like();
		return Response.status(200).entity("<like>ok</like>").build();
	}


	/**
	 * コメントする
	 * @param message コメント本文
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/comment")
	public Response comment(@QueryParam("msg") final String message) {
		if ("".equals(message)) {
			return Response.status(403).entity("<comment>error</comment>").build();
		}
		controller.comment(message);
		return Response.status(200).entity("<comment>ok</comment>").build();
	}

	/**
	 * コメント一覧といいね回数を取得
	 * @param n コメント数（デフォルトは20）
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("/report")
	public Response getReport(@DefaultValue("20") @QueryParam("n") final int n) {
		if (n < 0) {
			return Response.status(403).entity("<comment>error</comment>").build();
		}
		return Response.status(200).entity(controller.getReport(n)).build();
	}

	/**
	 * 写真をアップロードする
	 * POSTメソッドなのでブラウザのURL入力バーから直接呼べないことに注意
	 * @param photoData 写真データ（MIMEでシリアライズされた画像データ）
	 * @return 画像ID
	 */
	@POST
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("/photo")
	public Response uploadPhoto(
			@FormParam("src") String photoData
			) {
		String photoId = controller.savePhoto(photoData);
		return Response.status(200).entity("<photo_id>" + photoId + "</photo_id>").build();
	}

	/**
	 * 撮影した画像ファイルを返す
	 * @param 写真のid
	 * @return png画像
	 */
//	@GET
//	@Produces("image/png")
//	@Path("/photo/{id}.png")
//	public Response getPhoto(@PathParam("id") String id) {
//		ByteArrayOutputStream baos = controller.getPhoto(id);
//		if (baos == null) {
//			return Response.status(403).entity("<error>photo not found</error>").build();
//		}
//		byte[] photoData = baos.toByteArray();
//		return Response.ok(new ByteArrayInputStream(photoData)).build();
//	}


	/**
	 * 撮影した写真のIDのリストを返す
	 * @param n (写真の枚数，デフォルトは40）
	 * @return 写真IDリスト
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("/photo/list")
	public Response getPhotoList(
			@DefaultValue("40") @QueryParam("n") final int n
			) {
		List<String> list = controller.getPhotoList(n);
		PhotoIdList pil = new PhotoIdList(list);
		return Response.ok(pil).build();
	}


}
