/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
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

    public static List<NewsModel> getAll() {
        return new Select().from(NewsModel.class).execute();
    }

    static void deleteAll() {
        new Delete().from(NewsModel.class).execute();
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
}
