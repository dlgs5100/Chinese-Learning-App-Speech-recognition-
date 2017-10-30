package com.example.minnie.rec_web;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.CheckBox;
import android.widget.SeekBar;

public class ClsOscilloscope extends Activity{

    SeekBar barScale;
    CheckBox checkResult;
    private ArrayList<double[]> Frame;
    private ArrayList<Integer> Nosie;
    private int recBufSize;
    private int frequency;
    private SurfaceView sfv;
    private SelectObject UserSelection;
    public static boolean isrecording = false;
    private Record record;
    private Load load;
    private Preprocedure preprocedure;
    private PitchProcedure pitchprocedure;
    private Plot plot;
    private double[] WAV_double_value;
    private double[] pitch_array;
    private Context mCtx;
    private ArrayList<double[]> Real = new ArrayList<double[]>();
    private ArrayList<double[]> Imag = new ArrayList<double[]>();
    private ArrayList<double[]> Cx = new ArrayList<double[]>();
    private int[][] PitchSimilar;
    private int[][] VectorSimilar;
    private int score1, score2, sampleNumber = 4;
    double[] draw_samplePitch = null;
    int draw_sampleVectorlength = 0;
    private double[] drawTestPitch;
    private ArrayList<double[]> drawTestVector;
    private int finalScore;
    public static boolean loading = true;
    boolean score1_get = false, score2_get = false;

    public ClsOscilloscope(int recBufSize, int frequency, SurfaceView sfv, SelectObject UserSelection, Context context, CheckBox checkResult, SeekBar barScale) {
        this.recBufSize = recBufSize;
        this.frequency = frequency;
        this.sfv = sfv;
        this.UserSelection = UserSelection;
        this.mCtx = context;
        this.checkResult = checkResult;
        this.barScale = barScale;
    }

    public void setUserSelection(SelectObject us){
        this.UserSelection = us;
    }

    public void Start(AudioRecord audioRecord) {
        /*按下開始則建構Record，並開始錄音執行緒*/
        //isrecording = true;
        record = new Record(audioRecord, recBufSize, frequency);
        record.start();
    }

    public boolean Stop() {
        /*按下停止，以判斷式停止while，interrupt()API24以上會炸*/
        //isrecording = false;

        score1_get = false;
        score2_get = false;
        loading = true;
        if(Cx.size()!=0){
            Cx.clear();
            Real.clear();
            Imag.clear();
        }
        plot = new Plot(sfv);
        plot.loading();
        sleep();
        /*讀檔*/
        load = new Load(recBufSize);
        load.start();
        WAV_double_value = load.getWAV_value();
        /*休眠避免衝突*/
        if(Load.WAV_size > 13000) {
            sleep();
        /*前處理*/
            preprocedure = new Preprocedure(WAV_double_value);
            preprocedure.start(UserSelection.getType());
            Frame = preprocedure.getFrame();
            Nosie = preprocedure.getNosie();
            if (Frame.size() >= 30) {
        /*休眠避免衝突*/
                sleep();
        /*音高處理*/
                pitchprocedure = new PitchProcedure(Frame);
                pitchprocedure.start();
                pitch_array = pitchprocedure.getpitch();
        /*休眠避免衝突*/
                sleep();
        /*畫音高圖(Debug用)*/
                //plot = new Plot(sfv,pitch_array);
                //plot.draw(pitch_array);
        /*處理咬字相關資料*/
                DFT dft = new DFT();
                MEL mel = new MEL();
                for (int i = 0; i < Frame.size(); i++) {
                    dft.calculate(Frame.get(i));
                    Real.add(dft.getreal());
                    Imag.add(dft.getimag());
                }
                mel.Filter_Point();
                mel.calculate();
                for (int i = 0; i < Frame.size(); i++) {
                    Cx.add(mel.cepstrum(Real.get(i), Imag.get(i)));
                }
                sleep();
                //-----------------------評分-----------------------


                score1 = 0;
                score2 = 0;
                //音高評分
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String tmpPitch;
                        double[] samplePitch;
                        for (int i = 1; i < sampleNumber + 1; i++) {
                            tmpPitch = readPitch(UserSelection.getChapter(), UserSelection.getArticle(),
                                    UserSelection.getType(), UserSelection.getChooseNum(), i);
                            samplePitch = convertStringtoPitch(tmpPitch);

                            PitchCompare c = new PitchCompare(2, 3);
                            c.improtData(samplePitch, pitch_array);
                            c.doCompare();
                            if (c.getScore() >= score1) {
                                score1 = c.getScore();
                                PitchSimilar = c.getSimilarPart();
                                draw_samplePitch = samplePitch;
                                drawTestPitch = c.getTestAudio().clone();
                            }
                        }
                        Message message_score1 = handler.obtainMessage(1,score1);
                        handler.sendMessage(message_score1);
                    }
                }).start();

