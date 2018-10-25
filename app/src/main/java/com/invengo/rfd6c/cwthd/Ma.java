package com.invengo.rfd6c.cwthd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.invengo.lib.util.HexUtil;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.protocol.IRP1.IntegrateReaderManager;
import invengo.javaapi.protocol.IRP1.PowerOff;
import invengo.javaapi.protocol.IRP1.RXD_TagData;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;

public class Ma extends AppCompatActivity implements IMessageNotificationReceivedHandle {
	private Reader rd = null;
	private boolean isConnect = false;
	private boolean isScanning = false;
	private TextView tv;
	private ReadTag.ReadMemoryBank bank = ReadTag.ReadMemoryBank.EPC_TID_UserData_6C;

	// 连接设备
	private Runnable connectRa = new Runnable() {
		@Override
		public void run() {
			isConnect = rd.connect();
		}
	};

	// 断开设备
	private Runnable disConnectRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			if (isScanning) {
				isScanning = false;
			}
			rd.disConnect();
			isConnect = false;
		}
	};

	// 扫描
	private Runnable scanRa = new Runnable() {
		@Override
		public void run() {
			// 测温标签
			ReadTag rt = new ReadTag (bank);
			rd.send(rt);
		}
	};

	// 停止
	private Runnable stopRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			isScanning = false;
		}
	};

	private Handler hd = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			tv.append(msg.obj.toString());
			int offset = tv.getLineCount()*tv.getLineHeight();
			if(offset > tv.getHeight()){
				tv.scrollTo(0,offset - tv.getHeight());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma);

		tv = (TextView) findViewById(R.id.tv);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());

		// 标签模式切换
		CheckBox cb = (CheckBox)findViewById(R.id.cb);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					bank = ReadTag.ReadMemoryBank.EPC_TID_TEMPERATURE;
				} else {
					bank = ReadTag.ReadMemoryBank.EPC_TID_UserData_6C;
				}
			}
		});

		// 清空按钮
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv.setText("");
				tv.scrollTo(0, 0);
			}
		});

		// 读取按钮
		final Button btnm = (Button) findViewById(R.id.btnm);
		btnm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isConnect) {
					if (isScanning) {
						stop();
						((Button)v).setText("读取");
					} else {
						scan();
						((Button)v).setText("停止");
					}
				}
			}
		});

		rd = IntegrateReaderManager.getInstance();
		if (rd != null) {
			rd.onMessageNotificationReceived.add(this);
			open();
		}
	}

	@Override
	protected void onDestroy() {
		close();
		super.onDestroy();
	}

	private void open() {
		if (!isConnect && rd != null) {
			new Thread(connectRa).start();
		}
	}

	private void close() {
		if (isConnect) {
			new Thread(disConnectRa).start();
		}
	}

	private void scan() {
		if (!isScanning && isConnect) {
			isScanning = true;
			new Thread(scanRa).start();
		}
	}

	private void stop() {
		if (isScanning && isConnect) {
			new Thread(stopRa).start();
		}
	}

	@Override
	public void messageNotificationReceivedHandle(BaseReader baseReader, IMessageNotification iMessageNotification) {
//		Log.i("----------" + iMessageNotification.getMessageType() + "----------", HexUtil.toHexString(iMessageNotification.getReceivedData()));
		if (iMessageNotification instanceof RXD_TagData) {
			RXD_TagData.ReceivedInfo ri = ((RXD_TagData) iMessageNotification).getReceivedMessage();
			String s = HexUtil.toHexString(ri.getTID()) + " : " + ri.getTemperature() + "\n";
			hd.sendMessage(hd.obtainMessage(1, s));
		}
	}

}
