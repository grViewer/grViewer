package rss.gReader;

import android.os.AsyncTask;
import android.os.Handler;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RssParserTask extends AsyncTask<String, Integer, Long>{
    private static FeedListActivity mActivity;
    private ProgressDialog mProgressDialog;
	private String mAuth;
	private static final String tag = "Android";
	private int textList_count;
	private Handler mProgressHandler;
	private SQLiteDatabase mdb;
	// コンストラクタ
    public RssParserTask(FeedListActivity activity, String Auth) {
        mActivity = activity;
        mAuth = Auth;
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
			//URLの設定（一覧取得用）
			String url_ItemList = Constants.URL_SEARCH_CONTENTS +
							"?" + Constants.PARAMETER_STATE_FILTER + Constants.VALUE_SEPARATOR + Constants.FILTER_CURRENT_USER_READ +
							"&" + Constants.PARAMETER_NUMBER_OF_RESULTS + Constants.VALUE_SEPARATOR +"500";
			//一覧の取得
			JSONObject rootObject = new JSONObject(GetJson(url_ItemList).toString());
        	JSONArray  itemArrayItemList  = rootObject.getJSONArray("items");

        	// ProgressDialog の確定（false）／不確定（true）を設定します
            mProgressDialog.setIndeterminate(false);
	  	    // ProgressDialog の最大値を設定 (水平の時)
        	mProgressDialog.setMax(itemArrayItemList.length() + 1 );
			Log.d(tag, "SetJson");
			//一覧の登録
        	SetItemList(itemArrayItemList);

        	//URLの設定（登録フィード一覧取得用）
			String url_LabelList = Constants.URL_SUBSCRIPTION_LIST +
								"?" + Constants.PARAMETER_OUTPUT_FORMAT_JSON;

        	//登録フィード一覧の取得
			JSONArray  itemArrayLabel  = rootObject.getJSONArray(GetJson(url_LabelList).toString());
        	//登録フィード一覧の登録
        	SetLabelList(itemArrayLabel);

        	//最終更新日の設定
        	UpdateInfo(rootObject.getString("updated"));
        	//DBクローズ
        	mdb.close();

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
    	//ブログレスメッセージの解放
        mProgressDialog.dismiss();
    	//登録フィード一覧の画面表示
        mActivity.setAdapter();
    }
    //Httpリクエスト
	public StringBuilder GetJson(String url_string){

		    HttpClient objHttp = new DefaultHttpClient();
//		    HttpParams params = objHttp.getParams();
//		    HttpConnectionParams.setConnectionTimeout(params, 1000); //接続のタイムアウト
//		    HttpConnectionParams.setSoTimeout(params, 1000); //データ取得のタイムアウト
//		    String sReturn = "";
		    try {
		        HttpGet objGet   = new HttpGet(url_string);
	       		//ヘッダの指定
		        objGet.setHeader(Constants.AUTHORIZATION_HTTP_HEADER, Constants.GOOGLE_AUTH_KEY  + mAuth);
		    	//リクエスト
		        HttpResponse objResponse = objHttp.execute(objGet);
				Log.i(tag,"getResponseMessage");
		    	//結果確認
		        if (objResponse.getStatusLine().getStatusCode() < 400){
			    	//リクエスト結果取得
		            InputStream objStream = objResponse.getEntity().getContent();
		            BufferedReader objBuf = new BufferedReader(new InputStreamReader(objStream));
		            StringBuilder objJson = new StringBuilder();
		            String sLine;
		            while((sLine = objBuf.readLine()) != null){
		                objJson.append(sLine);
		            }
		            objStream.close();
				    return objJson;

		        }
		    } catch (IOException e) {
				Log.i(tag,"IOException:" + e.getMessage());
		        return null;
		    }
		    return null;

	}
	//アイテムの更新
	public void SetItemList(JSONArray itemArray){
        //トランザクション開始
        mdb.beginTransaction();
		try{
			//既読分だけ削除とか？？？
			//mdb.delete("gReaderItem", "itemPublished <= ?", published);
			//既読分だけ削除
			mdb.delete("gReaderItem","read='1' and star='0' and lock ='0'",null);
			//全件削除
			//mdb.delete("gReaderItem",null,null);
	        //プリコンパイルステートメント作成
	        SQLiteStatement stmt =
	        	mdb.compileStatement("insert into " +
	        		"gReaderItem("+
	        					 Constants.DB_gReaderItem_item_itemId_name +
	        					"," + Constants.DB_gReaderItem_item_itemPublished_name +
	        					"," + Constants.DB_gReaderItem_item_itemTitle_name  +
	        					"," + Constants.DB_gReaderItem_item_itemLink_name +
	        					"," + Constants.DB_gReaderItem_item_itemSummary_name +
	        					"," + Constants.DB_gReaderItem_item_feed_name +
	        					"," + Constants.DB_gReaderItem_item_title_name +
	        					"," + Constants.DB_gReaderItem_item_link_name  +
	        					"," + Constants.DB_gReaderItem_item_read_name  +
	        					"," + Constants.DB_gReaderItem_item_star_name +
	        					"," + Constants.DB_gReaderItem_item_lock_name +
	        					") " +
	        		"values (?,?,?,?,?,?,?,?,?,?,?);");
	        int itemArraylength = itemArray.length();
	        JSONObject itemObject;
	        for(textList_count = 0; textList_count < itemArraylength ;textList_count++){
	        	itemObject = null;
		        try {

					itemObject = itemArray.getJSONObject(textList_count);
		            //記事タイトル
					String Title = itemObject.getString("title");
					//広告を除く
					if ((Title.indexOf("PR:",0) == -1) && (Title.indexOf("AD:",0) == -1)) {

			            //ID
						String itemId = itemObject.getString("id");
			            //更新時間
						String itemPublished = itemObject.getString("published");

						//記事URL
			        	JSONArray  alternateArray  = itemObject.getJSONArray("alternate");
						String itemTitle = alternateArray.getJSONObject(0).getString("href");

						//RSSの種類により取得する項目を可変
						JSONObject SummaryObject;
						if (itemObject.has("summary")) {
							SummaryObject = itemObject.getJSONObject("summary");
						}else{
							SummaryObject = itemObject.getJSONObject("content");
						}

						//概要
						String itemSummary = SummaryObject.getString("content");
						if (itemSummary.isEmpty()) itemSummary = "概要なし";

						JSONObject originObject = itemObject.getJSONObject("origin");
						//フィード
						String feed = originObject.getString("streamId");
			            //サイトタイトル
						String title = originObject.getString("title");
			            //サイトURL
						String link = originObject.getString("htmlUrl");
	//					Log.i(tag,dtoHoge.getTitle());
	//					Log.i(tag,Summary);
						try {
							// SQLの処理
							stmt.bindString(1, itemId);
							stmt.bindString(2, itemPublished);
							stmt.bindString(3, Title);
							stmt.bindString(4, itemTitle);
							stmt.bindString(5, itemSummary);
							stmt.bindString(6, feed);
							stmt.bindString(7, title);
							stmt.bindString(8, link);
							stmt.bindString(9, "0");
							stmt.bindString(10, "0");
							stmt.bindString(11, "0");
							stmt.execute();

							//挿入された行のIDを取得する場合・・・
							//long id = stmt.executeInsert();

				        }catch(SQLiteException e){
							Log.d(tag, e.toString());
							Log.d(tag, e.getMessage());
							Log.d(tag, "SQLiteException ng");
						}
					}else{
						//広告は別スレッドで既読に
			            //ID
						final String itemId = itemObject.getString("id");
						//フィード
						JSONObject originObject = itemObject.getJSONObject("origin");
						final String feed = originObject.getString("streamId");
				    	//別スレッドで既読に変更
						new Thread() {

				        	public void run() {
				    			Edit Edit = new Edit(feed,itemId,mAuth);
				    	    	Edit.MarkRead();
				        	}
				        }.start();

					}
		        }catch (JSONException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}

	        }
	        //トランザクション・コミット
		    mdb.setTransactionSuccessful();
		} finally {
	        //トランザクション終了
		    mdb.endTransaction();
		}
	}
	//フィードの更新
	public void SetLabelList(JSONArray itemArray){
        //トランザクション開始
        mdb.beginTransaction();
		try{
			//件数を初期化
			WriteDB writedb = new WriteDB(mdb);
			writedb.Update_gReaderFeed_cut_Refresh();

	        int itemArraylength = itemArray.length();
	        JSONObject itemObject;
	        for(textList_count = 0; textList_count < itemArraylength ;textList_count++){
	        	itemObject = null;
		        try {

					itemObject = itemArray.getJSONObject(textList_count);
		            //ID
					String itemId = itemObject.getString("id");
		            //記事タイトル
					String Title = itemObject.getString("title");
					//広告を除く
					if ((Title.indexOf("PR:",0) == -1) && (Title.indexOf("AD:",0) == -1)) {

			            //更新時間
						String itemPublished = itemObject.getString("published");

						//記事URL
			        	JSONArray  alternateArray  = itemObject.getJSONArray("alternate");
						String itemTitle = alternateArray.getJSONObject(0).getString("href");

						//RSSの種類により取得する項目を可変
						JSONObject SummaryObject;
						if (itemObject.has("summary")) {
							SummaryObject = itemObject.getJSONObject("summary");
						}else{
							SummaryObject = itemObject.getJSONObject("content");
						}

						//概要
						String itemSummary = SummaryObject.getString("content");
						if (itemSummary.isEmpty()) itemSummary = "概要なし";

						JSONObject originObject = itemObject.getJSONObject("origin");
						//フィード
						String feed = originObject.getString("streamId");
			            //サイトタイトル
						String title = originObject.getString("title");
			            //サイトURL
						String link = originObject.getString("htmlUrl");
	//					Log.i(tag,dtoHoge.getTitle());
	//					Log.i(tag,Summary);
						try {
/*
							// SQLの処理
							stmt.bindString(1, itemId);
							stmt.bindString(2, itemPublished);
							stmt.bindString(3, Title);
							stmt.bindString(4, itemTitle);
							stmt.bindString(5, itemSummary);
							stmt.bindString(6, feed);
							stmt.bindString(7, title);
							stmt.bindString(8, link);
							stmt.bindString(9, "0");
							stmt.bindString(10, "0");
							stmt.bindString(11, "0");
							stmt.execute();
*/
							//挿入された行のIDを取得する場合・・・
							//long id = stmt.executeInsert();

				        }catch(SQLiteException e){
							Log.d(tag, e.toString());
							Log.d(tag, e.getMessage());
							Log.d(tag, "SQLiteException ng");
						}
					}else{
/*
						//広告は別スレッドで既読に
			            //ID
						final String itemId = itemObject.getString("id");
						//フィード
						JSONObject originObject = itemObject.getJSONObject("origin");
						final String feed = originObject.getString("streamId");
				    	//別スレッドで既読に変更
						new Thread() {

				        	public void run() {
				    			Edit Edit = new Edit(feed,itemId,mAuth);
				    	    	Edit.MarkRead();
				        	}
				        }.start();
*/
					}
		        }catch (JSONException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}

	        }
	        //トランザクション・コミット
		    mdb.setTransactionSuccessful();
		} finally {
	        //トランザクション終了
		    mdb.endTransaction();
		}
	}
	public void UpdateInfo(String updated){
		/*
		  	updateメソッドの第１引数には、insertメソッドと同様に、テーブル名を指定します。
			第２引数には、更新するデータをContentValuesオブジェクトで、指定します。
			第３引数には、更新するデータのwhere条件を指定します。
			上記の例では、name列のデータが「本田 圭佑」である行の年齢を、100才に更新します。
			この値をnullに指定すると、すべての行が更新対象になります。
			第４引数には、更新するデータのwhere条件を「?」を使ってパラメータで指定した場合の、パラメータ値をString配列で指定します。
			where条件にパラメータが無い場合は、上記の例のようにnullを指定します。
			以下に、where条件にパラメータを指定した例を示します。
			db.update("person_table", values, "name = ?", new String[]{"本田 圭佑"
		 */
		//パラメータの設定
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderInfo_item_lastUpdate_name, updated);
		//SQLの実行
		mdb.update(Constants.DB_gReaderInfo_name , values, null, null);

	}

}
