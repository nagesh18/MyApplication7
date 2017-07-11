package com.example.gomalon.myapplication;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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


public class MyBroadcastReciver extends BroadcastReceiver {

    private static Date callStartTime;
    private static String savedNumber;
    Context context1;

    HashMap<String, String> headers;
    JsonObject jsonObject;
    String urlPOst = " http://test.gomalon.com/mAPI/v7/search/updateUserCallReason";
    String tag_json_obj = "json_obj_req";

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("eno ondu",""+ Constants.recievedTheCall);
        context1 = context;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

            Log.d("the number :", " "+savedNumber);


        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.d("State ",""+ stateStr);

            onCallStateChanged(context, stateStr, number);

        }

    }

    public void onCallStateChanged(Context context, String state, String number) {


        switch (state) {
            case "RINGING" :

                Constants.recievedTheCall = "RINGING";
                callStartTime = new Date();
                savedNumber = number;

                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                break;

            case "OFFHOOK":

                if(savedNumber.length() > 10){
                    savedNumber = savedNumber.substring(3);
                }


                Constants.mobileNumber = savedNumber;

                if(Constants.recievedTheCall.equalsIgnoreCase("RINGING")) {

                    callStartTime = new Date();

                    try {

                        jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id","ebbaa021a82afd13c662803e9e163995");
                        jsonObject.addProperty("caller_id","");
                        jsonObject.addProperty("phone",savedNumber);
                        jsonObject.addProperty("action","received");
                        jsonObject.addProperty("call_time",toNOrmalDate(String.valueOf(callStartTime.getTime())));
                        jsonObject.addProperty("caller_name","");
                        jsonObject.addProperty("query_id","");

                        sendDatatoServer(jsonObject,"received",context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(context.getApplicationContext(),"Recived",Toast.LENGTH_SHORT).show();

                } else {
                    try {


                        jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id","ebbaa021a82afd13c662803e9e163995");
                        jsonObject.addProperty("caller_id","");
                        jsonObject.addProperty("phone",savedNumber);
                        jsonObject.addProperty("action","dialed");
                        jsonObject.addProperty("call_time",toNOrmalDate(String.valueOf(callStartTime.getTime())));
                        jsonObject.addProperty("caller_name","");
                        jsonObject.addProperty("query_id","");

                        sendDatatoServer(jsonObject,"dialed",context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    Toast.makeText(context.getApplicationContext(),"Dialed",Toast.LENGTH_SHORT).show();
                }

                Constants.recievedTheCall = "OFFHOOK";

                break;

            case "IDLE":

//                if(savedNumber.length() > 10){
//                    savedNumber = savedNumber.substring(3);
//                }

                Constants.mobileNumber = savedNumber;

                if(Constants.recievedTheCall.equalsIgnoreCase("RINGING")){

                    try {
                        jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id","ebbaa021a82afd13c662803e9e163995");
                        jsonObject.addProperty("caller_id","");
                        jsonObject.addProperty("phone",Constants.mobileNumber);
                        jsonObject.addProperty("action","missed");
                        jsonObject.addProperty("call_time",toNOrmalDate(String.valueOf(callStartTime.getTime())));
                        jsonObject.addProperty("caller_name","");
                        jsonObject.addProperty("query_id","");

                        sendDatatoServer(jsonObject,"missed",context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{

                    try {
                        jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id","ebbaa021a82afd13c662803e9e163995");
                        jsonObject.addProperty("caller_id","");
                        jsonObject.addProperty("phone",Constants.mobileNumber);
                        jsonObject.addProperty("action","received");
                        jsonObject.addProperty("call_time",toNOrmalDate(String.valueOf(callStartTime.getTime())));
                        jsonObject.addProperty("caller_name","");
                        jsonObject.addProperty("query_id","");

                        sendDatatoServer(jsonObject,"received",context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Constants.recievedTheCall = "IDLE";

                break;
        }
        }



    private void sendDatatoServer(JsonObject jsonObject, final String callType, final Context context){

        headers = new HashMap<>();
        headers.put("X-CLIENT-ID", "MAPI");
        headers.put("X-CLIENT-USER", "gomalon-apis");
        headers.put("X-CLIENT-KEY", "Z29tYWxvbi1hcGlz");
        headers.put("Content-Type", "application/json");

        System.out.print(jsonObject);

        GsonRequestJson gsonRequestJson = new GsonRequestJson(Request.Method.POST,
                urlPOst, SuccessModel.class, headers, jsonObject, new Response.Listener<SuccessModel>() {
            @Override
            public void onResponse(SuccessModel response) {

                System.out.println("Success "+ response.getMessage());

                Constants.calledID = response.getCaller_id();
                Constants.userName = response.getUsername();

                System.out.println("the user name  :"+response.getUsername());
                System.out.print("the caller_id"+response.getCaller_id());

                if(callType.equalsIgnoreCase("received")){

                    Intent i = new Intent();
                    i.setClassName("com.example.gomalon.myapplication", "com.example.gomalon.myapplication.activity");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }


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

    static class activity extends Activity{
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

            if (ContextCompat.checkSelfPermission(activity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity.this,Manifest.permission.READ_PHONE_STATE)){
                    ActivityCompat.requestPermissions(activity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
                }else {
                    ActivityCompat.requestPermissions(activity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
                }
            }else {

            }


        }

        @Override
        public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
            switch (requestCode){
                case 1 : {
                    if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                        if (ContextCompat.checkSelfPermission(activity.this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(activity.this,"Permission Granted !",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(activity.this,"no permission Granted !",Toast.LENGTH_SHORT).show();
                        }
                        return;

                    }
                }
            }

        }

        public void AlertDialogView() {

            builder = new AlertDialog.Builder(activity.this);
            builder.setCancelable(false);

            LayoutInflater inflater=activity.this.getLayoutInflater();
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

            final ProgressDialog dialog = new ProgressDialog(activity.this);
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


                    activity.this.finish();
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
}
