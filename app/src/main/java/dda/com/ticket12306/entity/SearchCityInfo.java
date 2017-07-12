package dda.com.ticket12306.entity;

import java.util.List;

/**
 * Created by nuo on 2016-09-14.
 * Created by 22:43.
 * 描述:搜索结果城市的车票信息
 */
public class SearchCityInfo {

    /*"buttonTextInfo":"预订"}]
     * messages : []
     * validateMessages : {}
     */

    public String validateMessagesShowId;
    public boolean status;
    public int httpstatus;
    /**
     * queryLeftNewDTO : {"train_no":"330000K5980R","station_train_code":"K599","start_station_telecode":"BTC","start_station_name":"包头","end_station_telecode":"GZQ","end_station_name":"广州","from_station_telecode":"BXP","from_station_name":"北京西","to_station_telecode":"GZQ","to_station_name":"广州","start_time":"05:14","arrive_time":"11:12","day_difference":"1","train_class_name":"","lishi":"29:58","canWebBuy":"Y","lishiValue":"1798","yp_info":"1000003000400000001010000000003000000025","control_train_day":"20301231","start_train_date":"20160920","seat_feature":"W3431333","yp_ex":"10401030","train_seat_feature":"3","seat_types":"1413","location_code":"C1","from_station_no":"09","to_station_no":"36","control_day":59,"sale_time":"0800","is_support_card":"0","controlled_train_flag":"0","controlled_train_message":"正常车次，不受控","yz_num":"无","rz_num":"--","yw_num":"有","rw_num":"10","gr_num":"--","zy_num":"--","ze_num":"--","tz_num":"--","gg_num":"--","yb_num":"--","wz_num":"无","qt_num":"--","swz_num":"--"}
     * secretStr : MjAxNi0wOS0yMSMwMCNLNTk5IzI5OjU4IzA1OjE0IzMzMDAwMEs1OTgwUiNCWFAjR1pRIzExOjEyI%2BWMl%2BS6rOilvyPlub%2Flt54jMDkjMzYjMTAwMDAwMzAwMDQwMDAwMDAwMTAxMDAwMDAwMDAwMzAwMDAwMDAyNSNDMSMxNDczOTAzMjI3NzE5IzE0NjkzMTg0MDAwMDAjMThBRjZGQTJFMjQ2MjlCODdDOEMwOENGQTUzNkQ3NTU1REJFODBFREFGMjEwNzQ0OEM0OTg2RDk%3D
     * buttonTextInfo : 预订
     */

    public List<DataBean> data;

    public static class DataBean {
        /**
         * train_no : 330000K5980R
         * station_train_code : K599
         * start_station_telecode : BTC
         * start_station_name : 包头
         * end_station_telecode : GZQ
         * end_station_name : 广州
         * from_station_telecode : BXP
         * from_station_name : 北京西
         * to_station_telecode : GZQ
         * to_station_name : 广州
         * start_time : 05:14
         * arrive_time : 11:12
         * day_difference : 1
         * train_class_name :
         * lishi : 29:58
         * canWebBuy : Y
         * lishiValue : 1798
         * yp_info : 1000003000400000001010000000003000000025
         * control_train_day : 20301231
         * start_train_date : 20160920
         * seat_feature : W3431333
         * yp_ex : 10401030
         * train_seat_feature : 3
         * seat_types : 1413
         * location_code : C1
         * from_station_no : 09
         * to_station_no : 36
         * control_day : 59
         * sale_time : 0800
         * is_support_card : 0
         * controlled_train_flag : 0
         * controlled_train_message : 正常车次，不受控
         * yz_num : 无
         * rz_num : --
         * yw_num : 有
         * rw_num : 10
         * gr_num : --
         * zy_num : --
         * ze_num : --
         * tz_num : --
         * gg_num : --
         * yb_num : --
         * wz_num : 无
         * qt_num : --
         * swz_num : --
         */

        public QueryLeftNewDTOBean queryLeftNewDTO;
        public String buttonTextInfo;

        public static class QueryLeftNewDTOBean {
            public String station_train_code;
            public String start_station_telecode;
            public String start_station_name;
            public String end_station_telecode;
            public String end_station_name;
            public String from_station_telecode;
            public String from_station_name;
            public String to_station_telecode;
            public String to_station_name;
            public String start_time;
            public String arrive_time;
            public String yp_info;
            public String day_difference;
            public String lishi;
            public String canWebBuy;
            public String yz_num;
            public String rz_num;
            public String yw_num;
            public String rw_num;
            public String gr_num;
            public String zy_num;
            public String ze_num;
            public String tz_num;
            public String gg_num;
            public String yb_num;
            public String wz_num;
            public String qt_num;
            public String swz_num;
        }
    }
}
