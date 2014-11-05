package com.njlabs.amrita.aid;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.njlabs.amrita.aid.util.HackyViewPager;

import uk.co.senab.photoview.PhotoView;

public class Gallery extends ActionBarActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        mViewPager = (HackyViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new GalleryPagerAdapter());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
    }

    static class GalleryPagerAdapter extends PagerAdapter {

        private static int[] sDrawables = {R.drawable.gallery_1, R.drawable.gallery_2, R.drawable.gallery_3,
                R.drawable.gallery_4, R.drawable.gallery_5, R.drawable.gallery_6, R.drawable.gallery_7};

        @Override
        public int getCount() {
            return sDrawables.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageResource(sDrawables[position]);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}