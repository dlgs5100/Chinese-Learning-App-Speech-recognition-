package com.example.minnie.rec_web;

import android.util.Log;
import java.util.Arrays;
import java.util.ArrayList;

public class PitchProcedure extends Thread{
    private ArrayList<double[]> Frame = new ArrayList<>();
    private ArrayList<Double> pitch = new ArrayList<>();
    public PitchProcedure(ArrayList Frame) {
        this.Frame = Frame;
    }
    public void start(){
        ACF_div_AMDF();
        smooth();
    }
    void ACF_div_AMDF(){
        int r, loc = 0;
        double ACF = 0f, AMDF = 0f, e, max = -9999;
        double[] ACFAMDF = new double[256];
        double[] F;
        int i = 0, j;
        while(i < Frame.size()) {
            F = Frame.get(i);
            for(r = 40; r < 256; r++){
                for(j = 0; j <= 256-1-r; j++){
                    ACF += F[j]*F[j+r];
                    AMDF += Math.abs(F[j]-F[j+r]);
                }
                //ACF/=256-r;
                AMDF/=256-r;
                e = ACF/(AMDF+1);
                ACFAMDF[r-1] = e;

                //Log.d(Integer.toString(i)+"ACF/AMDF", Double.toString(e));
                if(e > max) {
                    max = e;
                    loc = r;
                }
                ACF = 0;
                AMDF = 0;
            }

            //System.out.println("週期:"+Double.toString(loc));
            double ff = 8000.f/(double)loc;
            double pitch_value = 69+12*(Math.log(ff/440)/Math.log(2));
            pitch.add(pitch_value);
            //Log.d("音高", Double.toString(ff));
            //DrawACFAMDF(0, ACFAMDF, 15, baseLine);
            /*try {
                Thread.sleep(100000);
            }
            catch(Exception ex){
                Log.d("123", "123");
            }*/
            max = 0;
            i++;
        }
    }
    void smooth(){

        int i, j, k = 0, amount = 2;
        double[] filter = new double[amount*2+1];
        double[] pitch_array = getpitch();
        for(i=amount; i<pitch.size()-amount; i++){
            for(j=i-amount; j<=i+amount; j++){
                filter[k] = pitch_array[j];
                k++;
            }
            Arrays.sort(filter);
            pitch.set(i,filter[amount]);
            k=0;
        }
        /*for(i = 0; i<pitch.size(); i++){
            Log.d("音高", String.valueOf(pitch.get(i)));
        }*/
    }
    double[] getpitch(){
        double[] pitch_array = new double[pitch.size()];
        for(int i=0; i<pitch.size(); i++){
            pitch_array[i] = pitch.get(i);
        }
        return pitch_array;
    }
    int getpitch_length(){
        return pitch.size();
    }
}
