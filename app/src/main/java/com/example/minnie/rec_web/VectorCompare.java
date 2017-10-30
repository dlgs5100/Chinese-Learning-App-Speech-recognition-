package com.example.minnie.rec_web;

/**
 * Created by Sorry on 2017/4/28.
 */

import java.util.ArrayList;

public class VectorCompare {
    private int gapPoint;
    private int matchAndMisMatchPoint;
    private int vectorNum;

    private ArrayList<double[]> sampleVector;
    private ArrayList<double[]> testVector;
    private ArrayList<double[]> importVector;
    private double[][] pointTable;
    private int[][] trackTable;

    private int[][] similarPart;
    private int similarLength = 0;

    int bigI,  bigJ;
    double biggestPoint = 0;

    public VectorCompare(int gapPoint, int matchAndMisMatchPoint, int vectorNum, ArrayList<double[]> sampleVector, ArrayList<double[]> testVector) {
        this.gapPoint = gapPoint;
        this.matchAndMisMatchPoint = matchAndMisMatchPoint;
        this.vectorNum = vectorNum;
        this.sampleVector = new ArrayList<double[]>();
        this.importVector = new ArrayList<double[]>();
        this.sampleVector.addAll(sampleVector);
        this.importVector.addAll(testVector);
        pointTable = new double[sampleVector.size()+1][sampleVector.size()+1];
        trackTable = new int[sampleVector.size()+1][sampleVector.size()+1];

        similarPart = new int[2][sampleVector.size()*2];

    }

    public void cutLength() {
        if (importVector.size() > sampleVector.size()) {
            testVector = new ArrayList<double[]>();
            double tmp;
            for (int i = 0; i < importVector.size(); i++) {
                tmp = (double) i / (importVector.size() - 1) * (sampleVector.size() - 1);
                testVector.add(importVector.get(i));
            }
            for(int i=testVector.size()-1; i>=sampleVector.size(); i--)
                testVector.remove(i);
        } else if (importVector.size() < sampleVector.size()) {
            testVector = new ArrayList<double[]>();
            double tmp;
            for (int i = 0; i < sampleVector.size(); i++) {
                tmp = (double) i / (sampleVector.size() - 1) * (importVector.size() - 1);
                testVector.add(importVector.get((int) Math.round(tmp)));
            }

        }
        else
            testVector = (ArrayList<double[]>) importVector.clone();
        int i = 1;
    }

    public int[][] getSimilarPart() {
        return similarPart;
    }

    public int getSimilarLength(){
        return similarLength;
    }

    public double getBiggestPoint() {
        return biggestPoint;
    }

    public int getScore(){
        double tmp;
        tmp = ((double)similarLength / (double)sampleVector.size()) * 100;
        return (int)tmp;
    }

    public ArrayList getTestVector(){
        return testVector;
    }

    public void fillTable() {

        double tempScore = 0;

		/*
		 0 -> 表分數從左上方來
		 1 -> 表分數從左邊來
		 2 -> 表分數從上方來
		 3 -> 表分數從上&左上來
		 4 -> 表分數從左&左上來
		 5 -> 表分數從左&上來
		 6 -> 表分數從左&上&左上來
 		*/
        for(int i = 0; i< testVector.size()+1; i++){
            for(int j = 0; j < sampleVector.size()+1; j++){
                if(i == 0 | j == 0){
                    pointTable[i][j] = 0;
                    trackTable[i][j] = 0;
                }
                else{
                    //先算左上方分數
                    if(euclideanDistance(sampleVector.get(j-1), testVector.get(i-1))>2.5){
                        tempScore = pointTable[i-1][j-1] - matchAndMisMatchPoint;
                        trackTable[i][j] = 0;
                    } else{
                        tempScore = pointTable[i-1][j-1] + matchAndMisMatchPoint;
                        trackTable[i][j] = 0;
                    }
                    //算左邊分數
                    if(pointTable[i-1][j] - gapPoint >= tempScore){
                        if(pointTable[i-1][j] - gapPoint == tempScore){
                            trackTable[i][j] = 4;//來源為左&左上
                        } else{
                            trackTable[i][j] = 1;//來源為左
                        }
                        tempScore = pointTable[i-1][j] - gapPoint;
                    }
                    //算上方分數
                    if(pointTable[i][j-1] - gapPoint >= tempScore){
                        if(pointTable[i][j-1] - gapPoint == tempScore){
                            if(trackTable[i][j] == 4)//來源為三方
                                trackTable[i][j] = 6;
                            else if(trackTable[i][j] == 2)//來源為上&左
                                trackTable[i][j] = 5;
                            else//來源為上&左上
                                trackTable[i][j] = 3;
                        } else{
                            trackTable[i][j] = 2;
                        }
                        tempScore = pointTable[i][j-1] - gapPoint;
                    }

                    if(tempScore < 0)
                        tempScore = 0;
                    pointTable[i][j] = tempScore;

                }
                if(pointTable[i][j]>biggestPoint){
                    biggestPoint = pointTable[i][j];
                    bigI = i;
                    bigJ = j;
                }
				/*--------------------------*
                System.out.print(pointTable[i][j]);
                System.out.print(',');
				/*--------------------------*/
            }
            if(pointTable[i][sampleVector.size()]>biggestPoint){
                biggestPoint = pointTable[i][sampleVector.size()];
                bigI = i;
                bigJ = sampleVector.size();
            }
			/*-----------------*
            System.out.println("\n");
			/*-----------------*/
        }
    }

