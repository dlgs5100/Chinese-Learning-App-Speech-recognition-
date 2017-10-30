package com.example.minnie.rec_web;

import android.util.Log;

public class Data {
    private String[] CH11_words = new String[]{"請選擇題目(Please select subject)...","上學","爸爸","家庭","媽媽","幸福","爬山"};
    private String[] CH11_sentences = new String[]{"請選擇題目(Please select subject)...","我的家是一個三代同堂的家庭","爸爸、媽媽去上班","我和哥哥去上學","星期天，我們不上班，也不上學","全家一起去爬山","我的家庭真幸福"};
    private String[] CH12_words = new String[]{"請選擇題目(Please select subject)...","指教","國立東華大學","籃球","資訊工程學系","喜歡","假日"};
    private String[] CH12_sentences = new String[]{"請選擇題目(Please select subject)...","大家好，我叫王小明","每到假日都會去球場打球","請大家多多指教，謝謝","我喜歡打籃球","現在就讀國立東華大學資訊工程學系","在接下來的日子"};
    private String[] CH21_words = new String[]{"請選擇題目(Please select subject)...","老師","讀書","學校裡","教室","圖書館","人山人海"};
    private String[] CH21_sentences = new String[]{"請選擇題目(Please select subject)...","學校裡到處人山人海","這是我第一次到學校來","再向前看，前面是運動場","旁邊是圖書館","這個學校有一流的老師","真是個讀書的好地方"};
    private String[] CH22_words = new String[]{"請選擇題目(Please select subject)...","早上","程式","下午","清楚","課程","充實"};
    private String[] CH22_sentences = new String[]{"請選擇題目(Please select subject)...","早上上了程式設計課","老師教我們如何寫程式","下午上微積分課","老師教得很清楚","讓我對課程有了充分的了解","真是充實的一天"};
    private String[] CH31_words = new String[]{"請選擇題目(Please select subject)...","流行","百貨公司","上衣","裙子","便宜","品質"};
    private String[] CH31_sentences = new String[]{"請選擇題目(Please select subject)...","平常大家都很忙","我常在星期天和同學一起去逛街","我家附近有一家百貨公司","價錢都太貴了","我買了一條褲子，一雙鞋子","百貨公司裡賣的東西，品質不錯"};
    private String[] CH32_words = new String[]{"請選擇題目(Please select subject)...","昨天","花蓮","夜市","台灣","老闆","中文"};
    private String[] CH32_sentences = new String[]{"請選擇題目(Please select subject)...","昨天晚上天氣不錯","逛花蓮東大門夜市","台灣小吃，非常好吃","老闆可以算便宜一點嗎","中文講得很好","真是賺到了"};
    /*CH1 圖片資料*/
    private int[] CH1_articles_pic = new int[]{R.drawable.article11,R.drawable.article12};
    private int[] CH11_words_pic = new int[]{R.drawable.ch1_article1_word1,R.drawable.ch1_article1_word2,R.drawable.ch1_article1_word3,R.drawable.ch1_article1_word4,R.drawable.ch1_article1_word5,R.drawable.ch1_article1_word6};
    private int[] CH11_sentences_pic = new int[]{R.drawable.ch1_article1_sentence1,R.drawable.ch1_article1_sentence2,R.drawable.ch1_article1_sentence3,R.drawable.ch1_article1_sentence4,R.drawable.ch1_article1_sentence5,R.drawable.ch1_article1_sentence6};
    private int[] CH12_words_pic = new int[]{R.drawable.ch1_article2_word1,R.drawable.ch1_article2_word2,R.drawable.ch1_article2_word3,R.drawable.ch1_article2_word4,R.drawable.ch1_article2_word5,R.drawable.ch1_article2_word6};
    private int[] CH12_sentences_pic = new int[]{R.drawable.ch1_article2_sentence1,R.drawable.ch1_article2_sentence2,R.drawable.ch1_article2_sentence3,R.drawable.ch1_article2_sentence4,R.drawable.ch1_article2_sentence5,R.drawable.ch1_article2_sentence6};
    /*CH1 音檔資料*/
    private int[] CH11_words_sound = new int[]{R.raw.ch1_article1_sound_word1,R.raw.ch1_article1_sound_word2,R.raw.ch1_article1_sound_word3,R.raw.ch1_article1_sound_word4,R.raw.ch1_article1_sound_word5,R.raw.ch1_article1_sound_word6};
    private int[] CH11_sentences_sound = new int[]{R.raw.ch1_article1_sound_sentence1,R.raw.ch1_article1_sound_sentence2,R.raw.ch1_article1_sound_sentence3,R.raw.ch1_article1_sound_sentence4,R.raw.ch1_article1_sound_sentence5,R.raw.ch1_article1_sound_sentence6};
    private int[] CH12_words_sound = new int[]{R.raw.ch1_article2_sound_word1,R.raw.ch1_article2_sound_word2,R.raw.ch1_article2_sound_word3,R.raw.ch1_article2_sound_word4,R.raw.ch1_article2_sound_word5,R.raw.ch1_article2_sound_word6};
    private int[] CH12_sentences_sound = new int[]{R.raw.ch1_article2_sound_sentence1,R.raw.ch1_article2_sound_sentence2,R.raw.ch1_article2_sound_sentence3,R.raw.ch1_article2_sound_sentence4,R.raw.ch1_article2_sound_sentence5,R.raw.ch1_article2_sound_sentence6};
    /*CH2 圖片資料*/
    private int[] CH2_articles_pic = new int[]{R.drawable.article21,R.drawable.article22};
    private int[] CH21_words_pic = new int[]{R.drawable.ch2_article1_word1,R.drawable.ch2_article1_word2,R.drawable.ch2_article1_word3,R.drawable.ch2_article1_word4,R.drawable.ch2_article1_word5,R.drawable.ch2_article1_word6};
    private int[] CH21_sentences_pic = new int[]{R.drawable.ch2_article1_sentence1,R.drawable.ch2_article1_sentence2,R.drawable.ch2_article1_sentence3,R.drawable.ch2_article1_sentence4,R.drawable.ch2_article1_sentence5,R.drawable.ch2_article1_sentence6};
    private int[] CH22_words_pic = new int[]{R.drawable.ch2_article2_word1,R.drawable.ch2_article2_word2,R.drawable.ch2_article2_word3,R.drawable.ch2_article2_word4,R.drawable.ch2_article2_word5,R.drawable.ch2_article2_word6};
    private int[] CH22_sentences_pic = new int[]{R.drawable.ch2_article2_sentence1,R.drawable.ch2_article2_sentence2,R.drawable.ch2_article2_sentence3,R.drawable.ch2_article2_sentence4,R.drawable.ch2_article2_sentence5,R.drawable.ch2_article2_sentence6};
    /*CH2 音檔資料*/
    private int[] CH21_words_sound = new int[]{R.raw.ch2_article1_sound_word1,R.raw.ch2_article1_sound_word2,R.raw.ch2_article1_sound_word3,R.raw.ch2_article1_sound_word4,R.raw.ch2_article1_sound_word5,R.raw.ch2_article1_sound_word6};
    private int[] CH21_sentences_sound = new int[]{R.raw.ch2_article1_sound_sentence1,R.raw.ch2_article1_sound_sentence2,R.raw.ch2_article1_sound_sentence3,R.raw.ch2_article1_sound_sentence4,R.raw.ch2_article1_sound_sentence5,R.raw.ch2_article1_sound_sentence6};
    private int[] CH22_words_sound = new int[]{R.raw.ch2_article2_sound_word1,R.raw.ch2_article2_sound_word2,R.raw.ch2_article2_sound_word3,R.raw.ch2_article2_sound_word4,R.raw.ch2_article2_sound_word5,R.raw.ch2_article2_sound_word6};
    private int[] CH22_sentences_sound = new int[]{R.raw.ch2_article2_sound_sentence1,R.raw.ch2_article2_sound_sentence2,R.raw.ch2_article2_sound_sentence3,R.raw.ch2_article2_sound_sentence4,R.raw.ch2_article2_sound_sentence5,R.raw.ch2_article2_sound_sentence6};
    /*CH3 圖片資料*/
    private int[] CH3_articles_pic = new int[]{R.drawable.article31,R.drawable.article32};
    private int[] CH31_words_pic = new int[]{R.drawable.ch3_article1_word1,R.drawable.ch3_article1_word2,R.drawable.ch3_article1_word3,R.drawable.ch3_article1_word4,R.drawable.ch3_article1_word5,R.drawable.ch3_article1_word6};
    private int[] CH31_sentences_pic = new int[]{R.drawable.ch3_article1_sentence1,R.drawable.ch3_article1_sentence2,R.drawable.ch3_article1_sentence3,R.drawable.ch3_article1_sentence4,R.drawable.ch3_article1_sentence5,R.drawable.ch3_article1_sentence6};
    private int[] CH32_words_pic = new int[]{R.drawable.ch3_article2_word1,R.drawable.ch3_article2_word2,R.drawable.ch3_article2_word3,R.drawable.ch3_article2_word4,R.drawable.ch3_article2_word5,R.drawable.ch3_article2_word6};
    private int[] CH32_sentences_pic = new int[]{R.drawable.ch3_article2_sentence1,R.drawable.ch3_article2_sentence2,R.drawable.ch3_article2_sentence3,R.drawable.ch3_article2_sentence4,R.drawable.ch3_article2_sentence5,R.drawable.ch3_article2_sentence6};
    /*CH3 音檔資料*/
    private int[] CH31_words_sound = new int[]{R.raw.ch3_article1_sound_word1,R.raw.ch3_article1_sound_word2,R.raw.ch3_article1_sound_word3,R.raw.ch3_article1_sound_word4,R.raw.ch3_article1_sound_word5,R.raw.ch3_article1_sound_word6};
    private int[] CH31_sentences_sound = new int[]{R.raw.ch3_article1_sound_sentence1,R.raw.ch3_article1_sound_sentence2,R.raw.ch3_article1_sound_sentence3,R.raw.ch3_article1_sound_sentence4,R.raw.ch3_article1_sound_sentence5,R.raw.ch3_article1_sound_sentence6};
    private int[] CH32_words_sound = new int[]{R.raw.ch3_article2_sound_word1,R.raw.ch3_article2_sound_word2,R.raw.ch3_article2_sound_word3,R.raw.ch3_article2_sound_word4,R.raw.ch3_article2_sound_word5,R.raw.ch3_article2_sound_word6};
    private int[] CH32_sentences_sound = new int[]{R.raw.ch3_article2_sound_sentence1,R.raw.ch3_article2_sound_sentence2,R.raw.ch3_article2_sound_sentence3,R.raw.ch3_article2_sound_sentence4,R.raw.ch3_article2_sound_sentence5,R.raw.ch3_article2_sound_sentence6};
    private String[] return_TypeString;
    private int[] return_PictureCode;
    private int[] return_SoundCode;
    private int chapter = 0;
    private int article = 0;
    private String type = "";
    public Data(int chapter, int article){
        this.chapter = chapter;
        this.article = article;
        chose_article();
    }
    public Data(int chapter, int article,String type){
        this.chapter = chapter;
        this.article = article;
        this.type = type;
        chose_type();
    }
    private void chose_article(){
        switch (chapter){
            case 1:
                return_PictureCode = CH1_articles_pic;
                break;
            case 2:
                return_PictureCode = CH2_articles_pic;
                break;
            case 3:
                return_PictureCode = CH3_articles_pic;
                break;
            default:
                Log.d("Data.java","choose_article()");
                break;
        }
    }
    private void chose_type(){
        switch (chapter){
            case 1:
                if(article == 1) {
                    if (type == "Word") {
                        return_TypeString = CH11_words;
                        return_PictureCode = CH11_words_pic;
                        return_SoundCode = CH11_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH11_sentences;
                        return_PictureCode = CH11_sentences_pic;
                        return_SoundCode = CH11_sentences_sound;
                    }
                }
                else if(article == 2){
                    if (type == "Word") {
                        return_TypeString = CH12_words;
                        return_PictureCode = CH12_words_pic;
                        return_SoundCode = CH12_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH12_sentences;
                        return_PictureCode = CH12_sentences_pic;
                        return_SoundCode = CH12_sentences_sound;
                    }
                }
                break;
            case 2:
                if(article == 1) {
                    if (type == "Word") {
                        return_TypeString = CH21_words;
                        return_PictureCode = CH21_words_pic;
                        return_SoundCode = CH21_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH21_sentences;
                        return_PictureCode = CH21_sentences_pic;
                        return_SoundCode = CH21_sentences_sound;
                    }
                }
                else if(article == 2){
                    if (type == "Word") {
                        return_TypeString = CH22_words;
                        return_PictureCode = CH22_words_pic;
                        return_SoundCode = CH22_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH22_sentences;
                        return_PictureCode = CH22_sentences_pic;
                        return_SoundCode = CH22_sentences_sound;
                    }
                }
                break;
            case 3:
                if(article == 1) {
                    if (type == "Word") {
                        return_TypeString = CH31_words;
                        return_PictureCode = CH31_words_pic;
                        return_SoundCode = CH31_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH31_sentences;
                        return_PictureCode = CH31_sentences_pic;
                        return_SoundCode = CH31_sentences_sound;
                    }
                }
                else if(article == 2){
                    if (type == "Word") {
                        return_TypeString = CH32_words;
                        return_PictureCode = CH32_words_pic;
                        return_SoundCode = CH32_words_sound;
                    } else if (type == "Sentence") {
                        return_TypeString = CH32_sentences;
                        return_PictureCode = CH32_sentences_pic;
                        return_SoundCode = CH32_sentences_sound;
                    }
                }
                break;
            default:
                Log.d("Data.java","choose_type()");
                break;
        }
    }
    /*回傳給spinner*/
    public String[] getTypeSring(){
        return return_TypeString;
    }
    /*回傳給imageView*/
    public int[] getPictureCode(){
        return return_PictureCode;
    }
    /*回傳給button*/
    public int[] getSoundCode(){
        return return_SoundCode;
    }
}
