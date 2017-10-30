package com.example.minnie.rec_web;

/**
 * Created by Sorry on 2017/4/28.
 */

import java.util.Arrays;

public class PitchCompare {
    private int testAudioLength;// 欲比較之長度
    private int sampleAudioLength;
    private int gapPoint;
    private int matchAndMisMatchPoint;
    private double[] sampleAudio;
    private double[] testAudio;
    private double[] importTestAudio;

    private int[][] pointTable;
    private int[][] trackTable;

    private int pitchFlag;      //true: test higher than sample  false: test lower than sample

    /*
     * 0 -> 表分數從斜上方來 1 -> 表分數從左邊來 2 -> 表分數從上方來 3 -> 表分數從上&斜上來 4 -> 表分數從左&斜上來 5
     * -> 表分數從左&上來 6 -> 表分數從左&上&斜上來
     */
    private int[][] similarPart;
    private int similarLength = 0;
	/*
	 * i = 0 存放test之相似項數 i = 1存放sample相似項數
	 */

    int bigI, bigJ;
    int biggestPoint = 0;

    public PitchCompare(int gapPoint, int matchAndMisMatchPoint) {
        this.gapPoint = gapPoint;
        this.matchAndMisMatchPoint = matchAndMisMatchPoint;

    }

    public void doCompare(){
        int[][] tmp = similarPart.clone();
        double cost = 1.3;
        int lengthTmp=0;
        averageCompare();
        cutLength();
        if(pitchFlag == 1){
            for(double d = -cost*4; d<=cost*4; d+=cost){
                fillTable(d);
                findSimilarPart();
                if(similarLength>lengthTmp){
                    lengthTmp = similarLength;
                    tmp = similarPart.clone();
                }
                initialSimilar();
            }
        }
        else if(pitchFlag==2){
            for(double d = 0; d<=cost*8; d+=cost){
                fillTable(d);
                findSimilarPart();
                if(similarLength>lengthTmp){
                    lengthTmp = similarLength;
                    tmp = similarPart.clone();
                }
                initialSimilar();
            }
        }
        else if(pitchFlag==3){
            for(double d = 0; d>=-(cost*8); d-=cost){
                fillTable(d);
                findSimilarPart();
                if(similarLength>lengthTmp){
                    lengthTmp = similarLength;
                    tmp = similarPart.clone();
                }
                initialSimilar();
            }
        }
        similarLength = lengthTmp;
        similarPart = tmp.clone();
    }

    public void cutLength() {
        if (testAudioLength > sampleAudioLength) {
            testAudio = new double[sampleAudioLength];
            double tmp;
            for (int i = 0; i < testAudioLength; i++) {
                tmp = (double) i / (testAudioLength - 1) * (sampleAudioLength - 1);
                testAudio[(int) Math.round(tmp)] = importTestAudio[i];
            }
        } else if (testAudioLength < sampleAudioLength) {
            testAudio = new double[sampleAudioLength];
            double tmp;
            for (int i = 0; i < sampleAudioLength; i++) {
                tmp = (double) i / (sampleAudioLength - 1) * (testAudioLength - 1);
                testAudio[i] = importTestAudio[(int) Math.round(tmp)];
            }

        }
        else
            testAudio = importTestAudio.clone();
        testAudioLength = sampleAudioLength;
        int i = 1;
    }

    public void improtData(double[] sampleAudio, double[] testAudio) {
        testAudioLength = testAudio.length;
        sampleAudioLength = sampleAudio.length;

        this.sampleAudio = new double[sampleAudioLength]; //table x-axis
        this.importTestAudio = new double[testAudioLength]; //table y-axis

        this.sampleAudio = Arrays.copyOf(sampleAudio,sampleAudioLength);
        this.importTestAudio = Arrays.copyOf(testAudio,testAudioLength);

        initialSimilar();

        pointTable = new int[sampleAudioLength+1][sampleAudioLength+1];
        trackTable = new int[sampleAudioLength+1][sampleAudioLength+1];

    }

    private void initialSimilar(){
        if(testAudioLength>=sampleAudioLength)
            similarPart = new int[2][testAudioLength*2];
        else
            similarPart = new int[2][sampleAudioLength*2];
        similarLength = 0;
    }

    public void standardization() {
        int sampleAudioPitch = 0, testAudioPitch = 0;
        int count = 0;
        while (true) {
            if (count < sampleAudioLength)
                sampleAudioPitch += sampleAudio[count];
            if (count < testAudioLength)
                testAudioPitch += testAudio[count];
            if (count >= testAudioLength && count >= sampleAudioLength)
                break;
            count++;
        }
        sampleAudioPitch /= sampleAudioLength;
        testAudioPitch /= testAudioLength;

        count = sampleAudioPitch - testAudioPitch;

        for (int i = 0; i < testAudioLength; i++) {
            testAudio[i] += count;
        }
    }

    public void averageCompare(){
        int sampleAudioPitch = 0, testAudioPitch = 0;
        int count = 0;
        while (true) {
            if (count < sampleAudioLength)
                sampleAudioPitch += sampleAudio[count];
            if (count < testAudioLength)
                testAudioPitch += importTestAudio[count];
            if (count >= testAudioLength && count >= sampleAudioLength)
                break;
            count++;
        }
        sampleAudioPitch /= sampleAudioLength;
        testAudioPitch /= testAudioLength;
        if(sampleAudioPitch>testAudioPitch)
            pitchFlag = 2;
        else if(sampleAudioPitch<testAudioPitch)
            pitchFlag = 3;
        else
            pitchFlag = 1;
    }

    public int getGapPoint() {
        return gapPoint;
    }

    public int getMatchPoint() {
        return matchAndMisMatchPoint;
    }

