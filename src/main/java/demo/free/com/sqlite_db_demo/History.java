package demo.free.com.sqlite_db_demo;// Created by $USER_NAME on 09-08-2016.

import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class History extends ContentObserver {
     ExampleDBHelper dbHelper ;
    Context c;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    Cursor managedCursor;

    public History(Handler handler, Context cc) {
        // TODO Auto-generated constructor stub
        super(handler);
        c=cc;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        SharedPreferences sp=c.getSharedPreferences("SampleDemo", Activity.MODE_PRIVATE);
        String number=sp.getString("number", null);
        if(number!=null)
        {
            getCalldetailsNow();
            sp.edit().putString("number", null).commit();
        }
    }

    private void getCalldetailsNow() {
        // TODO Auto-generated method stub

        if (Build.VERSION.SDK_INT >= 23 && c.getApplicationContext().checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            managedCursor = c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
        }else{managedCursor = c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");}
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int type1 = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date1 = managedCursor.getColumnIndex(CallLog.Calls.DATE);

            if (managedCursor.moveToFirst() == true) {
                String phNumber = managedCursor.getString(number);
                String callDuration = managedCursor.getString(duration1);

                String type = managedCursor.getString(type1);
                String date = managedCursor.getString(date1);

                String dir = null;
                int dircode = Integer.parseInt(type);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                    default:
                        dir = "MISSED";
                        break;
                }

                SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
                // SimpleDateFormat sdf_dur = new SimpleDateFormat("KK:mm:ss");

                String dateString = sdf_date.format(new Date(Long.parseLong(date))) + "  " + sdf_time.format(new Date(Long.parseLong(date)));
                String timeString = sdf_time.format(new Date(Long.parseLong(date)));
                //  String duration_new=sdf_dur.format(new Date(Long.parseLong(callDuration)));


                Log.d("Number", phNumber);
                Log.d("CallType", dir);
                Log.d("Date", dateString);
                Log.d("Time", timeString);
                Log.d("Duration", callDuration);

                dbHelper = new ExampleDBHelper(c.getApplicationContext());
                try {
                    if (dbHelper.insertPerson(phNumber,
                            dir,
                            dateString,
                            callDuration)) {
                        Toast.makeText(c.getApplicationContext(), "CallLog Inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c.getApplicationContext(), "Could not Insert CallLog", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException numberEx) {
                    System.out.print(numberEx);
                }

         //   }

            managedCursor.close();
        }
    }
}