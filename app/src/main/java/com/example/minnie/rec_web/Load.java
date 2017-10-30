package com.example.minnie.rec_web;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class Load extends Thread{
    private int recBufSize;
    private InputStream Is = null;
    private ByteArrayOutputStream Baos = null;
    private double[] WAV_double_value;
    public static int WAV_size = 0;

    public Load(int recBufSize){
        this.recBufSize = recBufSize;
    }
    public void start(){
        int i, index;
        byte[] data = null;

        try {
            /*讀WAV轉byte array*/
            Is = new FileInputStream(AudioFileFunc.getWavFilePath());
            Baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[512];
            int length = 0;
            int total = 0;
            while((length = Is.read(buffer)) != -1){
                Baos.write(buffer,0,length);
                total += length;
            }
            /*Byte資料Array*/
            data = Baos.toByteArray();
            Is.close();
            Baos.close();
            Baos.flush();

            /*去掉WAV 44標頭*/
            WAV_size = data.length-44;
            /*--------------------*/
            Log.d("讀出 byte 數量", Integer.toString(WAV_size));
            /*--------------------*/
        }
        catch(Exception e){
            Log.d("Error", "Error");
        }

        /*Byte array -> Double array*/
        WAV_double_value = new double[WAV_size/2];
        index = 0;
        for(i=44; i<data.length; i++){
            if(i % 2 == 0) {
                WAV_double_value[index] = (double) getShort(data, i);
                index++;
            }
        }
        /*線性預估*/
        //zero_mean(WAV_double_value);
        /*執行緒停止*/
        Load.interrupted();
    }
    private short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }
    public double[] getWAV_value(){
        return WAV_double_value;
    }
}