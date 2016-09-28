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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends BaseActivity {

    private ExtendedSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private OkHttpClient client;

    @Override
    public void init(Bundle savedInstanceState) {
        setupLayout(R.layout.activity_news, Color.parseColor("#ffc107"));

        swipeRefreshLayout = (ExtendedSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ffc107"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews(true);
            }
        });
        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutParams);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        swipeRefreshLayout.setRefreshing(true);

        final Handler uiHandler = new Handler();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                final List<NewsModel> articles = NewsModel.getAll();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (articles != null && articles.size() > 0) {
                            recyclerView.setAdapter(new NewsAdapter(articles));
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            getNews(false);
                        }
                    }
                });
            }
        })).start();

        client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

    }

    private void getNews(final Boolean refresh) {
        swipeRefreshLayout.setRefreshing(true);

        Request request = new Request.Builder().url("https://www.amrita.edu/campus/Coimbatore/news").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity) baseContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(parentView, "Can't establish a reliable connection to the server.", Snackbar.LENGTH_SHORT)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getNews(refresh);
                                    }
                                }).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseString = response.body().string();
                ((Activity) baseContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (refresh) {
                            (new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    NewsModel.deleteAll();
                                }
                            })).start();
                        }

                        final List<NewsModel> newsArticles = new ArrayList<>();

                        Document doc = Jsoup.parse(responseString);
                        Elements articles = doc.select("article");
                        for (Element article : articles) {
                            Element header = article.select(".flexslider").first();
                            Element content = article.select(".group-blog-content").first();
                            Element footer = article.select(".group-blog-footer").first();
                            String imageUrl = header.select("ul > li > img").first().attr("src");
                            String title = content.select(".field-name-title > div > div > h2").first().text();
                            String url = "https://www.amrita.edu" + footer.select(".field-name-node-link > div > div > a").first().attr("href");
                            newsArticles.add(new NewsModel(imageUrl, title, url));
                        }

                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ActiveAndroid.beginTransaction();
                                try {
                                    for (NewsModel newsModel : newsArticles) {
                                        newsModel.save();
                                    }
                                    ActiveAndroid.setTransactionSuccessful();
                                } finally {
                                    ActiveAndroid.endTransaction();
                                }
                            }
                        })).start();

                        recyclerView.setAdapter(new SlideInLeftAnimationAdapter(new AlphaInAnimationAdapter(new NewsAdapter(newsArticles))));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void articleClick(View v) {
        Uri uri = Uri.parse(((TextView) v.findViewById(R.id.url)).getText().toString());
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private List<NewsModel> newsArticles;

        NewsAdapter(List<NewsModel> newsArticles) {
            this.newsArticles = newsArticles;
        }

        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NewsModel newsArticle = newsArticles.get(position);
            holder.title.setText(newsArticle.getTitle());
            holder.url.setText(newsArticle.getLink());
            Picasso.with(baseContext).load(newsArticle.getImageUrl()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return newsArticles.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView image;
            public TextView url;

            ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
                image = (ImageView) v.findViewById(R.id.image);
                url = (TextView) v.findViewById(R.id.url);

            }

        }
    }
}
