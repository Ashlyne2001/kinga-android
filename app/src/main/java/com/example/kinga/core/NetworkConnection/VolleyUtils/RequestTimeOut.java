package com.example.kinga.core.NetworkConnection.VolleyUtils;

public class RequestTimeOut {

    /**
     * Volley timeout in millis
     */
    public static int VOLLEY_INITIAL_TIMEOUT_MS = 10000;

    /**
     * Specifies the amount of time in millis view handlers should wait before making a retry from
     * the server
     */
    public static int VIEW_SERVER_HANDLER_POST_DELAY_MS = 2000;

    /**
     * Specifies the amount of time in millis view handlers should wait before making a retry from
     * the db
     */
    public static int VIEW_DB_HANDLER_POST_DELAY_MS = 100;

    /**
     * Specifies the max limit of retries views are allowed to make when requesting from the server
     */
    public static int VIEW_HANDLER_RETRIES = 5;


    /**
     * Specifies the amount of time in millis syncReceiptTask's handler should wait before making a
     * sync request
     */
    public static int SYNC_RECEIPT_HANDLER_POST_DELAY_MS = 10 * 1000; // 10 seconds

    /**
     * Specifies the amount of time in millis splash screen's handler should wait before checking
     * if it should redirect
     */
    public static int SPLASH_HANDLER_POST_DELAY_MS = 3000;

    /**
     * Specifies the amount of time in millis splash screen's handler should wait initially
     */
    public static int SPLASH_HANDLER_INITIAL_POST_DELAY_MS = 1000;

}
