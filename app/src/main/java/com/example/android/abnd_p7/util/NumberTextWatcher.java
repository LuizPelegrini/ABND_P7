package com.example.android.abnd_p7.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {
    private final EditText mEditText;       // Reference to the editText to be able to add and remove the watcher
    private String mPrevious;               // String to compare with the previous one whenever the text is changed

    public NumberTextWatcher(EditText editText){
        mEditText = editText;
        mPrevious = "";
    }

    public void afterTextChanged(Editable s) { }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // If the previous string is different from the one coming...
        if(!s.toString().equals(mPrevious)){
            // remove the listener, so we don't enter in an endless onTextChanged loop when changing the text
            mEditText.removeTextChangedListener(this);

            // Remove all special characters from the string
            String cleanString = s.toString().replaceAll("[$,.]", "");

            // Format the value and insert special currency characters
            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance(Locale.US).format((parsed/100));

            // Update the values
            mPrevious = formatted;
            mEditText.setText(formatted);
            mEditText.setSelection(formatted.length());

            // Add the listener again in order to be able to listen for future changes
            mEditText.addTextChangedListener(this);
        }
    }
}
