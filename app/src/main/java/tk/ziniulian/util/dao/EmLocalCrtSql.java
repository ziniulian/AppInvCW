package tk.ziniulian.util.dao;

/**
 * SQL建表语句
 * Created by 李泽荣 on 2018/7/19.
 */

public enum EmLocalCrtSql {
	sdDir("Invengo/CW/DB/"),	// 数据库存储路径

	dbNam("cw"),	// 数据库名

	TmpLog(	// 温度日志
		"create table TmpLog(" +	// 表名
		"tim NUMERIC primary key, " +	// 记录时间
		"nam TEXT, " +		// 位置
		"tmp NUMERIC, " +	// 温度
		"stu NUMERIC, " +	// 状态 （ 0:正常 、 1:缺失 、 2:恢复<从缺失状态恢复> 、 3:状态升 、 4:状态降 、 5:级别升 、 6:级别降 ）
		"ptim NUMERIC, " +	// 前置时间
		"ptmp NUMERIC)"),	// 前置温度

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
