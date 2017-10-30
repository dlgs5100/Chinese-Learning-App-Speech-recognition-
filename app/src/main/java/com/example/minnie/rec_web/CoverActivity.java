package com.example.minnie.rec_web;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CoverActivity extends Activity{
    RelativeLayout root;
    ImageView imageTitle;
    public static boolean tutor_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover_activity);

        root = (RelativeLayout) this.findViewById(R.id.cover_activiy);
        imageTitle = (ImageView) this.findViewById(R.id.imageTitle);

        root.setOnClickListener(new ClickEvent());
        imageTitle.setImageResource(R.drawable.title);
    }
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == root) {
                /*按開始測試會跳到處理程序的Activity*/
                Intent intent = new Intent();
                intent.setClass(CoverActivity.this, MenuActivity.class);

                startActivity(intent);
                CoverActivity.this.finish();
            }
        }
    }
}
