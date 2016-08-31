package com.appshole.ltd.technolive.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.appshole.ltd.technolive.CardAndListFragment;
import com.appshole.ltd.technolive.MainActivity;
import com.appshole.ltd.technolive.model.BoardList;

import java.util.ArrayList;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private ArrayList<CardAndListFragment> mFragmentsArrayList;
    private float mBaseElevation;
    private onSelectedFragmentPagerListener m_onSelectedFragmentPagerListener;

    public FragmentPagerAdapter(FragmentManager fm, float baseElevation, onSelectedFragmentPagerListener m_onSelectedFragmentPagerListener) {
        super(fm);
        mFragmentsArrayList = new ArrayList<>();
        mBaseElevation = baseElevation;
        this.m_onSelectedFragmentPagerListener = m_onSelectedFragmentPagerListener;

        try {

            if (MainActivity.globalBoardlist != null && MainActivity.globalBoardlist.size() > 0) {
                for (int i = 0; i < MainActivity.globalBoardlist.size(); i++) {
                    addCardFragment(new CardAndListFragment(MainActivity.globalBoardlist.get(i), m_ListFragemntListner));
                }
            }
            addCardFragment(new CardAndListFragment(new BoardList(), m_ListFragemntListner));
        } catch (Exception e) {


        }

    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragmentsArrayList.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragmentsArrayList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsArrayList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragmentsArrayList.set(position, (CardAndListFragment) fragment);
        return fragment;
    }

    public void addCardFragment(CardAndListFragment fragment) {
        mFragmentsArrayList.add(fragment);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {

        try {


            mFragmentsArrayList = new ArrayList<>();

            for (int i = 0; i < MainActivity.globalBoardlist.size(); i++) {
                addCardFragment(new CardAndListFragment(MainActivity.globalBoardlist.get(i), m_ListFragemntListner));
            }

            addCardFragment(new CardAndListFragment(new BoardList(), m_ListFragemntListner));

        } catch (Exception e) {

        }
        super.notifyDataSetChanged();
    }


    CardAndListFragment.onSelectedListFragmentListener m_ListFragemntListner = new CardAndListFragment.onSelectedListFragmentListener() {
        @Override
        public void onClick(int type, BoardList boardList) {

            if (type == 2) {

                for (int i = 0; i < MainActivity.globalBoardlist.size(); i++) {
                    if (MainActivity.globalBoardlist.get(i).getId().equals(boardList.getId())) {
                        MainActivity.globalBoardlist.remove(i);
                        break;
                    }
                }

            } else if (type == 1) {

                if (MainActivity.globalBoardlist == null) {
                    MainActivity.globalBoardlist = new ArrayList<>();
                }
                MainActivity.globalBoardlist.add(boardList);
            }

            notifyDataSetChanged();

            m_onSelectedFragmentPagerListener.onClick(1);


        }
    };


    public interface onSelectedFragmentPagerListener {
        void onClick(int type);
    }

}
