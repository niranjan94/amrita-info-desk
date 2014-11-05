package com.njlabs.amrita.aid.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CampusListener extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;

    /**
     * Constructor of the class
     */
    public CampusListener(FragmentManager fm) {
        super(fm);
    }

    /**
     * This method will be invoked when a page is requested to create
     */
    @Override
    public Fragment getItem(int arg0) {
        Bundle data = new Bundle();
        switch (arg0) {
            /** campusAbout tab is selected */
            case 0:
                CampusAbout campusAbout = new CampusAbout();
                data.putInt("current_page", arg0 + 1);
                data.putString("title","About Campus");
                campusAbout.setArguments(data);
                return campusAbout;

            /** campusContact tab is selected */
            case 1:
                CampusContact campusContact = new CampusContact();
                data.putInt("current_page", arg0 + 1);
                data.putString("title","Contact");
                campusContact.setArguments(data);
                return campusContact;
        }
        return null;
    }

    /**
     * Returns the number of pages
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "About Campus";
            case 1:
                return "Contact";
            default:
                return "";
        }
    }
}
