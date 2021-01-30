package com.eternalsrv.utils.asynctasks.model;

import com.eternalsrv.utils.asynctasks.model.jsonadapter.HashMapJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class TestRequest extends BaseUserRequest {
    @SerializedName("number_of_questions")
    private int numberOfQuestions;
    @JsonAdapter(HashMapJsonAdapter.class)
    private HashMap<String, String> answers;

    public TestRequest(Long userId, int numberOfQuestions, HashMap<String, String> answers) {
        super(userId);
        this.numberOfQuestions = numberOfQuestions;
        this.answers = answers;
    }
}
