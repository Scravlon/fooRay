package com.scravlon.fooray;

import java.io.Serializable;
import java.util.ArrayList;

public class itemObject implements Serializable {
    public int id;
    public String name;
    public double  prepareTime;
    public double cookingTime;
    public double servingAmount;
    public ArrayList<String[]> ingredient;
    public String instruction;
    public String imgLink;

    public itemObject(int id, String name, double prepareTime, double cookingTime, double servingAmount, String instruction, String imgLink, ArrayList<String[]> ingredient){
        this.id = id;
        this.name = name;
        this.prepareTime = prepareTime;
        this.cookingTime = cookingTime;
        this.servingAmount = servingAmount;
        this.instruction = instruction;
        this.imgLink = imgLink;
        this.ingredient = ingredient;

    }

}