    public double euclideanDistance(double[] sample, double[] test) {
        double tmp = 0;

        for (int i = 0; i < vectorNum; i++) {
            tmp += Math.pow(sample[i] - test[i], 2);
            if (i < vectorNum - 1)
                tmp += Math.pow((sample[i + 1] - sample[i]) - (test[i + 1] - test[i]), 2);
            if (i == vectorNum - 1)
                tmp += Math.pow((sample[vectorNum - 1] - sample[1]) - (test[vectorNum - 1] - test[1]), 2);
        }
        tmp = Math.sqrt(tmp);
        return tmp;
    }


    public void findSimilarPart(){

		/*
			 i = 0 存放test之相似項數
			 i = 1存放sample相似項數
		*/
		/*
			 op = 1 test add gap
			 op = 2 sample add gap
		*/

        int i = bigI;
        int j = bigJ;
        double biggest = biggestPoint;
        int tempIndex = 0;
        int op = 0;

        while(biggest>=0){
            if(pointTable[i][j] == 0)
                break;

            similarPart[0][tempIndex] = i-1;//因起始矩陣多出一空行填0，故須減一以符合陣列編號
            similarPart[1][tempIndex] = j-1;//同上

            tempIndex++;

			/*
			 查詢分數來源方向，單一方向及多重方向分開處理
			*/
            if(trackTable[i][j] < 3)
                op = trackTable[i][j];
            else
                op = backTrackDetermine(i, j);

            switch (op) {
                case 0:
                    i--;
                    j--;
                    similarLength++;
                    break;
                case 1:
                    i--;
                    break;
                case 2:
                    j--;
                    break;
            }

        }
    }

    private int backTrackDetermine(int i, int j){
        int op = 0;
		/*
		 op = 0 ->往左上
		 op = 1 ->往上
		 op = 2 ->往左
		*/


		/*
		 查表之後以查到的值分別作處理
		*/
        switch (trackTable[i][j]) {
            case 3: //上&斜上
                if(getSurroundPoint(i-1, j) > getSurroundPoint(i-1, j-1))
                    op = 1;
                else if(getSurroundPoint(i-1, j) == getSurroundPoint(i-1, j-1))
                    op = 0;
                else
                    op = 0;
                break;

            case 4: //左&斜上
                if(getSurroundPoint(i, j-1) > getSurroundPoint(i-1, j-1))
                    op = 2;
                else if(getSurroundPoint(i, j-1) == getSurroundPoint(i-1, j-1))
                    op = 0;
                else
                    op = 0;
                break;

            case 5: //左&上
                if(getSurroundPoint(i, j-1) > getSurroundPoint(i-1, j))
                    op = 2;
                else if(getSurroundPoint(i, j-1) == getSurroundPoint(i-1, j))
                    op = 1;
                else
                    op = 1;
                break;

            case 6: //左&上&左上
                if(getSurroundPoint(i-1, j-1) >= getSurroundPoint(i-1, j))
                    op = 0;
                else
                    op = 1;
                if(op == 1){
                    if(getSurroundPoint(i, j-1) > getSurroundPoint(i-1, j))
                        op = 2;
                }
                else if(op == 0){
                    if(getSurroundPoint(i, j-1) > getSurroundPoint(i-1, j-1))
                        op = 2;
                }
                break;

            default:
                break;
        }

        return op;
    }

    private double getSurroundPoint(int i, int j){
        return pointTable[i-1][j] + pointTable[i-1][j-1] + pointTable[i][j-1];
    }
}

