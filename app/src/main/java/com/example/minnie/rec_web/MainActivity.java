package com.example.minnie.rec_web;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import static android.Manifest.permission.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import java.io.IOException;

public class MainActivity extends Activity {
    SwitchCompat switcher, language;
    CheckBox checkResult;
    TextView TextAnnouncement;
    SeekBar barScale;
    SurfaceView sfv;
    ImageView btnRecord, btnListen, btnPitch, btnPronounce;
    Button btnScore;
    AlertDialog.Builder dialogTeach;
    AlertDialog dialog0;
    AlertDialog dialog1;
    static ProgressDialog loading;
    CheckBox skip;
    ClsOscilloscope clsOscilloscope;
    static final int frequency = 8000;
    static final int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;     //雙聲道
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    int recBufSize;
    AudioRecord audioRecord;
    Paint mPaint;
    SelectObject UserSelection;
    boolean End_Point_Error = false;
    int gender = 2, translate = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*Android版本處理*/
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED&&permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {RECORD_AUDIO, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, 123);
        }
        /*--------------------*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*保證通過read不會被因為延遲而被覆蓋掉的最小緩存空間*/
        recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        //buffer過大，聲音間有空隙
        recBufSize /= 4;

        /*按鈕事件*/
        switcher = (SwitchCompat) this.findViewById(R.id.switcher);

        TextAnnouncement = (TextView) this.findViewById(R.id.TextAnnouncement);
        checkResult = (CheckBox) this.findViewById(R.id.checkResult);
        checkResult.setOnClickListener(new CheckEvent());
        checkResult.setEnabled(false);

        barScale = (SeekBar) this.findViewById(R.id.barScale);
        barScale.setOnSeekBarChangeListener(OnSeekBarChange);

        btnRecord = (ImageView) this.findViewById(R.id.btnRecord);
        btnRecord.setImageResource(R.drawable.recordoff);
        btnRecord.setOnClickListener(new ClickEvent());
        btnScore = (Button) this.findViewById(R.id.btnScore);
        btnScore.setOnClickListener(new ClickEvent());
        btnListen = (ImageView) this.findViewById(R.id.btnListen);
        btnListen.setOnClickListener(new ClickEvent());
        btnListen.setImageResource(R.drawable.unlisten);
        btnPitch = (ImageView) this.findViewById(R.id.btnPitch);
        btnPitch.setOnClickListener(new ClickEvent());
        btnPitch.setImageResource(R.drawable.unpitch);
        btnPronounce = (ImageView) this.findViewById(R.id.btnPronounce);
        btnPronounce.setOnClickListener(new ClickEvent());
        btnPronounce.setImageResource(R.drawable.unpronounce);

        sfv = (SurfaceView) this.findViewById(R.id.SurfaceView01);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(1);

        /*接收使用者的選擇*/
        UserSelection = (SelectObject)getIntent().getSerializableExtra("UserSelection");
        TextAnnouncement.setTextSize(35);
        TextAnnouncement.setTextColor(Color.BLACK);
        TextAnnouncement.setText("題目: "+UserSelection.getSentence());
        clsOscilloscope = new ClsOscilloscope(recBufSize,frequency,sfv,UserSelection,getBaseContext(),checkResult,barScale);

        gender_switch();
        if(!CoverActivity.tutor_check)
            tutor();

        /*---Debug用---*/
        //debug時，註解掉
        btnScore.setEnabled(false);
        btnScore.setVisibility(View.INVISIBLE);
        /*-------------*/
        barScale.setEnabled(false);
        btnListen.setEnabled(false);
        btnPitch.setEnabled(false);
        btnPronounce.setEnabled(false);

    }

    /*mode2 男生為奇數,女生為偶數(沒做)*/
    public void gender_switch(){
        switcher.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (!isChecked) {
                    gender = 1;
                }
                else {
                    gender = 0;
                }
            }
        });
    }
    /*教學Dialog*/
    public void tutor(){
        String Message = null;
        Context mContext = MainActivity.this;
        LayoutInflater checkInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        //用inflater取得xml
        View checkboxLayout = checkInflater.inflate(R.layout.checkbox, null);
        skip = (CheckBox) checkboxLayout.findViewById(R.id.skip);
        language = (SwitchCompat) checkboxLayout.findViewById(R.id.language_switcher);
        //中英轉換switch
        language_switch();
        //中文(0)英文(1)
        if(translate == 0) {
            Message = tutor_change(0);
            dialogTeach = new AlertDialog.Builder(this);
            dialog0 = dialogTeach.setTitle("教學")
                    .setView(checkboxLayout)
                    .setMessage(Message)
                    .setPositiveButton("了解!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(skip.isChecked())
                                CoverActivity.tutor_check = true;
                        }
                    })
                    .show();
        }
        else {
            Message = tutor_change(1);
            dialogTeach = new AlertDialog.Builder(this);
            dialog1 = dialogTeach.setTitle("Tutorial")
                    .setView(checkboxLayout)
                    .setMessage(Message)
                    .setPositiveButton("I know!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(skip.isChecked())
                                CoverActivity.tutor_check = true;
                        }
                    })
                    .show();
        }

    }
    public void language_switch(){
        language.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (!isChecked) {
                    String Message = null;
                    Context mContext = MainActivity.this;
                    LayoutInflater checkInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View checkboxLayout = checkInflater.inflate(R.layout.checkbox, null);
                    skip = (CheckBox) checkboxLayout.findViewById(R.id.skip);
                    language = (SwitchCompat) checkboxLayout.findViewById(R.id.language_switcher);
                    language.setChecked(false);
                    language_switch();

                    Message = tutor_change(0);
                    dialogTeach = new AlertDialog.Builder(MainActivity.this);
                    dialog0 = dialogTeach.setTitle("教學")
                            .setView(checkboxLayout)
                            .setMessage(Message)
                            .setPositiveButton("了解!!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(skip.isChecked())
                                        CoverActivity.tutor_check = true;
                                }
                            })
                            .show();
                    //關閉英文版教學
                    dialog1.dismiss();
                }
                else {
                    String Message = null;
                    Context mContext = MainActivity.this;
                    LayoutInflater checkInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View checkboxLayout = checkInflater.inflate(R.layout.checkbox, null);
                    skip = (CheckBox) checkboxLayout.findViewById(R.id.skip);
                    language = (SwitchCompat) checkboxLayout.findViewById(R.id.language_switcher);
                    language.setChecked(true);
                    language_switch();

                    Message = tutor_change(1);
                    dialogTeach = new AlertDialog.Builder(MainActivity.this);
                    dialog1 = dialogTeach.setTitle("Tutorial")
                            .setView(checkboxLayout)
                            .setMessage(Message)
                            .setPositiveButton("I know!!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(skip.isChecked())
                                        CoverActivity.tutor_check = true;
                                }
                            })
                            .show();
                    //關閉中文版教學
                    dialog0.dismiss();
                }
            }
        });
    }
    //教學內容
    public String tutor_change(int select){
        String Chinese_Message = "第一步: 選擇性別\n" +
                "第二步: 點擊麥克風按鈕使其變紅色以錄音\n" +
                "第三步: 錄音結束後再次點擊麥克風按鈕開始評分\n" +
                "第四步: 勾選右上方'詳細結果'檢視表現，可使用比例尺縮放\n" +
                "第五步: 點擊耳機按鈕播放當前錄音內容\n" +
                "第六步: 點擊音高或咬字按鈕，可撥放各自準確的部分\n" +
                "(可反覆錄音進行評分)";
        String English_Message = "Step1: Choose your gender.\n" +
                "Step2: Click the microphone button. It's recording when the button become red.\n" +
                "Step3: When you complete recording. Click the microphone button again, and you will get a score.\n" +
                "Step4: Check the upper right checkbox can see the detail result, and you can be enlarged or reduced in proportion.\n" +
                "Step5: You can listen your previous recording, when you click the headphone button \n" +
                "Step6: In the lower right have two buttons. The upper one can play the right part in your pronunciation. And the lower one can play as same as previous in your pitch.\n" +
                "(You can test again and again if you want.)";
        if(select == 0)
            return Chinese_Message;
        else if(select == 1)
            return English_Message;
        else
            return "Error";
    }
    /*比例尺barScale*/
    private SeekBar.OnSeekBarChangeListener OnSeekBarChange = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            //拉動SeekBar停止時做的動作
            //依比例重新畫圖
            clsOscilloscope.scene(1);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            //開始拉動SeekBar時做的動作
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            //拖曳途中觸發事件，回傳參數 progress 告知目前拖曳數值
        }
    };

    /*用來選擇分數或圖表的checkbox*/
    class CheckEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(checkResult.isChecked()) {
                clsOscilloscope.scene(1);
                barScale.setEnabled(true);
            }
            else {
                clsOscilloscope.scene(2);
                barScale.setEnabled(false);
            }
        }
    }

    /*Button事件*/
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btnRecord) {
                if(!ClsOscilloscope.isrecording) {
                    /*AudioRecord*/
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);
                    ClsOscilloscope.isrecording = true;
                    btnRecord.setImageResource(R.drawable.recordon);
                    clsOscilloscope.Start(audioRecord);
                }
                else {
                    ClsOscilloscope.isrecording = false;
                    btnRecord.setImageResource(R.drawable.recordoff);
                    Loading_dialog();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            End_Point_Error = clsOscilloscope.Stop();

                            Message message = handler.obtainMessage(1,End_Point_Error);
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
            //全句撥放按鈕
            else if (v == btnListen) {
                try {
                    clsOscilloscope.Play();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            //音高部分撥放按鈕
            else if (v == btnPitch) {
                try {
                    clsOscilloscope.PlayPitch();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            //咬字部分撥放按鈕
            else if (v == btnPronounce) {
                try {
                    clsOscilloscope.PlayVector();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            //Debug用
            else if (v == btnScore) {
                clsOscilloscope.Show();
                clsOscilloscope.setUserSelection(UserSelection);
                barScale.setEnabled(false);
                checkResult.setEnabled(true);
                checkResult.setChecked(false);
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //避免端點偵測失敗進入error
            if((boolean)msg.obj == false) {
                btnListen.setImageResource(R.drawable.listen);
                btnPitch.setImageResource(R.drawable.pitch);
                btnPronounce.setImageResource(R.drawable.pronounce);
                btnListen.setEnabled(true);
                btnScore.setEnabled(true);
                btnPitch.setEnabled(true);
                btnPronounce.setEnabled(true);

                clsOscilloscope.setUserSelection(UserSelection);
                barScale.setEnabled(false);
                checkResult.setEnabled(true);
                checkResult.setChecked(false);
            }
        }
    };
    public static void stop_loading() {
        loading.dismiss();
    }
    public void Loading_dialog() {
        loading = ProgressDialog.show(MainActivity.this, "Processing", "Please wait with patience...",true);
    }
    /*避免未完成錄音直接返回的Error*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClsOscilloscope.isrecording = false;
            finish();
        }
        return true;
    }
}