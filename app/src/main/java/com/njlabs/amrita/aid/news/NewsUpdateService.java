/*
 * Copyright (c) 2015. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.news;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.njlabs.amrita.aid.R;
import com.njlabs.amrita.aid.classes.NewsModel;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by Niranjan on 07-07-2015.
 */
public class NewsUpdateService extends JobService {
    Context mContext;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mContext = this;
        new JobTask(this).execute(jobParameters);
        return true;
    }

    private class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;
        private List<NewsModel> oldArticles;

        public JobTask(JobService jobService) {
            this.jobService = jobService;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            oldArticles = NewsModel.listAll(NewsModel.class);
            if(oldArticles != null && oldArticles.size()>0){
                getNews(true,oldArticles);
            } else {
                getNews(false,oldArticles);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void getNews(final Boolean refresh, final List<NewsModel> oldArticles){
        SyncHttpClient client = new SyncHttpClient ();
        client.get("https://www.amrita.edu/campus/Coimbatore/news", new TextHttpResponseHandler() {

            private List<NewsModel> currentArticles;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                currentArticles = new ArrayList<>();
                if(refresh){
                    NewsModel.deleteAll(NewsModel.class);
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
                    (new NewsModel(imageUrl,title,url)).save();
                    inboxStyle.addLine(title);
                    currentArticles.add(new NewsModel(imageUrl,title,url));
                }

                /* Creates an explicit intent for an Activity in your app */
                Intent resultIntent = new Intent(mContext, NewsActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addParentStack(NewsActivity.class);

                /* Adds the Intent that starts the Activity to the top of the stack */
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

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
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

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {

            }

            @Override
            public void onRetry(int retryNo) {

            }
        });
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
