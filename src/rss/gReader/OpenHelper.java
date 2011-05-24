package rss.gReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 2;
	/* DB_VERSION = 2
	 * gReaderItemの項目を増やす。「read：未読、既読の保持」、「star：スター」、「lock：後で見る（更新時も消さないもの）」
	 */


    private static final String DB_NAME = "gReader.db";

	public OpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// table create
		db.execSQL(
			"create table IF NOT EXISTS gReaderItem("+
			"	_id integer primary key autoincrement,"+
			"	itemId text ,"+
			"	itemPublished text,"+
			"	itemTitle text,"+
			"	itemLink text,"+
			"	itemSummary text,"+
			"	feed text,"+
			"	title text,"+
			"	link text,"+
			"	read text,"+
			"	star text,"+
			"	lock text"+
			");"
		);
		// table create
		db.execSQL(
			"create table IF NOT EXISTS gReaderInfo("+
			"	_id integer primary key autoincrement,"+
			"	userId text,"+
			"	lastUpdate text" +
			");"
		);
		db.execSQL("insert into gReaderInfo(lastUpdate) values ('0');");

		// table create
		db.execSQL(
			"create table IF NOT EXISTS gReaderLDRFullFeed("+
			"	_id integer primary key autoincrement,"+
			"	name text,"+
			"	enc text,"+
			"	type text,"+
			"	url text,"+
			"	xpath text" +
			");"
		);
		db.execSQL(
				"create table IF NOT EXISTS gReaderFeed("+
				"	_id integer primary key autoincrement,"+
				"	feed text,"+
				"	cut text,"+
				"	title text,"+
				"	name text,"+
				"	enc text,"+
				"	type text,"+
				"	url text,"+
				"	xpath text" +
				");"
			);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// データベースの変更が生じた場合は、ここに処理を記述する。
        if( oldVersion == 1 && newVersion == 2 ){
    		// table create
    		db.execSQL(
    			"create table IF NOT EXISTS gReaderItem("+
    			"	_id integer primary key autoincrement,"+
    			"	itemId text ,"+
    			"	itemPublished text,"+
    			"	itemTitle text,"+
    			"	itemLink text,"+
    			"	itemSummary text,"+
    			"	feed text,"+
    			"	title text,"+
    			"	link text,"+
    			"	read text,"+
    			"	star text,"+
    			"	lock text"+
    			");"
    		);
        }
	}
}
