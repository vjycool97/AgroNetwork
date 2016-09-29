package agroconnectapp.agroconnect.in.agroconnect.utility;

import android.os.Environment;

/**
 * Created by nitin.gupta on 12/12/2015.
 */
public class Constants {
    public static final String baseUrl = "https://mandiex.com/FarmVille/api/";
    public static final String baseImgUrl = "https://mandiex.com/FarmVille/";





    public static final String feedUrl = baseUrl + "listings";
    public static final String profileUrl = baseUrl + "agents/";
    public static final String sendOtpUrl = baseUrl + "MobileNumberRegistration/SendOTPSMS?mobileNumber=";
    public static final String validateOtpUrl = baseUrl + "MobileNumberRegistration/validate?mobileNumber=";
    public static final String sendContactsUrl = baseUrl + "AgentContactBook/";
    public static final String sendGcmTokenUrl = baseUrl + "AppRegistration/";
    public static final String authenticationUrl = baseUrl + "authenticate";
    public static final String mandiPricesUrl = baseUrl + "MandiPrices?";
    public static final String updateLanguageUrl = baseUrl + "agents/UpdateLanguage?LanguageId=";
    public static final String advisoryPostUrl = baseUrl + "advisoryPosts";
    public static final String uploadImgUrl = baseUrl + "advisoryPosts/AddAdvisoryPostResource";
    public static final String getUserDetailsUrl = baseUrl + "agents/GetAgentByPhoneNumber/";



    public static final String commodityUrl = "https://mandiex.com/FarmVille/helpers/getcommodities?commodityVersion=";
    public static final String cityUrl = "https://mandiex.com/FarmVille/helpers/getcities?cityVersion=";

    public static final String cropProtectionUrl = baseUrl + "CropProtection/GetCrops";
    public static final String cropProtectionForCropUrl = baseUrl + "CropProtection/GetCropJson/";


    public static final String playStoreUrl = "https://play.google.com/store/apps/details?id=";
    public static final String rootPath = Environment.getExternalStorageDirectory() + "/.agroconnect";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String supportEmail = "agroconnectindia@gmail.com";

    public static final String token = "Token";
    public static final String defaultToken = "";
    public static final String feedParcelKey = "feed_data";
    public static final String agentIdKey = "agent_id";
    public static final String profileSaved = "profile_saved";
    public static final String id = "Id";
    public static final String name = "Name";
    public static final String mandi = "Mandi";
    public static final String commodity = "Commodity";
    public static final String commodityId = "CommodityId";
    public static final String city = "City";
    public static final String cityId = "CityId";
    public static final String phone = "PhoneNumber";
    public static final String agentType = "AgentType";
    public static final String description = "Description";
    public static final String password = "Password";
    public static final String isSell = "is_sell";
    public static final String encodedProfileBitmap = "encoded_image";
    public static final String editProfile = "edit_profile";
    public static final String allCommodities = "all_commodities";
    public static final String allCities = "all_cities";
    public static final String cityVersion = "CityVersion";
    public static final String commodityVersion = "CommodityVersion";
    public static final String contactsCount = "contacts_count";
    public static final String selectedLocale = "selected_locale";
    public static final String languageSelected = "language_selected";
    public static final String languageId = "LanguageId";
    public static final String reinitialize = "reinitialize";
    public static final String preferredCommodityId = "preferred_commodity_id";
    public static final String preferredCommodityName = "preferred_commodity_name";
    public static final String preferredCityId = "preferred_city_id";
    public static final String preferredCityName = "preferred_city_name";
    public static final String organization = "Organisation";
    public static final String department = "Department";
    public static final String email = "Email";
    public static final String postId = "post_id";
    public static final String title = "Title";
    public static final String parentId = "ParentId";
    public static final String problemAsDiagnosedByAdvisor = "ProblemAsDiagnosedByAdvisor";
    public static final String productToBeApplied = "ProductToBeApplied";
    public static final String dosage = "Dosage";
    public static final String applicationTime = "ApplicationTime";
    public static final String additionalAdvice = "AdditionalAdvice";
    public static final String otpSent = "otp_sent";
    public static final String userGeneralProfile = "user_profile";

    public static final String locationLat = "location_lat";
    public static final String locationLong = "location_long";

    public static final String phoneNumberForOtp = "phone_number_for_otp";



    public static final Integer TYPE_FEED = 0;
    public static final Integer TYPE_SHORTLIST = 1;
    public static final Integer TYPE_COMMODITY = 2;
    public static final Integer TYPE_CITY = 3;
    public static final Integer TYPE_PROGRESS_BAR = 4;
    public static final Integer TYPE_SEARCH = 5;
    public static final Integer TYPE_DETAIL = 6;
    public static final Integer TYPE_SEPERATOR = 7;
}

