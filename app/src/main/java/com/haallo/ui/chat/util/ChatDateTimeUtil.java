package com.haallo.ui.chat.util;

import android.app.Activity;
import android.util.Log;

import com.haallo.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DateTimeUtil.kt */
public class ChatDateTimeUtil {
    @NotNull
    public String getCurrentDateString() {
        Calendar instance = Calendar.getInstance();
        Intrinsics.checkExpressionValueIsNotNull(instance, "Calendar.getInstance()");
        Date c = instance.getTime();
        StringBuilder sb = new StringBuilder();
        sb.append("Current time => ");
        sb.append(c);
        System.out.println(sb.toString());
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(c);
        Intrinsics.checkExpressionValueIsNotNull(formattedDate, "formattedDate");
        return formattedDate;
    }

    @NotNull
    public String getDoctorFormatDate(@NotNull Date date) {
        Intrinsics.checkParameterIsNotNull(date, "date");
        SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(date));
        sb.append(", ");
        sb.append(getDayOfDate(date));
        return sb.toString();
    }

    @NotNull
    public String getStringFormatDate(@NotNull Date date) {
        Intrinsics.checkParameterIsNotNull(date, "date");
        String format = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(date);
        Intrinsics.checkExpressionValueIsNotNull(format, "df.format(date)");
        return format;
    }

    @NotNull
    public String getDayOfDate(@NotNull Date date) {
        Intrinsics.checkParameterIsNotNull(date, "date");
        String day = "";
        switch (date.getDay() < 6 ? date.getDay() + 1 : 0) {
            case 0:
                return "Saturday";
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            default:
                return day;
        }
    }

    @Nullable
    public String getBookingTime(@NotNull String bookingDate) {
        Intrinsics.checkParameterIsNotNull(bookingDate, "bookingDate");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy | hh:mm aa", Locale.ENGLISH);
        Date d = null;
        try {
            d = input.parse(bookingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(formatted);
        Log.i("DATE", sb.toString());
        return formatted;
    }

    @NotNull
    public String getBookingTimeOnly(@NotNull String bookingDate) {
        Intrinsics.checkParameterIsNotNull(bookingDate, "bookingDate");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        Date d = null;
        try {
            d = input.parse(bookingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(formatted);
        Log.i("DATE", sb.toString());
        Intrinsics.checkExpressionValueIsNotNull(formatted, "formatted");
        return formatted;
    }

    @Nullable
    public String getBookingDate(@NotNull String bookingDate) {
        Intrinsics.checkParameterIsNotNull(bookingDate, "bookingDate");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date d = null;
        try {
            d = input.parse(bookingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(formatted);
        Log.i("DATE", sb.toString());
        return formatted;
    }

    @Nullable
    public String getBookingDisplayDate(@NotNull String bookingDate) {
        Intrinsics.checkParameterIsNotNull(bookingDate, "bookingDate");
        SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date d = null;
        try {
            d = input.parse(bookingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(formatted);
        Log.i("DATE", sb.toString());
        return formatted;
    }

    public Calendar getCalendarDate(String bookingDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(bookingDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }

    @NotNull
    public Calendar getTodayCalendarDate() {
        Date date = new Date();
        Date otherdate = new Date(date.getYear(), date.getMonth(), date.getDate());
        Calendar cal = Calendar.getInstance();
        Intrinsics.checkExpressionValueIsNotNull(cal, "cal");
        cal.setTime(otherdate);
        return cal;
    }

    @NotNull
    public Date get3MonthsLaterDate(@NotNull Date date) {
        Intrinsics.checkParameterIsNotNull(date, "date");
        return new Date(date.getYear(), date.getMonth() + 3, date.getDate());
    }

    @NotNull
    public String get12HourTime1(@NotNull Date date) {
        Intrinsics.checkParameterIsNotNull(date, "date");
        String format = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(date);
        Intrinsics.checkExpressionValueIsNotNull(format, "df.format(date)");
        return format;
    }


    public boolean isTimeAfterCurrent(@NotNull String startDate, @NotNull String startTime) throws ParseException {
        Intrinsics.checkParameterIsNotNull(startDate, "startDate");
        Intrinsics.checkParameterIsNotNull(startTime, "startTime");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.ENGLISH);
        StringBuilder sb = new StringBuilder();
        sb.append(startDate);
        sb.append(MaskedEditText.SPACE);
        sb.append(startTime);
        switch (sdf.parse(sb.toString()).compareTo(new Date())) {
            case -1:
                return false;
            case 0:
                return false;
            case 1:
                return true;
            default:
                return false;
        }
    }

    private static final String SEPARATOR = " ";

    public static String getTimeAgo(Activity activity, long timestamp) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        Date timestampDate = new Date();
        timestampDate.setTime(timestamp);
        long now = System.currentTimeMillis();
        long secondsAgo = (now - timestamp) / 1000;

        int minute = 60;
        int hour = 60 * minute;
        int day = 24 * hour;
        int week = 7 * day;

        if (secondsAgo < minute)
            return "Just now";
        else if (secondsAgo < hour)
            //minutes ago
            return secondsAgo / minute + SEPARATOR + activity.getResources().getString(R.string.minutes_ago);
        else if (secondsAgo < day) {
            //hours ago
            int hoursAgo = (int) (secondsAgo / hour);
            if (hoursAgo <= 5)
                return hoursAgo + SEPARATOR + activity.getResources().getString(R.string.hours_ago);

            //today at + time AM or PM
            return activity.getResources().getString(R.string.today_at) + SEPARATOR + timeFormat.format(timestampDate);
        } else if (secondsAgo < week) {
            int daysAgo = (int) (secondsAgo / day);
            //yesterday + time AM or PM
            if (daysAgo == 1)
                return activity.getResources().getString(R.string.yesterday_at) + SEPARATOR + timeFormat.format(timestampDate);

            //days ago
            return secondsAgo / day + SEPARATOR + activity.getResources().getString(R.string.days_ago);
        }

        //otherwise it's been a long time show the full date
        return fullDateFormat.format(timestampDate) + SEPARATOR + activity.getResources().getString(R.string.at) + SEPARATOR + timeFormat.format(timestampDate);
    }

    public static String getTimeAgoForChatList(Activity activity, long timestamp) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        Date timestampDate = new Date();
        timestampDate.setTime(timestamp);
        long now = System.currentTimeMillis();
        long secondsAgo = (now - timestamp) / 1000;

        int minute = 60;
        int hour = 60 * minute;
        int day = 24 * hour;
        int week = 7 * day;

        if (secondsAgo < minute)
            return "Just now";
        else if (secondsAgo < day) {
            return timeFormat.format(timestampDate);
        } else if (secondsAgo < week) {
            int daysAgo = (int) (secondsAgo / day);
            if (daysAgo == 1) {
                return activity.getResources().getString(R.string.yesterday_at);
            }
        } else {
            return fullDateFormat.format(timestampDate);
        }
        return fullDateFormat.format(timestampDate);
    }

    public static String getDayForChat(Activity activity, long timestamp) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        Date timestampDate = new Date();
        timestampDate.setTime(timestamp);
        long now = System.currentTimeMillis();
        long secondsAgo = (now - timestamp) / 1000;
        int minute = 60;
        int hour = 60 * minute;
        int day = 24 * hour;
        int daysAgo = (int) (secondsAgo / day);

        if (secondsAgo < day) {
            return activity.getResources().getString(R.string.today);
        } else if (daysAgo == 1) {
            return activity.getResources().getString(R.string.yesterday);
        } else {
            return fullDateFormat.format(timestampDate);
        }
    }

    public String getChatMessageTime(Activity activity, long timestamp) {
        /*
        if today:
        today , 10:27PM

        if yesterday :
        yesterday , 10:28AM

        if same year:
        Feb 8 , 3:41AM

        else
        1/15/17 ,10:46PM  */

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd , hh:mm a", Locale.ENGLISH);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM d", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Date timestampDate = new Date();
        timestampDate.setTime(timestamp);
        long now = System.currentTimeMillis();
        long secondsAgo = (now - timestamp) / 1000;
        int minute = 60;
        int hour = 60 * minute;
        int day = 24 * hour;
        int week = 7 * day;

        if (secondsAgo < day) {
            return activity.getResources().getString(R.string.today) + ", " + timeFormat.format(timestampDate);
        } else if (secondsAgo < week) {
            int daysAgo = (int) (secondsAgo / day);
            if (daysAgo == 1) {
                return activity.getResources().getString(R.string.yesterday) + ", " + timeFormat.format(timestampDate);
            } else if (isSameYear(now, timestamp)) {
                return monthFormat.format(timestampDate) + ", " + timeFormat.format(timestampDate);
            }
        }
        return fullDateFormat.format(timestampDate);
    }

    //check if two dates are in the same year
    public static boolean isSameYear(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

}
