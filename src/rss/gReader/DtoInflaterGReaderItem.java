package rss.gReader;


import java.io.Serializable;

import android.database.Cursor;


public class DtoInflaterGReaderItem implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
    private String _id;
    private String itemId;
    private String itemPublished;
    private String itemTitle;
	private String itemLink;
    private String itemSummary;
    private String feed;
    private String title;
    private String link;
    private String read;
    private String star;
    private String lock;


    public DtoInflaterGReaderItem() {
        _id = null;
        itemId = null;
        itemPublished = null;
        itemTitle = null;
    	itemLink = null;
        itemSummary = null;
        feed = null;
        title = null;;
        link = null;
        read = null;
        star = null;
        lock = null;
    }
    public String get_id() {return _id;}
    public void set_id(String _id) {this._id = _id;}

    public String getItemId() {return itemId;}
    public void setItemId(String itemId) {this.itemId = itemId;}

    public String getItemPublished() {return itemPublished;}
    public void setItemPublished(String itemPublished) {this.itemPublished = itemPublished;}

    public String getItemTitle() {return itemTitle;}
    public void setItemTitle(String itemTitle) {this.itemTitle = itemTitle;}

    public String getItemLink() {return itemLink;}
    public void setItemLink(String itemLink) {this.itemLink = itemLink;}

    public String getItemSummary() {return itemSummary;}
    public void setItemSummary(String itemSummary) {this.itemSummary = itemSummary;}

    public String getFeed() {return feed;}
    public void setFeed(String feed) {this.feed = feed;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getLink() {return link;}
    public void setLink(String link) {this.link = link;}

    public String getRead() {return read;}
    public boolean getRead_boolean() {
    	if (read.equals("1")){
        	return true;

    	}else{
        	return false;

    	}
	}
    public void setRead(String read) {this.read = read;}

    public String getStar() {return star;}
    public boolean getStar_boolean() {
    	if (star.equals("1")){
        	return true;

    	}else{
        	return false;

    	}
	}
    public void setStar(String star) {this.star = star;}

    public String getLock() {return lock;}
    public boolean getLock_boolean() {
    	if (lock.equals("1")){
        	return true;

    	}else{
        	return false;

    	}
	}
    public void setLock(String lock) {this.lock = lock;}

    public void setCursor(Cursor c) {
    	set_id(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_id_name)));
    	setItemId(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_itemId_name)));
    	setItemPublished(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_itemPublished_name)));
    	setItemTitle(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_itemTitle_name)));
    	setItemLink(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_itemLink_name)));
    	setItemSummary(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_itemSummary_name)));
    	setFeed(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_feed_name)));
    	setTitle(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_title_name)));
    	setLink(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_link_name)));
    	setRead(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_read_name)));
    	setStar(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_star_name)));
    	setLock(c.getString(c.getColumnIndexOrThrow(Constants.DB_gReaderItem_item_lock_name)));
    	}

}

