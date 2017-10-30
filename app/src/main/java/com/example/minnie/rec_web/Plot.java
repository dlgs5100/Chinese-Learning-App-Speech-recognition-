package com.example.minnie.rec_web;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;

public class Plot{
    private SurfaceView sfv;
    private int[] PitchPoint = new int[2];
    private int[] VectorPoint = new int[2];

    public Plot(SurfaceView sfv){
        this.sfv = sfv;
    }

    public void loading(){
        Paint mPaint = new Paint();
        mPaint.setTextSize(100);
        mPaint.setColor(Color.LTGRAY);

        Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        canvas.drawText("Loading...",sfv.getWidth()/2,sfv.getHeight()/8*7,mPaint);
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }
    public void write(int score1, int score2, int finalscore){
        Paint mPaint = new Paint();
        mPaint.setTextSize(100);
        mPaint.setColor(Color.LTGRAY);

        Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        canvas.drawText("語調分數: "+Integer.toString(score1),sfv.getWidth()/2-250,sfv.getHeight()/2-100,mPaint);
        canvas.drawText("咬字分數: "+Integer.toString(score2),sfv.getWidth()/2-250,sfv.getHeight()/2,mPaint);
        canvas.drawText("總分: "+Integer.toString(finalscore),sfv.getWidth()/2-250,sfv.getHeight()/2+100,mPaint);
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }
    public void error(){
        Paint mPaint = new Paint();
        mPaint.setTextSize(60);
        mPaint.setColor(Color.LTGRAY);

        Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        canvas.drawText("請降低周圍的雜音或說大聲一點、慢一點",5,sfv.getHeight()/2-100,mPaint);
        canvas.drawText("並重新測驗",390,sfv.getHeight()/2-20,mPaint);
        canvas.drawText("Please test again after you reduce noise",5,sfv.getHeight()/2+60,mPaint);
        canvas.drawText("or speak loudly and slowly.",200,sfv.getHeight()/2+140,mPaint);
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }
    public void draw(double[] pitch_array){
        double x, y, old_x = 0, old_y = sfv.getHeight()/2-pitch_array[0], baseline = sfv.getHeight()/2;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(1);

        Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        for (int i = 1; i < pitch_array.length; i++) {
            y = baseline - pitch_array[i] * 3;
            x = i*2;
            canvas.drawLine((float)old_x, (float)old_y, (float)x, (float)y, mPaint);
            old_x = x;
            old_y = y;
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }
    /*音高圖*/
    public void drawPitch(int[][]PitchSimilar, double[] samplePitch, double[] testPitch, ArrayList<Integer> Noise, Canvas canvas, int x_times){
        double test_baseline = sfv.getHeight()/12*4, sample_baseline = sfv.getHeight()/12*6;
        double x, y, old_x, old_y;
        float x_shift = 90f;    //為了保留圖前空間以寫字
        boolean FistPoint = true;

        Paint sample1Paint = new Paint();
        sample1Paint.setColor(Color.GREEN);
        sample1Paint.setStrokeWidth(3);
        Paint test1Paint = new Paint();
        test1Paint.setColor(Color.GREEN);
        test1Paint.setStrokeWidth(3);
        Paint pair1Paint = new Paint();
        pair1Paint.setColor(Color.RED);
        pair1Paint.setStrokeWidth(2);
        Paint word1Paint = new Paint();
        word1Paint.setColor(Color.RED);
        word1Paint.setStrokeWidth(3);

        old_x = x_shift;
        old_y = test_baseline-testPitch[0];
        for (int i = 1; i < testPitch.length; i++) {
            y = test_baseline - testPitch[i] * 3;
            x = i*x_times+x_shift;
            canvas.drawLine((float)old_x, (float)old_y, (float)x, (float)y, test1Paint);
            old_x = x;
            old_y = y;
        }
        old_x = x_shift;
        old_y = sample_baseline-samplePitch[0];
        for (int i = 1; i < samplePitch.length; i++) {
            y = sample_baseline - samplePitch[i] * 3;
            x = i*x_times+x_shift;
            canvas.drawLine((float)old_x, (float)old_y, (float)x, (float)y, sample1Paint);
            old_x = x;
            old_y = y;
        }

        for(int i = PitchSimilar[0].length-1; i >= 0; i--) {
            int t = PitchSimilar[0][i];
            int s = PitchSimilar[1][i];
            //Log.d(String.valueOf(s),String.valueOf(t));
            if(t == 0 && s == 0);   //全部都有0對0，避免畫到
            else {
                canvas.drawLine((float) t * x_times + x_shift, (float) (test_baseline - testPitch[t] * 3 + 2), (float) s * x_times + x_shift, (float) (sample_baseline - samplePitch[s] * 3 - 2), pair1Paint);
                if(FistPoint == true){
                    PitchPoint[0] = t;
                    FistPoint = false;
                }
                if(i == 0)
                    PitchPoint[1] = t;
            }
        }

        word1Paint.setTextSize(60);
        canvas.drawText("語調比較(Pitch)",20,sfv.getHeight()/12*1,word1Paint);
        word1Paint.setTextSize(30);
        canvas.drawText("用戶",10,sfv.getHeight()/12*3,word1Paint);
        canvas.drawText("範例",10,sfv.getHeight()/12*5,word1Paint);
    }
    /*咬字圖*/
    public void drawVector(int[][]VectorSimilar, int sampleVectorlength, int testVectorlength, Canvas canvas, int x_times){
        double test_baseline = sfv.getHeight()/12*9, sample_baseline = sfv.getHeight()/12*11;
        double x, y, old_x, old_y;
        float x_shift = 90f;    //為了保留圖前空間以寫字
        boolean FistPoint = true;

        Paint sample2Paint = new Paint();
        sample2Paint.setColor(Color.GREEN);
        sample2Paint.setStrokeWidth(3);
        Paint test2Paint = new Paint();
        test2Paint.setColor(Color.GREEN);
        test2Paint.setStrokeWidth(3);
        Paint pair2Paint = new Paint();
        pair2Paint.setColor(Color.RED);
        pair2Paint.setStrokeWidth(2);
        Paint word2Paint = new Paint();
        word2Paint.setColor(Color.RED);
        word2Paint.setStrokeWidth(3);

        old_x = x_shift;
        old_y = test_baseline;
        for (int i = 1; i < testVectorlength; i++) {
            y = test_baseline;
            x = i*x_times+x_shift;
            canvas.drawLine((float)old_x, (float)old_y, (float)x, (float)y, test2Paint);
            old_x = x;
            old_y = y;
        }
        old_x = x_shift;
        old_y = sample_baseline;
        for (int i = 1; i < sampleVectorlength; i++) {
            y = sample_baseline;
            x = i*x_times+x_shift;
            canvas.drawLine((float)old_x, (float)old_y, (float)x, (float)y, sample2Paint);
            old_x = x;
            old_y = y;
        }
        for(int i = VectorSimilar[0].length-1; i >= 0; i--) {
            int t = VectorSimilar[0][i];
            int s = VectorSimilar[1][i];
            //Log.d(String.valueOf(s),String.valueOf(t));
            if(t == 0 && s == 0);   //全部都有0對0，避免畫到
            else {
                canvas.drawLine((float) t * x_times + x_shift, (float) (test_baseline + 2), (float) s * x_times + x_shift, (float) (sample_baseline - 2), pair2Paint);
                if(FistPoint == true){
                    VectorPoint[0] = t;
                    FistPoint = false;
                }
                if(i == 0)
                    VectorPoint[1] = t;
            }
        }
        word2Paint.setTextSize(60);
        canvas.drawText("咬字比較(Pronunciation)",20,sfv.getHeight()/12*8,word2Paint);
        word2Paint.setTextSize(30);
        canvas.drawText("用戶",10,sfv.getHeight()/12*9,word2Paint);
        canvas.drawText("範例",10,sfv.getHeight()/12*11,word2Paint);
    }
    public int[] getPitchPoint(){
        return PitchPoint;
    }
}
