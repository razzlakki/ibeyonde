package com.technorabit.ibeyonde.constants;

/**
 * Created by rpeela on 10/15/15.
 */
public class AppConstants {


    public static final String REPLACER = "%--Re--%";
    public static String BASE_URL = "https://" + REPLACER + "app.ibeyonde.com/api/iot.php?view=";
    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTER = BASE_URL + "register";
    public static String GET_DEVICE_LIST = BASE_URL + "devicelist";
    public static String LIVE_VIEW = BASE_URL + "live";
    public static String LATEST_ALERTS = BASE_URL + "lastalerts";
    public static String HOLIDAY_URL = BASE_URL + "Calender/HOLIDAYS.jsp";
    public static String GET_SIBLING = BASE_URL + "Login/siblings.jsp";
    public static String GET_GALLERY = BASE_URL + "Calender/GALLERY.jsp";
    public static String GET_PAYMENTS = BASE_URL + "fee/parent_fee.jsp";
    public static String GET_PARENT_INBOX = BASE_URL + "Compose/parentinbox1.jsp";

    public static String ATTENDANCE_REPORT = BASE_URL + "Attendance/ParentAttendanceReport.jsp";
    public static String PARENT_FACILITIES = BASE_URL + "Compose/parentfacilities.jsp";
    public static String PARENT_NOTICES = BASE_URL + "Compose/parentnotices.jsp";

    public static String PARENT_FEE = BASE_URL + "fee/parent_fee.jsp";

    public static String HOMEWORK_REPORT = BASE_URL + "HomeWork/ParentHomeWorkReportDisplay.jsp";

    public static String POLL_SELECT = BASE_URL + "opinionpoll/ParentOpinionPollSelect.jsp";
    public static String POLL_SELECT_OPTIONS = BASE_URL + "opinionpoll/ParentOpinionPoll.jsp";


    public static int SPLASH_SCREEN_TIME = 5 * 1000;// 5 Sec


}
