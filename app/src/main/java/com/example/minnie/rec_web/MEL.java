package com.example.minnie.rec_web;

/**
 * Created by Sorry on 2017/4/28.
 */

public class MEL {

    private static double minFreq = 0; //最低頻率
    private static double maxFreq = 8000;//最高頻率
    private static int Filter_num = 12;//頻帶數量
    private int[] Table = new int[256];//各點所屬中心頻帶
    public double[] melFilterPoints;
    public double[] outcome;
    public double[] result;
    public double[] Cx;
    public double minMel = hz2mel(minFreq);
    public double maxMel = hz2mel(maxFreq);

    void Filter_Point(/*double min, double max*/){ //切成12個頻帶
        double count = (maxMel - minMel) / (Filter_num + 1);
        double temp = minMel;
        melFilterPoints = new double[Filter_num + 2];
        melFilterPoints[0] = minMel;
        melFilterPoints[Filter_num + 1] = maxMel;
        for(int i = 1; i < Filter_num + 1; i++){
            melFilterPoints[i] = temp + count;
            temp = temp + count;
        }
    }

    void calculate (){
        for(int i = 0; i < 256; i++){
            for(int k = 0; k < Filter_num; k++){
                if((melFilterPoints[Filter_num + 1] / 256 * i <= melFilterPoints[k + 2]) && (melFilterPoints[Filter_num + 1] / 256 * i >= melFilterPoints[k])){
                    Table[i] = k + 1;
                    break;
                }
            }
        }
    }

    public double[] cepstrum(double real[], double imag[]){//做完傅立葉的所有值 通過三角濾波器
        outcome = new double[256]; //存平方之後的結果
        result = new double[Filter_num]; //存12個特徵數
        Cx = new double[Filter_num];//DCT

        for(int i = 0; i < 256; i++){
            outcome[i] = Math.pow(real[i], 2) + Math.pow(imag[i], 2);
        }
        int temp = 0;
        int count = 0;
        for(int i = 0; i < 256; i++){
            if(melFilterPoints[Filter_num + 1] / 256 * i <= melFilterPoints[temp + 2] && melFilterPoints[Filter_num + 1] / 256 * i >= melFilterPoints[temp]){
                if(melFilterPoints[Filter_num + 1] / 256 * i < melFilterPoints[temp + 1]){
                    result[temp] += outcome[i] * (melFilterPoints[Filter_num + 1] / 256 * i - melFilterPoints[temp]) / (melFilterPoints[temp + 1] - melFilterPoints[temp]);
                }
                else{
                    result[temp] += outcome[i] * (melFilterPoints[temp + 2] - melFilterPoints[Filter_num + 1] / 256 * i) / (melFilterPoints[temp + 2] - melFilterPoints[temp + 1]);
                }
                if(i < 255 && Table[i] != Table[i + 1] && count != i + 1){
                    count = i + 1;
                    i -= 20;
                    temp++;
                }
            }
        }
        for(int j = 0; j < Filter_num; j++){//j跑外面12個, i跑裡面的迴圈
            result[j] = Math.log(result[j]);
        }
        for(int j = 0; j < Filter_num; j++){//DCT
            for(int k = 0; k < Filter_num; k++){
                Cx[j] += result[k] * Math.cos(Math.PI * j * (k - 0.5) / (Filter_num));
            }
            Cx[j] = Cx[j] / (Filter_num);
        }
        return Cx;
    }

    public double hz2mel(double freq){
        return 1125 * Math.log(1 + freq / 700);
    }

}