    public int[][] getSimilarPart() {
        return similarPart;
    }

    public int getSimilarLength() {
        return similarLength;
    }

    public int getBiggestPoint() {
        return biggestPoint;
    }

    public int getScore() {
        double tmp;
        tmp = ((double) similarLength / (double) sampleAudioLength) * 100;
        return (int) tmp;
    }

    public void fillTable(double close) {
        int tempScore = 0;

		/*
		 0 -> 表分數從左上方來
		 1 -> 表分數從左邊來
		 2 -> 表分數從上方來
		 3 -> 表分數從上&左上來
		 4 -> 表分數從左&左上來
		 5 -> 表分數從左&上來
		 6 -> 表分數從左&上&左上來
 		*/
        for(int i = 0; i< testAudioLength+1; i++){
            for(int j = 0; j < sampleAudioLength+1; j++){
                if(i == 0 | j == 0){
                    pointTable[i][j] = 0;
                    trackTable[i][j] = 0;
                }
                else{
                    //先算左上方分數
                    if(Math.abs(testAudio[i-1] + close - sampleAudio[j-1])>1.6){
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
            if(pointTable[i][sampleAudioLength]>biggestPoint){
                biggestPoint = pointTable[i][sampleAudioLength];
                bigI = i;
                bigJ = sampleAudioLength;
            }
			/*-----------------*
            System.out.println("\n");
			/*-----------------*/
        }
    }

    public double[] getTestAudio(){
        return  testAudio;
    }
    public void findSimilarPart() {

		/*
		 * i = 0 存放test之相似項數 i = 1存放sample相似項數
		 */
		/*
		 * op = 1 test add gap op = 2 sample add gap
		 */

        int i = bigI;
        int j = bigJ;
        int biggest = biggestPoint;
        int tempIndex = 0;
        int op = 0;

        while (biggest >= 0) {
            if (pointTable[i][j] == 0)
                break;

            similarPart[0][tempIndex] = i - 1;// 因起始矩陣多出一空行填0，故須減一以符合陣列編號
            similarPart[1][tempIndex] = j - 1;// 同上

            tempIndex++;

			/*
			 * 查詢分數來源方向，單一方向及多重方向分開處理
			 */
            if (trackTable[i][j] < 3)
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

    /*
     * 傳入之係數 i,j 為"當前"之位置
     * 目前決定回推方法為查詢分數來源之方向(trackTable)，若來源為複數則將其來源周圍(左&上&左上)的分數加總決定
     * 大者為優先，若依然相等則優先順序為 斜上>上>左(此順序為暫時，會以實測狀況做調整)
     *
     * 3 -> 表分數從上&左上上來 4 -> 表分數從左&左上上來 5 -> 表分數從左&上來 6 -> 表分數從左&上&左上來
     */
    private int backTrackDetermine(int i, int j) {
        int op = 0;
		/*
		 * op = 0 ->往左上 op = 1 ->往上 op = 2 ->往左
		 */

		/*
		 * 查表之後以查到的值分別作處理
		 */
        switch (trackTable[i][j]) {
            case 3: // 上&斜上
                if (getSurroundPoint(i - 1, j) > getSurroundPoint(i - 1, j - 1))
                    op = 1;
                else if (getSurroundPoint(i - 1, j) == getSurroundPoint(i - 1, j - 1))
                    op = 0;
                else
                    op = 0;
                break;

            case 4: // 左&斜上
                if (getSurroundPoint(i, j - 1) > getSurroundPoint(i - 1, j - 1))
                    op = 2;
                else if (getSurroundPoint(i, j - 1) == getSurroundPoint(i - 1, j - 1))
                    op = 0;
                else
                    op = 0;
                break;

            case 5: // 左&上
                if (getSurroundPoint(i, j - 1) > getSurroundPoint(i - 1, j))
                    op = 2;
                else if (getSurroundPoint(i, j - 1) == getSurroundPoint(i - 1, j))
                    op = 1;
                else
                    op = 1;
                break;

            case 6: // 左&上&左上
                if (getSurroundPoint(i - 1, j - 1) >= getSurroundPoint(i - 1, j))
                    op = 0;
                else
                    op = 1;
                if (op == 1) {
                    if (getSurroundPoint(i, j - 1) > getSurroundPoint(i - 1, j))
                        op = 2;
                } else if (op == 0) {
                    if (getSurroundPoint(i, j - 1) > getSurroundPoint(i - 1, j - 1))
                        op = 2;
                }
                break;

            default:
                break;
        }

        return op;
    }

    /*
     * 用處為取得 i,j 格周圍(左&上&左上)分數之加總
     */
    private int getSurroundPoint(int i, int j) {
        return pointTable[i - 1][j] + pointTable[i - 1][j - 1] + pointTable[i][j - 1];
    }

    public double[] smooth(double[] frame, int t) {
        double[] frameUse2Return = new double[frame.length];

        frameUse2Return[0] = frame[0];
        frameUse2Return[frame.length - 1] = frame[frame.length - 1];
        for (int i = 1; i < frame.length - 1; i++) {
            if (Math.abs(frame[i] - frame[i - 1]) > t & Math.abs(frame[i] - frame[i + 1]) > t) {
                frameUse2Return[i] = (frame[i - 1] + frame[i + 1]) / 2;
            } else
                frameUse2Return[i] = frame[i];
        }
        for (int i = 2; i < frame.length - 2; i++) {
            if (Math.abs(frame[i] - frame[i - 2]) > t & Math.abs(frame[i] - frame[i + 2]) > t) {
                frameUse2Return[i] = (frame[i - 2] + frame[i + 2]) / 2;
            }
        }
        return frameUse2Return;
    }
}
