package com.siddique.androidwear.today;

/**
 * Created by siddique on 8/20/16.
 */
public enum TodoItemType {
    HOME("Home", "white_house"),
    WORK("Work", "capitol_hill");

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
