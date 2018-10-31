package tk.ziniulian.util.dao;

/**
 * SQL建表语句
 * Created by 李泽荣 on 2018/7/19.
 */

public enum EmLocalCrtSql {
	sdDir("ziniulian/"),	// 数据库存储路径

	dbNam("test"),	// 数据库名

	Bkv(	// 基本键值对表
		"create table Bkv(" +	// 表名
		"k text primary key not null, " +	// 键
		"v text)");	// 值

	private final String sql;
	EmLocalCrtSql(String s) {
		sql = s;
	}

	@Override
	public String toString() {
		return sql;
	}
}
