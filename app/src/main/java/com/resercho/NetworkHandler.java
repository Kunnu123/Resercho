package com.resercho;

import android.content.Context;
import android.util.Log;

import com.resercho.POJO.ModelCitation;
import com.resercho.POJO.ModelEvent;
import com.resercho.POJO.ModelGroup;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.POJO.ModelNewGroup;
import com.resercho.POJO.ModelWork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.resercho.DataHandler.SCHOOL_MODE;
import static com.resercho.DataHandler.getUserId;
import static com.resercho.SignInActivity.TAG;

public class NetworkHandler {

    static OkHttpClient client = new OkHttpClient();

    public static final String app_dir = "https://resercho.com/linkapp/";
    public static final String app_res = "https://resercho.com/appress/";
    public static final String root_dir = "https://resercho.com/";
    public static final String post_share_link = "https://resercho.com/view_post.php?id=";
    public static final String group_share_link = "https://resercho.com/open_group.php?itype=view&id=";
    public static final String event_share_link = "https://resercho.com/view_event.php?id=";


    public Call sampleRequest(Callback callback) {
        String url = "someurl";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchInitial(Callback callback) {
        String url = "https://resercho.com/android_test.php";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchCategories(Callback callback) {
        String url = "https://resercho.com/linkapp/fetchCategories.php";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchSimilarPosts(String type, int univ, int cat, String uid,String pid,Callback callback) {
        String url = "https://resercho.com/linkapp/fetchSimilarPosts.php?uid="+uid+"&cat="+cat+"&type="+type+"&mode="+univ+"&pid="+pid;


        Log.d("SimiPost","URL NETWORK : "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call sendShared(Context context,String pid, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "sendShared.php?uid="+uid;
        url += "&pid="+pid;


        Log.d("REs","savit URL : "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call sendSaved(Context context,String pid,boolean delete, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "sendSaved.php?uid="+uid;
        url += "&pid="+pid;

        if(delete)
            url +="&delete";

        Log.d("REs","savit URL : "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchStories(Context context, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "fetchStorySkeletol.php?uid="+uid;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchChats(Context context, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "fetchMessages.php?uid="+uid;

        Log.d("Disc","Debug : fetchat : URL : "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchUserStories(Context context,String uid, Callback callback) {
        String url = app_dir + "fetchStories.php?uid="+uid;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call toggleOnline(Context context,boolean online, Callback callback) {
        String uid = getUserId(context);
        int onl = online?1:0;
        String url = app_dir + "toggleOnline.php?";
        url += "online="+onl+"&uid="+uid;

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchTypes( Callback callback) {
        String url = app_dir + "fetchTypes.php";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchOffsetVideos(Context context, String offset,String cat, boolean type,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        url += "fetchVideoPosts.php?uid=" + getUserId(context);
        if(cat!=null)
            url+="&cat="+cat;

        if(type){
            url+="&type";
        }

        url+="&offset="+offset;

        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchOffsetTrendingWithClass(Context context, String offset,String cat,String selClass, boolean type,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        url += "fetchTrending.php?uid=" + getUserId(context);
        if(cat!=null)
            url+="&cat="+cat;

        if(type){
            url+="&type";
        }

        url+="&class="+selClass;
        url+="&offset="+offset;

        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchOffsetTrending(Context context, String offset,String cat,String selClass, boolean type,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

            url += "fetchTrending.php?uid=" + getUserId(context);
            if(cat!=null)
                url+="&cat="+cat;

            if(type){
                url+="&type";
            }

            if(mode ==SCHOOL_MODE && selClass!=null)
                url+="&class="+selClass;

            url+="&offset="+offset;

        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchSearched(Context context, String search,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;
        url += "fetchSearched.php?uid=" + getUserId(context);
        url += "&search="+search;
        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call searchEvents(Context context,String key,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;


        url += "searchEvents.php?uid="+ getUserId(context);
        url += "&key="+key;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call searchGroups(Context context,String key,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;


        url += "searchGroups.php?uid="+ getUserId(context);
        url += "&key="+key;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call searchPeople(Context context,String key,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;


        url += "searchPeople.php?uid="+ getUserId(context);
        url += "&key="+key;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call searchPosts(Context context,String key, boolean isWork,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        if(isWork)
            url += "searchWorks.php?uid="+ getUserId(context);
        else
            url += "searchPosts.php?uid="+ getUserId(context);



        url += "&mode="+mode;
        url += "&key="+key;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call deleteMessage(Context context,String msgid, String toid,Callback callback) {
        String url = app_dir;
        url += "delMessage.php?uid="+ getUserId(context);
        url += "&msgid="+msgid;
        url += "&to="+toid;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchVideoPosts(Context context, String cat,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        url += "fetchVideoPosts.php?uid="+ getUserId(context);
        url += "&mode="+mode;
        url += "&cat="+cat;
        url += "&type=1";

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchVideoPosts(Context context,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        url += "fetchVideoPosts.php?uid="+ getUserId(context);
        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchOffsetFollowing(Context context,final String offset,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

            url += "fetchPosts.php?uid="+ getUserId(context);
            url += "&offset="+offset;
            url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchTrending(Context context,boolean following, String cat,String selClass, boolean type,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;

        if(following)
            url += "fetchPosts.php?uid="+ getUserId(context);
        else {
            url += "fetchTrending.php?uid=" + getUserId(context);
            if(cat!=null)
                url+="&cat="+cat;

            if(type){
                url+="&type";
            }

            Log.d("fetchTrending","Classes:"+selClass);
            if(selClass!=null && !(selClass.equalsIgnoreCase("All Classes"))){
                url+="&class="+selClass;
            }else{
                Log.d("fetchTrending","Class Else Part :  Mode "+ mode);
            }
        }



        url += "&mode="+mode;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchTrendingWithNewPost(Context context,int pid,Callback callback) {
        String url = app_dir;
        int mode = DataHandler.isUnivMode(context)?1:0;


        url += "fetchPostsWithNew.php?uid="+ getUserId(context);
        url += "&mode="+mode;
        url += "&pid="+pid;

        Log.d("Resercho","Networkall : fetchTrending: URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //https://resercho.com/linkapp/fetchGroupMsgs.php?gid=1
    public static Call fetchGroupMsgs(Context context,String gid,Callback callback) {

        String url = app_dir + "fetchGroupMsgs.php?gid="+gid;

        Log.d(TAG,"fetchGroupMsgs URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchChats(Context context,String tid,Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "fetchChats.php?uid="+uid;
        url+="&tid="+tid;
        Log.d(TAG,"fetchSug URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchSuggested(Context context,Callback callback) {
        String uid = getUserId(context);
        String url = app_dir + "fetchSuggestedV2.php?uid="+uid;

        Log.d(TAG,"fetchSug URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchEvents(Context context, Callback callback) {
        String uid  = getUserId(context);
        String url = app_dir + "fetchEvents.php?uid="+uid;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d(TAG,"NetworkHandler URL :"+url);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchInterestEvent(Context context, Callback callback) {
        String uid  = getUserId(context);
        String url = app_dir + "fetchIntrustedEvents.php?uid="+uid;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d(TAG,"NetworkHandler URL :"+url);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchGroupPosts(Context context,String gid, Callback callback) {
        String url = app_dir + "fetchGroupPosts.php?uid="+getUserId(context);
        if(gid!=null)
            url+="&gid="+gid;

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchOffsetGroups(Context context,String offset,String cat, Callback callback) {
        String url = app_dir + "fetchGroups.php?uid="+getUserId(context);
        int mode = DataHandler.isUnivMode(context)?1:0;

        if(cat!=null)
            url+="&cat="+cat;

        url += "&offset="+offset;
        url +="&mode="+mode;

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchGroups(Context context,String cat, Callback callback) {
        String url = app_dir + "fetchGroups.php?uid="+getUserId(context);
        int mode = DataHandler.isUnivMode(context)?1:0;

        if(cat!=null)
            url+="&cat="+cat;

        url +="&mode="+mode;

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchSinglEvent(Context context,String eventId, Callback callback) {
        String url = app_dir + "fetchEventInd.php?uid="+getUserId(context);

            url+="&eid="+eventId;

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchSingleProfile(Context context, Callback callback) {
        String url = app_dir + "fetchUserProf.php?uid="+getUserId(context);

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchJoinedGroups(Context context,Callback callback) {
        String url = app_dir + "fetchJoinedGroups.php?uid="+ getUserId(context);

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchCreatedGroups(Context context,Callback callback) {
        String url = app_dir + "fetchCreatedGroups.php?uid="+ getUserId(context);

        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchNotifs(Context context,Callback callback) {
        String url = "https://resercho.com/linkapp/fetchNotifications.php?uid=" + getUserId(context);
        Log.d("Resercho","Notif : URL:"+url);
        Log.d(TAG,"NetworkHandler URL :"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call syncGoogleSignInWithServer(String uname, String token, String mail,Callback callback) {
        String url = app_dir+"googleSignIn.php?mail="+mail+"&name="+uname+"&token="+token;
        Log.d("Resercho","Gsignin sik : URL:"+url);

        //
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    // SOCIAL INTERACTION

    public static Call joinGroup(Context context, ModelGroup group, boolean remove, Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendGroupJoin.php?uid="+uid+"" +
                "&gid="+group.getGid();

        if(remove)
            url +="&remove";

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendGroupMessage(Context context,String msg, String tid,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendGroupMessage.php?uid="+uid+"&gid="+tid;
        url+="&msg="+msg;

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendMessage(Context context,String msg, String tid,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendMessage.php?uid="+uid+"&to="+tid;
        url+="&msg="+msg;

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
    public static Call sendProfileView(Context context,String owner,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendProfileView.php?uid="+uid;
        url+="&profid="+owner;

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendDownload(Context context,String owner,String pid,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendDownload.php?pid="+pid+"&uid="+uid;
        url+="&owner="+owner;

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendlike(Context context,boolean like,String pid,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendlike.php?pid="+pid+"&uid="+uid;
        if(!like)
            url += "&delete";

        Log.d(TAG,"NetworkHandler sendlie URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendFollow(Context context,boolean follow,String fid,Callback callback) {
        String uid = getUserId(context);

        String url = app_dir + "sendfollow.php?follow="+fid+"&uid="+uid;
        if(!follow)
            url += "&unfollow";

        Log.d(TAG,"NetworkHandler sendfollow URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call updatePost(Context context, ModelWork model,boolean newCover, Callback callback) {
        String uid = getUserId(context);
        String url = "https://resercho.com/linkapp/edit_post.php?" +
                "uid="+uid+"&" +
                "cover="+(newCover?'1':'0')+"&" +
                "pid="+model.getId()+"&" +
                "heading="+model.getHeading()+"&" +
                "media="+model.getMedia_src()+"&" +
                "cover="+model.getCover_img()+"&" +
                "wtype="+model.getTypework()+"&" +
                "type="+model.getType()+"&" +
                "place="+model.getPlace()+"&" +
                "cat="+model.getCategory()+"&" +
                "doi="+model.getDoi()+"&" +
                "author="+model.getAuthor()+"&" +
                "dop="+model.getDop()+"&" +
                "down="+(model.isDenabled()?"1":"0")+"&" +
                "share="+(model.isSenabled()?"1":"0")+"&" +
                "desc="+model.getStatus();

        if(DataHandler.isSchoolMode(context))
            url +="&mode=0";
        else
            url +="&mode=1";

        Log.d(TAG,"NetworkHandler sendfollow URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call uploadPost(Context context, ModelWork model, Callback callback) {
        String uid = getUserId(context);
        String url = "https://resercho.com/linkapp/upload_post.php?" +
                "uid="+uid+"&" +
                "heading="+model.getHeading()+"&" +
                "media="+model.getMedia_src()+"&" +
                "cover="+model.getCover_img()+"&" +
                "wtype="+model.getTypework()+"&" +
                "type="+model.getType()+"&" +
                "place="+model.getPlace()+"&" +
                "cat="+model.getCategory()+"&" +
                "doi="+model.getDoi()+"&" +
                "author="+model.getAuthor()+"&" +
                "dop="+model.getDop()+"&" +
                "down="+(model.isDenabled()?"1":"0")+"&" +
                "share="+(model.isSenabled()?"1":"0")+"&" +
                "desc="+model.getStatus();

        if(DataHandler.isSchoolMode(context))
            url +="&mode=0";
        else
            url +="&mode=1";

        Log.d(TAG,"NetworkHandler sendfollow URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call deleteEvent(Context context, String eid, Callback callback) {
        String uid = getUserId(context);
        int mode = DataHandler.isUnivMode(context)?1:0;
        String url = app_dir+"sendNewEvent.php?delete&" +
                "uid="+uid;

        url +="&eid="+eid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call sendNewEvent(Context context, ModelEvent group, Callback callback) {
        String uid = getUserId(context);
        int mode = DataHandler.isUnivMode(context)?1:0;
        String url = app_dir+"sendNewEvent.php?" +
                "uid="+uid;

        url +="&date="+group.getDate();
        url +="&name="+group.getName();
        url +="&desc="+group.getDescription();
        url +="&time="+group.getTime();
        url +="&loc="+group.getLocation();
        url +="&img="+group.getImage();
        url +="&mode="+mode;


        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendUpdatedGroup(Context context, ModelNewGroup group, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"updateGroup.php?" +
                "uid="+uid;
        int mode = DataHandler.isUnivMode(context)?1:0;
        url +="&mode="+mode;
        url +="&gid="+group.getGid();
        url +="&about="+group.getAbout();
        url +="&name="+group.getName();
        url +="&friends="+group.getFriends();
        url +="&cat="+group.getCatid();
        url +="&image="+group.getImage();
        url +="&private="+(group.isPrivateGroup()?1:0);

        Log.d(TAG,"NetworkHandler UpGroup URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendNewGroup(Context context, ModelNewGroup group, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendGroupv2.php?" +
                "uid="+uid;
        int mode = DataHandler.isUnivMode(context)?1:0;
        url +="&mode="+mode;

        url +="&about="+group.getAbout();
        url +="&name="+group.getName();
        url +="&friends="+group.getFriends();
        url +="&cat="+group.getCatid();
        url +="&image="+group.getImage();
        url +="&private="+(group.isPrivateGroup()?1:0);

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendSimplePost(Context context, String heading, String desc, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"fetch_user_post.php?" +
                "uid="+uid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static String urlEncode(String str){
        try{
            return URLEncoder.encode(str,"utf-8");
        }catch (UnsupportedEncodingException e){
            return str;
        }
    }

    public static Call sendCitation(Context context, ModelCitation citation, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendCitation.php?" +
                "pid="+citation.getPid()+"&"+
                "uid="+uid+"&"+
                "ownerid="+ citation.getOwner();



        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendStory(Context context, String imgurl, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendStory.php?" +
                "uid="+uid+"&"+
                "imgurl="+ imgurl;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call deleteStory(Context context, String sid, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendStory.php?delete" +
                "uid="+uid+"&"+
                "sid="+ sid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call sendComment(Context context, ModelJoinedGroups.ModelComment comment, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendComment.php?" +
                "pid="+comment.getPid()+"&"+
                "uid="+uid+"&"+
                "comment="+ urlEncode(comment.getComment());

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchComments(Context context, String pid, Callback callback) {
//        String uid = DataHandler.getUserId(context);
        String url = app_dir+"fetchComments.php?" +
                "pid="+pid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchCitations(Context context, String pid, boolean user, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"fetchCitations.php?";

        if(!user)
            url+="pid="+pid;
        else
            url+= "uid="+uid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call sendEvent(Context context, String eid,boolean remove, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"sendEventInterest.php?uid=" +uid+
                "&eid="+eid;

        if(remove)
            url += "&remove";

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call fetchUserPost(Context context, String uid, Callback callback) {
        String reqid = getUserId(context);
        String url = app_dir+"fetch_user_post.php?reqid=" +reqid+
                "&uid="+uid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call fetchUserDetails(Context context, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"fetchUserDetail.php?" +
                "uid="+uid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call fetchSavedWork(Context context, Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"fetchSaved.php?" +
                "uid="+uid;

        Log.d(TAG,"NetworkHandler fetchuserpost URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static Call deletePost(Context context,String pid, Callback callback) {
        String uid = getUserId(context);
        String delkey = context.getResources().getString(R.string.deletekey);

        String url = app_dir+"deletePost.php?" +
                "uid="+uid;
        url+= "&delkey="+ delkey;
        url+= "&pid="+ pid;

        Log.d(TAG,"NetworkHandler delete post URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call updateDbField(Context context,String key, String val, Callback callback) {
        String uid = getUserId(context);
        String upkey = context.getResources().getString(R.string.deletekey);

        String url = app_dir+"updateProfile.php?" +
                "uid="+uid;
        url+= "&upkey="+ upkey;
        url+= "&"+key+"="+ val;

        Log.d(TAG,"NetworkHandler delete post URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static Call fetchFollowing(Context context, boolean following,Callback callback) {
        String uid = getUserId(context);
        String url = app_dir+"fetchFollowing.php?" +
                "uid="+uid;
        if(following)
            url+="&followers";

        Log.d(TAG,"NetworkHandler delete post URL:"+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

}


