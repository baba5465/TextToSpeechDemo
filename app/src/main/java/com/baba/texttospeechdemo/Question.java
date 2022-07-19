package com.baba.texttospeechdemo;

public class Question {
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void generateQuestion(){
        int a = getRandomNumber();
        int b = getRandomNumber();
        question = a +" * "+b;
        answer = Integer.toString(a*b);
    }

    private int getRandomNumber(){
        int max = 10;
        int min = 0;
        return  (int)(Math.random()*(max-min+1)+min);
    }
}
