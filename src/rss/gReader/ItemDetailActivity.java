package rss.gReader;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class ItemDetailActivity extends Activity{
	private static final String tag = "Android";
	private SQLiteDatabase db;
	private DtoInflaterGReaderItem DtoInflaterGReaderItem = new DtoInflaterGReaderItem();
    // コンストラクタ
    public ItemDetailActivity() {
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //データベースオブジェクトの取得
        OpenHelper dbHelper=new OpenHelper(ItemDetailActivity.this);
        db = dbHelper.getWritableDatabase();

        Log.i(tag,"onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web_viwe);
        //this.setProgressBarIndeterminateVisibility(true);

		Log.i(tag,"intent");
        Intent intent = getIntent();
        final String id = intent.getStringExtra(Constants.DB_gReaderItem_item_id_name);
        final String Auth = intent.getStringExtra(Constants.AUTH);

        Cursor c = db.query("gReaderItem", new String[]{ Constants.DB_gReaderItem_item_id_name
										        		,Constants.DB_gReaderItem_item_itemId_name
										        		,"strftime('%Y-%m-%d %H:%M:%S',datetime(" +
										        		          "datetime(" + Constants.DB_gReaderItem_item_itemPublished_name + ",'unixepoch'), 'localtime')) "+
										        		          "as " +  Constants.DB_gReaderItem_item_itemPublished_name
										        		,Constants.DB_gReaderItem_item_itemTitle_name
										        		,Constants.DB_gReaderItem_item_itemLink_name
										        		,Constants.DB_gReaderItem_item_itemSummary_name
										        		,Constants.DB_gReaderItem_item_feed_name
										        		,Constants.DB_gReaderItem_item_title_name
										        		,Constants.DB_gReaderItem_item_link_name
										        		,Constants.DB_gReaderItem_item_read_name
										        		,Constants.DB_gReaderItem_item_star_name
										        		,Constants.DB_gReaderItem_item_lock_name
										        		},
				            "_id = ?",
				            new String[]{String.valueOf(id)},
				            null, null, null);
        boolean isEof = c.moveToFirst();
        DtoInflaterGReaderItem = new DtoInflaterGReaderItem();

		while (isEof) {
			DtoInflaterGReaderItem.setCursor(c);
			isEof = c.moveToNext();
		}
		c.close();
    	//別スレッドで既読に変更
		new Thread() {

        	public void run() {
        		//Googleリーダの更新（既読へ）
    			Edit Edit = new Edit(DtoInflaterGReaderItem.getFeed(),
    								 DtoInflaterGReaderItem.getItemId(),
    								 Auth);
    	    	Edit.MarkRead();
    	    	//DBの更新
    	    	WriteDB wdb = new WriteDB(db);
    	    	wdb.Update_gReaderItem_lock("1",DtoInflaterGReaderItem.get_id());
        	}

		}.start();

        //設定ファイルを読み込む
		SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String xpath ="";
		if (myPrefs.getBoolean("checkbox_key",false)){
			Log.i(tag,"getLDRFullFeed_s");
			xpath = getLDRFullFeed(DtoInflaterGReaderItem.getLink(),DtoInflaterGReaderItem.getItemLink());
			Log.i(tag,"getLDRFullFeed_e");
		}
		String Summary;
		//LDRFullFeedに登録されていなかったらXpathの処理を行わない
		if (xpath.isEmpty()){
			Summary = "";
		}else{
			Log.i(tag,"getMainClause_s");
	        Summary = getMainClause(getBoilerpipe(DtoInflaterGReaderItem.getItemLink()),xpath);
			Log.i(tag,"getMainClause_e");
		}
		//本文が取得できなかった場合は概要をセット
        if (Summary.isEmpty()){
        	Summary = intent.getStringExtra("itemSummary");
        	if (Summary.isEmpty()){
        		Summary="本文を配信または取得できないサイトです。";
	        }
        }

		Log.i(tag,"html");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\">");
        html.append("<html  xmlns=\"http://www.w3.org/1999/xhtml\">");
        html.append("<head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        html.append("<style type=\"text/css\">");
        html.append("img  {max-width:100px;max-height:100px;}");
        html.append("body {font-size: 10px}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append(Summary);
        html.append("</body></html>");
		Log.i(tag,"html_e");
        WebView webView = (WebView)findViewById(R.id.webview01);
        webView.loadData(html.toString(), "text/html", "utf-8");
		Log.i(tag,"loadData");

        TextView tVtitle = (TextView)findViewById(R.id.tVtitle);
        tVtitle.setText(DtoInflaterGReaderItem.getTitle());
        TextView tVtime = (TextView)findViewById(R.id.tVtime);
        tVtime.setText(DtoInflaterGReaderItem.getItemPublished());

        //後で見るボタン
        ToggleButton tbLock = (ToggleButton) findViewById(R.id.iButLock);
        tbLock.setChecked(DtoInflaterGReaderItem.getLock_boolean());
        //ToggleのCheckが変更したタイミングで呼び出されるリスナー
        tbLock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	//DBの更新
    			final boolean Checked = isChecked;
    			//別スレッドで処理する
    			new Thread() {

    	        	public void run() {
    	        		String lock ="0";
    	        		if (Checked){
    	        			//スターを付ける
    	        			lock ="1";
    	        		}else{
    	        			//スターを外す
    	        			lock ="0";
    	        		}
    	    	    	//DBの更新
    	    	    	WriteDB wdb = new WriteDB(db);
    	    	    	wdb.Update_gReaderItem_lock(lock,DtoInflaterGReaderItem.get_id());
    	        	}
    	        }.start();
            }
        });
        //スター
        ToggleButton tbStar = (ToggleButton) findViewById(R.id.iButStar);
        tbStar.setChecked(DtoInflaterGReaderItem.getStar_boolean());

        //ToggleのCheckが変更したタイミングで呼び出されるリスナー
        tbStar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			final boolean Checked = isChecked;
    			//別スレッドで処理する
    			new Thread() {

    	        	public void run() {
    	    			final Edit Edit = new Edit(DtoInflaterGReaderItem.getFeed()
    	    					                  ,DtoInflaterGReaderItem.getItemId()
    	    					                  ,Auth);
    	        		String Subscription_Feed ="";
    	        		String star ="0";
    	        		if (Checked){
    	        			//スターを付ける
    	        			Subscription_Feed = Constants.EDIT_SUBSCRIPTION_FEED_ADD;
        	        		star ="1";
    	        		}else{
    	        			//スターを外す
    	        			Subscription_Feed = Constants.EDIT_SUBSCRIPTION_FEED_REMOVE;
        	        		star ="0";
    	        		}
    	            	//Googleリーダの更新
    	    	    	Edit.AddStar(Subscription_Feed);
    	    	    	//DBの更新
    	    	    	WriteDB wdb = new WriteDB(db);
    	    	    	wdb.Update_gReaderItem_star(star,DtoInflaterGReaderItem.get_id());
    	        	}
    	        }.start();
            }
        });
        Button btnGlobe = (Button)findViewById(R.id.iButGlobe);
        btnGlobe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		try{
        			Intent i = new Intent(Intent.ACTION_VIEW,
        			Uri.parse(DtoInflaterGReaderItem.getItemLink()));
        			startActivity(i);
        		}catch(Exception e){
        			// 省略
        		}
            }
        });



    }
    @Override
    public void onDestroy() {
    	db.close();
        super.onDestroy();
    }
	public String getBoilerpipe(String itemLink) {
		Log.i(tag,"getBoilerpipe_s");

		String url_string = "http://boilerpipe-web.appspot.com/extract?url=" + itemLink + "&extractor=CanolaExtractor";
//		String url_string = itemLink;
		Log.i(tag,"DefaultHttpClient");
	    HttpClient objHttp = new DefaultHttpClient();
		Log.i(tag,"DefaultHttpClient_end");
//	    HttpParams params = objHttp.getParams();
//	    HttpConnectionParams.setConnectionTimeout(params, 1000); //接続のタイムアウト
//	    HttpConnectionParams.setSoTimeout(params, 1000); //データ取得のタイムアウト
	    String sReturn = "";
	    try {
			Log.i(tag,"HttpGet");
	        HttpGet objGet   = new HttpGet(url_string);
       		//ヘッダの指定
			Log.i(tag,"HttpResponse");
	        HttpResponse objResponse = objHttp.execute(objGet);
			Log.i(tag,"getResponseMessage");
	        if (objResponse.getStatusLine().getStatusCode() < 400){
				Log.i(tag,"getResponseMessage-OK");
	        	HttpEntity httpEntity = objResponse.getEntity();
	            try {
					Log.i(tag,"EntityUtils");

					String EntityUtilsStrin = EntityUtils.toString(httpEntity);
					//InputStream in =httpEntity.getContent();
					//正規表現の処理
					Matcher matcher = Pattern.compile("<meta .*?>|<base .*?>|\n").matcher(EntityUtilsStrin);
					String strReplace = matcher.replaceAll("");
					sReturn = changeString(strReplace);
					Log.i(tag,"sReturn");
	            }
	            catch (ParseException e) {
	                //例外処理
	    			Log.i(tag,"ParseException:" + e.getMessage());
	            }
	            catch (IOException e) {
	                //例外処理
	    			Log.i(tag,"IOException:" + e.getMessage());
	            }catch (Exception ex) {
	    			Log.d(tag, ex.toString());
	    			Log.d(tag, ex.getMessage());
	            }
	            finally {
	                try {
	               		// リソースを解放
	                    httpEntity.consumeContent();
	               		// クライアントを終了させる
	                    objHttp.getConnectionManager().shutdown();
	                }
	                catch (IOException e) {
	                    //例外処理
	        			Log.i(tag,"IOException:" + e.getMessage());
					}
	            }
	        }
	    } catch (IOException e) {
			Log.i(tag,"IOException:" + e.getMessage());
	        return null;
        }catch (Exception ex) {
			Log.i(tag,"Exception:" + ex.getMessage());
	    }
	    return sReturn;


	}
	public String getLDRFullFeed(String link,String itemLink) {
		String xpath = "";
        try {

/*
            Cursor Cursor = db.query("gReaderLDRFullFeed", new String[] {"url","xpath"},null,null,null, null, null);
            boolean isEof = Cursor.moveToFirst();

    		while (isEof) {

    			if (Pattern.compile(Cursor.getString(0)).matcher(link).matches()){
        			xpath = Cursor.getString(1);
    			}
    			isEof = Cursor.moveToNext();
    		}
*/
            Pattern pattern =Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
            Matcher matcher = pattern.matcher(link);
            matcher.find();

            String[] columns = new String[] {"url","xpath"};
            String where = "url like ?";
/*
            String matchergroup ;
            int index = matcher.group(4).indexOf(".");
            if (index >=1) {
            	if (matcher.group(4).substring(0, index).equals("www")){
                	matchergroup = "%" + matcher.group(4).substring(index+1, matcher.group(4).indexOf(".",index+1)) +"%";
            	}else{
                	matchergroup = "%" + matcher.group(4).substring(0, index) +"%";
            	}

            }else{
            	matchergroup = "%" + matcher.group(4) +"%";

            }
*/
            String matchergroup = "%" + matcher.group(4).replaceAll("\\.|/", "%") + "%";

            String[] param = new String[]{matchergroup};

            //データベースオブジェクトの取得(5)
            Cursor Cursor = db.query("gReaderLDRFullFeed", columns,where, param,null, null, null);

            boolean isEof = Cursor.moveToFirst();
    		while (isEof) {
    			//正規表現コンパイル
    			Pattern CursorPattern = Pattern.compile(Cursor.getString(0));
    			//評価（サイトURLと記事URLで）
    			if (CursorPattern.matcher(link).find()){
        			xpath = Cursor.getString(1);
        			break;
    			}else if (CursorPattern.matcher(itemLink).find()){
        			xpath = Cursor.getString(1);
        			break;
    			}
    			isEof = Cursor.moveToNext();
    		}
            Cursor.close();
        }catch(SQLiteException e){
			Log.d(tag, e.toString());
			Log.d(tag, e.getMessage());
			Log.d(tag, "SQLiteException ng");
        }catch (Exception ex) {
			Log.d(tag, ex.toString());
			Log.d(tag, ex.getMessage());
        }

	    return xpath;

	}
	public String getMainClause(String Summary,String xpath) {
	    String sReturn = "";
		try {
//			Log.i("Summary", Summary.toString());
			//StringをDocumentに変換
			Document document = string2Document(Summary);
			XPathExpression XPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
			Node node = (Node)XPathExpression.evaluate(document, XPathConstants.NODE);
	        if (null != node) {
	            StringWriter writer = new StringWriter();

	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            transformer.transform(new DOMSource(node), new StreamResult(writer));
	            return  writer.toString();
	        }else{
	        	return sReturn;
	        }


			/*
			// DOMパーサ用ファクトリの生成
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーファクトリを生成
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
//			Summary = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+Summary;
			ByteArrayInputStream stream = new ByteArrayInputStream(Summary.getBytes());
			// Documentインスタンスの取得
			Document document = builder.parse(stream);
*/
/*
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			//ByteArrayInputStream stream = new ByteArrayInputStream(Summary.getBytes());
			// Documentインスタンスの取得
			Document document = builder.parse(new InputSource(new StringReader(Summary)));

			XPathExpression XPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
			//Node node = (Node)XPathExpression.evaluate(reader, XPathConstants.NODE);
			Node node = (Node)XPathExpression.evaluate(document, XPathConstants.NODE);
	        if (null != node) sReturn =node.getNodeValue();
*/
	        /*
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			//ByteArrayInputStream stream = new ByteArrayInputStream(Summary.getBytes());
			// Documentインスタンスの取得
			Document document = builder.parse(new InputSource(new StringReader(changeString)));
			//StringをDocumentに変換
			Document document = XMLUtils.string2Document(changeString);
*/

/*

			XPath xpath = XPathFactory.newInstance().newXPath();
			return sReturn = xpath.evaluate(getLDRFullFeed(title), document);
*/


		} catch (XPathExpressionException e) {
			Log.d(tag, e.toString());
			Log.d(tag, e.getMessage());
			e.printStackTrace();
        }catch (Exception ex) {
			Log.d(tag, ex.toString());
			Log.d(tag, ex.getMessage());
		}



		return sReturn;


	}

    public static String changeString(String changeString) {
    	String delim = "\"";      // 区切り文字
    	int index = -1;           // インデックス
        StringBuilder beforeText = new StringBuilder(changeString);// 変換前文字列
        StringBuilder afterText = new StringBuilder(changeString.length());// 変換後文字列
    	String[] splitText;       // 分割後文字列

    	/* "<"が無くなるまでループ */
    	while ((index = beforeText.indexOf("<")) != -1) {
    		afterText.append(beforeText.substring(0, index + 1));
    		beforeText.delete(0,index + 1);
    		/* ">"が有った場合 */
    		if ((index = beforeText.indexOf(">")) != -1){
    			/* "で分割 */
    			//splitText = split(delim, beforeText.substring(0, index + 1));
    			splitText = beforeText.substring(0, index + 1).split(delim);

    			for (int i = 0; i < splitText.length; i++) {
    				/* "  " の前後は変換 */
    				if (i % 2 == 0) {
    					/* 大文字→小文字変換 */
    					afterText.append(splitText[i].toLowerCase());
    				/* "  " の中は無変換 */
    				} else {
    					afterText.append(splitText[i]);
    				}
    				/* "を追加 ただし>の右には追加しない */
    				if (i != splitText.length - 1) {
    					afterText.append(delim);
    				}
    			}
        		beforeText.delete(0,index + 1);

    		}
    	}
    	return afterText.append(beforeText).toString();
	}
	/* 文字列を分割して配列で返します */
    /*
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] split(String delim, String text) {
		int index = -1;
		ArrayList list = new ArrayList();
		while ((index = text.indexOf(delim)) != -1) {
			list.add(text.substring(0, index));
			text = text.substring(index + delim.length());
		}
		list.add(text);
		String[] ret = (String[]) list.toArray(new String[list.size()]);
		return ret;
	}
*/
	/**
	* Stringをorg.w3c.dom.Documentに変換する
	* 変換失敗時にはｎullを返す。
	* @param string
	* @return Document
	*/
	public static Document string2Document(String str){
		Document doc = null;
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader strReader = new StringReader(str);
			doc = builder.parse(new InputSource(strReader));

			builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
		}

		return doc;

	}


}
