package com.android.project.nnfriends_;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.project.nnfriends_.Classes.DialogListAdapter;
import com.android.project.nnfriends_.Classes.PreferenceManager;
import com.android.project.nnfriends_.Classes.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.android.project.nnfriends_.LoginActivity.KEY_USER_NAME;

public class RoomAddActivity extends AppCompatActivity {

    public static int year;
    public static int day;
    public static int month;
    public static int hour;
    public static int minute;
    private Button GuBtn, DongBtn;
    public TextView GuTxt, DongTxt;
    public static TextView dateTxt;
    public static TextView timeTxt;

    ArrayList<Room> rooms = new ArrayList<>();

    ArrayList<String> GuList, DongList;
    int guNum;
    int[] entries = {
            R.array.dong0, R.array.dong1, R.array.dong2, R.array.dong3, R.array.dong4, R.array.dong5, R.array.dong6, R.array.dong7, R.array.dong8, R.array.dong9,
            R.array.dong10, R.array.dong11, R.array.dong12, R.array.dong13, R.array.dong14, R.array.dong15, R.array.dong16, R.array.dong17, R.array.dong18, R.array.dong19,
            R.array.dong20, R.array.dong21, R.array.dong22, R.array.dong23, R.array.dong24, R.array.dong25
    };


    boolean ttsReady =false;
    public static int REQ_SPEAK_CODE = 100;
    TextToSpeech tts;
    TextView tv_quest;
    TextView tv_answer;

    String ans2, ans3, ans4;
    int ansNum;

