package com.nze.nzexchange.widget.chart.formatter;



import com.nze.nzexchange.widget.chart.base.IDateTimeFormatter;
import com.nze.nzexchange.widget.chart.utils.DateUtil;

import java.util.Date;

/**
 * 时间格式化器
 * Created by tifezh on 2016/6/21.
 */

public class TimeFormatter implements IDateTimeFormatter {
    @Override
    public String format(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.shortTimeFormat.format(date);
    }
}
