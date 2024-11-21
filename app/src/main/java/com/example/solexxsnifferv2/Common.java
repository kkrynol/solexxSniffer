package com.example.solexxsnifferv2;

public class Common {
    public static String SERVER_URL = "https://capybara.s1.zetohosting.pl/";
    public static String TOKEN = "KtE35AKrlEoUlo5xjWFPzWs0CYvWdhATqrakqlxj2Mbg9ZxRTFWlIHh1xTL5wBqf";
    public static String[] ENDPOINT;
    public static boolean AUTOTRADE;
    public static String NOTIFY_PACKAGE_NAME;

    static {
        ENDPOINT = new String[EndPoints.COUNT];
        ENDPOINT[EndPoints.ADD_POST.ordinal()] = SERVER_URL + "add.php";
        ENDPOINT[EndPoints.DELETE_POST.ordinal()] = SERVER_URL + "delete.php";
        ENDPOINT[EndPoints.CUSTOM_SIGNAL.ordinal()] = SERVER_URL + "customSignal.php";
        ENDPOINT[EndPoints.INIT_DATA.ordinal()] = SERVER_URL + "initData.php";
        ENDPOINT[EndPoints.SETTINGS.ordinal()] = SERVER_URL + "settings.php";
    }
    public static String GetServerUrl()
    {
        return SERVER_URL;
    }
    public static String GetEndPoint(EndPoints endpoint) {
        return ENDPOINT[endpoint.ordinal()];
    }
    public static String GetToken() {
        return TOKEN;
    }
    public static void SetToken(String token) {
        TOKEN = token;
    }
    public static String GetNotifyPackageName() {
        return NOTIFY_PACKAGE_NAME;
    }
    public static void SetNotifyPackageName(String name) {
        NOTIFY_PACKAGE_NAME = name;
    }
    public static boolean GetAutoTrade() {
        return AUTOTRADE;
    }
    public static void SetAutoTrade(boolean autoTrade) {
        AUTOTRADE = autoTrade;
    }
    public static void UpdateSettings(String packageName, String postUrl, String token, boolean autoTrade){
        NOTIFY_PACKAGE_NAME = packageName;
        SERVER_URL = postUrl;
        TOKEN = token;
        AUTOTRADE = autoTrade;
    }

}