package dda.com.ticket12306.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by nuo on 2016-09-18.
 * Created by 14:43.
 * 描述:最近查询功能
 */
@Table("LastQuery")
public class LastQuery {

    private static final long serialVersionUID = 1L;

    // 设置为主键,自增
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    // 取名为“_id”,如果此处不重新命名,就采用属性名称
    @Column("_id")
    private int id;
    //出发城市
    public String go_city;
    //目的城市
    public String to_city;
    //出发城市代码
    public String go_code;
    //目的城市代码
    public String to_code;
}
