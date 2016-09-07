package com.appshole.ltd.technolive;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.appshole.ltd.technolive.adapter.FragmentPagerAdapter;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardCardListDao;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardCardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardListDao;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.appshole.ltd.technolive.model.BoardList;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {


    private  KProgressHUD hud;
    private ViewPager mViewPager;
    private int count = 0;
    private ShadowTransformer mCardShadowTransformer;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    FragmentPagerAdapter.onSelectedFragmentPagerListener m_FragmentPagerListener = new FragmentPagerAdapter.onSelectedFragmentPagerListener() {
        @Override
        public void onClick(int type) {
            getBoardListFromServer();
        }
    };
    private boolean mShowingFragments = false;

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this),m_FragmentPagerListener);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentPagerAdapter);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(4);


        getBoardListFromServer();


    }

    private void getBoardListFromServer() {

        hud = KProgressHUD.create(ViewPagerActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();



        String url = "https://api.trello.com/1/boards/" + MainActivity.globalBoard.getBoardID() + "?lists=open&list_fields=name&fields=name,desc&key=39615017597892d384d576145f2003c1";

        client.get(ViewPagerActivity.this, url, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);


                hud.dismiss();
                try {
                    IBoardListDao boardDao = new BoardListDao(
                            ViewPagerActivity.this);


                    ArrayList<BoardList> boardArrayList = boardDao.GetBoardListFromJSONObject(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {

                        MainActivity.globalBoardlist = boardArrayList;


                        count = 0;
                        hud = KProgressHUD.create(ViewPagerActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Please wait")
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();



                        getCardFromListArrayInBoard();


                    }


                } catch (Exception ex) {
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                hud.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                hud.dismiss();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hud.dismiss();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hud.dismiss();

            }


        });


    }

    private void getCardFromListArrayInBoard() {


        if (count < MainActivity.globalBoardlist.size()) {
            getJsonObjectFromApi(MainActivity.globalBoardlist.get(count));
        } else {
            mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                    dpToPixels(2, this),m_FragmentPagerListener);
            mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentPagerAdapter);
            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
            mViewPager.setOffscreenPageLimit(4);

            hud.dismiss();

        }


    }

    private void getJsonObjectFromApi(BoardList boardList) {






        AsyncHttpClient client = new AsyncHttpClient();



        String url = "https://api.trello.com/1/lists/" + boardList.getId() + "/cards?key=39615017597892d384d576145f2003c1";

        client.get(ViewPagerActivity.this, url, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
               // hud.dismiss();


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                //hud.dismiss();

                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(
                            ViewPagerActivity.this);
                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONArray(response);

                    if (boardArrayList != null && boardArrayList.size() > 0) {


                        for (int i = 0; i < MainActivity.globalBoardlist.size(); i++) {
                            for (int j = 0; j < boardArrayList.size(); j++) {
                                if (MainActivity.globalBoardlist.get(i).getId().equals(boardArrayList.get(j).getListId())) {
                                    MainActivity.globalBoardlist.get(i).boardCardListArrayList.add(boardArrayList.get(j));
                                }

                            }


                        }


                    }

                    count++;
                    getCardFromListArrayInBoard();


                } catch (Exception ex) {
                    ex.getMessage();
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                //hud.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //hud.dismiss();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //hud.dismiss();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //hud.dismiss();

            }


        });


    }

    @Override
    public void onClick(View view) {
        if (!mShowingFragments) {

            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        }

        mShowingFragments = !mShowingFragments;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCardShadowTransformer.enableScaling(b);
        mFragmentCardShadowTransformer.enableScaling(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
