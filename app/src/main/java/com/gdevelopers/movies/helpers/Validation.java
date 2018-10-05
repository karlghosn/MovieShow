package com.gdevelopers.movies.helpers;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class Validation {
    private static final String ONLY_CHARS_REGEX = "^[a-zA-Z\\s]*$";
    private static final String REQUIRED_MSG = "Required";
    private static final String ONLY_CHARS_MSG = "No special characters allowed";

    private static boolean isValid(TextInputLayout inputLayout) {

        @SuppressWarnings("ConstantConditions") String text = inputLayout.getEditText().getText().toString().trim();
        inputLayout.setError(null);

        // text required and editText is blank, so return false
        if (!hasText(inputLayout)) return false;

        // pattern doesn't match so returning false
        if (!Pattern.matches(Validation.ONLY_CHARS_REGEX, text)) {
            inputLayout.setError(Validation.ONLY_CHARS_MSG);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(TextInputLayout inputLayout) {
        @SuppressWarnings("ConstantConditions") String text = inputLayout.getEditText().getText().toString().trim();
        inputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            inputLayout.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public static boolean isName(TextInputLayout inputLayout) {
        return isValid(inputLayout);
    }


}
	 
