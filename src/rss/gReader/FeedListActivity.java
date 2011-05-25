package rss.gReader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FeedListActivity extends Activity {
	private static final String tag = "Android";
    public static final int MENU_ITEM_RELOAD = Menu.FIRST;
    private String Auth =null;
	// オプションメニューアイテム識別用
	private static final int
		MENU_UPDATE = 0,
		MENU_SETTINGS = 1;
	private ListView lv;
	private SQLiteDatabase db;
	private Cursor cursor;
    // コンストラクタ
    public FeedListActivity() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // タイトルバーのカスタマイズを設定可能にする
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        //データベースオブジェクトの取得(5)
        OpenHelper dbHelper=new OpenHelper(FeedListActivity.this);
        db = dbHelper.getWritableDatabase();

        lv = (ListView)findViewById(R.id.list);
		lv.setOnItemClickListener(new ClickEvent());
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getString("etpID_key","").toString().trim().isEmpty()){

            Intent intent = new Intent(FeedListActivity.this,
            		GReaderPreferenceActivity.class);
			 try {
			     startActivity(intent);
			 } catch (Exception ex) {
			     Toast.makeText(this,
			                    "GReaderPreferenceActivityへの画面遷移に失敗しました。(" + ex.getMessage() + ")",
			                     Toast.LENGTH_LONG)
			                     .show();
			 }
        }
    	checkAuth();
        //前回の内容を表示
        //setAdapter();
        //タイトルの更新
        //setTitle();

    }
    // MENUボタンを押したときの処理
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
    	// MenuItemの生成
    	MenuItem addItem = menu.add(0, MENU_UPDATE, 0, "更新");
    	addItem.setIcon(android.R.drawable.ic_menu_set_as);

    	MenuItem updateItem = menu.add(0, MENU_SETTINGS, 0, "設定");
    	updateItem.setIcon(android.R.drawable.ic_menu_preferences);

    	return result;
    }

	// イベントクラスの定義
	class ClickEvent implements OnItemClickListener {

	     // onItemClickメソッドには、AdapterView(adapter)、選択した項目View(TextView)、
	     // 選択された位置のint値、IDを示すlong値が渡される
	    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

            // タスクはその都度生成する
			Log.i(tag,"intent");
	        Intent intent = new Intent(FeedListActivity.this,ItemListActivity.class);
			Log.i(tag,"putExtra");
			intent.putExtra(Constants.DB_gReaderItem_item_id_name,String.valueOf(id));
			intent.putExtra(Constants.AUTH,Auth);
	        try {
				Log.i(tag,"startActivity1");
	            startActivity(intent);
				Log.i(tag,"startActivity2");
	        } catch (Exception ex) {
	            Toast.makeText(FeedListActivity.this,
	                    "OtherActivityへの画面遷移に失敗しました。", Toast.LENGTH_LONG)
	                    .show();
	        }
	     }

	}
    // MENUの項目を押したときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	            // 更新
	        case MENU_UPDATE:
	            if (Auth == null){
	            	AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
	                // ダイアログの設定
	                alertDialog.setTitle("警告");          //タイトル
	                alertDialog.setMessage("ログインが失敗しました。メールアドレスとパスワードを確認してください。");
	                //内容
	                // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
	                alertDialog.setPositiveButton("OK",
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int which) {
	                            	dialog.cancel();
	                            }
	                        });

	                // ダイアログの作成と表示
	                alertDialog.create();
	                alertDialog.show();
	            }else{
		            // タスクはその都度生成する
		            RssParserTask task = new RssParserTask(this, Auth);
		            task.execute();
		            //db.close();
	            }
	            return true;
	            // 設定変更
	        case MENU_SETTINGS:
	            Intent intent = new Intent(FeedListActivity.this,
	            		GReaderPreferenceActivity.class);
				 try {
				     startActivity(intent);
				 } catch (Exception ex) {
				     Toast.makeText(this,
				                    "rssreaderMyPreferenceActivityへの画面遷移に失敗しました。(" + ex.getMessage() + ")",
				                     Toast.LENGTH_LONG)
				                     .show();
				 }

	        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setAdapter() {
        try {
            //前回取得した内容を表示
        	/*
        	 第２引数には、取得するテーブルの列を、文字列配列で指定します。
        	 第３引数には、SQLのwhere条件
        	 where条件には、パラメータ？を使う事ができ、パラメータ値を文字列配列で、第４引数で指定します。
        	 第５引数には、SQLの「GROUP BY」条件
        	 第６引数には「HAVING」条件
        	 第７引数には「ORDER BY」条件
        	 */
            //
            cursor = db.query(Constants.DB_gReaderFeed_name
            		          ,new String[] { Constants.DB_gReaderFeed_item_id_name
            		                         ,Constants.DB_gReaderFeed_item_feed_name
            		                         ,Constants.DB_gReaderFeed_item_unreadcut_name}
            				  ,Constants.DB_gReaderFeed_item_cut_name + "> 0", null, null, null, null);
    				//    							null, null, null, null, "itemPublished desc");

            startManagingCursor(cursor);

    		ListAdapter adapter = new SimpleCursorAdapter(this,
    							R.layout.feed_row,
    							cursor,
    							new String[] {"title","cut" },
    							new int[] {   R.id.feed_title,R.id.feed_cut }
    							);
            lv.setAdapter(adapter);
        }catch(SQLiteException e){
			Log.d(tag, e.toString());
			Log.d(tag, e.getMessage());
			Log.d(tag, "SQLiteException ng");
        }catch (Exception ex) {
			Log.d(tag, ex.toString());
			Log.d(tag, ex.getMessage());
        }

	}
	public void checkAuth() {
		if (Auth==null){
	    	//Authを取得
	    	Login Login = new Login(FeedListActivity.this);
	        Auth = Login.GoogleLogin();
		}

	}
	public void setTitle() {
        try {
        	//最終更新日設定
            ReadDB readdb = new ReadDB(db);
            TextView mTitle;
            mTitle = (TextView) findViewById(R.id.title_right_text);
            mTitle.setText("LastUpdate：" + readdb.Select_lastUpdate_strftime());
        }catch(SQLiteException e){
			Log.d(tag, e.toString());
			Log.d(tag, e.getMessage());
			Log.d(tag, "SQLiteException ng");
        }catch (Exception ex) {
			Log.d(tag, ex.toString());
			Log.d(tag, ex.getMessage());
        }

	}



}
