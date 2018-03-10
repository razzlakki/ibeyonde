package com.applandeo.rcalender.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.rcalender.EventDay;
import com.applandeo.rcalender.R;
import com.applandeo.rcalender.listeners.EventClickLister;
import com.applandeo.rcalender.listeners.OnSelectDateListener;
import com.applandeo.rcalender.utils.DateUtils;
import com.applandeo.rcalender.utils.DayColorsUtils;
import com.applandeo.rcalender.utils.SelectedDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class is responsible for loading a one day cell.
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

class CalendarDayAdapter extends ArrayAdapter<Date> {
    private CalendarPageAdapter mCalendarPageAdapter;
    private Context mContext;
    private List<EventDay> mEventDays;
    private LayoutInflater mLayoutInflater;
    private int mItemLayoutResource;
    private int mMonth;
    private Calendar mToday = DateUtils.getCalendar();
    private boolean mIsDatePicker;
    private int mTodayLabelColor;
    private int mSelectionColor;
    private EventClickLister eventClickListenr;
    private OnSelectDateListener onSelectDateListener;

    CalendarDayAdapter(CalendarPageAdapter calendarPageAdapter, Context context, int itemLayoutResource,
                       ArrayList<Date> dates, List<EventDay> eventDays, int month, boolean isDatePicker,
                       int todayLabelColor, int selectionColor) {
        super(context, itemLayoutResource, dates);

        mCalendarPageAdapter = calendarPageAdapter;
        mContext = context;
        mEventDays = eventDays;
        mMonth = month < 0 ? 11 : month;
        mLayoutInflater = LayoutInflater.from(context);
        mItemLayoutResource = itemLayoutResource;
        mIsDatePicker = isDatePicker;
        mTodayLabelColor = todayLabelColor;
        mSelectionColor = selectionColor;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mLayoutInflater.inflate(mItemLayoutResource, parent, false);
        }

        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);
        LinearLayout dayIcon = (LinearLayout) view.findViewById(R.id.event_layout);

        final Calendar day = new GregorianCalendar();
        day.setTime(getItem(position));

        // Loading an image of the event
        if (dayIcon != null) {
            loadIcon(dayIcon, day, view);
        }

        if (mIsDatePicker && day.equals(mCalendarPageAdapter.getSelectedDate())
                && day.get(Calendar.MONTH) == mMonth) {
            // Setting selected day color
            mCalendarPageAdapter.setSelectedDay(new SelectedDay(dayLabel, day));
            DayColorsUtils.setSelectedDayColors(mContext, dayLabel, mSelectionColor);

        } else {
            if (day.get(Calendar.MONTH) == mMonth) { // Setting current month day color
                DayColorsUtils.setCurrentMonthDayColors(mContext, day, mToday, dayLabel, mTodayLabelColor);
            } else { // Setting not current month day color
                DayColorsUtils.setDayColors(dayLabel, ContextCompat.getColor(mContext,
                        R.color.nextMonthDayColor), Typeface.NORMAL, R.drawable.background_transparent);
            }
        }

        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSelectDateListener != null)
                    onSelectDateListener.onSelect(day);
            }
        });

        return view;
    }

    private void loadIcon(final LinearLayout dayIcon, final Calendar day, View view) {
        if (mEventDays != null) {
            final EventDay id = dayInCalender(day, mEventDays);
            if (id != null) {
                for (Integer ids : id.getImageResource()) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageResource(ids);
                    imageView.setVisibility(View.VISIBLE);
                    dayIcon.addView(imageView, new LinearLayout.LayoutParams(20, 20));
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (eventClickListenr != null)
                            eventClickListenr.onEventClick(id);
                    }
                });
            }
            return;
        }
    }

    private EventDay dayInCalender(Calendar day, List<EventDay> mEventDays) {
        for (EventDay eventDay : mEventDays) {
            if ((eventDay.getCalendar().get(Calendar.YEAR) == day.get(Calendar.YEAR))
                    && (eventDay.getCalendar().get(Calendar.MONTH) == day.get(Calendar.MONTH))
                    && eventDay.getCalendar().get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH)) {
                return eventDay;
            }
        }
        return null;
    }

    public void setEventClickListenr(EventClickLister eventClickListenr) {
        this.eventClickListenr = eventClickListenr;
    }

    public void setOnSelectDateListener(OnSelectDateListener onSelectDateListener) {
        this.onSelectDateListener  = onSelectDateListener;
    }
}
