/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.njlabs.amrita.aid.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsUpdateService extends GcmTaskService {
    Context mContext;
    int status = GcmNetworkManager.RESULT_SUCCESS;
    boolean allowNotification = true;
    @Override
    public int onRunTask(TaskParams taskParams) {
        mContext = this;
        SharedPreferences preferences = getSharedPreferences("com.njlabs.amrita.aid_preferences", Context.MODE_PRIVATE);
        allowNotification = preferences.getBoolean("news_updates_notification", true);
        return new JobTask(this).execute();
    }

    private class JobTask {
        private final NewsUpdateService jobService;
        private List<NewsModel> oldArticles;

        public JobTask(NewsUpdateService jobService) {
            this.jobService = jobService;
        }

        public int execute() {
            oldArticles = NewsModel.getAll();
            if (oldArticles != null && oldArticles.size() > 0) {
                getNews(true, oldArticles);
            } else {
                getNews(false, oldArticles);
            }
            return status;
        }
    }


    private void getNews(final Boolean refresh, final List<NewsModel> oldArticles){

        List<NewsModel> currentArticles;

        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        Request request = new Request.Builder()
                .url("https://www.amrita.edu/campus/Coimbatore/news")
                .build();

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                String responseString = response.body().string();
                status = GcmNetworkManager.RESULT_SUCCESS;
                currentArticles = new ArrayList<>();
                if(refresh){
                    NewsModel.deleteAll();
                }

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("News Articles");

                Document doc = Jsoup.parse(responseString);
                Elements articles = doc.select("article");
                for(Element article : articles){
                    Element header = article.select(".flexslider").first();
                    Element content = article.select(".group-blog-content").first();
                    Element footer = article.select(".group-blog-footer").first();
                    String imageUrl = header.select("ul > li > img").first().attr("src");
                    String title = content.select(".field-name-title > div > div > h2").first().text();
                    String url = "https://www.amrita.edu"+footer.select(".field-name-node-link > div > div > a").first().attr("href");

                    inboxStyle.addLine(title);
                    currentArticles.add(new NewsModel(imageUrl,title,url));
                }

                ActiveAndroid.beginTransaction();
                try {
                    for(NewsModel newsModel: currentArticles) {
                        newsModel.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                }
                finally {
                    ActiveAndroid.endTransaction();
                }

                Intent resultIntent = new Intent(mContext, NewsActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addParentStack(NewsActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                if(refresh&&!currentArticles.get(0).getTitle().equals(oldArticles.get(0).getTitle())){
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(NewsUpdateService.this)
                                    .setSmallIcon(R.drawable.ic_stat_news)
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_icon))
                                    .setContentTitle("Latest News")
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setContentIntent(resultPendingIntent)
                                    .setAutoCancel(true)
                                    .setContentText(currentArticles.get(0).getTitle());

                    if(allowNotification){
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, mBuilder.build());
                    }

                }
                if(!refresh){
                    NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(mContext);

                    mBuilder.setContentTitle("New Posts");
                    mBuilder.setContentText("Latest News Updated");
                    mBuilder.setTicker("Latest News Updated");
                    mBuilder.setSmallIcon(R.drawable.ic_stat_news);
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_icon));
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setNumber(currentArticles.size());
                    mBuilder.setAutoCancel(true);
                    mBuilder.setStyle(inboxStyle);

                    if(allowNotification){
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, mBuilder.build());
                    }

                }
            } else {
                status = GcmNetworkManager.RESULT_RESCHEDULE;
            }
        } catch (IOException e) {
            status = GcmNetworkManager.RESULT_RESCHEDULE;
        }

    }

    @Override
    public void onInitializeTasks() {

        long periodSecs = 21600L;
        long flexSecs = 30L;
        String tag = "periodic  | NewsUpdateService: " + periodSecs + "s, f:" + flexSecs;
        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(NewsUpdateService.class)
                .setPeriod(periodSecs)
                .setFlex(flexSecs)
                .setTag(tag)
                .setPersisted(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();

        GcmNetworkManager.getInstance(this).schedule(periodic);

        super.onInitializeTasks();
    }
}
