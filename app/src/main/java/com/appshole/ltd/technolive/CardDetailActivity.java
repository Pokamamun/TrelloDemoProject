/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appshole.ltd.technolive.adapter.ListGirdViewAdapter;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardCardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardCardListDao;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class CardDetailActivity extends AppCompatActivity {


    private GridViewWithHeaderAndFooter gridView;
    public static boolean isOpenedDetailActivity=false;
    private KProgressHUD hud;
    private BoardCardList boardCardList;
    private ImageView imageView;
    private EditText edtxtCardName;

    private ImageView imgAttachmentImage;
    private Bitmap thePic;
    private Uri picUri;
    final int PIC_CROP = 4;
    final int CAMERA_CAPTURE = 3;
    final int SELECT_IMAGE = 7;
    static final int CAPTURE_IMAGE = 3;
    private static final int PICK_FROM_GALLERY = 2;
    private String imageFile;

    private ArrayList<BoardCardList>boardCardListArrayList;


    private ListGirdViewAdapter listGirdViewAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        boardCardList = (BoardCardList) getIntent().getExtras().getSerializable("card");

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(boardCardList.getName());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupImageFromGalleryorTake();
            }
        });



        edtxtCardName=(EditText)findViewById(R.id.edtxtCardName);
        edtxtCardName.setText(boardCardList.getName());

        edtxtCardName.setFocusable(false);
        edtxtCardName.setFocusableInTouchMode(true);

        imgAttachmentImage=(ImageView) findViewById(R.id.imgAttachmentImage);


        edtxtCardName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {


                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    String listname = textView.getText().toString();


                        if (listname.equals("")) {
                            Toast.makeText(CardDetailActivity.this, "Please write list name", Toast.LENGTH_SHORT).show();
                        } else {
                            editCardNameInfoToServer(listname);
                        }

                    hide_keyboard(edtxtCardName);
                    edtxtCardName.setFocusable(false);


                    return true;
                }
                hide_keyboard(edtxtCardName);
                edtxtCardName.setFocusable(false);
                return false;


            }
        });




        gridView = (GridViewWithHeaderAndFooter) findViewById(R.id.girdview);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });












        loadBackdrop();
    }


    public void fillAdapter(ArrayList<BoardCardList> boardCardListArrayList){
        listGirdViewAdapter= new ListGirdViewAdapter(CardDetailActivity.this,boardCardListArrayList,m_onSelectedListGirdViewAdapterListener);
        gridView.setAdapter(listGirdViewAdapter);
    }

    ListGirdViewAdapter.onSelectedListGirdViewAdapterListener m_onSelectedListGirdViewAdapterListener = new ListGirdViewAdapter.onSelectedListGirdViewAdapterListener() {
        @Override
        public void onClick(int type, BoardCardList myboard) {



        }
    };





    private void loadBackdrop() {
         imageView = (ImageView) findViewById(R.id.backdrop);

        getAttachmentFileFromServer(boardCardList,imageView);

    }







    private void getAttachmentFileFromServer(final BoardCardList boardCardList, final ImageView imgAttachImage) {


        AsyncHttpClient client = new AsyncHttpClient();



        String url = "https://api.trello.com/1/cards/"+boardCardList.getId()+"/attachments?fields=all&key=39615017597892d384d576145f2003c1";

        client.get(CardDetailActivity.this, url, new JsonHttpResponseHandler() {



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);


                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(
                            CardDetailActivity.this);


                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONArray(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {


                        boardCardListArrayList=boardArrayList;

                        fillAdapter(boardCardListArrayList);

                        Glide.with(CardDetailActivity.this)
                                .load(boardArrayList.get(0).getUrl())
                                .placeholder(R.drawable.card_cover_placeholder)
                                .crossFade().override(200, 200)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgAttachImage);


                        Glide.with(CardDetailActivity.this)
                                .load(boardArrayList.get(0).getUrl())
                                .placeholder(R.drawable.card_cover_placeholder)
                                .crossFade().override(200, 200)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgAttachmentImage);








                    }


                } catch (Exception ex) {
                    ex.getMessage();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void hide_keyboard(View v) {
        try {


            InputMethodManager imm = (InputMethodManager) CardDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }



    private void editCardNameInfoToServer(final String listname) {


        hud = KProgressHUD.create(CardDetailActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.trello.com/1/cards/" + this.boardCardList.getId() + "/name?key=39615017597892d384d576145f2003c1&token=521c79beb07d996b8ed363f147c63e101804d17d29752fb75d96842b90b24b33";

        RequestParams params = new RequestParams();
        params.add("value", listname);


        client.put(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                edtxtCardName.setText(listname);
                boardCardList.setName(listname);
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

    public void pickupImageFromGalleryorTake() {


        new MaterialDialog.Builder(CardDetailActivity.this)
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
                if (resultCode == CardDetailActivity.this.RESULT_OK) {
                    if (requestCode == CAMERA_CAPTURE) {
                        final ContentResolver cr = CardDetailActivity.this
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

                                    thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(CardDetailActivity.this, 500, 500, picUri);


                                    imgAttachmentImage.setImageBitmap(thePic);
                                    imageView.setImageBitmap(thePic);

                                    imageFile = getRealPathFromURI(picUri);
                                    attatchMentUploadToCard(boardCardList);

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
                    imgAttachmentImage.setImageBitmap(thePic);
                    imageView.setImageBitmap(thePic);


                    imageFile = getRealPathFromURI(picUri);
                    attatchMentUploadToCard(boardCardList);

                }
                break;
            case SELECT_IMAGE:
                if (requestCode == SELECT_IMAGE) {

                    Log.e("SELECT_IMAGE", "SELECT_IMAGE");

                    picUri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                        try {

                            thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(CardDetailActivity.this, 500, 500, picUri);

                            imgAttachmentImage.setImageBitmap(thePic);
                            imageView.setImageBitmap(thePic);

                            imageFile = getRealPathFromURI(picUri);
                            attatchMentUploadToCard(boardCardList);
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

                                thePic = GetBitmapImageCompatiablewithDeviceFromdecodeUri(CardDetailActivity.this, 500, 500, picUri);


                                imgAttachmentImage.setImageBitmap(thePic);
                                imageView.setImageBitmap(thePic);
                                imageFile = getRealPathFromURI(picUri);

                                attatchMentUploadToCard(boardCardList);
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
            Toast toast = Toast.makeText(CardDetailActivity.this, errorMessage,
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
        Cursor cursor = CardDetailActivity.this.getContentResolver().query(contentURI, null, null, null, null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }



    private void attatchMentUploadToCard(BoardCardList boardcardlist) {


        hud = KProgressHUD.create(CardDetailActivity.this)
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



        params.put("name",boardCardList.getName());



        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                getAttachmentFileFromServer(boardCardList,imageView);



                try {


                } catch (Exception ex) {
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                getAttachmentFileFromServer(boardCardList,imageView);

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
