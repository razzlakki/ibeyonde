package com.dms.collapsiblecalendarview.data;

/**
 * Created by Raja on 28/04/18.
 */

public class Event {
    private int mYear;
    private int mMonth;
    private int mDay;

    public Event(int year, int month, int day){
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }

    public int getMonth(){
        return mMonth;
    }

    public int getYear(){
        return mYear;
    }

    public int getDay(){
        return mDay;
    }

}
