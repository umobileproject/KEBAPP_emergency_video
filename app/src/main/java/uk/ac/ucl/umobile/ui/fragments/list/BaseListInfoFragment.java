package uk.ac.ucl.umobile.ui.fragments.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

//import uk.ac.ucl.umobile.backend.NotificationService;
//import uk.ac.ucl.umobile.backend.VideoListService;
import uk.ac.ucl.kbapp.KebappService;
import uk.ac.ucl.umobile.data.Content;
import uk.ac.ucl.umobile.data.VideoDatabaseHandler;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.ListInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamType;
//import uk.ac.ucl.umobile.ui.util.Constants;
import uk.ac.ucl.umobile.utils.G;

import java.io.File;
import java.util.List;

import icepick.State;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DATE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public abstract class BaseListInfoFragment<I extends ListInfo> extends BaseListFragment<I, ListExtractor.NextItemsResult> {

    @State
    protected int serviceId = -1;
    @State
    protected String name;
    @State
    protected String url;

    protected I currentInfo;
    protected String currentNextItemsUrl;
    protected Disposable currentWorker;

    private Handler handler;

    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        setTitle(name);
        showListFooter(hasMoreItems());
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        if (currentWorker != null) currentWorker.dispose();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        handler = new Handler();
        super.onViewCreated(rootView, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check if it was loading when the fragment was stopped/paused,

         mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                G.Log(TAG,"Broadcast received "+intent);
                switch (intent.getAction()) {
                    case KebappService.DOWNLOAD_COMPLETED:
                        startLoading(true);
                        break;
                }
            }
        };
        getContext().registerReceiver(mBroadcastReceiver, KebappService.getIntentFilter());

        if (wasLoading.getAndSet(false)) {
            if (hasMoreItems() && infoListAdapter.getItemsList().size() > 0) {
                loadMoreItems();
            } else {
                doInitialLoadLogic();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentWorker != null) currentWorker.dispose();
        currentWorker = null;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    public void setTitle(String title) {
        Log.d(TAG, "setTitle() called with: title = [" + title + "]");
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Load and handle
    //////////////////////////////////////////////////////////////////////////*/

    protected void doInitialLoadLogic() {
        if (DEBUG) Log.d(TAG, "doInitialLoadLogic() called");
        if (currentInfo == null) {
            startLoading(false);
        } else handleResult(currentInfo);
    }

    /**
     * Implement the logic to load the info from the network.<br/>
     * You can use the default implementations from
     *
     * @param forceLoad allow or disallow the result to come from the cache
     */
    protected abstract Single<I> loadResult(boolean forceLoad);

    @Override
    public void startLoading(boolean forceLoad) {

        File dir = getContext().getFilesDir();
        File[] subFiles = dir.listFiles();
        G.Log(TAG,"Files " +subFiles.length);

        VideoDatabaseHandler db = new VideoDatabaseHandler(getActivity());
        List<Content> content = db.getContentDownloaded();


        infoListAdapter.clearStreamItemList();
        for (Content cn : content) {
            // Writing Contacts to log
            G.Log(TAG,"Name:"+ cn.getName()+ " desc:" + cn.getText() + " url:"+cn.getUrl());
            boolean found=false;
            if (subFiles != null) {
                //G.Log("Files " +subFiles);
                for (File file : subFiles) {
                    G.Log("Filename " + cn.getName() + " " +file.getAbsolutePath()+" "+file.getName()+" "+file.length());
                    if (file.getName().equals(cn.getName()+".mp4")) {

                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        try {
                            retriever.setDataSource(file.getAbsolutePath());

                            StreamInfoItem info = new StreamInfoItem();
                            info.stream_type = StreamType.VIDEO_STREAM;
                            G.Log(TAG,"METADATA :"+retriever.extractMetadata(METADATA_KEY_TITLE)+" "+retriever.extractMetadata(METADATA_KEY_DATE)+" "+retriever.extractMetadata(METADATA_KEY_DURATION));
                            info.service_id = 0;
                            info.thumbnail_url = "file://"+file.getAbsolutePath();
                            info.name = retriever.extractMetadata(METADATA_KEY_TITLE)!=null ? retriever.extractMetadata(METADATA_KEY_TITLE) : cn.getText();
                            info.url = "file://"+file.getAbsolutePath();
                            info.upload_date = retriever.extractMetadata(METADATA_KEY_DATE);
                            info.duration = retriever.extractMetadata(METADATA_KEY_DURATION)!=null ? Integer.parseInt(retriever.extractMetadata(METADATA_KEY_DURATION))/1000 : 0;
                            infoListAdapter.addInfoItem(info);

                        } catch (Exception e) {
                            G.Log("Exception : " + e.getMessage());
                        }
                    }
                }
            }

        }
        /*handler.postDelayed(new Runnable() {
            public void run() {
           startLoading(true);
            }
        }, 1000);*/
      //  isLoading.set(false);

    }

    /**
     * Implement the logic to load more items<br/>
     * You can use the default implementations from
     */
    protected abstract Single<ListExtractor.NextItemsResult> loadMoreItemsLogic();

    protected void loadMoreItems() {
        isLoading.set(true);

        if (currentWorker != null) currentWorker.dispose();
        currentWorker = loadMoreItemsLogic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListExtractor.NextItemsResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull ListExtractor.NextItemsResult nextItemsResult) throws Exception {
                        isLoading.set(false);
                        handleNextItems(nextItemsResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        isLoading.set(false);
                        onError(throwable);
                    }
                });
    }

    @Override
    public void handleNextItems(ListExtractor.NextItemsResult result) {
        super.handleNextItems(result);
        currentNextItemsUrl = result.nextItemsUrl;
        infoListAdapter.addInfoItemList(result.nextItemsList);

        showListFooter(hasMoreItems());
    }

    @Override
    protected boolean hasMoreItems() {
        return !TextUtils.isEmpty(currentNextItemsUrl);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void handleResult(@NonNull I result) {
        super.handleResult(result);
        Log.d(TAG,"Load new result "+result.name);
        url = result.url;
        name = result.name;
        setTitle(name);

        if (infoListAdapter.getItemsList().size() == 0) {
            if (result.related_streams.size() > 0) {
                Log.d(TAG,"Related streams "+result.related_streams.size());
                infoListAdapter.addInfoItemList(result.related_streams);
                showListFooter(hasMoreItems());
            } else {
                infoListAdapter.clearStreamItemList();
                showEmptyState();
            }
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    protected void setInitialData(int serviceId, String url, String name) {
        this.serviceId = serviceId;
        this.url = url;
        this.name = !TextUtils.isEmpty(name) ? name : "";
    }
}
