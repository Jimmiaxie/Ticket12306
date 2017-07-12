package dda.com.ticket12306.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by nuo on 2016-09-13.
 * Created by 10:40.
 * 描述:历史城市选择
 */
@Table("HistoryCity")
public class HistoryCity {

    private static final long serialVersionUID = 1L;

    // 设置为主键,自增
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    // 取名为“_id”,如果此处不重新命名,就采用属性名称
    @Column("_id")
    private int id;
    //火车站名
    public String st_name;
    //火车站拼音全名
    public String st_code;
}
