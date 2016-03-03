/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

import com.orm.SugarRecord;

/**
 * Created by Niranjan on 07-07-2015.
 */
public class NewsModel extends SugarRecord<NewsModel> {
    String imageUrl;
    String title;
    String link;

    public NewsModel() {
    }

    public NewsModel(String imageUrl, String title, String link) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
