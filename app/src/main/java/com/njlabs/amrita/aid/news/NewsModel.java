/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "News")
public class NewsModel extends Model {

    @Column
    private
    String imageUrl;

    @Column
    private
    String title;

    @Column
    private
    String link;

    public NewsModel() {
        super();
    }

    NewsModel(String imageUrl, String title, String link) {
        super();
        this.imageUrl = imageUrl;
        this.title = title;
        this.link = link;
    }

    String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public static List<NewsModel> getAll() {
        return new Select().from(NewsModel.class).execute();
    }

    static void deleteAll() {
        new Delete().from(NewsModel.class).execute();
    }
}
