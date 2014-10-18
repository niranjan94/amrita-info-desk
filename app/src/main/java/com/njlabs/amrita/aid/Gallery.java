package com.njlabs.amrita.aid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import uk.co.senab.photoview.PhotoView;

public class Gallery extends Activity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        mViewPager = new HackyViewPager(this);
        setContentView(mViewPager);
        mViewPager.setAdapter(new GalleryPagerAdapter());
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
        getActionBar().setDisplayHomeAsUpEnabled(true);
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

}