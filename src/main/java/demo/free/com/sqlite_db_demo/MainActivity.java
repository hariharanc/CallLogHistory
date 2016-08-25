package demo.free.com.sqlite_db_demo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    ExampleDBHelper dbHelper;
    Button btn_sync, btn_upload;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private boolean csv_status = false;
    public static final String UPLOAD_URL = "http://172.31.98.126/project/demo/public/uploadIndex.php";
    String uploadId = UUID.randomUUID().toString();
    final String uploadFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    final String uploadFileName = "CallLog.csv";
    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    InputStream stream;
    String jsonReply;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallLog.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_sync = (Button) findViewById(R.id.btn_sync);
        btn_upload = (Button) findViewById(R.id.btn_upload);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        Log.d("random no",""+m);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                               // messageText.setText("uploading started.....");
                                Toast.makeText(MainActivity.this, "uploading started.....", Toast.LENGTH_SHORT).show();
                            }
                        });
                        uploadFile(path);
                    }
                }).start();
            }
        });
        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    GetRuntimePermission();
                } else {
                    createCSV();
                }
            }
        });
        dbHelper = new ExampleDBHelper(this);

        final Cursor cursor = dbHelper.getAllPersons();
        String[] columns = new String[]{
                ExampleDBHelper.CALL_COLUMN_NUMBER,
                ExampleDBHelper.CALL_COLUMN_TYPE,
                ExampleDBHelper.CALL_COLUMN_DATE_TIME,
                ExampleDBHelper.CALL_COLUMN_DURATION
        };
        int[] widgets = new int[]{
                R.id.callNumber,
                R.id.callType,
                R.id.callDateTime,
                R.id.callDuration
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.person_info,
                cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(cursorAdapter);
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                int personID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.CALL_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, personID);
                startActivity(intent);
            }
        });*/

    }
    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        Log.e("uploadFile", ":"+ sourceFile);
        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                  //  messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                    Toast.makeText(MainActivity.this, "Source File not exist :" +uploadFilePath + uploadFileName, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URL);
                Log.i("URL", " : "
                        + UPLOAD_URL);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploadedfile", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                InputStream response = conn.getInputStream();
                jsonReply = convertStreamToString(response);
                Log.i("uploadFile", "HTTP Response is : " + jsonReply+ ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                           // messageText.setText(msg);
                            Toast.makeText(MainActivity.this, "File Upload Complete."+ jsonReply, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                      //  messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload server Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private void GetRuntimePermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        createCSV();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    createCSV();
                } else {
                    // Permission Denied
                    //Toast.makeText(WalletToWalletActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void createCSV() {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallLog.csv"));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        writer.writeColumnNames(); // Write column header

        dbHelper = new ExampleDBHelper(this);
        final Cursor cursor = dbHelper.getAllPersons();

        if (cursor.moveToFirst()) {
            do {
                String mobno = cursor.getString(cursor.getColumnIndex("mobile_number"));
                String calltype = cursor.getString(cursor.getColumnIndex("call_type"));
                String datetime = cursor.getString(cursor.getColumnIndex("date_time"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                writer.writeNext((mobno + "," + calltype + "," + datetime + "," + duration).split(","));

            } while (cursor.moveToNext());
            csv_status = true;
            Toast.makeText(getBaseContext(),
                    "Exported to Csv successfully'",
                    Toast.LENGTH_SHORT).show();
        } else {
            csv_status = false;
        }
        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            Log.w("Test", e.toString());
        }

    }// Method  close.
}
