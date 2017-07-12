package dda.com.ticket12306.entity;

import com.litesuits.orm.db.annotation.Table;

/**
 * Created by nuo on 2016-09-13.
 * Created by 10:40.
 * 描述:12306火车站实体类
 */
@Table("st12306")
public class City12306 implements Comparable<City12306> {

    private static final long serialVersionUID = 1L;

    // 设置为主键,自增
    //@PrimaryKey(AssignType.AUTO_INCREMENT)
    // 取名为“_id”,如果此处不重新命名,就采用属性名称
    //@Column("_id")
    //public int id;
    //火车站名
    private String st_name;
    //火车站拼音全名
    private String st_py_full;
    //火车站首字母的索引
    private String ind;
    //火车站索引拼音第二次
    private String st_py_s2;
    //标志是哪个位置
    private String pinyi;

    private String st_code;

    //当前标志位名称
    private String name;

    public City12306(String name, String pinyi) {
        this.pinyi = pinyi;
        this.name = name;
    }

    public City12306() {

    }

    public String getSt_code() {
        return st_code;
    }

    public void setSt_code(String st_code) {
        this.st_code = st_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyi() {
        return pinyi;
    }

    public void setPinyi(String pinyi) {
        this.pinyi = pinyi;
    }

    public String getSt_name() {
        return st_name;
    }

    public void setSt_name(String st_name) {
        this.st_name = st_name;
    }

    public String getSt_py_full() {
        return st_py_full;
    }

    public void setSt_py_full(String st_py_full) {
        this.st_py_full = st_py_full;
    }

    public String getIndex() {
        return ind;
    }

    public void setIndex(String index) {
        this.ind = ind;
    }

    @Override
    public int compareTo(City12306 o) {
        return getSt_py_full().compareTo(o.st_py_full);
    }


    public String getSt_py_s2() {
        return st_py_s2;
    }

    public void setSt_py_s2(String st_py_s2) {
        this.st_py_s2 = st_py_s2;
    }
}
