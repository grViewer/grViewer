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





}
