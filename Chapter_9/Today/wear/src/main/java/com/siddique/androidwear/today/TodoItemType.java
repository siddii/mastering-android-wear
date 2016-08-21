package com.siddique.androidwear.today;

/**
 * Created by siddique on 8/20/16.
 */
public enum TodoItemType {
    HOME("Home", "white_house.jpg"),
    WORK("Work", "capitol_holl.jpg");

    private String typeValue;
    private String backgroundImage;
    TodoItemType(String typeValue, String backgroundImage) {
        this.typeValue = typeValue;
        this.backgroundImage = backgroundImage;
    }
    public String getBackgroundImage() {
        return backgroundImage;
    }

    public String getTypeValue() {
        return typeValue;
    }
}
