package com.deep.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangfei on 2018/1/11.
 */

public class Tools {
    public static String  formatData(long t){
        Date date=new Date(t);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=formatter.format(date);
        return time;
    }
    public static String  formatData(Date t){

        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=formatter.format(t);
        return time;
    }
}
