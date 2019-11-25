package com.example.problemfix.Helper;

import android.app.ProgressDialog;
import android.content.Context;

public class Dialog {
    Context context;

    ProgressDialog pDialog;

    public Dialog(Context context) {
        this.context = context;
        pDialog = new ProgressDialog(context);
    }

    public void show() {
        pDialog.setMessage("Loading...");
        pDialog.show();

    }


    public void hide() {
        pDialog.setMessage("Loading...");
        pDialog.hide();

    }

}
