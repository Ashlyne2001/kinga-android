package com.example.kinga.core.NetworkConnection.WebRequestHelpers;

public class StatusCodeDescriptor {

    public static String codeMessage(int status_code) {
        String message = "";

        if (status_code == 200){
            message = "Ok";
        }else if (status_code == 400){
            message = "Bad Request Error";
        }else if (status_code == 401){
            message = "Authorization Error";
        }else if (status_code == 403){
            message = "You do not have permission to perform this action";
        }else if (status_code == 404){
            message = "Item Not Found Error";
        }else if (status_code == 408){
            message = "Request Timeout Error";
        }else if (status_code == 412){
            message = "Logout from other pos devices first";
        }else if (status_code == 417){
            message = "Expectation Failed Error";
        }else if (status_code == 429){
            message = "This request cannot be completed at the moment. Please try again later.";
        }else if (status_code == 501){
            message = "Not Implemented Error";
        }else if (status_code == 502){
            message = "Bad Gateway Error";
        }else if (status_code == 503){
            message = "Our servers are currently under maintenance. Please try again later.";
        }else if (status_code == 510){
            message = "Not Extended Error";
        }else if (status_code == 598){
            message = "Network Timeout Error";
        }else if (status_code == 600){
            message = "No internet connection";
        }else if (status_code == 601){
            message = "Parse Error";
        }else if (status_code == 800){
            message = "Unknown Error";
        }else {

            if (String.valueOf(status_code).startsWith("5")){
                message = "Internal Server Error";
            }else{
                message = "Unknown Server Error";
            }

        }

        return message;
    }
}
