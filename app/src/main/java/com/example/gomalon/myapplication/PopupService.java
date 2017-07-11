package com.example.gomalon.myapplication;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PopupService extends Service {

    private static final String TAG = PopupService.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;
    String MY_PREFS_NAME = "MyPrefsFile";
    String user;
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Cllaed onlbind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("Cllaed onstart");
        showDialog("gfhgffhj");
        return super.onStartCommand(intent, flags, startId);
    }

    private void showDialog(String aTitle)
    {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "YourServie");
        mWakeLock.acquire();
        mWakeLock.release();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = View.inflate(getApplicationContext(), R.layout.dialog_popup_notification_received, null);
        mView.setTag(TAG);

        int top = getApplicationContext().getResources().getDisplayMetrics().heightPixels / 2;

        LinearLayout dialog = (LinearLayout) mView.findViewById(R.id.pop_exit);
// if you want to set params
//        android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) dialog.getLayoutParams();
//        lp.topMargin = top;
//        lp.bottomMargin = top;
//        mView.setLayoutParams(lp);

        final EditText etMassage = (EditText) mView.findViewById(R.id.editTextInPopupMessageReceived);
        SharedPreferences prefs =getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user= prefs.getString("name",null);
        etMassage.setText(user);





        ImageButton imageButtonSend = (ImageButton) mView.findViewById(R.id.imageButtonSendInPopupMessageReceived);
//        lp = (LayoutParams) imageButton.getLayoutParams();
//        lp.topMargin = top - 58;
//        imageButton.setLayoutParams(lp);
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLicked");
//                mView.setVisibility(View.INVISIBLE);
                if(!etMassage.getText().toString().equals(""))
                {
                    System.out.println("sent");
                    etMassage.setText("");
                }
            }
        });

        TextView close = (TextView) mView.findViewById(R.id.TextViewCloseInPopupMessageReceived);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideDialog();
            }
        });

        TextView view = (TextView) mView.findViewById(R.id.textviewViewInPopupMessageReceived);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideDialog();
            }
        });

        TextView message = (TextView) mView.findViewById(R.id.TextViewMessageInPopupMessageReceived);
        message.setText(aTitle);

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mView, mLayoutParams);
        mWindowManager.updateViewLayout(mView, mLayoutParams);

    }

    private void hideDialog(){
        if(mView != null && mWindowManager != null){
            mWindowManager.removeView(mView);
            mView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}