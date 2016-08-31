package com.appshole.ltd.technolive;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appshole.ltd.technolive.adapter.CardAdapter;
import com.appshole.ltd.technolive.adapter.FragmentPagerAdapter;
import com.appshole.ltd.technolive.adapter.ListViewMyBoardCardsAdapter;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardCardListDao;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardCardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardListDao;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.appshole.ltd.technolive.model.BoardList;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.jar.Pack200;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;


public class CardAndListFragment extends Fragment implements View.OnClickListener {


    private KProgressHUD hud;
    private Bitmap thePic;
    private Uri picUri;
    final int PIC_CROP = 4;
    final int CAMERA_CAPTURE = 3;
    final int SELECT_IMAGE = 7;
    static final int CAPTURE_IMAGE = 3;
    private static final int PICK_FROM_GALLERY = 2;
    private String imageFile;
    private BoardCardList boardcardList;
    private CardView mCardView;
    private Button btnAddList, btnAddCard, btnAdd;
    private LinearLayout titleLayout, linBelowLayout;
    private ListView lisviewBoard;
    private EditText edtxtListName, edtxtCardname;
    private onSelectedListFragmentListener m_onSelectedListFragmentListener;
    private ListViewMyBoardCardsAdapter listViewMyBoardCardsAdapter;
    private BoardList boardList;

    private ImageView listdeleteImg, imgAddDialog;

    private boolean isAddClicked = false;

    public CardAndListFragment(BoardList boardList, onSelectedListFragmentListener m_onSelectedListFragmentListener) {
        this.boardList = boardList;
        this.m_onSelectedListFragmentListener = m_onSelectedListFragmentListener;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adapter, container, false);


        init(view);

        CardDetailActivity.isOpenedDetailActivity = false;
        getAllCardsInlist();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();


