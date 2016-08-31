package com.appshole.ltd.technolive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appshole.ltd.technolive.adapter.ListViewMyBoardAdapter;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardDao;
import com.appshole.ltd.technolive.dao.imp.BoardDao;
import com.appshole.ltd.technolive.model.Board;
import com.appshole.ltd.technolive.model.BoardList;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;


public class MainActivity extends AppCompatActivity {


    private KProgressHUD hud;
    private ListView boardListview;
    private ListViewMyBoardAdapter listViewMyBoardAdapter;
    public static Board globalBoard;
    public static ArrayList<BoardList> globalBoardlist;

    private ArrayList<Board> boardarraylist;
    private Board selectedBoard;
    private String boardName = "";


    private String accountToken = "521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";
    private String Developerkey = "39615017597892d384d576145f2003c1";
    private String secretKey = "5fc3cf8f1a2e63f28fb3d3a32241754d299eb84913d42bff59341b5ba72181e8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditextDialog();
            }
        });

        boardListview = (ListView) findViewById(R.id.lisviewBoard);


        getBoardListFromServer();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnCancel) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showEditextDialog() {


        boardName = "";

        new MaterialDialog.Builder(this)
                .title("Create Board")
                .content("").positiveText("Create").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


            }
        })
                .negativeText("Cancel").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


            }
        })
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Board name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                        boardName = input.toString();

                        if (boardName != null && !boardName.equals("")) {

                            createBoardToServer(boardName);


                        } else {
                            Toast.makeText(MainActivity.this, "Please write board name", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).show();


    }

    private void createBoardToServer(final String boardName) {


        hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();


        String url = "https://api.trello.com/1/boards?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("name", boardName);
        params.add("prefs_permissionLevel", "public");
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();


                getBoardListFromServer();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();
                try {
                    IBoardDao boardDao = new BoardDao(
                            MainActivity.this);


                    ArrayList<Board> boardArrayList = boardDao.GetBoardFromJSONArray(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {
                        fillAdapter(boardArrayList);
                    }


                } catch (Exception ex) {
                }


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

    private void getBoardListFromServer() {

        hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject json = new JSONObject();


        String url = "https://api.trello.com/1/members/57c1fc6faf45cc1d8bd1acab/boards?key=39615017597892d384d576145f2003c1";

        client.get(MainActivity.this, url, new JsonHttpResponseHandler() {



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hud.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                hud.dismiss();

                try {
                    IBoardDao boardDao = new BoardDao(
                            MainActivity.this);


                    ArrayList<Board> boardArrayList = boardDao.GetBoardFromJSONArray(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {

                        for (int i = 0; i < boardArrayList.size(); i++) {
                            if (boardArrayList.get(i).getIsClosed().equals("true")) {
                                boardArrayList.remove(i);
                            }
                        }
                        boardarraylist = boardArrayList;
                        fillAdapter(boardArrayList);
                    }


                } catch (Exception ex) {
                }


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

    private void fillAdapter(ArrayList<Board> boardArrayList) {

        listViewMyBoardAdapter = new ListViewMyBoardAdapter(MainActivity.this, boardArrayList, m_listnerOFBoard);
        boardListview.setAdapter(listViewMyBoardAdapter);


    }

    ListViewMyBoardAdapter.onSelectedBoardListener m_listnerOFBoard = new ListViewMyBoardAdapter.onSelectedBoardListener() {
        @Override
        public void onClick(int type, Board myboard) {


            if (type == 1) {
                globalBoard = myboard;
                Intent i = new Intent(MainActivity.this, ViewPagerActivity.class);
                startActivity(i);

            } else if (type == 2) {

                selectedBoard = myboard;
                deleteDialog();
            }


        }
    };


    public void deleteDialog() {

        new MaterialDialog.Builder(this)
                .title("Are you sure to Delete Board?")
                .content("").positiveText("Yes").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                deleteBoardToServer();

            }
        })
                .negativeText("Cancel").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


            }
        }).show();


    }

    private void deleteBoardToServer() {

        hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.trello.com/1/boards/" + selectedBoard.getBoardID() + "/closed?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("value", "true");


        client.put(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();

                for (int i = 0; i < boardarraylist.size(); i++) {
                    if (selectedBoard.getBoardID().equals(boardarraylist.get(i).getBoardID())) {
                        boardarraylist.remove(i);
                    }
                }

                fillAdapter(boardarraylist);

                globalBoard = null;
                globalBoardlist = null;
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

}
