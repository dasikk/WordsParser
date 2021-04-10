package com.company;

public class Pairs {
    private Integer amount;
    private final String  word;
    public Pairs(String word)
    {
        this.word = word;
        this.amount = 1;
    }
    public void add_counter()
    {
        amount++;
    }

    //Used by TableView/ IDE cant see it
    public Integer getAmount() { return amount; }

    public String getWord() {
        return word;
    }

}

