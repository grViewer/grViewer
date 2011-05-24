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
				"create table IF NOT EXISTS " + Constants.DB_gReaderItem_name + "("+
					  Constants.DB_gReaderItem_item_id_name + Constants.Space + Constants.DB_integer_PK_autoincrement +
				"," + Constants.DB_gReaderItem_item_itemId_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_itemPublished_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_itemTitle_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_itemLink_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_itemSummary_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_feed_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_title_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_link_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_read_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_star_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderItem_item_lock_name + Constants.Space + Constants.DB_type_text +
				");"
			);
			// table create
			db.execSQL(
				"create table IF NOT EXISTS " + Constants.DB_gReaderInfo_name + "("+
					  Constants.DB_gReaderInfo_item_id_name + Constants.Space + Constants.DB_integer_PK_autoincrement +
				"," + Constants.DB_gReaderInfo_item_userId_name + Constants.Space + Constants.DB_type_text +
				"," + Constants.DB_gReaderInfo_item_lastUpdate_name + Constants.Space + Constants.DB_type_text +
				");"
			);
			db.execSQL("insert into " + Constants.DB_gReaderInfo_name +
				       				"(" + Constants.DB_gReaderInfo_item_lastUpdate_name + ")" +
				       " values ('0');");


			// table create
			db.execSQL(
				"create table IF NOT EXISTS " + Constants.DB_gReaderLDRFullFeed_name + "("+
						  Constants.DB_gReaderLDRFullFeed_item_id_name + Constants.Space + Constants.DB_integer_PK_autoincrement +
					"," + Constants.DB_gReaderLDRFullFeed_item_name_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderLDRFullFeed_item_enc_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderLDRFullFeed_item_type_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderLDRFullFeed_item_url_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderLDRFullFeed_item_xpath_name + Constants.Space + Constants.DB_type_text +
				");"
			);
			db.execSQL(
					"create table IF NOT EXISTS " + Constants.DB_gReaderFeed_name + "("+
						  Constants.DB_gReaderFeed_item_id_name + Constants.Space + Constants.DB_integer_PK_autoincrement +
					"," + Constants.DB_gReaderFeed_item_feed_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_cut_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_title_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_name_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_enc_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_type_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_url_name + Constants.Space + Constants.DB_type_text +
					"," + Constants.DB_gReaderFeed_item_xpath_name + Constants.Space + Constants.DB_type_text +
					");"
				);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// データベースの変更が生じた場合は、ここに処理を記述する。
        if( oldVersion == 1 && newVersion == 2 ){
        	/*
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
*/
        }
	}
}
