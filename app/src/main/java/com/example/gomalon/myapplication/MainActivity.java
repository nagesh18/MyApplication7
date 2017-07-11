package com.example.gomalon.myapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import gson.GsonRequestJson;
import query.Queries;
import query.Query;
import query.SuccessModel;


public class MainActivity extends AppCompatActivity {

    String tag_json_obj = "json_obj_req";
    String url = "http://test.gomalon.com/mAPI/v7/search/getUserQuries";
    String urlPost = " http://test.gomalon.com/mAPI/v7/search/updateUserCallReason";

    AlertDialog.Builder builder;
    View dialogView;

    HashMap<String, String> headers;
    JsonObject jsonObject;

    LinearLayout linearLayout;
    RadioGroup rg;
    AlertDialog alert;
    private static Date callEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        rg = new RadioGroup(this);

        if(Constants.calledID.length() > 0){
            AlertDialogView();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }else {

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1 : {
                if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(MainActivity.this,"Permission Granted !",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this,"no permission Granted !",Toast.LENGTH_SHORT).show();
                    }
                    return;

                }
            }
        }

    }

    public void AlertDialogView() {

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);

        LayoutInflater inflater=MainActivity.this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_signin, null);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.addlayout);
        Button submit = (Button) dialogView.findViewById(R.id.chooseBtn);
        final EditText userName = (EditText) dialogView.findViewById(R.id.username);
        final TextInputLayout edittextinput = (TextInputLayout) dialogView.findViewById(R.id.edittextinput);
        userName.setText(Constants.userName);

        fetchData();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userName.getText().toString().length() > 0){
                    sendDatatoServer(rg.getCheckedRadioButtonId(),userName.getText().toString());
                }else{
                    edittextinput.setError("PLease enter user name");
                }

            }
        });

        builder.setView(dialogView);
        alert = builder.create();
        /*if(calledID != "") {
            alert.show();
        }*/
        alert.show();
    }
    private void createRadioButton(Queries data) {
        final RadioButton[] rb = new RadioButton[data.getList().size()];
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for(int i=0; i<data.getList().size(); i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(" " +data.getList().get(i).getQueryDescription());
            rb[i].setId(Integer.parseInt(data.getList().get(i).getQueryId()));
            rb[i].setPadding(16,16,16,16);
            rg.addView(rb[i]);

            if(i==0){
                rb[i].setChecked(true);
            }
        }
        linearLayout.addView(rg);//you add the whole RadioGroup to the layout

    }

    private void fetchData(){

        headers = new HashMap<>();
        headers.put("X-CLIENT-ID", "MAPI");
        headers.put("X-CLIENT-USER", "gomalon-apis");
        headers.put("X-CLIENT-KEY", "Z29tYWxvbi1hcGlz");
        headers.put("Content-Type", "application/json");

        jsonObject = new JsonObject();

        jsonObject.addProperty("user_id","3a4f8519d3d8aca632119b705cc5c0bc");

        GsonRequestJson gsonRequestJson = new GsonRequestJson(Request.Method.POST,
                url, Query.class, headers, jsonObject, new Response.Listener<Query>() {
            @Override
            public void onResponse(Query response) {

                Log.d("Sucess ","");
                createRadioButton(response.getQueries());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("your Error"+error.getMessage());

                Log.d("Error ", "" + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(gsonRequestJson, tag_json_obj);

    }

    private void sendDatatoServer(int selectedId,String userNaame){

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(true);
        dialog.setMessage("Please wait");
        dialog.show();



        headers = new HashMap<>();
        headers.put("X-CLIENT-ID", "MAPI");
        headers.put("X-CLIENT-USER", "gomalon-apis");
        headers.put("X-CLIENT-KEY", "Z29tYWxvbi1hcGlz");
        headers.put("Content-Type", "application/json");
        callEndTime = new Date();

        jsonObject = new JsonObject();

        jsonObject.addProperty("user_id","ebbaa021a82afd13c662803e9e163995");
        jsonObject.addProperty("caller_id", "");
        jsonObject.addProperty("phone",Constants.mobileNumber);
        jsonObject.addProperty("action","");
        jsonObject.addProperty("call_time",toNOrmalDate(String.valueOf(callEndTime.getTime())));
        jsonObject.addProperty("caller_name",userNaame);
        jsonObject.addProperty("query_id",selectedId);


        Log.d("JSON Object",""+ jsonObject);

        GsonRequestJson gsonRequestJson = new GsonRequestJson(Request.Method.POST,
                urlPost, SuccessModel.class, headers, jsonObject, new Response.Listener<SuccessModel>() {
            @Override
            public void onResponse(SuccessModel response) {

                Constants.calledID="";
                System.out.println("Success"+ response.getMessage());
                System.out.println("userName"+ response.getUsername());
                System.out.println("code"+ response.getCode());


                MainActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("your Error"+error.getMessage());

                Log.d("Error ", "" + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(gsonRequestJson, tag_json_obj);

    }

    private String toNOrmalDate(String time){

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long milliSeconds= Long.parseLong(time);
        System.out.println(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        System.out.println(formatter.format(calendar.getTime()));


        return formatter.format(calendar.getTime());
    }



}
