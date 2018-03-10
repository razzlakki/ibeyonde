package com.applandeo.rcalender;

import com.applandeo.rcalender.utils.DateUtils;

import java.util.Calendar;

/**
 * This class represents an event of a day. An instance of this class is returned when user click
 * a day cell. This class can be overridden to make calendar more functional. A list of instances of
 * this class can be passed to CalendarView object using setEvents() method.
 * <p>
 * Created by Mateusz Kornakiewicz on 23.05.2017.
 */

public class EventDay {
    public int _id;
    private Calendar mDay;
    public int type;
    private int[] mImageResource;

    public EventDay() {

    }

    public void setCalendar(Calendar calendar) {
        this.mDay = calendar;
    }

    public void setIcons(int... images) {
        this.mImageResource = images;
    }


    /**
     * @param day Calendar object which represents a date of the event
     */
    public EventDay(Calendar day) {
        mDay = day;
    }

    /**
     * @param day           Calendar object which represents a date of the event
     * @param imageResource Resource of an image which will be displayed in a day cell
     */
    public EventDay(int _id, int type, Calendar day, int... imageResource) {
        DateUtils.setMidnight(day);
        this._id = _id;
        mDay = day;
        this.type = type;
        mImageResource = imageResource;
    }


    /**
     * @return An image resource which will be displayed in the day row
     */
    public int[] getImageResource() {
        return mImageResource;
    }

    /**
     * @return Calendar object which represents a date of current event
     */
    public Calendar getCalendar() {
        return mDay;
    }


    public enum Type {
        TYPE_EVENT(1), TYPE_HOLIDAY(2);
        private final int type;

        Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
