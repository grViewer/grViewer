package rss.gReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Login {
    private  FeedListActivity mActivity;
	private  final String tag = "Android";

    // コンストラクタ
    public Login(FeedListActivity activity) {
        mActivity = activity;
    }
	@SuppressWarnings("finally")
	public String GoogleLogin() {
		 	String Auth = null;

			String post_string = "https://www.google.com/accounts/ClientLogin?accountType=HOSTED_OR_GOOGLE&Email={0}&Passwd={1}&service=reader&source=GRN_mod&continue=https://www.google.com/reader";
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mActivity);
			Object []arguments = {
					pref.getString("etpID_key","").toString().trim(),
					pref.getString("etpPass_key","").toString().trim(),
			};

			String url_string = MessageFormat.format(post_string, arguments);
			Log.d(tag, url_string);

			try{
				URL url = new URL(url_string);
				HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
				urlconn.setRequestProperty("User-Agent", "Mozilla/5.0");
				urlconn.setRequestProperty(Constants.AUTHORIZATION_HTTP_HEADER, "ja;q=0.7,en;q=0.3");
				Log.d(tag, "connect");
				urlconn.connect();
				//ログインが成功したか
				if ( urlconn.getResponseMessage().toString().equals("OK") ){

					//ストリームへ格納
					BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));

					while (true){
						//一行ずつ格納
						String line = reader.readLine();
						//最終行判定
						if ( line == null ){
							break;
						}
						//正規表現パターン
						String patternRegex = "([^=]+)=(.+)";//#{xxx}
						Pattern pattern = Pattern.compile(patternRegex);

						//正規表現の処理
						Matcher matcher = pattern.matcher(line);
						//各文字列に対し変数へ格納する。
						if(matcher.find()){
							int group = matcher.groupCount();
							if (group == 2) {
								if (matcher.group(1).equals(Constants.AUTH)){
									Auth = matcher.group(2);
								}
							}else{
								Log.i(tag,"形式が変わった？");
							}
						}
					}
					reader.close();
				}else{
					Log.i(tag,"ログイン-NG");
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
				return Auth;
			}

	 }
}
