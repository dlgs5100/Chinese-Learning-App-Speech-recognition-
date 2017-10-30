package com.example.minnie.rec_web;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends Activity {

    TextView CH1,CH2,CH3;
    ImageView ivCH1,ivCH2,ivCH3;
    int chapter = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        CH1 = (TextView)findViewById(R.id.CH1);
        ivCH1 = (ImageView)findViewById(R.id.ivCH1);
        ivCH1.setImageResource(R.drawable.ch1);
        CH2 = (TextView)findViewById(R.id.CH2);
        ivCH2 = (ImageView)findViewById(R.id.ivCH2);
        ivCH2.setImageResource(R.drawable.ch2);
        CH3 = (TextView)findViewById(R.id.CH3);
        ivCH3 = (ImageView)findViewById(R.id.ivCH3);
        ivCH3.setImageResource(R.drawable.ch3);

        CH1.setOnClickListener(new ClickEvent());
        ivCH1.setOnClickListener(new ClickEvent());
        CH2.setOnClickListener(new ClickEvent());
        ivCH2.setOnClickListener(new ClickEvent());
        CH3.setOnClickListener(new ClickEvent());
        ivCH3.setOnClickListener(new ClickEvent());
    }
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == CH1 || v == ivCH1) {
                chapter = 1;
                /*按文字或圖片都可跳到指定的章節*/
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, CatalogActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("chapter", chapter);

                intent.putExtras(bundle);
                startActivity(intent);
            }
            else if (v == CH2 || v == ivCH2) {
                chapter = 2;
                /*按文字或圖片都可跳到指定的章節*/
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, CatalogActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("chapter", chapter);

                intent.putExtras(bundle);
                startActivity(intent);
            }
            else if (v == CH3 || v == ivCH3) {
                chapter = 3;
                /*按文字或圖片都可跳到指定的章節*/
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this, CatalogActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("chapter", chapter);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }
}
