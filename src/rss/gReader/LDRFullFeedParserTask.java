package rss.gReader;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class LDRFullFeedParserTask extends AsyncTask<String, Integer, Long> {
    private static GReaderPreferenceActivity mActivity;
    private ProgressDialog mProgressDialog;
	private static final String tag = "Android";
	private int textList_count;
	private Handler mProgressHandler;
	private SQLiteDatabase mdb;
	// コンストラクタ
    public LDRFullFeedParserTask(GReaderPreferenceActivity activity) {
        mActivity = activity;
        OpenHelper dbHelper=new OpenHelper(mActivity);
        mdb = dbHelper.getWritableDatabase();
    }

    // タスクを実行した直後にコールされる
    @Override
    protected void onPreExecute() {
        // プログレスバーを表示する
        mProgressDialog = new ProgressDialog(mActivity);

	    // ProgressDialog のタイトルを設定
//        mProgressDialog.setTitle("Title");

	    // ProgressDialog のメッセージを設定
        mProgressDialog.setMessage("データ取得中");

	    // ProgressDialog の確定（false）／不確定（true）を設定します
        mProgressDialog.setIndeterminate(true);

	    // ProgressDialog のスタイルを水平スタイルに設定
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

	    // ProgressDialog の初期値を設定 (水平の時)
        mProgressDialog.incrementProgressBy(0);

	    // ProgressDialog のセカンダリ値を設定 (水平の時)
        mProgressDialog.incrementSecondaryProgressBy(0);

        mProgressDialog.show();
        mProgressHandler = new Handler();
        new Thread() {
        	public void run() {
        		try {
        			while (mProgressDialog.getMax() > textList_count) {
        				//Log.d(tag, "while");
        				// Do some Fake-Work
        				sleep(1000);
        				mProgressHandler.post(new Runnable() {
        					public void run() {
        						mProgressDialog.setProgress(textList_count);
        					}
        				});
        			}
        		} catch (Exception e) {
        		}
        		// Dismiss the Dialog
        		mProgressDialog.dismiss();
        	}
        }.start();
  }

    // バックグラウンドにおける処理を担う。タスク実行時に渡された値を引数とする
    @Override
    protected Long doInBackground(String... params) {
    	Long result = null;
        try {
			Log.d(tag, "GetJson");
			JSONArray itemArray = new JSONArray(GetJson());
    	    // ProgressDialog の確定（false）／不確定（true）を設定します
            mProgressDialog.setIndeterminate(false);
	  	    // ProgressDialog の最大値を設定 (水平の時)
            int itemArraylength = itemArray.length();
        	mProgressDialog.setMax(itemArraylength);
			Log.d(tag, "SetJson");
			SetJson(itemArray,itemArraylength);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // ここで返した値は、onPostExecuteメソッドの引数として渡される
		Log.d(tag, "return");
        return result;
    }

    // メインスレッド上で実行される
    @Override
    protected void onPostExecute(Long result) {
        mProgressDialog.dismiss();
    }
	public String GetJson(){

			String url_string = "http://wedata.net/databases/LDRFullFeed/items.json";
		    HttpClient objHttp = new DefaultHttpClient();
		    String sReturn = "";
		    try {
		        HttpGet objGet   = new HttpGet(url_string);
		        HttpResponse objResponse = objHttp.execute(objGet);
				//Log.i(tag,"getResponseMessage");
		        if (objResponse.getStatusLine().getStatusCode() < 400){
					//Log.i(tag,"getEntity");
		        	HttpEntity httpEntity = objResponse.getEntity();
		            try {
						//Log.i(tag,"httpEntity");
		            	sReturn = EntityUtils.toString(httpEntity);

						//Log.i(tag,"httpEntity-end");
		            }
		            catch (ParseException e) {
		                //例外処理
		    			Log.i(tag,"ParseException:" + e.getMessage());
		            }
		            catch (IOException e) {
		                //例外処理
		    			Log.i(tag,"IOException:" + e.getMessage());
		            }
		            finally {
		                try {
		                    httpEntity.consumeContent();
		                }
		                catch (IOException e) {
		                    //例外処理
		        			Log.i(tag,"IOException:" + e.getMessage());
		                }
		            }
		        }
		    } catch (IOException e) {
				Log.i(tag,"IOException:" + e.getMessage());
		        return null;
		    }
			//Log.i(tag,"return");
		    return sReturn;

	}
	public void SetJson(JSONArray itemArray,int itemArraylength){
        //トランザクション開始
        mdb.beginTransaction();
		try{
			//既読分だけ削除とか？？？
			//mdb.delete("gReaderItem", "itemPublished <= ?", published);
			//Log.i(tag,"delete");
			//全件削除
			mdb.delete("gReaderLDRFullFeed",null,null);
			//Log.i(tag,"SQLiteStatement");
	        //プリコンパイルステートメント作成
	        SQLiteStatement stmt =
	        	mdb.compileStatement("insert into " +
	        		"gReaderLDRFullFeed(name"+
	        					",enc"+
	        					",type"+
	        					",url"+
	        					",xpath"+
	        					") " +
	        		"values (?,?,?,?,?);");
			//Log.i(tag,"for");
	        JSONObject itemObject;
        	String name ="";
			String enc = "";
			String type = "";
			String url = "";
			String xpath = "";
	        for(textList_count = 0; textList_count < itemArraylength ;textList_count++){
	        	itemObject = null;
		        try {
		        	name ="";
					enc = "";
					type = "";
					url = "";
					xpath = "";
					try {
						//Log.i(tag,"itemObject");
						itemObject = itemArray.getJSONObject(textList_count);

						//Log.i(tag,"name");
						//サイト名
						name = itemObject.getString("name");

						JSONObject dataObject = itemObject.getJSONObject("data");
						//Log.i(tag,"enc");
						//enc
						if (dataObject.has("enc")) {
							enc = dataObject.getString("enc");
						}
						//Log.i(tag,"type");
			            //type
						if (dataObject.has("type")) {
							type = dataObject.getString("type");
						}
						//Log.i(tag,"url");
						//url
						if (dataObject.has("url")) {
							url = dataObject.getString("url");
						}
						//Log.i(tag,"xpath");
						//xpath
						if (dataObject.has("xpath")) {
							xpath = dataObject.getString("xpath");
						}

					}catch (JSONException e1) {
						Log.d(tag, e1.toString());
						Log.d(tag, e1.getMessage());
						Log.d(tag, "enc ng");
					}

					try {
						//Log.i(tag,"SQLの処理");
						// SQLの処理
						stmt.bindString(1, name);
						stmt.bindString(2, enc);
						stmt.bindString(3, type);
						stmt.bindString(4, url);
						stmt.bindString(5, xpath);
						//Log.i(tag,"execute");
						stmt.execute();

						//挿入された行のIDを取得する場合・・・
						//long id = stmt.executeInsert();

			        }catch(SQLiteException e){
						Log.d(tag, e.toString());
						Log.d(tag, e.getMessage());
						Log.d(tag, "SQLiteException ng");
					}
			        } catch (Exception e) {
						Log.d(tag, e.toString());
						Log.d(tag, e.getMessage());
						Log.d(tag, "Exception ng");
						e.printStackTrace();
					}

	        }
	        //トランザクション・コミット
		    mdb.setTransactionSuccessful();
		} finally {
	        //トランザクション終了
		    mdb.endTransaction();
		}
	}


}
