package rss.gReader;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class WriteDB {
	private SQLiteDatabase mdb;
	// コンストラクタ
    public WriteDB(SQLiteDatabase db) {
        mdb = db;
    }

    //gReaderItemのread（未読・既読）の更新
	public void Update_gReaderItem_read(String updated,String id){
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderItem_item_read_name, updated);
		mdb.update(Constants.DB_gReaderItem_name ,
				   values,
				   Constants.DB_gReaderItem_item_id_name + " = ?",
				   new String[]{id});

	}
    //gReaderItemのstar（スター付）の更新
	public void Update_gReaderItem_star(String updated,String id){
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderItem_item_star_name, updated);
		mdb.update(Constants.DB_gReaderItem_name ,
				   values,
				   Constants.DB_gReaderItem_item_id_name + " = ?",
				   new String[]{id});

	}
    //gReaderItemのlock（後で読む）の更新
	public void Update_gReaderItem_lock(String updated,String id){
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderItem_item_lock_name, updated);
		mdb.update(Constants.DB_gReaderItem_name ,
				   values,
				   Constants.DB_gReaderItem_item_id_name + " = ?",
				   new String[]{id});

	}
    //gReaderFeedの件数を0にする
	public void Update_gReaderFeed_cut_Refresh(){
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderFeed_item_cut_name, "0");
		values.put(Constants.DB_gReaderFeed_item_unreadcut_name, "0");
		mdb.update(Constants.DB_gReaderFeed_name ,
				   values,
				   null,
				   null);

	}
    //gReaderFeedの件数を更新
	public int Update_gReaderFeed_cut(String feed,String cut,String unread_cut){
		ContentValues values = new ContentValues();
		values.put(Constants.DB_gReaderFeed_item_cut_name, cut);
		values.put(Constants.DB_gReaderFeed_item_unreadcut_name, unread_cut);
		return mdb.update(Constants.DB_gReaderFeed_name ,
				   values,
				   Constants.DB_gReaderFeed_item_feed_name + " = ?",
				   new String[]{feed});

	}





}
