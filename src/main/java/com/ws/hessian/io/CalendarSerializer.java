package com.ws.hessian.io;


import java.util.Calendar;

public class CalendarSerializer extends AbstractSerializer {
    public static final Serializer SER = new CalendarSerializer();

    public CalendarSerializer() {
    }

    public Object writeReplace(Object obj) {
        Calendar cal = (Calendar)obj;
        return new CalendarHandle(cal.getClass(), cal.getTimeInMillis());
    }
}
