package com.example.pdfdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    private EditText myEditText;
    private Button Createbtn;
    private ImageButton mic;
    private Button Clear;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myEditText = findViewById(R.id.et2);
        Createbtn = findViewById(R.id.btn);
        mic = findViewById(R.id.ib);
        Clear = findViewById(R.id.btn3);
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        Createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMyPdf(v);
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                speech();
            }
        });

        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditText.setText("");
            }
        });

    }
        public void CreateMyPdf (View view){
            if (isEmpty(myEditText.getText().toString())) {
                Toast.makeText(this, "Enter Text Please", Toast.LENGTH_SHORT).show();
            } else {
                PdfDocument mypdfdocument = new PdfDocument();
                PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
                PdfDocument.Page mypage = mypdfdocument.startPage(mypageinfo);

                Paint mypaint = new Paint();
                String myString = myEditText.getText().toString();

                int x = 10, y = 25;
                for (String line : myString.split("\n")) {
                    mypage.getCanvas().drawText(line, x, y, mypaint);
                    y += mypaint.descent() - mypaint.ascent();
                }
                mypdfdocument.finishPage(mypage);
                String myfile11 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

                String myfilepath = Environment.getExternalStorageDirectory().getPath() + "/" + myfile11 + ".pdf";
                File myfile = new File(myfilepath);

                try {
                    mypdfdocument.writeTo(new FileOutputStream(myfile));
                    Toast.makeText(this, "Pdf Created Successfully`", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "" + myfile11 + ".pdf" + "is Saved to " + myfilepath, Toast.LENGTH_SHORT).show();
                    myEditText.setText(null);

                } catch (Exception e) {
                    e.printStackTrace();
                    myEditText.setText("ERROR");
                }
                mypdfdocument.close();
            }
        }
        private void speech () {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQUEST_CODE_SPEECH_INPUT: {
                    if (resultCode == RESULT_OK) {
                        if (null != data) {
                            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                            myEditText.append(result.get(0));

                        }

                    }
                    break;
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.toolbar_menu, menu);
            return true;

        }

}

