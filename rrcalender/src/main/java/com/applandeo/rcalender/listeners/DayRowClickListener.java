package com.applandeo.rcalender.listeners;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.applandeo.rcalender.EventDay;
import com.applandeo.rcalender.R;
import com.applandeo.rcalender.adapters.CalendarPageAdapter;
import com.applandeo.rcalender.utils.DateUtils;
import com.applandeo.rcalender.utils.DayColorsUtils;
import com.applandeo.rcalender.utils.SelectedDay;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class is responsible for handle click events
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

public class DayRowClickListener implements AdapterView.OnItemClickListener {
    private CalendarPageAdapter mCalendarPageAdapter;
    private Context mContext;
    private List<EventDay> mEventDays;
    private OnDayClickListener mOnDayClickListener;
    private boolean mIsDatePicker;
    private int mTodayLabelColor;
    private int mSelectionColor;


    public DayRowClickListener(CalendarPageAdapter calendarPageAdapter, Context context,
                               List<EventDay> eventDays, OnDayClickListener onDayClickListener,
                               boolean isDatePicker, int todayLabelColor, int selectionColor) {
        mCalendarPageAdapter = calendarPageAdapter;
        mContext = context;
        mEventDays = eventDays;
        mOnDayClickListener = onDayClickListener;
        mIsDatePicker = isDatePicker;
        mTodayLabelColor = todayLabelColor;
        mSelectionColor = selectionColor;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Calendar day = new GregorianCalendar();
        day.setTime((Date) adapterView.getItemAtPosition(position));

        // If calendar is in picker mode than day is selected
        if (mIsDatePicker) {
            selectDay(view, day);
            return;
        }

        // If calendar is not in the picker mode than onClick method is called
        if (mOnDayClickListener != null) {
            mCalendarPageAdapter.setSelectedDate(day);

            onClick(position, day);
        }
    }

    private void selectDay(View view, Calendar day) {
        // Getting previous selected day
        SelectedDay selectedDay = mCalendarPageAdapter.getSelectedDay();

        if (selectedDay != null && !day.equals(selectedDay.getCalendar())) {
            TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

            // Checking if current month day is selecting
            if (dayLabel.getCurrentTextColor() !=
                    ContextCompat.getColor(mContext, R.color.nextMonthDayColor)) {

                mCalendarPageAdapter.setSelectedDate(day);

                // Coloring selected day
                DayColorsUtils.setSelectedDayColors(mContext, dayLabel, mSelectionColor);

                TextView previousDayLabel =
                        (TextView) selectedDay.getView().findViewById(R.id.dayLabel);

                // Coloring previous selected day
                DayColorsUtils.setCurrentMonthDayColors(mContext, selectedDay.getCalendar(),
                        DateUtils.getCalendar(), previousDayLabel, mTodayLabelColor);

                mCalendarPageAdapter.setSelectedDay(new SelectedDay(dayLabel, day));
            }
        }
    }

    private void onClick(int position, final Calendar day) {
        if (mEventDays != null) {
            mOnDayClickListener.onDayClick(mEventDays.get(position));
            return;
        }

        Stream.of(mEventDays).filter(new Predicate<EventDay>() {
            @Override
            public boolean test(EventDay eventDate) {
                if (eventDate.getCalendar().equals(day)) {
                    if (mOnDayClickListener != null)
                        mOnDayClickListener.onDayClick(new EventDay(day));
                }
                return false;
            }
        });

//        Stream.of(mEventDays).filter(eventDate ->
//                eventDate.getCalendar().equals(day)).findFirst().ifPresentOrElse(
//                calendarEventDay -> mOnDayClickListener.onDayClick(calendarEventDay),
//                () -> mOnDayClickListener.onDayClick(new EventDay(day)));
    }
}
