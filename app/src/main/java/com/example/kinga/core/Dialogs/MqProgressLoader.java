package com.example.kinga.core.Dialogs;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.kinga.R;
import com.example.kinga.core.MyCalculatorUtil;

public class MqProgressLoader {

    public static AlertDialog showLoader(Context mContext, String loadMessage){
        
        int linearLayoutHeight = MyCalculatorUtil.pxToDp(100);
        int progressBarRightPadding = 50;

        // Create a linear layout
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, 0, 0, 0);
        linearLayout.setGravity(Gravity.CENTER);

        // Create linear layout params
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                linearLayoutHeight);

        linearLayoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(linearLayoutParams);

        // Create a progress bar
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);

        progressBar.setPadding(0, 0, progressBarRightPadding, 0);
        progressBar.setLayoutParams(linearLayoutParams);
        progressBar.getIndeterminateDrawable().setTint(
                mContext.getResources().getColor(R.color.colorPrimary)
        );


        // Create linear layout params for text view
        linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.CENTER;


        // Create a text view
        TextView messageTextView = new TextView(mContext);
        messageTextView.setText(loadMessage);
        messageTextView.setTextColor(Color.parseColor("#000000"));
        messageTextView.setTextSize(15);
        messageTextView.setLayoutParams(linearLayoutParams);

        // Add progress bar and text view into the layout
        linearLayout.addView(progressBar);
        linearLayout.addView(messageTextView);

        // Add the linear layout into the the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;

    }

}
