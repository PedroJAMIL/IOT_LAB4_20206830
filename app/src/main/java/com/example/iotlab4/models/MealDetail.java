package com.example.iotlab4.models;

import java.util.List;

public class MealDetail {
    private String idMeal;
    private String strMeal;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String strMealThumb;
    private List<String> ingredients;

    public MealDetail(String idMeal, String strMeal, String strCategory, String strArea,
                      String strInstructions, String strMealThumb, List<String> ingredients) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.ingredients = ingredients;
    }

    public String getIdMeal() { return idMeal; }
    public String getStrMeal() { return strMeal; }
    public String getStrCategory() { return strCategory; }
    public String getStrArea() { return strArea; }
    public String getStrInstructions() { return strInstructions; }
    public String getStrMealThumb() { return strMealThumb; }
    public List<String> getIngredients() { return ingredients; }
}