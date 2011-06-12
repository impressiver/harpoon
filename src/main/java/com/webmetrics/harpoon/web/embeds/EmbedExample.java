package com.webmetrics.harpoon.web.embeds;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("EmbedExample")
public class EmbedExample {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
