package rss.gReader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReadDB {
	private SQLiteDatabase mdb;
	// コンストラクタ
    public ReadDB(SQLiteDatabase db) {
        mdb = db;
    }

    //
	public String Select_lastUpdate_strftime(){
		Cursor c = mdb.rawQuery("select strftime('%m-%d %H:%M',datetime(datetime(lastUpdate,'unixepoch'), 'localtime'))  from gReaderInfo ",null);
		boolean isEof = c.moveToFirst();
		String lastUpdate = null;

		while (isEof) {
			lastUpdate = c.getString(0);
			isEof = c.moveToNext();
		}
		c.close();
		return lastUpdate;

	}
	public String Select_lastUpdate(){
		Cursor c = mdb.rawQuery("select lastUpdate from gReaderInfo ",null);
		boolean isEof = c.moveToFirst();
		String lastUpdate = null;

		while (isEof) {
			lastUpdate = c.getString(0);
			isEof = c.moveToNext();
		}
		c.close();
		return lastUpdate;

	}


}