                //咬字評分
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<double[]> sampleVector;
                        String tmpVector = "";
                        for (int i = 1; i < sampleNumber + 1; i++) {
                            tmpVector = readVector(UserSelection.getChapter(), UserSelection.getArticle(),
                                    UserSelection.getType(), UserSelection.getChooseNum(), i);
                            sampleVector = convertStringtoVector(tmpVector);

                            VectorCompare v = new VectorCompare(2, 3, 12, sampleVector, Cx);
                            v.cutLength();
                            v.fillTable();
                            v.findSimilarPart();
                            if (v.getScore() >= score2) {
                                score2 = v.getScore();
                                VectorSimilar = v.getSimilarPart();
                                draw_sampleVectorlength = sampleVector.size();
                                drawTestVector = (ArrayList<double[]>) v.getTestVector().clone();
                            }
                        }
                        Message message_score2 = handler.obtainMessage(2,score2);
                        handler.sendMessage(message_score2);
                    }
                }).start();

                return false;
            }
            else{
                plot.error();
                //結束ProgressDialog
                MainActivity.stop_loading();
                return true;
            }
        }
        else{
            plot.error();
            //結束ProgressDialog
            MainActivity.stop_loading();
            return true;
        }
    }
    //接收並畫出分數
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //判斷接收到的是音高或咬字的分數
            switch (msg.what){
                case 1:
                    score1 = (int)msg.obj;
                    score1_get = true;
                    break;
                case 2:
                    score2 = (int)msg.obj;
                    score2_get = true;
                    break;
            }
            if(score1_get == true && score2_get == true) {
                //結束ProgressDialog
                MainActivity.stop_loading();
                finalScore = (score1 + score2) / 2;
                Log.d("Score1", String.valueOf(score1));
                Log.d("Score2", String.valueOf(score2));
                Log.d("FinalScore", String.valueOf(finalScore));
                plot.write(score1, score2, finalScore);
            }
        }
    };
    /*全句撥放*/
    public void Play() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setDataSource(AudioFileFunc.getWavFilePath());
        mPlayer.prepare();
        mPlayer.start();

    }
    /*音高部分撥放*/
    public void PlayPitch()  throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
        float start, end;

        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setDataSource(AudioFileFunc.getWavFilePath());
        mPlayer.prepare();

        int t = 0;
        for(int i = PitchSimilar[0].length-1; i >= 0; i--) {
            t = PitchSimilar[0][i];
            if(t != 0)      //避免陣列後段0的部分
                break;
        }
        start = (float)mPlayer.getDuration()*((float)t/(float)pitch_array.length);
        end = (float)mPlayer.getDuration()*((float)PitchSimilar[0][0]/(float)pitch_array.length);
        mPlayer.seekTo((int)start);
        mPlayer.start();
        while(mPlayer.isPlaying()){
            if(mPlayer.getCurrentPosition() == (int)end) {
                mPlayer.stop();
            }
        }
    }
    /*咬字部分撥放*/
    public void PlayVector()  throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        float start, end;

        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setDataSource(AudioFileFunc.getWavFilePath());
        mPlayer.prepare();

        int t = 0;
        for(int i = VectorSimilar[0].length-1; i >= 0; i--) {
            t = VectorSimilar[0][i];
            if(t != 0)      //避免陣列後段0的部分
                break;
        }
        start = (float)mPlayer.getDuration()*((float)t/(float)Cx.size());
        end = (float)mPlayer.getDuration()*((float)VectorSimilar[0][0]/(float)Cx.size());
        mPlayer.seekTo((int)start);
        mPlayer.start();
        while(mPlayer.isPlaying()){
            if(mPlayer.getCurrentPosition() == (int)end) {
                mPlayer.stop();
            }
        }
    }
    /*Debug*/
    public void Show() {
        if(Cx.size()!=0){
            Cx.clear();
            Real.clear();
            Imag.clear();
        }
        plot = new Plot(sfv);
        plot.loading();
        /*讀檔*/
        load = new Load(recBufSize);
        load.start();
        WAV_double_value = load.getWAV_value();

        /*休眠避免衝突*/
        sleep();
        /*前處理*/
        preprocedure = new Preprocedure(WAV_double_value);
        preprocedure.start(UserSelection.getType());
        Frame = preprocedure.getFrame();
        Nosie = preprocedure.getNosie();
        if(Frame.size()>=30) {
        /*休眠避免衝突*/
            sleep();
        /*音高處理*/
            pitchprocedure = new PitchProcedure(Frame);
            pitchprocedure.start();
            pitch_array = pitchprocedure.getpitch();
        /*休眠避免衝突*/
            sleep();
        /*畫音高圖(Debug用)*/
            //plot = new Plot(sfv,pitch_array);
            //plot.draw(pitch_array);
        /*處理咬字相關資料*/
            DFT dft = new DFT();
            MEL mel = new MEL();
            for (int i = 0; i < Frame.size(); i++) {
                dft.calculate(Frame.get(i));
                Real.add(dft.getreal());
                Imag.add(dft.getimag());
            }
            mel.Filter_Point();
            mel.calculate();
            for (int i = 0; i < Frame.size(); i++) {
                Cx.add(mel.cepstrum(Real.get(i), Imag.get(i)));
            }
            sleep();
            //-----------------------評分-----------------------
            String tmpPitch;
            double[] samplePitch;
            ArrayList<double[]> sampleVector;
            String tmpVector = "";
            score1 = 0;score2 = 0;
            //音高評分部分
            for (int i = 1; i < sampleNumber + 1; i++) {
                tmpPitch = readPitch(UserSelection.getChapter(), UserSelection.getArticle(),
                        UserSelection.getType(), UserSelection.getChooseNum(), i);
                samplePitch = convertStringtoPitch(tmpPitch);

                PitchCompare c = new PitchCompare(2, 3);
                c.improtData(samplePitch, pitch_array);
                c.doCompare();
                if(c.getScore() >= score1) {
                    score1 = c.getScore();
                    PitchSimilar = c.getSimilarPart();
                    draw_samplePitch = samplePitch;
                    drawTestPitch = c.getTestAudio().clone();
                }
            }
            sleep();
            //咬字評分
            for (int i = 1; i < sampleNumber + 1; i++) {
                tmpVector = readVector(UserSelection.getChapter(), UserSelection.getArticle(),
                        UserSelection.getType(), UserSelection.getChooseNum(), i);
                sampleVector = convertStringtoVector(tmpVector);

                VectorCompare v = new VectorCompare(2, 3, 12, sampleVector, Cx);
                v.cutLength();
                v.fillTable();
                v.findSimilarPart();
                if(v.getScore() >= score2) {
                    score2 = v.getScore();
                    VectorSimilar = v.getSimilarPart();
                    draw_sampleVectorlength = sampleVector.size();
                    drawTestVector = (ArrayList<double[]>) v.getTestVector().clone();
                }
            }
            sleep();
            //計分
            finalScore = (score1 + score2)/ 2;
            Log.d("Score1", String.valueOf(score1));
            Log.d("Score2", String.valueOf(score2));
            Log.d("FinalScore", String.valueOf(finalScore));
            plot.write(score1, score2, finalScore);

        }
        else{
            plot.error();
        }
    }
    /*分數及畫圖切換*/
    public void scene(int mode){
        if(mode == 1){
            Canvas canvas = sfv.getHolder().lockCanvas(new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
            canvas.drawColor(Color.BLACK);
            plot = new Plot(sfv);
            plot.drawPitch(PitchSimilar,draw_samplePitch,drawTestPitch,Nosie,canvas,barScale.getProgress()+1);
            plot.drawVector(VectorSimilar,draw_sampleVectorlength,draw_sampleVectorlength,canvas,barScale.getProgress()+1);
            sfv.getHolder().unlockCanvasAndPost(canvas);
        }
        else if(mode == 2){
            finalScore = (score1 + score2)/ 2;
            Log.d("Score1", String.valueOf(score1));
            Log.d("Score2", String.valueOf(score2));
            Log.d("FinalScore", String.valueOf(finalScore));
            plot = new Plot(sfv);
            plot.write(score1, score2, finalScore);
        }
    }

    private void sleep(){
        try{
            Thread.sleep(800);
        }catch(Exception e){
            System.out.println("休眠終止");
        }
    }
    /*將音高字串切成陣列*/
    private double[] convertStringtoPitch(String data){
        int count = 0;
        for(int i=0; i<data.length(); i++){
            if(data.charAt(i) == ',')
                count++;
        }
        double[] pitch = new double[count];
        int indexForPitch = 0;
        String tempDouble = "";
        for(int i=0; i<data.length(); i++){
            if(data.charAt(i) != ',' ){
                tempDouble += data.charAt(i);
            }
            else
            {
                pitch[indexForPitch] = Double.parseDouble(tempDouble);
                indexForPitch++;
                tempDouble = "";
            }

        }
        return pitch;
    }
    /*讀音高*/
    private String readPitch(int chapter, int arcticle, String type, int num, int count){
        String path;
        if(type.equals("Word"))
            path = "ch"+chapter+"_article"+arcticle+"_pitch_word"+num+"_"+count;
        else {
            path = "ch"+chapter+"_article"+arcticle+"_pitch_sentence"+num+"_"+count;
        }
        String mReadText = "";
        InputStream ins = mCtx.getResources().openRawResource(mCtx.getResources().getIdentifier(path,"raw", mCtx.getPackageName()));
        BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(ins));

        try
        {

            mReadText = "";
            String mTextLine = mBufferedReader.readLine();

            //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出
            while (mTextLine!=null)
            {
                mReadText += mTextLine;
                mTextLine = mBufferedReader.readLine();
            }

        }
        catch(Exception e)
        {
            System.out.print("goddamn");
        }
        return mReadText;
    }
    /*將咬字字串切成陣列*/
    private ArrayList convertStringtoVector(String data){
        ArrayList<double[]> vector = new ArrayList<double[]>();;
        double[] tmpForVector = new double[12];

        int indexForVector = 0;

        String tempDouble = "";
        for(int i=0; i<data.length(); i++){
            if(data.charAt(i) != ',' ){
                tempDouble += data.charAt(i);
            }
            else
            {
                tmpForVector[indexForVector] = Double.parseDouble(tempDouble);
                indexForVector++;
                tempDouble = "";
            }
            if(indexForVector == 12){
                vector.add(tmpForVector);
                indexForVector = 0;
                tmpForVector = new double[12];
            }

        }
        return vector;
    }
    /*讀咬字*/
    private String readVector(int chapter, int arcticle, String type, int num, int count){
        String path;
        if(type.equals("Word"))
            path = "ch"+chapter+"_article"+arcticle+"_vector_word"+num+"_"+count;
        else {
            path = "ch"+chapter+"_article"+arcticle+"_vector_sentence"+num+"_"+count;
        }
        String mReadText = "";
        InputStream ins = mCtx.getResources().openRawResource(mCtx.getResources().getIdentifier(path,"raw", mCtx.getPackageName()));
        BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(ins));
        try
        {
            mReadText = "";
            String mTextLine = mBufferedReader.readLine();

            //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出
            while (mTextLine!=null)
            {
                mReadText += mTextLine;
                mTextLine = mBufferedReader.readLine();
            }

        }
        catch(Exception e)
        {
            System.out.print("goddamn");
        }
        return mReadText;
    }
}
