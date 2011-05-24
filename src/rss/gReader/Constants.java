package rss.gReader;

public final class Constants {
	public static final String HTTP_CHARSET_PARAMETER =
			"http.protocol.content-charset";
	public static final String HTTP_CHARSET_VALUE = "UTF-8";
	public static final String USER_AGENT_PARAMETER = "http.useragent";
	public static final String USER_AGENT_VALUE =
			"Lechtitseb Google Reader Unofficial API/0.1";
	public static final String PARAMETER_LOGIN_USERNAME = "Email";
	public static final String PARAMETER_LOGIN_PASSWORD = "Passwd";
	public static final String COOKIE_DOMAIN = ".google.com";
	public static final String COOKIE_PATH = "/";
	public static final int COOKIE_MAX_AGE = 1600000000;
	/**
	 * If true, the protocol has to be secure (https)
	 */
	public static final boolean COOKIE_SECURE = true;
	public static final String VALUE_SEPARATOR = "=";
	public static final String SID = "SID";
	public static final String LSID = "LSID";
	public static final String AUTH = "Auth";
	public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
	public static final String GOOGLE_AUTH_KEY = "GoogleLogin auth=";
	public static final String SID_TAG = SID + VALUE_SEPARATOR;
	public static final String LSID_TAG = LSID + VALUE_SEPARATOR;
	public static final String AUTH_TAG = AUTH + VALUE_SEPARATOR;
	public static final String PARAMETER_ITEM_ID = "i";
	public static final String PARAMETER_TOKEN = "T";
	public static final String PARAMETER_SHUFFLE_TOKEN = "t";
	public static final String PARAMETER_NUMBER_OF_RESULTS = "n";
	public static final String PARAMETER_OUTPUT_FORMAT = "output";
	public static final String PARAMETER_SEARCH_NUMBER_OF_ELEMENTS = "num";
	public static final String PARAMETER_SEARCH_SOURCE_FEED = "s";
	public static final String PARAMETER_SEARCH_TERM = "q";
	public static final String PARAMETER_STATE_FILTER = "xt";
	public static final String URL_PROTOCOL = "https://";
	public static final String URL_GOOGLE = URL_PROTOCOL + "www.google.com";
	public static final String URL_LOGIN = URL_GOOGLE + "/accounts/ClientLogin";
	public static final String URL_READER = URL_GOOGLE + "/reader";
	public static final String URL_NEXT_UNREAD = URL_READER + "/next";
	public static final String URL_API = URL_READER + "/api/0";
	public static final String URL_LABELS = URL_API + "/tag/list";
	public static final String URL_TOKEN = URL_API + "/token";
	public static final String URL_SEARCH_IDS = URL_API + "/search/items/ids";
	public static final String URL_SEARCH_CONTENTS =
			URL_API + "/stream/items/contents";
	public static final String URL_SUBSCRIPTION_LIST =
			URL_API + "/subscription/list";
	public static final String URL_PREFERENCE_LIST =
			URL_API + "/preference/list";
	public static final String URL_FEED = URL_READER + "/atom/";
	public static final String URL_FEED_JSON = URL_READER + "/api/0/stream/contents/";
	public static final String URL_UNREAD_COUNT = URL_API + "/unread-count";
	public static final String URL_LABEL = URL_FEED + "user/-/label/";
	public static final String ITEM_STATE = "/state/com.google";
	public static final String ITEM_STATE_UNREAD = ITEM_STATE + "/unread";
	public static final String ITEM_STATE_READ = ITEM_STATE + "/read";
	public static final String ITEM_STATE_STARRED = ITEM_STATE + "/starred";
	public static final String ITEM_STATE_SHARED = ITEM_STATE + "/broadcast";
	public static final String ITEM_STATE_SHARED_FRIENDS = ITEM_STATE + "/broadcast-friends";
	public static final String ITEM_STATE_READING_LIST =
			ITEM_STATE + "/reading-list";
	public static final String ITEM_STATE_FRESH = ITEM_STATE + "/fresh";
	public static final String CURRENT_USER = "user/-";
	public static final String FILTER_CURRENT_USER_UNREAD =
			CURRENT_USER + ITEM_STATE_UNREAD;
	public static final String FILTER_CURRENT_USER_READ =
			CURRENT_USER + ITEM_STATE_READ;
	public static final String FILTER_CURRENT_USER_STARRED =
			CURRENT_USER + ITEM_STATE_STARRED;
	public static final String URL_ITEMS_READ =
			URL_READER + "/atom/user/-" + ITEM_STATE_READ;
	public static final String URL_ITEMS_STARRED =
			URL_READER + "/atom/user/-" + ITEM_STATE_STARRED;
	public static final String URL_ITEMS_SHARED =
			URL_READER + "/atom/user/-" + ITEM_STATE_SHARED;
	public static final String URL_ITEMS_SHARED_FRIENDS =
		URL_READER + "/atom/user/-" + ITEM_STATE_SHARED_FRIENDS;
	public static final String URL_USER_TOKEN = "%%%USER_ID%%%";
	public static final String URL_ITEMS_SHARED_BY_SOMEONE_ELSE =
			URL_READER + "/public/atom/user/" + URL_USER_TOKEN
					+ ITEM_STATE_SHARED;
	public static final String URL_OPML_EXPORT =
			URL_READER + "/subscriptions/export";
	public static final String URL_ITEMS_READING_LIST =
			URL_READER + "/atom/user/-" + ITEM_STATE_READING_LIST;



