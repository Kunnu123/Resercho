package com.resercho;

import android.util.Log;

import com.resercho.POJO.ModelCategory;
import com.resercho.POJO.ModelChat;
import com.resercho.POJO.ModelEvent;
import com.resercho.POJO.ModelGroup;
import com.resercho.POJO.ModelGroupMsg;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.POJO.ModelMsg;
import com.resercho.POJO.ModelNewGroup;
import com.resercho.POJO.ModelNotif;
import com.resercho.POJO.ModelProfile;
import com.resercho.POJO.ModelSaved;
import com.resercho.POJO.ModelStory;
import com.resercho.POJO.ModelStoryV2;
import com.resercho.POJO.ModelSuggested;
import com.resercho.POJO.ModelType;
import com.resercho.POJO.ModelWork;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;

import static com.resercho.NetworkHandler.root_dir;
import static com.resercho.SignInActivity.TAG;

public class ConverterJson {
    public static ModelWork parseTrendingJson(JSONObject jobj){
        try{
            ModelWork work = new ModelWork();
            work.setId(jobj.getString("post_id"));
            work.setAudience(jobj.getString("audience"));
            work.setCategory(jobj.getString("category"));
            work.setCover_img(root_dir +jobj.getString("cover_img"));
            work.setFiltertype(jobj.getString("filtertype"));
            work.setMedia_src(jobj.getString( "media_src"));
            work.setHeading(jobj.getString("heading"));
            work.setPlace(jobj.getString("place"));
            work.setPost_of(jobj.getString("post_of"));
            work.setTime(jobj.getString("time"));
            work.setType(jobj.getString("type"));
            work.setTypework(jobj.getString("typeWork"));
            work.setStatus(jobj.getString("status"));
            work.setUsername(jobj.getString("fullname"));
            work.setLike(jobj.getInt("liked")==1);
            work.setDenabled(jobj.getInt("denabled")==1);
            work.setSenabled(jobj.getInt("senabled")==1);
            work.setSaved(jobj.getInt("saved")==1);
            work.setProfpic(root_dir+jobj.getString("profpic"));
            work.setUserBio(jobj.getString("about"));
            work.setAuthor(jobj.getString("author"));
            work.setDop(jobj.getString("dop"));
            work.setDoi(jobj.getString("doi"));
            work.setLikes(jobj.getLong("likes"));
            work.setComments(jobj.getLong("comments"));
            if(jobj.has("savedCount")){
                work.setSavedCount(jobj.getLong("savedCount"));
            }

            if(jobj.has("cites")){
                work.setCiteCount(jobj.getLong("cites"));
            }

            if(jobj.has("shared")){
                work.setShareCount(jobj.getLong("shared"));
            }



            if(work.getMedia_src()!=null && !work.getMedia_src().equalsIgnoreCase("null")) {
                String mime = UtilityMethods.getMimeFromUrl(work.getMedia_src());
                if(mime!=null) {
//                    if (mime.equalsIgnoreCase("pdf")) {
                    if(mime.equalsIgnoreCase("pdf") || mime.equalsIgnoreCase("doc") || mime.equalsIgnoreCase("docx") || mime.equalsIgnoreCase("ppt") || mime.equalsIgnoreCase("pptx")){
                        work.setPdflist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));
                        Log.d("Resercho", "CJson Setted list of PDFS" + work.getPdflist().size());
                    } else {
                        work.setImglist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));
                        Log.d("Resercho", "CJson Setted list of IMAGES : " + work.getImglist().size());
                    }
                }
            }


            return work;
        }catch (JSONException e){
            Log.d("Resercho","CJson Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelNewGroup parseNewGroupModel(JSONObject object){
        try{
            ModelNewGroup group = new ModelNewGroup();
            group.setAbout(object.getString("about"));
            group.setCatid(object.getString("category"));
            group.setGid(object.getInt("grp_id"));
            group.setImage(object.getString("image"));
            group.setUniversity(object.getInt("university")==1);
            group.setName(object.getString("name"));
            group.setPrivateGroup(object.getInt("private")==0);
            return group;
        }catch (JSONException e){
            Log.d("GroupModel","JSON Exception:"+e);
            return null;
        }
    }

    public static ModelStory parseStoryJson(JSONObject jobj){
        try{
            ModelStory story = new ModelStory();
            story.setId(jobj.getString("id"));
            story.setImage(root_dir+jobj.getString("image"));
            story.setTime(jobj.getString("time"));

                story.setFullname(jobj.getString("fullname"));


                story.setProfile(root_dir+  jobj.getString("profpic"));

            return story;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Lins Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelSaved parseSaveJson(JSONObject object){
        try{
            ModelSaved saved = new ModelSaved();
            saved.setId(object.getString("id"));
            saved.setPid(object.getString("pid"));
            JSONObject workObj = object.getJSONObject("work");
            saved.setWork(parseTrendingJson(workObj));
            return saved;
        }catch (JSONException e){
            Log.d(TAG, "JSON Exception : "+e);
            return null;
        }
    }

    public static ModelChat parseChatFromJson(JSONObject object){
        try{
            ModelChat saved = new ModelChat();
            saved.setMid(object.getString("id"));
            saved.setFrom_id(object.getString("from_user"));
            saved.setProfile(root_dir+object.getString("profile"));
            saved.setTime(object.getString("time"));
            saved.setLastMsg(object.getString("message"));
            saved.setTo_id(object.getString("to_user"));
            saved.setUsername(object.getString("username"));

            return saved;
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+e);
            return null;
        }
    }

    public static ModelMsg parseMsgFromJson(JSONObject object){
        try{
            ModelMsg saved = new ModelMsg();
            saved.setId(object.getString("id"));
            saved.setTime(object.getString("time"));
            saved.setMsg(object.getString("msg"));
            saved.setDel(object.getString("del_status"));
            saved.setStatus(object.getString("status"));
            saved.setFrom(object.getString("from_user"));
            saved.setTo(object.getString("to_user"));

            return saved;
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+e);
            return null;
        }
    }

    public static ModelGroupMsg parseGroupMsgFromJson(JSONObject object){
        try{
            ModelGroupMsg saved = new ModelGroupMsg();
            saved.setId(object.getString("id"));
            saved.setAttime(object.getString("attime"));
            saved.setMsg(object.getString("msg"));
            saved.setUid(object.getString("uid"));
            saved.setGid(object.getString("gid"));

            return saved;
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+e);
            return null;
        }
    }

    public static ModelStoryV2 parseStoryJsonV2(JSONObject jobj){
        try{
            ModelStoryV2 story = new ModelStoryV2();
            story.setAdded_by(jobj.getString("added_by"));
            story.setCount(jobj.getString("count"));
            story.setProfUrl(root_dir+jobj.getString("profpic"));
            story.setUsername(jobj.getString("fullname"));
            return story;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelProfile parseProfileJson(JSONObject jobj){
        try{
            ModelProfile story = new ModelProfile();
            story.setBio(jobj.getString("bio"));
            return story;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelJoinedGroups.ModelComment parseCommentson(JSONObject jobj){
        try{
            ModelJoinedGroups.ModelComment story = new ModelJoinedGroups.ModelComment();
            story.setId(jobj.getString("id"));
            story.setComid(jobj.getString("commenter"));
            story.setComment(jobj.getString("comment"));
            story.setTime(jobj.getString("time"));
            story.setComprof(root_dir + jobj.getString("profpic"));
            story.setComname(jobj.getString("fullname"));
            story.setPid(jobj.getString("post_id"));
            return story;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelSuggested parseSuggestedJson(JSONObject jobj){
        try{
            ModelSuggested suggested = new ModelSuggested();
            suggested.setUsername(jobj.getString("username"));
            suggested.setProfUrl(root_dir +jobj.getString("profile"));
            suggested.setId(jobj.getString("id"));
            suggested.setFullname(jobj.getString("fullname"));
            suggested.setSentRequest(false);
            return suggested;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelGroup parseGroupJson(JSONObject jobj){
        try{
            ModelGroup group = new ModelGroup();
            group.setAbout(jobj.getString("about"));
            group.setCategory(jobj.getString("category"));
            group.setGid(jobj.getString("grp_id"));
            group.setImageUrl(root_dir + jobj.getString("image"));
            group.setName(jobj.getString("name"));
            group.setUid(jobj.getString("user_id"));
            group.setHasJoined(jobj.getInt("joined")==1);


            if(jobj.has("username"))
                group.setUsername(jobj.getString("username"));
            if(jobj.has("profpic"))
                group.setProfPic(jobj.getString("profpic"));
            if(jobj.has("userbio"))
                group.setUserBio(jobj.getString("userbio"));

            group.setTime(jobj.getString("time"));
            return group;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelJoinedGroups parseJoinedGroupJson(JSONObject jobj){
        try{
            ModelJoinedGroups group = new ModelJoinedGroups();
            group.setAbout(jobj.getString("about"));
            group.setGid(jobj.getString("grp_id"));
            group.setStatus(jobj.getString("status"));
            group.setImage(root_dir + jobj.getString("image"));
            group.setName(jobj.getString("name"));
            group.setUid(jobj.getString("user_id"));
            group.setTimestamp(UtilityMethods.getAgoTimeFromTimestamp(UtilityMethods.getTimeStamp(jobj.getString("time"))));
            return group;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Jgin Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelCategory parseCategoryJson(JSONObject jobj){
        try{
            ModelCategory group = new ModelCategory();
            group.setName(jobj.getString("category"));
            group.setId(jobj.getString("id"));
            group.setTime(UtilityMethods.getAgoTimeFromTimestamp(UtilityMethods.getTimeStamp(jobj.getString("time"))));
            return group;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb Catin JSON Exception :"+e);
            return null;
        }
    }

    public static ModelType parseTypeJson(JSONObject jobj){
        try{
            ModelType group = new ModelType();
            group.setName(jobj.getString("name"));
            group.setId(jobj.getString("id"));
            return group;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb Catin JSON Exception :"+e);
            return null;
        }
    }

    public static ModelEvent parseEventJson(JSONObject object){
        try{
            ModelEvent event = new ModelEvent();
            event.setId(object.getString("id"));
            event.setName(object.getString("event_name"));
            event.setDate(object.getString("date"));
            event.setTime(object.getString("time"));
            event.setLocation(object.getString("location"));
            event.setDescription(html2text(object.getString("description")));
            event.setCreatedAt(object.getString("created_at"));
            event.setHost(object.getString("host"));
            event.setImage(root_dir + object.getString("image"));
            event.setInterested(object.getInt("intrust")==1);
            Log.d("Eventikk","Interest ? "+ event.isInterested());
            return event;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }

    public static ModelNotif parseNotifJson(JSONObject object){
        try{
            ModelNotif event = new ModelNotif();
            event.setId(object.getString("id"));
            event.setTime(object.getString("time"));
            event.setOpened(object.getString("opened"));
            event.setChecked(object.getString("checked"));
            event.setProfUrl(root_dir+object.getString("prof"));
            event.setType(object.getString("type"));
            event.setSrc(object.getString("src"));
            event.setProfName(object.getString("profname"));
            return event;
        }catch (JSONException e){
            Log.d("Resercho","HomeFrag Deb JSON Exception :"+e);
            return null;
        }
    }


    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}


/*
 protected void fetchUserDetails(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchUserDetails(getApplicationContext(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Reser","Wokin onFailure:"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if(response.isSuccessful()){
                            String resp = response.body().string();
                            Log.d("Reser","Wokin onResponse:"+resp);

                            try{
                                JSONObject jsonObject = new JSONObject(resp);
                                if(jsonObject.getInt("success")==1){
                                    JSONObject data = jsonObject.getJSONObject("data");

                                }
                            }catch (JSONException e){

                            }
                        }else{

                        }
                    }
                });
            }
        });
        thread.start();
    }
 */