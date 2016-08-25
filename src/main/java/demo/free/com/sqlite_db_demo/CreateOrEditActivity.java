package demo.free.com.sqlite_db_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class CreateOrEditActivity extends ActionBarActivity {


    private ExampleDBHelper dbHelper ;
    EditText numberEditText;
    EditText calltypeEditText;
    EditText datetimeEditText;
    EditText durationEditText;

    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton;

    int personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personID = getIntent().getIntExtra(MainActivity.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_edit);
        numberEditText = (EditText) findViewById(R.id.editNumber);
        calltypeEditText = (EditText) findViewById(R.id.editCallType);
        datetimeEditText = (EditText) findViewById(R.id.editDateTime);
        durationEditText = (EditText) findViewById(R.id.editDuration);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (dbHelper.insertPerson(numberEditText.getText().toString(),
                            calltypeEditText.getText().toString(),
                            datetimeEditText.getText().toString(),
                            durationEditText.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Person Inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not Insert person", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }catch(NumberFormatException numberEx) {
                    System.out.print(numberEx);
                }
            }
        });
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        //editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
      //  deleteButton.setOnClickListener(this);

        dbHelper = new ExampleDBHelper(this);

      /*  if(personID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Cursor rs = dbHelper.getPerson(personID);
            rs.moveToFirst();
            int callNumber = rs.getInt(rs.getColumnIndex(ExampleDBHelper.CALL_COLUMN_NUMBER));
            String callType = rs.getString(rs.getColumnIndex(ExampleDBHelper.CALL_COLUMN_TYPE));
            String callDateTime = rs.getString(rs.getColumnIndex(ExampleDBHelper.CALL_COLUMN_DATE_TIME));
            String callDuration = rs.getString(rs.getColumnIndex(ExampleDBHelper.CALL_COLUMN_DURATION));
            if (!rs.isClosed()) {
                rs.close();
            }

            numberEditText.setText(callNumber);
            numberEditText.setFocusable(false);
            numberEditText.setClickable(false);

            calltypeEditText.setText(callType);
            calltypeEditText.setFocusable(false);
            calltypeEditText.setClickable(false);

            datetimeEditText.setText((callDateTime));
            datetimeEditText.setFocusable(false);
            datetimeEditText.setClickable(false);

            durationEditText.setText((callDuration));
            durationEditText.setFocusable(false);
            durationEditText.setClickable(false);
        }*/
    }

 /*   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
               // persistPerson();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);

                numberEditText.setEnabled(true);
                numberEditText.setFocusableInTouchMode(true);
                numberEditText.setClickable(true);

                calltypeEditText.setEnabled(true);
                calltypeEditText.setFocusableInTouchMode(true);
                calltypeEditText.setClickable(true);

                datetimeEditText.setEnabled(true);
                datetimeEditText.setFocusableInTouchMode(true);
                datetimeEditText.setClickable(true);

                durationEditText.setEnabled(true);
                durationEditText.setFocusableInTouchMode(true);
                durationEditText.setClickable(true);

                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deletePerson)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deletePerson(personID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Person?");
                d.show();
                return;
        }
    }
*/
   /* public void persistPerson() {
        if(personID > 0) {
            if(dbHelper.updatePerson(personID, numberEditText.getText().toString(),
                    calltypeEditText.getText().toString(),
                    datetimeEditText.getText().toString(),
                    durationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Person Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Person Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.insertPerson(numberEditText.getText().toString(),
                    calltypeEditText.getText().toString(),
                    datetimeEditText.getText().toString(),
                    durationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Person Inserted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not Insert person", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }*/
}
