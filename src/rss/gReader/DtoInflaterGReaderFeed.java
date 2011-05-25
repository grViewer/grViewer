package rss.gReader;


import java.io.Serializable;

import android.database.Cursor;


public class DtoInflaterGReaderFeed implements Serializable{
	private static final long serialVersionUID = 1L;
    private String _id;
    private String feed;
    private String cut;
    private String name;
	private String enc;
    private String type;
    private String url;
    private String title;
    private String xpath;


    public DtoInflaterGReaderFeed() {
        _id = null;
        feed = null;
        cut = null;
        name = null;
        enc = null;
        type = null;
        url = null;
        title = null;
        xpath = null;
    }
    public String get_id() {return _id;}
    public void set_id(String _id) {this._id = _id;}

    public String getFeed() {return feed;}
    public void setFeed(String feed) {this.feed = feed;}

    public String getCut() {return cut;}
    public void setCut(String cut) {this.cut = cut;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getEnc() {return enc;}
    public void setEnc(String enc) {this.enc = enc;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public String getUrl() {return url;}
    public void setUrl(String url) {this.url = url;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getXpath() {return xpath;}
    public void setXpath(String xpath) {this.xpath = xpath;}




    public void setCursor(Cursor c) {
    	set_id(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_id_name)));
    	setFeed(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_feed_name)));
    	setCut(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_cut_name)));
    	setName(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_name_name)));
    	setEnc(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_enc_name)));
    	setType(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_type_name)));
    	setUrl(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_url_name)));
    	setTitle(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_title_name)));
    	setXpath(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderFeed_item_xpath_name)));
    	}

}

