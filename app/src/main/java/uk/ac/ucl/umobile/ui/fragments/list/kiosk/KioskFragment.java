package uk.ac.ucl.umobile.ui.fragments.list.kiosk;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.named_data.jndn.Data;
import net.named_data.jndn.util.Blob;

import uk.ac.ucl.umobile.R;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.UrlIdHandler;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import uk.ac.ucl.umobile.data.Content;
import uk.ac.ucl.umobile.data.VideoDatabaseHandler;
import uk.ac.ucl.umobile.ui.fragments.list.BaseListInfoFragment;
//import  uk.ac.ucl.umobile.ui.util.ExtractorHelper;
//import uk.ac.ucl.umobile.ui.util.KioskTranslator;

import uk.ac.ucl.umobile.utils.G;
import io.reactivex.Single;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DATE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;
import static  uk.ac.ucl.umobile.utils.AnimationUtils.animateView;

//import uk.ac.ucl.umobile.Report.UserAction;

/**
 * Created by Christian Schabesberger on 23.09.17.
 *
 * Copyright (C) Christian Schabesberger 2017 <chris.schabesberger@mailbox.org>
 * KioskFragment.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class KioskFragment extends BaseListInfoFragment<KioskInfo> {

    private String kioskId = "";
    private Button button;
    private static final int CHOOSE_FILE_REQUESTCODE = 42;


    /*//////////////////////////////////////////////////////////////////////////
    // Views
    //////////////////////////////////////////////////////////////////////////*/

    private View headerRootLayout;
    private TextView headerTitleView;

    private Menu menu;

    public static KioskFragment getInstance(int serviceId)
            throws ExtractionException {
        return getInstance(serviceId, NewPipe.getService(serviceId)
                .getKioskList()
                .getDefaultKioskId());
    }

    public static KioskFragment getInstance(int serviceId, String kioskId)
            throws ExtractionException {
        KioskFragment instance = new KioskFragment();
        StreamingService service = NewPipe.getService(serviceId);
        UrlIdHandler kioskTypeUrlIdHandler = service.getKioskList()
                .getUrlIdHandlerByType(kioskId);
        instance.setInitialData(serviceId,
                kioskTypeUrlIdHandler.getUrl(kioskId),
                kioskId);
        instance.kioskId = kioskId;
        return instance;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(useAsFrontPage && isVisibleToUser) {
            try {
            //    activity.getSupportActionBar().setTitle(KioskTranslator.getTranslatedKioskName(kioskId, getActivity()));
            } catch (Exception e) {
                onError(e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.menu_item_clear) {
            deleteVideos();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        useAsFrontPage(true);
        View v = inflater.inflate(R.layout.fragment_kiosk, container, false);
        //button = (Button)v.findViewById(R.id.filebutton);
        //button.setText("Select video");
        //button.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        openFile("*/*");
                // Code here executes on main thread after user presses button
        //    }
        //});
        return v;    }

    /*//////////////////////////////////////////////////////////////////////////
    // Menu
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar supportActionBar = activity.getSupportActionBar();

     //   defaultPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        inflater.inflate(R.menu.video_detail_menu, menu);
        if (supportActionBar != null && useAsFrontPage) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }
        this.menu = menu;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Load and handle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public Single<KioskInfo> loadResult(boolean forceReload) {
        String contentCountry = PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getString(getString(R.string.search_language_key),
                        getString(R.string.default_language_value));
        //return ExtractorHelper.getKioskInfo(serviceId, url, contentCountry, forceReload);
        return null;
    }

    @Override
    public Single<ListExtractor.NextItemsResult> loadMoreItemsLogic() {
        //return ExtractorHelper.getMoreKioskItems(serviceId, url, currentNextItemsUrl);
        return null;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void showLoading() {
        super.showLoading();
        animateView(itemsList, false, 100);
    }

    @Override
    public void handleResult(@NonNull final KioskInfo result) {
        super.handleResult(result);

        //String title = KioskTranslator.getTranslatedKioskName(result.id, getActivity());
        ActionBar supportActionBar = activity.getSupportActionBar();
        supportActionBar.setTitle(R.string.app_name);

        if (!result.errors.isEmpty()) {
      //      showSnackBarError(result.errors,
      //              UserAction.REQUESTED_PLAYLIST,
      //              NewPipe.getNameOfService(result.service_id), result.url, 0);
        }
    }

    @Override
    public void handleNextItems(ListExtractor.NextItemsResult result) {
        super.handleNextItems(result);

        if (!result.errors.isEmpty()) {
       //     showSnackBarError(result.errors,
       //             UserAction.REQUESTED_PLAYLIST, NewPipe.getNameOfService(serviceId)
       //             , "Get next page of: " + url, 0);
        }
    }

    private void deleteVideos()
    {
        File dir = getContext().getFilesDir();
        File[] subFiles = dir.listFiles();
        G.Log(TAG,"Files " +subFiles.length);

        VideoDatabaseHandler db = new VideoDatabaseHandler(getActivity());
        List<Content> content = db.getContentDownloaded();

        infoListAdapter.clearStreamItemList();
        for (Content cn : content) {
            // Writing Contacts to log
            G.Log(TAG,"Name:"+ cn.getName()+ " desc:" + cn.getText() + " url:"+cn.getUrl());
            if (subFiles != null) {
                //G.Log("Files " +subFiles);
                for (File file : subFiles) {
                    G.Log("Filename " + cn.getName() + " " +file.getAbsolutePath()+" "+file.getName()+" "+file.length());
                    if ((file.getName().equals(cn.getName()+".mp4"))||(file.getName().equals(cn.getName()+".mp4.bin"))) {
                        file.delete();
                    }
                }
            }
            db.rmContent(cn.getUri());

        }

    }

    /*private void activateMalicious()
    {
        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.empty_state_view2);
        linearLayout.setVisibility(View.VISIBLE);
        menu.findItem(R.id.menu_item_act_malicious).setVisible(false);
        menu.findItem(R.id.menu_item_deact_malicious).setVisible(true);

    }

    private void deActivateMalicious()
    {
        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.empty_state_view2);
        linearLayout.setVisibility(View.GONE);
        menu.findItem(R.id.menu_item_act_malicious).setVisible(true);
        menu.findItem(R.id.menu_item_deact_malicious).setVisible(false);

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == CHOOSE_FILE_REQUESTCODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                G.Log(TAG, "Uri: " + uri.toString() + " " +uri.getPath());
                saveFile(uri.getPath());

            }
        }
    }
    private void saveFile(String path)
    {
        try {

            File inputFile = new File(path);
            long fileSize = inputFile.length();
            InputStream inputStream = new FileInputStream(inputFile);
            byte[] buf = new byte[(int) fileSize];
            inputStream.read(buf);

            Blob blob = new Blob(buf);
            Data contentData = new Data();
            contentData.wireDecode(blob);

            G.Log("Content "+ contentData.getName() +" "+  contentData.getContent().size());

            File outputFile = new File(getContext().getFilesDir()+"/"+inputFile.getName());
            if (outputFile.exists()) {
                G.Log(TAG, "File exsist - deleting");
                outputFile.delete();
            }
            G.Log(TAG, "Creating an empty " + outputFile.getAbsolutePath());
            outputFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(outputFile);

            outputStream.write(blob.getImmutableArray());
            outputStream.flush();
            outputStream.close();

            Content c = new Content(contentData.getName().get(2).toEscapedString(), contentData.getName().toString(),contentData.getName().get(2).toEscapedString(), "");
            VideoDatabaseHandler db = new VideoDatabaseHandler(getContext());
            db.addContent(c);
            db.setContentDownloaded(contentData.getName().toString());
            File f = new File(getContext().getFilesDir()+"/"+c.getName()+".mp4");


            G.Log("File received "+contentData.getName() +" "+contentData.getContent().size() + " "+f.getAbsolutePath());
            Blob b = contentData.getContent();
            b.getImmutableArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b.getImmutableArray());
            fos.flush();
            fos.close();

        }catch (Exception e){G.Log("Exception "+e);}
    }
    private void openFile(String minmeType) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", minmeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getContext().getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

}
