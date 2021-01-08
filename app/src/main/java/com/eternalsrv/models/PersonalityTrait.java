package com.eternalsrv.models;

public class PersonalityTrait {
    private String trait;
    private String[] questions;
    private int[] numbersOfQuestions;
    private int[] answerPoints;
    private int numberOfAnswered;
    private double score;

    public PersonalityTrait(String tr, String [] q, int[] numbOfQ)
    {
        trait = tr;
        questions = q;
        numberOfAnswered = 0;
        numbersOfQuestions = numbOfQ;
        answerPoints = new int[q.length];
    }

    public String[] getQuestions()
    {
        return questions;
    }

    public boolean containsNumber(int number)
    {
        boolean result = false;
        for(int i = 0; i < numbersOfQuestions.length; i++) {
            if(numbersOfQuestions[i] == number)
                result = true;
        }
        return result;
    }

    public void saveScore(int num, int number) {
        for(int i = 0; i < numbersOfQuestions.length; i++) {
            if(numbersOfQuestions[i] == num) {
                answerPoints[i] = number;
            }
        }
    }

    public void setQuestions(String[] questions) {
        this.questions = questions;
    }

    public int[] getNumbersOfQuestions() {
        return numbersOfQuestions;
    }

    public void setNumbersOfQuestions(int[] numbersOfQuestions) {
        this.numbersOfQuestions = numbersOfQuestions;
    }

    public int[] getAnswerPoints() {
        return answerPoints;
    }

    public void setAnswerPoints(int[] answerPoints) {
        this.answerPoints = answerPoints;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
