package com.example.kinga.core.NetworkConnection.WebRequestHelpers;


public class CleanApiErrorMessages {

    public static String removeBrackets(String error_message) {

        String message = "";

        // Remove brackets and "
        message = error_message.replace("[", "");
        message = message.replace("]", "");
        message = message.replace("\"", "");

        return message;
    }

    public static String removeEnclosingBrackets(String error_message) {

        String message = "";

        // Remove brackets and "
        message = error_message.replace("{", "");
        message = message.replace("}", "");
        message = message.replace("\"", "");

        message = removeBrackets(message);

        return message;
    }
}
