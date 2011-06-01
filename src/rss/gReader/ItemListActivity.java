package rss.gReader;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ItemListActivity extends Activity{

	private static final String tag = "Android";
    private String Auth =null;
	private ListView lv;
	private SQLiteDatabase db;
	private Cursor cursor;
	private DtoInflaterGReaderFeed DtoInflaterGReaderFeed = new DtoInflaterGReaderFeed();
    // コンストラクタ
    public ItemListActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // タイトルバーのカスタマイズを設定可能にする
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        //データベースオブジェクトの取得
        OpenHelper dbHelper=new OpenHelper(ItemListActivity.this);
        db = dbHelper.getWritableDatabase();

        lv = (ListView)findViewById(R.id.list);
		lv.setOnItemClickListener(new ClickEvent());
        Intent intent = getIntent();
        Auth = intent.getStringExtra(Constants.AUTH);
        String Feed_Id = intent.getStringExtra(Constants.DB_gReaderFeed_item_id_name);

		//前回の内容を表示
        setAdapter(Feed_Id);
        //タイトルの更新
        setTitle(Feed_Id);

    }


	// イベントクラスの定義
	class ClickEvent implements OnItemClickListener {

	     // onItemClickメソッドには、AdapterView(adapter)、選択した項目View(TextView)、
	     // 選択された位置のint値、IDを示すlong値が渡される
	    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

            // タスクはその都度生成する
			Log.i(tag,"intent");
	        Intent intent = new Intent(ItemListActivity.this,ItemDetailActivity.class);
			Log.i(tag,"putExtra");
			intent.putExtra(Constants.DB_gReaderItem_item_id_name,String.valueOf(id));
			intent.putExtra(Constants.AUTH,Auth);
	        try {
				Log.i(tag,"startActivity1");
	            startActivity(intent);
				Log.i(tag,"startActivity2");
	        } catch (Exception ex) {
	            Toast.makeText(ItemListActivity.this,
	                    "OtherActivityへの画面遷移に失敗しました。", Toast.LENGTH_LONG)
	                    .show();
	        }
	     }

	}

	public void setAdapter(String Feed_Id) {
        try {
            //フィード一覧画面で指定されたフィードのアイテム一覧を取得
            cursor = db.rawQuery("select gr_item._id " +
            		                    ",gr_item.title"+
            		                    ",strftime('%m-%d %H:%M',datetime(datetime(gr_item.itemPublished,'unixepoch'), 'localtime')) as itemPublished "+
            		                    ",gr_item.itemTitle " +
								  " from gReaderItem gr_item"+
								       ",gReaderFeed gr_feed" +
								 " where gr_feed._id = ? " +
								  " and gr_feed.feed = gr_item.feed "
								  //+"order by itemPublished desc "
					,new String[]{Feed_Id});

    		startManagingCursor(cursor);

    		ListAdapter adapter = new SimpleCursorAdapter(this,
    							R.layout.item_row,
    							cursor,
    							new String[] {  "title","itemPublished","itemTitle" },
    							new int[] {   R.id.item_descr,R.id.item_time,R.id.item_title }
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
	public void setTitle(String Feed_Id) {
        try {
        	Cursor c = db.query(Constants.DB_gReaderFeed_name,
            		new String[]{ Constants.DB_gReaderItem_item_id_name
            					,Constants.DB_gReaderFeed_item_feed_name
								,Constants.DB_gReaderFeed_item_cut_name
								,Constants.DB_gReaderFeed_item_unreadcut_name
								,Constants.DB_gReaderFeed_item_title_name
								,Constants.DB_gReaderFeed_item_name_name
								,Constants.DB_gReaderFeed_item_enc_name
								,Constants.DB_gReaderFeed_item_type_name
								,Constants.DB_gReaderFeed_item_url_name
								,Constants.DB_gReaderFeed_item_xpath_name
	        		},
					"_id = ?",
					new String[]{Feed_Id},
					null, null, null);
            boolean isEof = c.moveToFirst();
        	DtoInflaterGReaderFeed = new DtoInflaterGReaderFeed();

    		while (isEof) {
    			DtoInflaterGReaderFeed.setCursor(c);
    			isEof = c.moveToNext();
    		}
    		c.close();

            //フィード名称設定
            TextView mTitle;
            mTitle = (TextView) findViewById(R.id.title_left_text);
            mTitle.setText(DtoInflaterGReaderFeed.getTitle() + "(" + DtoInflaterGReaderFeed.getCut() + ")");
            //最終更新日設定
            ReadDB readdb = new ReadDB(db);
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

