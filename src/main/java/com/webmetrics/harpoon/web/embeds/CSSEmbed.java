package com.webmetrics.harpoon.web.embeds;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("CSS")
public class CSSEmbed {
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