	public static final String URL_EDIT_SUSCRIPTION = URL_API + "/subscription/edit";

	public static final String EDIT_SUBSCRIPTION_SUBSCRIBE = "subscribe";
	public static final String EDIT_SUBSCRIPTION_UNSUBSCRIBE = "unsubscribe";
	public static final String EDIT_SUBSCRIPTION_ACTION = "ac";
	public static final String EDIT_SUBSCRIPTION_FEED = "s";
        // Add Subscription to a label
	public static final String EDIT_SUBSCRIPTION_FEED_ADD = "a";
        // Remove Subscription from a label
	public static final String EDIT_SUBSCRIPTION_FEED_REMOVE = "r";
	public static final String EDIT_SUBSCRIPTION_EDIT_ACTION = "edit";

	public static final String URL_MARK_ALL_AS_READ = URL_API + "/mark-all-as-read";
	public static final String URL_MARK_ITEM_AS_READ = URL_API + "/edit-tag";

	public static final String URL_USER_INFO = URL_READER + "/user-info";

	// PREFERENCE_STREAM_URL = READER_URL +
	// '/api/0/preference/stream/list?output=json'
	// PACKAGE = "user/-/state/com.google"
	// SUBSCRIPTION_EDIT_URL = READER_URL + '/api/0/subscription/edit'
	// READ = "#{PACKAGE}/read"
	// KEPT_UNREAD = "#{PACKAGE}/kept-unread"
	// FRESH = "#{PACKAGE}/fresh"
	// STARRED = "#{PACKAGE}/starred"
	// BROADCAST = "#{PACKAGE}/broadcast"
	// READING_LIST = "#{PACKAGE}/reading-list"
	// TRACKING_BODY_LINK_USED = "#{PACKAGE}/tracking-body-link-used"
	// TRACKING_EMAILED = "#{PACKAGE}/tracking-emailed"
	// TRACKING_ITEM_LINK_USED = "#{PACKAGE}/tracking-item-link-used"
	// TRACKING_KEPT_UNREAD = "#{PACKAGE}/tracking-kept-unread"

	//gReaderItemテーブル名
    public static final String DB_gReaderItem_item_id_name ="_id";
    public static final String DB_gReaderItem_item_itemId_name = "itemId";
    public static final String DB_gReaderItem_item_itemPublished_name = "itemPublished";
    public static final String DB_gReaderItem_item_itemTitle_name = "itemTitle";
    public static final String DB_gReaderItem_item_itemLink_name = "itemLink";
    public static final String DB_gReaderItem_item_itemSummary_name = "itemSummary";
    public static final String DB_gReaderItem_item_feed_name = "feed";
    public static final String DB_gReaderItem_item_title_name = "title";
    public static final String DB_gReaderItem_item_link_name = "link";
    public static final String DB_gReaderItem_item_read_name = "read";
    public static final String DB_gReaderItem_item_star_name = "star";
    public static final String DB_gReaderItem_item_lock_name = "lock";


}
