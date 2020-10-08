package com.resercho;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.resercho.POJO.ModelProfile;

import static android.content.Context.MODE_PRIVATE;

public class DataHandler {

    public static final String USER_INFO = "USER_INFO";
    public static final String APP_PREFS = "APP_PREFS";

    public static final String UID = "userId";
    public static final String USERNAME = "username";
    public static final String PROFILE = "profile";
    public static final String UMAIL = "userMail";
    public static final String UPHONE = "userPhone";
    public static final String UDISPNAME = "userDispName";
    public static final String UDISPIC = "userDisplayPic";
    public static final String MODE = "app_mode";
    public static int[] hideFromSchool = {5,6,7,8,9,10,11,15,16,18,20};

    public static final int SCHOOL_MODE = 1;
    public static final int UNIV_MODE = 2;

    public static void setMode(int mode,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE).edit();
        editor.putInt(MODE,mode);
        editor.apply();
    }

    public static void setGsignIn(boolean isit,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE).edit();
        editor.putBoolean("GOOGLE_SIGN",isit);
        editor.apply();
    }

    public static int getMode(Context context){
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE);
        return prefs.getInt(MODE,UNIV_MODE);
    }

    public static String getModeName(Context context){
        if(getMode(context) == SCHOOL_MODE)
            return "SCHOOL";
        else
            return "UNIVERSITY";
    }

    public static boolean isSchoolMode(Context context){
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE);
        int mode = prefs.getInt(MODE,UNIV_MODE);
        if(mode == SCHOOL_MODE)
            return true;
        else
            return false;
    }

    public static boolean isUnivMode(Context context){
        if(context!=null) {
            SharedPreferences prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
            int mode = prefs.getInt(MODE, UNIV_MODE);
            if (mode == UNIV_MODE)
                return true;
            else
                return false;
        }else
            return false;
    }


    public static void setDisplayName(String name, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(UDISPNAME,name);
        editor.apply();
    }

    public static void setProfilePic(String url, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(UDISPIC,url);
        editor.apply();
    }

    public static String getProfilePic(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(UDISPIC,null);
    }

    public static String getDisplayName(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(UDISPNAME,null);
    }

    public static void setUsernameName(String name, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME,name);
        editor.apply();
    }

    public static void setUserProfile(ModelProfile profile, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        String js = new Gson().toJson(profile);
        editor.putString(PROFILE,js);
        editor.apply();
    }


    public static ModelProfile getProfileModel(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        String js = prefs.getString(PROFILE,null);
        if(js==null)
            return null;
        return new Gson().fromJson(js, ModelProfile.class);
    }


    public static String getUsername(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(USERNAME,null);
    }

    public static void setUid(String uid, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(UID,uid);
        editor.apply();
    }

    public static String getUserId(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(UID,null);

    }

    public static void setUserMail(String mail, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(UMAIL,mail);
        editor.apply();
    }

    public static void resetLogout(Context context){
        setUserProfile(null,context);
        setUsernameName(null,context);
        setDisplayName(null,context);
        setUid(null,context);
        setProfilePic(null,context);
        setUserPhone(null,context);
        setUserMail(null,context);
    }
    public static void setIntroShown(Context context){
        SharedPreferences.Editor edit = context.getSharedPreferences("intro_shown",MODE_PRIVATE).edit();
        edit.putBoolean("shown",true);
        edit.apply();
    }

    public static boolean isIntroShown(Context context){
        SharedPreferences prefs = context.getSharedPreferences("intro_shown",MODE_PRIVATE);
        return prefs.getBoolean("shown",false);
    }


    public static String getUserMail(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(UMAIL,null);
    }

    public static void setUserPhone(String phone, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE).edit();
        editor.putString(UPHONE,phone);
        editor.apply();
    }

    public static String getUserphone(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return prefs.getString(UPHONE,null);
    }
}
