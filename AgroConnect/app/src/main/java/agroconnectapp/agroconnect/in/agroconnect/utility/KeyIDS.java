package agroconnectapp.agroconnect.in.agroconnect.utility;

/**
 * Created by sumanta on 4/6/16.
 */
public interface KeyIDS {
    /*
    *  URLs
    */
    String BASE_URI = "https://mandiex.com/FarmVille";
    String BASE_IMAGE_URI = "http://mandiex.com/FarmVille";
    String REGISTER_URI = "/api/MobileNumberRegistration/SendOTPSMS";
    String VALIDATE_OTP = "/api/MobileNumberRegistration/validate";
    String LANGUAGE_UPDATE_URI = "/api/agents/UpdateLanguage";
    String CITY_URI = "/helpers/getcities";
    String COMMODITY_URI = "/helpers/getcommodities";
    String PROFILE_URI = "/api/agents/";
    String FEED_UPLOAD_URI = "/api/feed";
    String NEWS_FEED_URI = "/api/feed";
    String ADVISOR_DATA_URI = "/api/advisoryPosts";
    String FEEDBACK_URI = "/api/feedback";
    String GENERAL_INFROMATION_URI = "/api/generalinformation";
    /*
    * youtube API key
    */
    String DEVELOPER_KEY = "AIzaSyBRqmPf4ZisqSVztSOiIIxClY6_zuiSx7M";

    /*
    *  Preference keys
    */
    String PHONE_NUMBER = "PhoneNumber";
    String PHONE_SUBMITTED = "p_s";
    String PROFILE_PAGE = "pp";
    String LANGUAGE = "LanguageId";
    String IS_LANGUAGE_SET = "lan_set";
    String SELECTED_LANGUAGE = "selected_locale";
    String LATITUDE = "location_lat";
    String LONGITUDE = "location_long";
    String CITY_VERSION = "CityVersion";
    String COMMODITY_VERSION = "CommodityVersion";
    String NAME = "Name";
    String CITY = "City";
    String CITYID = "CityId";
    String COMMODITY = "Commodity";
    String COMMODITYID = "CommodityId";
    String AGENT_TYPE = "AgentType";
    String DESCRIPTION = "Description";
    String ORGANIZATION = "Organisation";
    String DEPARTMENT = "Department";
    String EMAIL = "Email";
    String PASSWORD = "Password";
    String TOKEN = "Token";
    String PROFILE_SAVE = "profile_s";
    String AGENT_ID = "agent_id";
    String PREFERRED_COMMODITY_ID = "preferred_commodity_id";
    String PREFERRED_COMMODITY_NAME = "preferred_commodity_name";
    String PREFERRED_CITY_ID = "preferred_city_id";
    String PREFERRED_CITY_NAME = "preferred_city_name";
    String EDIT_PROFILE = "edit_profile";
    String ID = "Id";
    String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    String LAST_COMMODITY_ID= "lst_com";
    String LAST_CITY_ID = "lst_city";
    String IS_SYNC_CONTACT = "sync_con";
    String IS_FEEDBACK_SEND = "is_feedback";
    String FEEDBACK_COUNTER = "feed_con";
    String IS_SHARE = "is_share";
    String SHARE_COUNT = "share_count";
    String IS_UPDATE = "is_update";
    String UPDATE_COUNT = "update_count";
    /*
    *  Databases Key
    */
    String CITY_TABLE = "ct";
    String CITY_ID = "id";
    String CITY_NAME = "cn";
    String CITY_LOCAL_NAME = "cln";

    String COMMODITY_TABLE = "com_t";
    String COMMODITY_ID = "com_id";
    String COMMODITY_NAME = "com_n";
    String COMMODITY_LOCAL_NAME = "com_ln";

    String NOTIFICATION_TABLE = "notif_t";
    String NOTIFICATION_MESSAGE = "notif_mesg";
    String NOTIFICATION_ID = "notif_id";
    String NOTIFICATION_AGENT_ID = "notif_a_id";
    String NOTIFICATION_CITY_ID = "notif_c_id";
    String NOTIFICATION_COMMODITY_ID = "notif_com_id";
    String NOTIFICATION_ADVISOR_POST_ID = " notif_ad_id";
    String NOTIFICATION_TIMESTAMP = "notif_tmp";
    String NOTIFICATION_FEED_ID="notif_feed_id";
    String NOTIFICATION_FEED_TYPE ="notif_feed_type";
}
