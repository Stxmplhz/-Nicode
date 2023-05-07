package com.example.nicode.Activity;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONENUMBER = "phonenumber";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_PREFERENCE_NAME = "NicodePreference";
    public static final String KEY_IS_LOG_IN = "isLoginin";
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BIRTHDATE = "birtdate";

    public static final String KEY_CIGARETTESTOPROLL = "cigaretteStopRoll";
    public static final String KEY_CIGARETTEROLLPERMONTH = "cigaretteRollPerMonth";
    public static final String KEY_SMOKINGLYZER = "valueSmokingLyzer";
    public static final String KEY_NICOCOIN = "nicoCoin";
    public static final String KEY_ALLDAYFORMONTH = "allDayForMonth";
    public static final String KEY_DAYFROMSTART = "dayFromStart";

    public static final String KEY_STATISTIC_NICOCOIN = "nicocoinStatistic";
    public static final String KEY_STATISTIC_SMOKINGLYZER = "smokinglyzerStatistic";

    public static final String KEY_COLLECTION_EXCHANGEDGIFT = "exchangedGift";
    public static final String KEY_GIFT = "gift";

    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDERID = "senderID";
    public static final String KEY_RECEIVERID = "receiverID";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDERNAME = "sendername";
    public static final String KEY_RECEIVERNAME = "receivername";
    public static final String KEY_SENDERIMAGE = "senderimage";
    public static final String KEY_RECEIVERIMAGE = "receiverimage";
    public static final String KEY_LASTMESSAGE = "lastmessage";

    public static final String REMOTE_MESSAGE_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MESSAGE_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MESSAGE_DATA = "data";
    public static final String REMOTE_MESSAGE_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_AVAILABILITY = "availability";

    public static final String KEY_COLLECTION_GROUPS = "groups";
    public static final String KEY_COLLECTION_GROUPCHAT = "groupchat";
    public static final String KEY_RECEIVERGROUPID = "receivergroupID";
    public static final String KEY_COLLECTION_GROUPCONVERSATIONS = "groupconversations";
    public static final String KEY_RECEIVERGROUPNAME = "receivergroupname";

    public static HashMap<String, String> remotemessageheaders = null;
    public static HashMap<String ,String> getRemotemessageheaders(){
        if (remotemessageheaders == null) {
            remotemessageheaders = new HashMap<>();
            remotemessageheaders.put(REMOTE_MESSAGE_AUTHORIZATION,"key=AAAAgUOC7YE:APA91bFaw--BeawoX3JI0PbHRS-ssLCk5EkhKeEC65cHf06Hva297f3opL5ttH0EFYsxrFeiBaFPEYOe_vzm_DweIVHP9lTgPdlkeNLvPIN82ls1oJD3tQ07V63wGkBpGMleSYOBBFap");
            remotemessageheaders.put(REMOTE_MESSAGE_CONTENT_TYPE, "application/JSON");
        } return remotemessageheaders;
    }
}