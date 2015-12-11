/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.njlabs.amrita.aid.BaseActivity;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.classes.NewsModel;
import com.njlabs.amrita.aid.util.ExtendedSwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Niranjan on 07-07-2015.
 */
public class NewsActivity extends BaseActivity {

    private ExtendedSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_news, Color.parseColor("#ffc107"));

        swipeRefreshLayout = (ExtendedSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews(true);
            }
        });
        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutParams);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                final List<NewsModel> articles = NewsModel.listAll(NewsModel.class);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(articles != null && articles.size()>0){
                            recyclerView.setAdapter(new NewsAdapter(articles));
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            getNews(false);
                        }
                    }
                });
            }
        })).start();
    }

    private void getNews(final Boolean refresh){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.amrita.edu/campus/Coimbatore/news", new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {


                if(refresh){
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NewsModel.deleteAll(NewsModel.class);
                        }
                    })).start();
                }

                final List<NewsModel> newsArticles = new ArrayList<>();

                Document doc = Jsoup.parse(responseString);
                Elements articles = doc.select("article");
                for(Element article : articles){
                    Element header = article.select(".flexslider").first();
                    Element content = article.select(".group-blog-content").first();
                    Element footer = article.select(".group-blog-footer").first();
                    String imageUrl = header.select("ul > li > img").first().attr("src");
                    String title = content.select(".field-name-title > div > div > h2").first().text();
                    String url = "https://www.amrita.edu"+footer.select(".field-name-node-link > div > div > a").first().attr("href");
                    newsArticles.add(new NewsModel(imageUrl, title, url));
                }

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NewsModel.saveInTx(newsArticles);
                    }
                })).start();

                recyclerView.setAdapter(new NewsAdapter(newsArticles));
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(parentView,"Can't establish a reliable connection to the server.", Snackbar.LENGTH_SHORT)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getNews(refresh);
                            }
                        }).show();
            }

            @Override
            public void onRetry(int retryNo) {

            }
        });
    }



    public void articleClick(View v){
        Uri uri = Uri.parse(((TextView)v.findViewById(R.id.url)).getText().toString());
        Intent it  = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(it);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private List<NewsModel> newsArticles;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView image;
            public TextView url;
            public ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
                image = (ImageView) v.findViewById(R.id.image);
                url = (TextView) v.findViewById(R.id.url);

            }

        }

        public NewsAdapter(List<NewsModel> newsArticles) {
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
    }
}
