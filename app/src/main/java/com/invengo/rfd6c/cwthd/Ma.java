package com.invengo.rfd6c.cwthd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.invengo.rfd6c.cwthd.entity.Web;
import com.invengo.rfd6c.cwthd.enums.EmUh;
import com.invengo.rfd6c.cwthd.enums.EmUrl;

import tk.ziniulian.util.Str;

public class Ma extends AppCompatActivity {
	private Web w = new Web(this);
	private WebView wv;
	private Handler uh = new UiHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma);

		// 读写器初始化
		w.initRd();

		// 数据库初始化
		w.initDb();

		// 页面设置
		wv = (WebView)findViewById(R.id.wv);
		WebSettings ws = wv.getSettings();
		ws.setDefaultTextEncodingName("UTF-8");
		ws.setJavaScriptEnabled(true);
//		wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.addJavascriptInterface(w, "rfdo");

		sendUrl(EmUrl.Home);
	}

	@Override
	protected void onResume() {
		w.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		w.close();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		w.close();
		super.onDestroy();
	}

	// 获取当前页面信息
	private EmUrl getCurUi () {
		try {
			return EmUrl.valueOf(wv.getTitle());
		} catch (Exception e) {
			return null;
		}
	}

	// 页面跳转
	public void sendUrl (String url) {
		uh.sendMessage(uh.obtainMessage(EmUh.Url.ordinal(), 0, 0, url));
	}

	// 页面跳转
	public void sendUrl (EmUrl e) {
		sendUrl(e.toString());
	}

	// 页面跳转
	public void sendUrl (EmUrl e, String... args) {
		sendUrl(Str.meg(e.toString(), args));
	}

	// 发送页面处理消息
	public void sendUh (EmUh e) {
		uh.sendMessage(uh.obtainMessage(e.ordinal()));
	}

	// 页面处理器
	private class UiHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			EmUh e = EmUh.values()[msg.what];
			switch (e) {
				case Url:
					wv.loadUrl((String)msg.obj);
					break;
				case Connected:
					if (getCurUi() == EmUrl.Err) {
						wv.goBack();
					}
				default:
					break;
			}
		}
	}
}
