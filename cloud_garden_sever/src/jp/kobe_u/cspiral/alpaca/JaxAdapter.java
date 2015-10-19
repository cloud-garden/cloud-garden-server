package jp.kobe_u.cspiral.alpaca;

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

import jp.kobe_u.cspiral.alpaca.model.PhotoIdList;

@Path("/")
public class JaxAdapter {

	private final AlpacaController controller = new AlpacaController();
	private static int count = 0;

	/**
	 * いいねを投稿する
	 * @return okだけ
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML})
	@Path("/like")
	public Response like() {
		controller.like();
		return Response.status(200).entity("<like>ok"+count++ +"</like>").build();
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
	@GET
	@Produces("image/png")
	@Path("/photo/{id}.png")
	public Response getPhoto(
			@PathParam("id") String id) {
		ByteArrayOutputStream baos = controller.getPhoto(id);
		if (baos == null) {
			return Response.status(403).entity("<error>photo not found</error>").build();
		}
		byte[] photoData = baos.toByteArray();
		return Response.ok(new ByteArrayInputStream(photoData)).build();
	}


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

}
