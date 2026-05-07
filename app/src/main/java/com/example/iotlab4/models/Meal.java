package com.example.iotlab4.models;

public class Meal {
    private String idMeal;
    private String strMeal;
    private String strMealThumb;

    public Meal(String idMeal, String strMeal, String strMealThumb) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
    }

    public String getIdMeal() { return idMeal; }
    public String getStrMeal() { return strMeal; }
    public String getStrMealThumb() { return strMealThumb; }
}