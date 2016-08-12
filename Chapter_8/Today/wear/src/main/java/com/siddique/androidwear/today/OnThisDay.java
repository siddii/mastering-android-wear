package com.siddique.androidwear.today;

import java.util.ArrayList;

public class OnThisDay {

    private String heading;
    private ArrayList<String> listItems;

    public OnThisDay(String heading, ArrayList<String> listItems) {
        this.heading = heading;
        this.listItems = listItems;
    }


    public String getHeadingHtml() {
        return "<u>" + heading + "</u>";
    }

    public String getListItemsHtml() {
        StringBuilder html = new StringBuilder();
        for (String listItem : listItems) {
            html.append("<em><small>").append(listItem).append("</small></em><br><br>");
        }
        return html.toString();
    }

    public ArrayList<String> getListItems() {
        return listItems;
    }
}