        if (CardDetailActivity.isOpenedDetailActivity) {
            CardDetailActivity.isOpenedDetailActivity = false;
            getAllCardsInlist();
        }

    }

    private void init(View view) {


        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        btnAddList = (Button) view.findViewById(R.id.btnAddList);
        btnAddList.setOnClickListener(this);

        btnAddCard = (Button) view.findViewById(R.id.btnAddCard);
        btnAddCard.setOnClickListener(this);


        imgAddDialog = (ImageView) view.findViewById(R.id.imgAddDialog);
        imgAddDialog.setOnClickListener(this);

        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);


        titleLayout = (LinearLayout) view.findViewById(R.id.titleLayout);
        linBelowLayout = (LinearLayout) view.findViewById(R.id.linBelowLayout);


        edtxtListName = (EditText) view.findViewById(R.id.edtxtListName);
        edtxtListName.setFocusable(false);
        edtxtListName.setFocusableInTouchMode(true);


        edtxtCardname = (EditText) view.findViewById(R.id.edtxtCardname);
        edtxtCardname.setFocusable(false);
        edtxtCardname.setFocusableInTouchMode(true);


        lisviewBoard = (ListView) view.findViewById(R.id.lisviewBoard);

        listdeleteImg = (ImageView) view.findViewById(R.id.listdeleteImg);

        listdeleteImg.setOnClickListener(this);

        edtxtListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {


                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    String listname = textView.getText().toString();

                    if (isAddClicked) {
                        isAddClicked = false;
                        createListToServer();
                    } else {
                        if (listname.equals("")) {
                            Toast.makeText(getActivity(), "Please write list name", Toast.LENGTH_SHORT).show();

                        } else {
                            editListInfoToServer(listname);
                        }
                    }
                    hide_keyboard(edtxtListName);
                    edtxtListName.setFocusable(false);


                    return true;
                }
                hide_keyboard(edtxtListName);
                edtxtListName.setFocusable(false);
                return false;


            }
        });

        if (boardList != null && boardList.getId() != null && !boardList.getId().equals("")) {
            btnAddList.setVisibility(View.GONE);

            titleLayout.setVisibility(View.VISIBLE);
            lisviewBoard.setVisibility(View.VISIBLE);
            edtxtListName.setText(boardList.getName());


        } else {


            btnAddList.setVisibility(View.VISIBLE);
            btnAddCard.setVisibility(View.GONE);
            titleLayout.setVisibility(View.GONE);
            lisviewBoard.setVisibility(View.GONE);


        }


    }

    private void getAllCardsInlist() {


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.trello.com/1/lists/" + this.boardList.getId() + "/cards?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        client.get(getActivity(), url, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);


                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(getActivity());


                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONArray(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {

                        boardList.boardCardListArrayList = boardArrayList;


                        fillAdapter(boardList);
                    }

                } catch (Exception ex) {
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }


        });


    }

    private void editListInfoToServer(final String listname) {


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.trello.com/1/lists/" + this.boardList.getId() + "?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("name", listname);


        client.put(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                edtxtListName.setText(listname);
                boardList.setName(listname);

                hud.dismiss();
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


    public CardView getCardView() {
        return mCardView;
    }


    @Override
    public void onClick(View view) {


        if (view == btnAddList) {
            isAddClicked = true;
            edtxtListName.setText("");
            btnAddList.setVisibility(View.GONE);

            lisviewBoard.setVisibility(View.VISIBLE);
            titleLayout.setVisibility(View.VISIBLE);


        } else if (view == listdeleteImg) {


            if (boardList == null || boardList.getId() == null || boardList.getId().equals("")) {

                titleLayout.setVisibility(View.GONE);
                btnAddList.setVisibility(View.VISIBLE);


                return;
            }


            deleteDialog();


        } else if (view == btnAddCard) {

            if (!isAddClicked) {

                edtxtCardname.setVisibility(View.VISIBLE);
                linBelowLayout.setVisibility(View.VISIBLE);
                btnAddCard.setText("Cancel");
                isAddClicked = true;
                imgAddDialog.setImageResource(R.drawable.ic_attachment);
                edtxtCardname.setText("");
            } else {
                isAddClicked = false;
                btnAddCard.setText("Add Card");
                edtxtCardname.setVisibility(View.GONE);
                linBelowLayout.setVisibility(View.GONE);
            }


        } else if (view == imgAddDialog) {


            pickupImageFromGalleryorTake();

        } else if (view == btnAdd) {
            linBelowLayout.setVisibility(View.VISIBLE);
            edtxtCardname.setVisibility(View.VISIBLE);


            if (edtxtCardname.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please write card name", Toast.LENGTH_SHORT).show();
                return;
            }
            addcardTolist();


        }


    }

    private void addcardTolist() {


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.trello.com/1/cards?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();


        params.add("name", edtxtCardname.getText().toString());
        params.add("due", null);
        params.add("idList", boardList.getId());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();

                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(
                            getActivity());


                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONObject(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {
                        boardList.boardCardListArrayList.add(boardArrayList.get(0));

                        if (imageFile != null && !imageFile.equals("")) {

                            attatchMentUploadToCard(boardArrayList.get(0));
                        } else {
                            fillAdapter(boardList);
                        }


                    }


                    edtxtCardname.setVisibility(View.GONE);
                    linBelowLayout.setVisibility(View.GONE);
                    isAddClicked = false;
                    btnAddCard.setText("Add Card");


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

    private void attatchMentUploadToCard(final BoardCardList boardcardlist) {


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.trello.com/1/cards/" + boardcardlist.getId() + "/attachments?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        File file = new File(imageFile);

        try {
            params.put("file", file, "image/jpeg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        params.put("name", edtxtCardname.getText().toString());


        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();


                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(
                            getActivity());


                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONObject(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {
//
                        for (int i = 0; i < boardList.boardCardListArrayList.size(); i++) {
                            if (boardList.boardCardListArrayList.get(i).getId().equals(boardcardlist.getId())) {
                                boardList.boardCardListArrayList.get(i).setAttachmentCoverId("test");
                            }
                        }
                    }

//

                    fillAdapter(boardList);
                    imageFile = "";
                    edtxtCardname.setVisibility(View.GONE);
                    linBelowLayout.setVisibility(View.GONE);
                    isAddClicked = false;
                    btnAddCard.setText("Add Card");


                } catch (Exception ex) {
                    ex.getMessage();
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                imageFile = "";
                hud.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                imageFile = "";
                hud.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                imageFile = "";
                hud.dismiss();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                imageFile = "";
                hud.dismiss();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                imageFile = "";
                hud.dismiss();
            }


        });


    }

    private void fillAdapter(BoardList boardList) {

        listViewMyBoardCardsAdapter = new ListViewMyBoardCardsAdapter(getActivity(), boardList.boardCardListArrayList, m_onSelectedBoardCardListListener);

        lisviewBoard.setAdapter(listViewMyBoardCardsAdapter);


    }

    ListViewMyBoardCardsAdapter.onSelectedBoardCardListListener m_onSelectedBoardCardListListener = new ListViewMyBoardCardsAdapter.onSelectedBoardCardListListener() {
        @Override
        public void onClick(int type, BoardCardList boardCardList) {

            if (type == 2) {

                boardcardList = boardCardList;

                deleteDialog1();


            } else if (type == 1) {

                CardDetailActivity.isOpenedDetailActivity = true;
                Intent i = new Intent(getActivity(), CardDetailActivity.class);
                i.putExtra("card", boardCardList);
                startActivity(i);
            }


        }
    };


    private void createListToServer() {


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();


        String url = "https://api.trello.com/1/lists?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();


        params.add("name", edtxtListName.getText().toString());
        params.add("idBoard", MainActivity.globalBoard.getBoardID());


        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();
                try {
                    IBoardListDao boardDao = new BoardListDao(
                            getActivity());


                    ArrayList<BoardList> boardArrayList = boardDao.GetBoardListDeleteFromJSONObject(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {
                        m_onSelectedListFragmentListener.onClick(1, boardArrayList.get(0));

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

    private void hide_keyboard(View v) {
        try {


            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }


    public interface onSelectedListFragmentListener {
        void onClick(int type, BoardList boardList);
    }


    public void deleteDialog() {

        new MaterialDialog.Builder(getActivity())
                .title("Are you sure to Delete List?")
                .content("").positiveText("Yes").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                archivelistToServer();


            }
        })
                .negativeText("Cancel").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


            }
        }).show();


    }


    private void archivelistToServer() {


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();


        String url = "https://api.trello.com/1/lists/" + this.boardList.getId() + "/closed?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("value", "true");

        client.put(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                hud.dismiss();


                try {
                    IBoardListDao boardDao = new BoardListDao(
                            getActivity());


                    ArrayList<BoardList> boardArrayList = boardDao.GetBoardListDeleteFromJSONObject(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {
                        m_onSelectedListFragmentListener.onClick(2, boardArrayList.get(0));

                    }


                } catch (Exception ex) {
                    ex.getMessage();
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

    public void pickupImageFromGalleryorTake() {


        new MaterialDialog.Builder(getActivity())
                .title("Choose one")
                .items(R.array.pic_up)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (which == 0) {

                            GetImageFromGallery();
                        } else {
                            takePicture();
                        }


                    }
                })
                .show();


    }

    private void takePicture() {

        /**
         * take picture
         */

        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(cameraIntent, "image.png"),
                CAMERA_CAPTURE);
    }


    private void GetImageFromGallery() {

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }

        switch (requestCode) {

            case CAPTURE_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (requestCode == CAMERA_CAPTURE) {
                        final ContentResolver cr = getActivity()
                                .getContentResolver();
                        final String[] p1 = new String[]{
                                MediaStore.Images.ImageColumns._ID,
                                MediaStore.Images.ImageColumns.DATE_TAKEN};
                        Cursor c1 = cr.query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p1,
                                null, null, p1[1] + " DESC");
                        if (c1.moveToFirst()) {
                            String uristringpic = "content://media/external/images/media/"
                                    + c1.getInt(0);
                            picUri = Uri.parse(uristringpic);
                            Log.i("TAG", "newuri   " + picUri);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                try {

                                    thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(getActivity(), 500, 500, picUri);


                                    imgAddDialog.setImageBitmap(thePic);

                                    imageFile = getRealPathFromURI(picUri);


                                } catch (Exception ex) {
                                }


                            } else {

                                if (picUri != null)
                                    performCrop();
                            }

                        }
                        c1.close();

                    }
                }
                break;
            case PIC_CROP:
                if (requestCode == PIC_CROP) {
                    Bundle extras = data.getExtras();
                    thePic = extras.getParcelable("data");
                    imgAddDialog.setImageBitmap(thePic);
                    imageFile = getRealPathFromURI(picUri);

                }
                break;
            case SELECT_IMAGE:
                if (requestCode == SELECT_IMAGE) {

                    Log.e("SELECT_IMAGE", "SELECT_IMAGE");

                    picUri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                        try {

                            thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(getActivity(), 500, 500, picUri);

                            imgAddDialog.setImageBitmap(thePic);
                            imageFile = getRealPathFromURI(picUri);

                        } catch (Exception ex) {
                        }


                    } else {

                        if (picUri != null)
                            performCrop();
                    }


                }
                break;
            case PICK_FROM_GALLERY:
                if (requestCode == PICK_FROM_GALLERY) {

                    try {
                        picUri = data.getData();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            try {

                                thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(getActivity(), 500, 500, picUri);


                                imgAddDialog.setImageBitmap(thePic);
                                imageFile = getRealPathFromURI(picUri);
                            } catch (Exception ex) {
                            }


                        } else {

                            if (picUri != null)
                                performCrop();
                        }
                    } catch (Exception ex) {

                    }
                }

        }

    }

    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Your device does not support croping";
            Toast toast = Toast.makeText(getActivity(), errorMessage,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static Bitmap GetBitmapImageCompatiablewithDeviceFromdecodeUri(
            Context context, int width, int height, Uri selectedImage)
            throws FileNotFoundException {
        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_HEIGHT_SIZE = height - 200;
            final int REQUIRED_WIDGHT_SIZE = width;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_WIDGHT_SIZE
                        || height_tmp / 2 < REQUIRED_HEIGHT_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(selectedImage), null, o2);

        } catch (Exception ex) {
        }

        return null;

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void deleteDialog1() {

        new MaterialDialog.Builder(getActivity())
                .title("Are you sure to Delete Card?")
                .content("").positiveText("Yes").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                deleteCardToServer();


            }
        })
                .negativeText("Cancel").onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


            }
        }).show();


    }

    private void deleteCardToServer() {

        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject json = new JSONObject();


        String url = "https://api.trello.com/1/cards/" + boardcardList.getId() + "?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("value", "true");

        client.delete(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hud.dismiss();

                for (int i = 0; i < boardList.boardCardListArrayList.size(); i++) {

                    if (boardList.boardCardListArrayList.get(i).getId().equals(boardcardList.getId())) {
                        boardList.boardCardListArrayList.remove(i);
                        break;
                    }


                }


                fillAdapter(boardList);
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
