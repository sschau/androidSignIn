package com.google.samples.quickstart.signin.helpers;

import com.microsoft.aad.adal.AuthenticationResult;

/**
 * Created by SChau on 7/17/2017.
 */

public class AzureConstants {


    public static final String SDK_VERSION = "1.0";

    /**
     * UTF-8 encoding
     */
    public static final String UTF8_ENCODING = "UTF-8";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    // -------------------------------AAD
    // PARAMETERS----------------------------------
    public static String AUTHORITY_URL = "https://login.microsoftonline.com/aecf4727-e977-4d26-a0de-867925b9e666";  // tenant id
    public static String CLIENT_ID = "ee598083-0ce7-4d0b-8b97-6c4ba35746d9";  // Application ID of the client
    public static String RESOURCE_ID = "https://graph.microsoft.com"; // Must have resource to request.  Else error.  "http://kidventus.com/TodoListService";
    public static String REDIRECT_URL = "https://unrealSignInUri"; // Must match the redirect url in Azure !!"mstodo://com.microsoft.windowsazure.activedirectory.samples.microsofttasks"; //
    public static String CORRELATION_ID = "";
    public static String USER_HINT = "";
    public static String EXTRA_QP = "";
    public static boolean FULL_SCREEN = true;
    public static AuthenticationResult CURRENT_RESULT = null;
    // Endpoint we are targeting for the deployed WebAPI service
    public static String SERVICE_URL = "http://10.0.1.44:8080/tasks";

    // ------------------------------------------------------------------------------------------

    static final String TABLE_WORKITEM = "WorkItem";

    public static final String SHARED_PREFERENCE_NAME = "com.example.com.test.settings";

    public static final String KEY_NAME_ASK_BROKER_INSTALL = "test.settings.ask.broker";
    public static final String KEY_NAME_CHECK_BROKER = "test.settings.check.broker";


}
