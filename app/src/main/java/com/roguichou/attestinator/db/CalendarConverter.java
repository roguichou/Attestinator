package com.roguichou.attestinator.db;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class CalendarConverter {


    /**
     * Calendar <=> Long
     */
    @TypeConverter
    public static Calendar fromLongToCalendar (Long l) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        return c;
    }

    @TypeConverter
    public static Long fromCalendarToLong(Calendar c){
        return c == null ? null : c.getTime().getTime();
    }
}