    DatabaseReference table;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_add);


        init();
        init2();

    }

    public void init(){
        dateTxt = (TextView)findViewById(R.id.DateTxt);
        timeTxt = (TextView)findViewById(R.id.TimeTxt);
        GuBtn = (Button) findViewById(R.id.gubtn);
        DongBtn = (Button) findViewById(R.id.dongbtn);
        GuTxt = (TextView)findViewById(R.id.GuTxt);
        DongTxt = (TextView)findViewById(R.id.DongTxt);

        GuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GuList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.gu)));


                final DialogListAdapter GuAdapter = new DialogListAdapter(RoomAddActivity.this, GuList);


                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomAddActivity.this).setAdapter(GuAdapter,null);
                alertBuilder.setTitle("CHOICE");

                alertBuilder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                final AlertDialog alertDialog = alertBuilder.create();
                final ListView listView = alertDialog.getListView();
                alertDialog.show();
                listView.setAdapter(GuAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String strName = GuList.get(i);
                        GuTxt.setText(strName);
                        guNum=i;
                        alertDialog.dismiss();
                    }
                });
            }
        });
        DongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DongList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(entries[guNum])));

                final DialogListAdapter DongAdapter = new DialogListAdapter(RoomAddActivity.this, DongList);

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomAddActivity.this).setAdapter(DongAdapter,null);
                alertBuilder.setTitle("CHOICE");
                alertBuilder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                final AlertDialog alertDialog = alertBuilder.create();
                final ListView listView = alertDialog.getListView();
                alertDialog.show();
                listView.setAdapter(DongAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String strName = DongList.get(i);
                        DongTxt.setText(strName);
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setDate() {
        //datepicker부분

        DialogFragment dialogfragment = new RoomAddActivity.DatePickerDialogTheme();
        dialogfragment.show(getFragmentManager(), "Theme");

    }

    private void setTime(){
        //timepicker 부분
        TimePickerFragment timefrgment = new TimePickerFragment();
        timefrgment.show(getFragmentManager(),"TimePicker");
    }

    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,year,month,day);

            return datepickerdialog;
        }
        public void onResume(){
            super.onResume();
            //        Window window = getDialog().getWindow();
            //        window.setLayout(1200,1500);
            //        window.setGravity(Gravity.CENTER);
        }
        public void onDateSet(DatePicker view, int year, int month, int day){

            dateTxt.setText(String.valueOf(year)+String.valueOf(month+1)+String.valueOf(day));

        }

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,
                    hour,minute, DateFormat.is24HourFormat(getActivity()));

            return tpd;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            String aMpM = "AM";
            if(hourOfDay>11){
                aMpM = "PM";
            }
            int currentHour;
            if(hourOfDay>12){
                currentHour = hourOfDay-12;
            }
            else{
                currentHour = hourOfDay;
            }
            timeTxt.setText(currentHour+":"+minute+aMpM);
        }
    }

    public void btnClick(View view){
        if(view.getId()==R.id.DateBtn){
            setDate();
        }
        else if(view.getId()==R.id.TimeBtn){
            setTime();
        }
    }

    private void init2() {

        //TTS부분
        tts = new TextToSpeech(
                this,
                new TextToSpeech.OnInitListener(){
                    @Override
                    public void onInit(int status) {
                        ttsReady = true;
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    private void initQuest() {
        if (ansNum == 4) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //질문 읽어주는 부분
                tts.speak(tv_quest.getText().toString(), TextToSpeech.QUEUE_ADD, null, null);

                final ArrayList<String> teamNum = new ArrayList<String>();
                for (int i = 1; i < 11; i++) {
                    teamNum.add(String.valueOf(i));
                }

                final DialogListAdapter teamAdapter = new DialogListAdapter(RoomAddActivity.this, teamNum);


                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoomAddActivity.this).setAdapter(teamAdapter, null);
                alertBuilder.setTitle("CHOICE");

                alertBuilder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                final AlertDialog alertDialog = alertBuilder.create();
                final ListView listView = alertDialog.getListView();
                alertDialog.show();
                listView.setAdapter(teamAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String strName = teamNum.get(i);
                        tv_answer.setText(strName);
                        ans4 = strName;
                        alertDialog.dismiss();
                    }
                });

            }
        } else {
            if (ttsReady) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //질문 읽어주는 부분
                    tts.speak(tv_quest.getText().toString(), TextToSpeech.QUEUE_ADD, null, null);
                    while (true) {
                        if (!tts.isSpeaking()) {
                            //대답 받는 부분
                            Intent intent = new Intent(
                                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                            );
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "대답하세요");
                            startActivityForResult(intent, REQ_SPEAK_CODE);
                            break;
                        }
                    }
                } else {
                    tts.speak(tv_quest.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_SPEAK_CODE){
            ArrayList<String> list =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(list == null || list.isEmpty()){
                return;
            }
            tv_answer.setText(list.get(0));
            switch (ansNum) {
                case 2:
                    ans2 = list.get(0);
                    break;
                case 3:
                    ans3 = list.get(0);
                    break;
            }
        }
    }

    public void questClick(View view) {
        switch(view.getId()) {
            case R.id.quest2:
                tv_quest = (TextView)findViewById(R.id.quest2);
                tv_answer = (TextView)findViewById(R.id.ans2);
                ansNum = 2;
                break;
            case R.id.quest3:
                tv_quest = (TextView)findViewById(R.id.quest3);
                tv_answer = (TextView)findViewById(R.id.ans3);
                ansNum = 3;
                break;
            case R.id.quest4:
                tv_quest = (TextView)findViewById(R.id.quest4);
                tv_answer = (TextView)findViewById(R.id.ans4);
                ansNum = 4;
                break;
        }

        initQuest();
    }

    public void saveClick(View view){

        PreferenceManager pref = new PreferenceManager();

        //db 저장 부분
        final String Gu = GuTxt.getText().toString();
        final String Dong =DongTxt.getText().toString();
        final String groupDate = dateTxt.getText().toString()+","+timeTxt.getText().toString();
        final String groupPlace = ans2;
        final String groupContent = ans3;
        final String Leader = pref.getStringPref(RoomAddActivity.this, KEY_USER_NAME);
        final String TeamNum = String.valueOf(ans4);
        final String Active = String.valueOf(0);    //모집중
        final String Roomkey = "0"; // 바꿀예정


        table = FirebaseDatabase.getInstance().getReference("NNfriendsDB/RoomDB");  //모집중인 방들 모음
        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int matchNum = 0; // 처리해야됨
                String key = String.valueOf(matchNum) + "_" + year + month + day;
                SimpleDateFormat wTime = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm"); // 작성시간
                final Date today = new Date();

                DatabaseReference roomRef = table.child(key);
                Room room = new Room(key, Active, Leader, TeamNum, Gu, Dong, groupDate, groupPlace, groupContent);
                roomRef.setValue(room);
                Toast.makeText(RoomAddActivity.this, "작성 완료", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // 액티비티 끄기
        finish();
    }

}
