package rss.gReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class Edit {
	private  final String tag = "Android";
	private  String mFeed;
	private  String mItemIdentifier;
	private  String mAuth;

    // コンストラクタ
    public Edit(String Feed,String ItemIdentifier,String Auth) {
    	mFeed = Feed;
    	mItemIdentifier = ItemIdentifier;
    	mAuth = Auth;
    }

    @SuppressWarnings("finally")
	private String GetToken( String Auth) {
	 	String mtoken = null;
		try{
		 	//token取得
//			Log.d(tag, post_string);
			URL url = new URL(Constants.URL_TOKEN);
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			urlconn.setRequestProperty(Constants.AUTHORIZATION_HTTP_HEADER, Constants.GOOGLE_AUTH_KEY + Auth);
			urlconn.connect();
//			Log.i(tag,"getResponseMessage");
			if ( urlconn.getResponseMessage().toString().equals("OK") ){
//				Log.i(tag,"getResponseMessage-OK");

				//ストリームへ格納
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));

				//一行ずつ格納
				mtoken = reader.readLine();

				reader.close();
			}else{
//				Log.i(tag,"ログイン-NG");
			}
			urlconn.disconnect();
		}catch (UnknownHostException e) {
			e.printStackTrace();
			Log.d(tag, e.toString());
			Log.d(tag, e.getMessage());
			Log.d(tag, "UnknownHostException_Exception_http ng");
		}catch (Exception e) {
			e.printStackTrace();
			Log.d(tag, e.toString());
			Log.d(tag, "Exception_http ng");
		}finally{
		//	return mtoken.toString().substring(2);
			return mtoken;
		}
	 }
    //アイテムを既読へ
	public String MarkRead() {
		return EditItem(Constants.FILTER_CURRENT_USER_READ,Constants.EDIT_SUBSCRIPTION_FEED_ADD);
	}
    //アイテムをスターへ
	public String AddStar(String Subscription_Feed) {
		return EditItem(Constants.FILTER_CURRENT_USER_STARRED,Subscription_Feed);
	}
	public String EditItem(String Tag,String Subscription_Feed) {

		//token取得
		String Token = GetToken(mAuth);

		//uriの設定
		String Uri_Read = Constants.URL_MARK_ITEM_AS_READ;// +"?client=scroll&hl=ja";

		try
	    {
       		//URIの指定
       		HttpPost httpPost = new HttpPost(Uri_Read);

       		// パラメータを設定
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair(Subscription_Feed,Tag));
	        //params.add(new BasicNameValuePair("async","true"));
       		params.add(new BasicNameValuePair(Constants.EDIT_SUBSCRIPTION_FEED,mFeed));
       		params.add(new BasicNameValuePair(Constants.PARAMETER_ITEM_ID,mItemIdentifier));
       		params.add(new BasicNameValuePair(Constants.PARAMETER_TOKEN,Token));
       		httpPost.setEntity(new UrlEncodedFormEntity(params));

       		//ヘッダの指定
       		httpPost.setHeader(Constants.AUTHORIZATION_HTTP_HEADER, Constants.GOOGLE_AUTH_KEY + mAuth);

       		DefaultHttpClient client = new DefaultHttpClient();
       		// リクエスト
       		HttpResponse httpResponse = client.execute(httpPost);

       		// ステータスコードを取得
       		int statusCode = httpResponse.getStatusLine().getStatusCode();

       		// レスポンスを取得
       		HttpEntity entity = httpResponse.getEntity();
       		String response = EntityUtils.toString(entity);

       		// リソースを解放
       		entity.consumeContent();

       		// クライアントを終了させる
       		client.getConnectionManager().shutdown();

       		if ( statusCode != HttpStatus.SC_OK )
	            throw new Exception( "" );

	        return response;

	    }
	    catch ( Exception e )
	    {
	        return null;
	    }
	}
}
