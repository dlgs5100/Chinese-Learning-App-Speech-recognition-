package com.example.minnie.rec_web;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


public class CatalogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RadioGroup rbGroup;
    private RadioButton rbWord, rbSentence;
    private Spinner spinSelection;
    private ImageView btnArticle1, btnArticle2, btnPlay, ivSample, btnTest;
    private boolean isSpinnerFirst = true;
    private String[] spinner_content;
    private int[] picture_list;
    private int[] sound_list;
    private int chapter;
    private int article;
    private String type;
    private String exam_sentence;
    private Data data;

    private int spinner_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);
        /*側目錄*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*接收傳入的chapter值*/
        Bundle bundle = getIntent().getExtras();
        chapter = bundle.getInt("chapter");

        rbGroup = (RadioGroup) this.findViewById(R.id.RadioGroup);
        rbWord = (RadioButton) this.findViewById(R.id.rbWord);
        rbSentence = (RadioButton) this.findViewById(R.id.rbSentence);
        spinSelection = (Spinner) this.findViewById(R.id.spinSelection);
        ivSample = (ImageView) this.findViewById(R.id.ivSample);
        btnArticle1 = (ImageView) this.findViewById(R.id.btnArticle1);
        btnArticle2 = (ImageView) this.findViewById(R.id.btnArticle2);
        btnPlay = (ImageView) this.findViewById(R.id.btnPlay);
        btnTest = (ImageView) this.findViewById(R.id.btnTest);

        btnArticle1.setOnClickListener(new CatalogActivity.ClickEvent());
        btnArticle2.setOnClickListener(new CatalogActivity.ClickEvent());
        btnTest.setOnClickListener(new CatalogActivity.ClickEvent());
        btnPlay.setOnClickListener(new CatalogActivity.ClickEvent());

        /*讓一開始就有第一篇文章*/
        initial_image();
        /*初始化spinner，沒有內容*/
        initial_spinner();
    }
    /*側目錄返回*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
    /*側目錄選項，透過呼叫自己刷新，Activity不變*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //第一章
        if (id == R.id.CH1) {
            int new_chapter = 1;
            Intent intent = new Intent();
            intent.setClass(CatalogActivity.this, CatalogActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("chapter", new_chapter);

            intent.putExtras(bundle);
            startActivity(intent);
            CatalogActivity.this.finish();
        }
        //第二章
        else if (id == R.id.CH2) {
            int new_chapter = 2;
            Intent intent = new Intent();
            intent.setClass(CatalogActivity.this, CatalogActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("chapter", new_chapter);

            intent.putExtras(bundle);
            startActivity(intent);
            CatalogActivity.this.finish();
        }
        //第三章
        else if (id == R.id.CH3) {
            int new_chapter = 3;
            Intent intent = new Intent();
            intent.setClass(CatalogActivity.this, CatalogActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("chapter", new_chapter);

            intent.putExtras(bundle);
            startActivity(intent);
            CatalogActivity.this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*讓一開始點進就能出現文章圖*/
    public void initial_image(){
        //文章一先呈現
        article = 1;
        data = new Data(chapter,article);
        picture_list = data.getPictureCode();
        ivSample.setImageDrawable(getResources().getDrawable(picture_list[article-1]));
        //讓一開始不要顯示範例音播放鍵
        btnPlay.setVisibility(View.INVISIBLE);
        //讓測試按鍵一開始無法按
        btnTest.setEnabled(false);
    }

    /*文章切換，清空RadioButton、Spinner及隱藏撥放按鍵*/
    public void change_article(){
        //RadioButton
        rbGroup.clearCheck();
        rbWord.setChecked(false);
        rbSentence.setChecked(false);
        //Spinner
        spinSelection.setSelection(-1);
        spinSelection.setAdapter(null);
        isSpinnerFirst = true;
        //Button
        btnPlay.setVisibility(View.INVISIBLE);
        btnTest.setImageResource(R.drawable.test);
        btnTest.setEnabled(false);
    }

    /*選擇單字或句子*/
    public void onRadioButtonClicked(View view){
        boolean type_checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbWord:
                if(type_checked) {
                    type = "Word";
                    //避免連續點自己跑出試聽及測驗按鈕
                    isSpinnerFirst = true;
                }
                else
                    Log.d("rbWord Error", "type_checked");
                break;
            case R.id.rbSentence:
                if(type_checked) {
                    type = "Sentence";
                    isSpinnerFirst = true;
                }
                else
                    Log.d("rbWord Error", "type_checked");
                break;
        }
        //透過文章號及類型，到Data裡面找資料
        data = new Data(chapter,article,type);
        spinner_content = data.getTypeSring();
        picture_list = data.getPictureCode();
        sound_list = data.getSoundCode();
        //透過章節、文章號及類型來決定spinner呈現
        ArrayAdapter<String> sentenceAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_content);
        spinSelection.setAdapter(sentenceAdapter);
        spinSelection.setSelection(-1);
    }
    /*下拉式選單*/
    public void initial_spinner(){
        //ArrayAdapter中sentence型別可為ArrayList
        spinSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //改變spinner顏色
                view.setBackgroundColor(Color.parseColor("#f2b818"));
                //讓spinner一開始為默認，要按第0個以外才算跳出
                if (isSpinnerFirst) {
                    view.setVisibility(View.VISIBLE);
                    isSpinnerFirst = false;
                }
                else {
                    btnPlay.setVisibility(View.VISIBLE);
                    btnTest.setEnabled(true);
                    //用來判斷"請選擇..."以外的選項
                    if (position == 0)
                        Log.d("Spinner position","0");
                    else {
                        exam_sentence = adapterView.getSelectedItem().toString();
                        Toast.makeText(CatalogActivity.this, ("您選了: ") + exam_sentence, Toast.LENGTH_SHORT).show();
                        //讓圖片為顯示
                        ivSample.setVisibility(View.VISIBLE);
                        //spinner位置
                        ivSample.setImageDrawable(getResources().getDrawable(picture_list[position - 1]));
                        //決定示範音檔是哪個
                        spinner_num = position;
                        //更換測驗button圖片
                        btnTest.setImageResource(R.drawable.testing);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //?
            }
        });
    }
    /*測驗按鈕*/
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btnPlay) {
                //撥放測試音
                MediaPlayer mp = MediaPlayer.create(CatalogActivity.this,sound_list[spinner_num-1]);
                mp.start();
            }
            if (v == btnTest) {
                SelectObject UserSelection = new SelectObject(chapter, article, type, exam_sentence, spinner_num);
                //按開始測試會跳到處理程序的Activity
                Intent intent = new Intent();
                intent.setClass(CatalogActivity.this, MainActivity.class);
                //傳送spinner的選項
                Bundle bundle = new Bundle();
                bundle.putSerializable("UserSelection", UserSelection);

                intent.putExtras(bundle);
                startActivity(intent);
            }
            if(v == btnArticle1){
                if(article != 1) {
                    change_article();
                    btnArticle1.setImageResource(R.drawable.article1_on);
                    btnArticle2.setImageResource(R.drawable.article2_off);
                    article = 1;
                    data = new Data(chapter, article);
                    picture_list = data.getPictureCode();
                    ivSample.setImageDrawable(getResources().getDrawable(picture_list[article - 1]));
                }
            }
            if(v == btnArticle2){
                if(article != 2) {
                    change_article();
                    btnArticle1.setImageResource(R.drawable.article1_off);
                    btnArticle2.setImageResource(R.drawable.article2_on);
                    article = 2;
                    data = new Data(chapter, article);
                    picture_list = data.getPictureCode();
                    ivSample.setImageDrawable(getResources().getDrawable(picture_list[article - 1]));
                }
            }
        }
    }
}
