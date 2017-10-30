package com.example.minnie.rec_web;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Preprocedure extends Thread{
    private double[] WAV_double_value;
    private ArrayList<double[]> Frame = new ArrayList<>();
    private ArrayList<Double> Energy = new ArrayList<>();
    private ArrayList<Double> Zero = new ArrayList<>();
    private ArrayList<Integer> Noise = new ArrayList<>();
    int i, j, k;

    public Preprocedure(double[] WAV_double_value){
        this.WAV_double_value = WAV_double_value;
    }
    public void start(String type){

        Frame.clear();
        Energy.clear();
        Zero.clear();
        Frame();
        //Preemphasis();
        End_Point_Detection(type);
        //Hanmming();

        /*執行緒停止*/
        Preprocedure.interrupted();
    }
    /*音框化*/
    void Frame(){
        i = 0;
        while(i < WAV_double_value.length) {
            double[] temp = new double[256];
            /*256個點為一個音框*/
            for(j = 0 ; j < 256; j++) {
                temp[j] = WAV_double_value[i];
                i++;
                if(i == WAV_double_value.length)
                    break;
            }
            Frame.add(temp);
            /*重疊128個點*/
            if(i != WAV_double_value.length)
                i-=128;
        }
    }
    /*預強調*/
    void Preemphasis(){
        double[] F;
        for(i=0; i<Frame.size(); i++){
            F = Frame.get(i);
            for(j=1; j<F.length; j++){
                F[j] = F[j] - 0.95*F[j-1];
            }
        }
    }
    /*端點偵測*/
    void End_Point_Detection(String type){
        i = 0;
        int count = 0;
        boolean Sensor = true;
        double E = 0f, total_E = 0, average_Z = 0, SD_E = 0, SD_Z = 0, T_EL, T_Z;
        double[] F;
        /*--------------------*/
        System.out.println("端點偵測前:"+Integer.toString(Frame.size()));
        /*--------------------*/
        while(i < Frame.size()){
            F = Frame.get(i);
            j = 0;
            while(j < F.length){
                /*能量*/
                E += F[j]*F[j];
                /*過零率*/
                if(j == 0){
                    if(F[j] > 0)            //過零率表通過0的次數，用sensor來判斷
                        Sensor = true;
                    else
                        Sensor = false;
                }
                else if(F[j] > 0 && Sensor == false) {
                    Sensor = true;
                    count++;
                }
                else if(F[j] < 0 && Sensor == true) {
                    Sensor = false;
                    count++;
                }
                j++;
            }
            E = Math.log10(E);
            /*若能量小於負值，取Log會導致負無窮大*/
            if(E<0)
                E=0;
            /*--------------------*/
            //Log.d(Integer.toString(i)+"音框能量", Double.toString(E));
            //Log.d(Integer.toString(i)+"音框過零率", Double.toString((double)count/256));
            /*--------------------*/
            /*能量總值*/
            total_E += E;
            Energy.add(E);
            /*能量平均值*/
            average_Z += (double)count/256;
            Zero.add((double)count/256);

            E = 0;
            count = 0;
            i++;
        }

        /*前N噪音框*/
        final int noise = 10;
        /*前N噪音框的平均值*/
        total_E /= noise;
        average_Z /= noise;
        /*前N噪音框的變異值*/
        for(i = 0; i<noise; i++){
            SD_E += (Energy.get(i)-total_E)*(Energy.get(i)-total_E);
            SD_Z += (Zero.get(i)-average_Z)*(Zero.get(i)-average_Z);
        }
        SD_E /= noise;
        SD_Z /= noise;
        SD_E = Math.sqrt(SD_E);
        SD_Z = Math.sqrt(SD_Z);
        /*透過係數找出門檻值*/
        /*
        if(Load.WAV_size <= 45440)
            T_EL = total_E+(-0.974*SD_E);
        else if(Load.WAV_size > 45440 && Load.WAV_size <= 57600)
            T_EL = total_E+(-0.97*SD_E);
        else if(Load.WAV_size > 57600 && Load.WAV_size <= 69760)
            T_EL = total_E+(-0.9816*SD_E);
         */
        if(type.equals("Sentence")) {
            if (Load.WAV_size <= 130000)
                T_EL = total_E + (-0.987 * SD_E);   //-0.99
            else
                T_EL = total_E + (-0.993 * SD_E);
            T_Z = average_Z+(-0.985*SD_Z);              //alpha3?-0.984,-0.990,-0.984,-0.981,-0.981
        }
        else {
            if (Load.WAV_size <= 45440)
                T_EL = total_E + (-0.974 * SD_E);
            else if (Load.WAV_size > 45440 && Load.WAV_size <= 57600)
                T_EL = total_E + (-0.97 * SD_E);
            else if (Load.WAV_size > 57600 && Load.WAV_size <= 69760)
                T_EL = total_E + (-0.9816 * SD_E);           //alpha1?-0.965,-0.963,-0.963,-0.962,-0.975
            else
                T_EL = total_E + (-0.9816 * SD_E);
            T_Z = average_Z+(-0.98*SD_Z);              //alpha3?-0.984,-0.990,-0.984,-0.981,-0.981
        }

        /*--------------------*/
        Log.d("能量門檻", Double.toString(T_EL));
        Log.d("過零率門檻", Double.toString(T_Z));
        /*--------------------*/

        int start = -1, end = -1;
        for(i=0; i<Frame.size(); i++){
            /*找雜音起始點(能量小於能量門檻且未設起始點)*/
            if((Energy.get(i) < T_EL && Zero.get(i) > T_Z && start == -1) || i == 0)
                start = i;
            /*找雜音終止點(能量大於能量門檻，過零率小於過零門檻)*/
            if((Energy.get(i) >= T_EL && Zero.get(i) <= T_Z && start != -1) || i == Frame.size()-1)
                end = i;
            //System.out.println(start+","+end);
            if (start != -1 && end != -1) {
                Log.d(String.valueOf(start),String.valueOf(end));
                for (k = 0; k < end - start/ + 1; k++) { // 前後都要算
                    Frame.remove(start);
                    Energy.remove(start);
                    Zero.remove(start);
                }
                Noise.add(start);
                i = start;
                start = -1;
                end = -1;
            }
        }
        /*--------------------*/
        System.out.println("端點偵測後:"+Integer.toString(Frame.size()));
        /*--------------------*/
    }
    /*漢明窗*/
    void Hanmming(){
        i = 0;
        double[] F;
        while(i < Frame.size()) {
            F = Frame.get(i);
            j = 0;
            while (j < F.length) {
                F[j] = F[j] * (0.54-(0.46*Math.cos(2*Math.PI*j/255f)));
                j++;
            }
            i++;
        }
    }
    /*取得音框ArrayList*/
    ArrayList getFrame(){
        return Frame;
    }
    ArrayList getNosie(){
        return Noise;
    }
}
