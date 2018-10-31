package tk.ziniulian.job.rfid.tag;

/**
 * 6C型标签（小方块）
 * Created by LZR on 2017/8/10.
 */

public class T6Cblock extends T6C {
	private int ewl = 30;	// epc过长会导use长短变化，缩短时use会多出冗余数据
	private int uwl = 32;
//	private int uwl = 64;	// 实际可写64个字节。但当指定tid进行写入时，只能将后32位写入；若写入字节长度不大于32，则能够写到前32位中。
}
