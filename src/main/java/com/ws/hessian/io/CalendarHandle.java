package com.ws.hessian.io;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarHandle implements Serializable, HessianHandle {
    private Class type;
    private Date date;

    public CalendarHandle() {
    }

    public CalendarHandle(Class type, long time) {
        if(!GregorianCalendar.class.equals(type)) {
            this.type = type;
        }

        this.date = new Date(time);
    }

    private Object readResolve() {
        try {
            Object e;
            if(this.type != null) {
                e = (Calendar)this.type.newInstance();
            } else {
                e = new GregorianCalendar();
            }

            ((Calendar)e).setTimeInMillis(this.date.getTime());
            return e;
        } catch (RuntimeException var2) {
            throw var2;
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }
}
