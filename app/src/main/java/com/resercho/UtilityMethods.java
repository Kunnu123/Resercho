package com.resercho;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilityMethods {

    public static final long hourToMillis = 3600000;
    public static final long minToMillis = 60000;

    public static String getAgoTimeFromTimestamp(String timestamp){
        String ret = "";
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp)*1000);
        long curTime = System.currentTimeMillis();
        long diff =  curTime - (Long.parseLong(timestamp)*1000);

        diff = diff/1000;
        String post = "moments ago";

        if(diff>59) {
            diff = diff / 60;
            post = diff==1?"min ago":"mins ago";
            if (diff > 59) {
                diff = diff / 60;
                post = diff == 1 ? "hour ago" : "hours ago";
                if (diff > 24) {
                    diff = diff / 24;
                    post = diff == 1 ? "day ago" : "days ago";
                    if (diff > 7) {
                        diff = diff / 7;
                        post = diff == 1 ? "week ago" : "weeks ago";
                        if(diff>7){
                            post = getDateFromTimeStamp(timestamp);
                        }
                    }
                }
            }
        }
        if(post.contains("moments"))
            ret = post;
        else if(post.contains("ago"))
            ret = diff + " " + post;
        else
            ret = post;

        return ret;
    }

    public static String getCurrentYear(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public static int getCurrentMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date date = new Date();
        return Integer.parseInt(formatter.format(date));
    }

    public static long getDatesDuration(String d, String d1){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(d);
            Date date2 = simpleDateFormat.parse(d1);

            return printDifference(date1,date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : "+ endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
//
//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }

    public static String getCurrentTimeFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":"+ cal.get(Calendar.SECOND)  +" "+ cal.get(Calendar.AM_PM);
    }

    public static String getDateFromTimeStamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.DATE) +" "+getMonthName(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR);
    }

    public static String getLongDateFromTimeStamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.DATE) +"/"+cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"+cal
                .get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);

    }

    public static int getWeekFromTimeStamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getMonthFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return getMonthName(cal.get(Calendar.MONTH));
    }

    public static String getMinutesFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        int min = cal.get(Calendar.MINUTE);
        return min>9?min+"":"0"+min;
    }

    public static String getHourFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        int hour = cal.get(Calendar.HOUR);
        return hour>9?hour+"":"0"+hour;
    }


    public static String getAMPmFromT(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.AM_PM)==Calendar.AM?"AM":"PM";
    }



    public static String getYearFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.YEAR)+"";
    }

    public static String getYearDividedFromTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        return cal.get(Calendar.YEAR)+"";
    }

    public static String getMonthName(int i){
        switch (i){
            case 0:return "January";
            case 1:return "February";
            case 2:return "March";
            case 3:return "April";
            case 4:return "May";
            case 5:return "June";
            case 6:return "July";
            case 7:return "August";
            case 8:return "September";
            case 9:return "October";
            case 10:return "November";
            case 11:return "December";
            default:return "null";
        }
    }



    public static int getMonthNumber(String timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000);
        return cal.get(Calendar.MONTH);
    }

    public static String getNewsTimestamp(String fromDateTime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try {
            Date from = formatter.parse(fromDateTime);
            Log.d("NewsFeed","NewsFeed timestamp inside : "+  new Timestamp(from.getTime()) + " Time:"+from.getTime());
            return from.getTime()+"";
        }catch (Exception e){
            Log.d("Stock","UtilityMethods getTimestamp() Exception:"+e);
        }
        return fromDateTime;
    }

    public static String getTimeFromHHMMSS(String fromDateTime){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",Locale.ENGLISH);
        try {
            Date from = formatter.parse(fromDateTime);
            return getHourFromTimestamp(from.getTime()+"") + " : " +getMinutesFromTimestamp(from.getTime()+"") + " " + getAMPmFromT(from.getTime()+"");
        }catch (Exception e){
            Log.d("Stock","UtilityMethods getTimestamp() Exception:"+e);
        }
        return fromDateTime;
    }

    public static String getServerTimeStamp(String fromDateTime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try {
            Date from = formatter.parse(fromDateTime);
            Log.d("NewsFeed","NewsFeed timestamp inside : "+  new Timestamp(from.getTime()) + " Time:"+from.getTime());
            return (from.getTime()+(12*hourToMillis + 30*minToMillis)) +"";
        }catch (Exception e){
            Log.d("Stock","UtilityMethods getTimestamp() Exception:"+e);
        }
        return fromDateTime;
    }

    public static String getFormattedTime(String fromDate) throws ParseException {
        String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(fromDate))/1000)+"";

        String form= "";

        form = getHourFromTimestamp(time)+":"+getMinutesFromTimestamp(time)+getAMPmFromT(time);

        return form;
    }

    public static String getTimeStamp(String fromDateTime){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        try {
            Date from = formatter.parse(fromDateTime);
            return from.getTime()+"";
        }catch (Exception e){
            Log.d("Stock","UtilityMethods timex getTimestamp() Exception:"+e);
        }
//        return new Date().getTime()+"";
        return "Coudnt";
    }
    public static String capitalizeFirst(String name){
        String[] div = name.split(" ");
        String ret = "";
        for(String s:div){
            ret = (ret + s.substring(0,1).toUpperCase() + s.substring(1))+" ";
        }
        return ret;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension="unknown";

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            try {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }catch (NullPointerException e){
                return "unknown";
            }

        }

        return extension;
    }

    public static boolean isImageMime(String mime){
        if(mime.equalsIgnoreCase("jpg")||mime.equalsIgnoreCase("png")||mime.equalsIgnoreCase("jpeg"))
            return true;
        else
            return false;
    }

    public static boolean isVideoMime(String mime){
        if(mime.equalsIgnoreCase("mp4")||mime.equalsIgnoreCase("avi")||mime.equalsIgnoreCase("mkv"))
            return true;
        else
            return false;
    }



    public static String getFormatCount(long count){
        if(count>999999999){
            return count/1000000000 + "B";
        }else if(count>999999){
            return count/1000000 + "M";
        }else if(count>999){
            return count/1000 + "K";
        }else{
            return count+"";
        }
    }

    public static boolean isAudioMime(String mime){
        if(mime.equalsIgnoreCase("mp3"))
            return true;
        else
            return false;
    }

    public static boolean isDocMime(String mime){
        if(mime.equalsIgnoreCase("pdf"))
            return true;
        else
            return false;
    }

    public static String getMimeFromUrl(String url){
        try {
            return url.substring(url.lastIndexOf(".")+1);
        }catch (Exception e){
            return null;
        }
    }

}
