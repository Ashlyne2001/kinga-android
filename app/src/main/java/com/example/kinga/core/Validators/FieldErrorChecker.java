package com.example.kinga.core.Validators;

public class FieldErrorChecker {

    /**
     *
     * @param fields Flags indicating if field is valid or not
     * @return Returns true if all fields are true, returns false otherwise
     */
    public static boolean areAllFieldsValid(boolean[] fields){

        for (boolean field: fields){
            if (!field) return false;
        }
        return true;
    }


}
