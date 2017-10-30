package com.example.minnie.rec_web;

/**
 * Created by Sorry on 2017/4/28.
 */

public class DFT {

    private double real[], imag[]; //real存實數 imag存虛數

    void calculate(double[] frame){ //一次傳一個音框
        real = new double[frame.length];
        imag = new double[frame.length];
        for(int k = 0; k < frame.length; k++){
            for(int t = 0; t < frame.length; t++){
                double angle = (2 * Math.PI * t * k) / frame.length; //n是點數量
                real[k] += frame[t] * Math.cos(angle);
                imag[k] += frame[t] * -Math.sin(angle);
            }
        }
    }

    public double[] getreal(){
        return real;
    }

    public double[] getimag(){
        return imag;
    }

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*DFT d = new DFT();
		double[] frame = {1, 2, 3, 4};
		double[] real, imag;
		d.calculate(frame);
		real = d.getreal();
		imag = d.getimag();
		for(int k = 0; k < real.length; k++){
			System.out.println(real[k]);
			System.out.println(imag[k]);
		}
		MEL m = new MEL();
		m.display();
	}*/
}
