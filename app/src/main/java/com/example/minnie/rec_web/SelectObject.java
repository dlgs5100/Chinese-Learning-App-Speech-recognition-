package com.example.minnie.rec_web;

import java.io.Serializable;

public class SelectObject implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private int chapter;
    private int article;
    private String type;
    private String exam_sentence;
    private int chooseNum;
    public SelectObject(int chapter, int article, String type, String exam_sentence, int chooseNum){
        this.chapter = chapter;
        this.article = article;
        this.type = type;
        this.exam_sentence = exam_sentence;
        this.chooseNum = chooseNum;
    }
    public void setChooseNum(int num){chooseNum = num;}
    public int getChooseNum(){return chooseNum;}
    public int getChapter(){
        return chapter;
    }
    public int getArticle(){
        return article;
    }
    public String getType(){
        return type;
    }
    public String getSentence(){
        return exam_sentence;
    }
}
