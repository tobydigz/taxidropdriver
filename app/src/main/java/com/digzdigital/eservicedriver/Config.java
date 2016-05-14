package com.digzdigital.eservicedriver;

/**
 * Created by Belal on 11/14/2015.
 */
public class Config {
    //URL to our login.php file
    public static final String LOGIN_URL = "http://digzdigital.net16.net/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "username";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "eservice_driver_login";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "driver_username";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "driver_loggedin";
    public static final String GOOGLEDIN_SHARED_PREF = "user_loggedin";

    //Used by main activity to access shared preferences
    public static final String USER_EMAIL_SHARED_PREF = "user_email";
    public static final String USER_NAME_SHARED_PREF = "user_name";
    public static final String USER_ID_SHARED_PREF = "user_id";


}