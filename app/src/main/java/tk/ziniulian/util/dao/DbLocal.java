package tk.ziniulian.util.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static tk.ziniulian.util.Str.meg;

/**
 * 本地数据库
 * Created by 李泽荣 on 2018/7/19.
 */

public class DbLocal extends SQLiteOpenHelper {
	public DbLocal(Context c) {
		super(new SdDb(c), EmLocalCrtSql.dbNam.toString(), null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (EmLocalCrtSql e : EmLocalCrtSql.values()) {
			switch (e.name()) {
				case "sdDir":
				case "dbNam":
					break;
				default:
					db.execSQL(e.toString());
					break;
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	private String getStr (Cursor c, int i) {
		String s = null;
		s = c.getString(i);
		if (s != null) {
			return "\"" + s + "\"";
		} else {
			return null;
		}
	}

	private Long getTim (Cursor c, int i) {
		Long t = null;
		t = c.getLong(i);
		return t;
	}

	// 执行多条SQL语句
	public void exe (String... args) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (String s : args) {
			db.execSQL(s);
		}
		db.close();
	}

	// 执行SQL语句
	public void exe (EmLocalSql e, String... args) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(meg(e.toString(), args));
		db.close();
	}

	// 获取键值对
	public String kvGet (String k) {
		String r = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EmLocalSql.KvGet.toString(),
				k
		), null);

		if (c.moveToNext()) {
			r = c.getString(0);
		}

		c.close();
		db.close();
		return r;
	}

	// 设置键值对
	public void kvSet (String k, String v) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EmLocalSql.KvGet.toString(),
				k
		), null);
		boolean b = c.moveToNext();
		c.close();
		db.close();

		if (b) {
			exe(EmLocalSql.KvSet, k, v);
		} else {
			exe(EmLocalSql.KvAdd, k, v);
		}
	}

	// 获取键值对
	public void kvDel (String k) {
		exe(EmLocalSql.KvDel, k);
	}

}
