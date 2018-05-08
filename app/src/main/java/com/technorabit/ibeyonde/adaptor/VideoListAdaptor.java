package com.technorabit.ibeyonde.adaptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.CommonPoolExecutor;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.technorabit.ibeyonde.HistorActivity;
import com.technorabit.ibeyonde.LiveViewActivity;
import com.technorabit.ibeyonde.R;
import com.technorabit.ibeyonde.anim.BackgroundToForegroundTransformer;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.fragment.TabFragment;
import com.technorabit.ibeyonde.model.VideoItem;
import com.technorabit.ibeyonde.util.AutoRotateUtil;
import com.technorabit.ibeyonde.util.SharedUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import yjkim.mjpegviewer.MjpegView;

/**
 * Created by raja on 18/02/18.
 */

public class VideoListAdaptor extends RecyclerView.Adapter<VideoListAdaptor.VideoViewHolder> {


    private Context context;
    private TabFragment.Type type;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    private boolean destroyed = false;

    public VideoListAdaptor(Context context, TabFragment.Type type) {
        this.type = type;
        this.context = context;
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(itemView);
    }

    public void setVideoItems(ArrayList<VideoItem> videoItems) {
        this.videoItems = videoItems;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        final VideoItem videoItem = videoItems.get(position);
        holder.device_name.setText(videoItem.uuid + ":" + videoItem.device_name);
        if (type == TabFragment.Type.LIVE) {
            initLiveCall(videoItem, holder);
            holder.img_items.setVisibility(View.GONE);
            holder.video_item.setVisibility(View.VISIBLE);
            holder.video_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LiveViewActivity.class);
                    intent.putExtra("udid", videoItem.uuid);
                    context.startActivity(intent);
                }
            });
            holder.root_square_layout.setOnClickListener(null);
        } else {
            initMotionCall(videoItem, holder.img_items);
            holder.img_items.setVisibility(View.VISIBLE);
            holder.video_item.setVisibility(View.GONE);
            holder.root_square_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, HistorActivity.class);
                    intent.putExtra("udid", videoItem.uuid);
                    context.startActivity(intent);
                }
            });
            holder.video_view.setOnClickListener(null);
        }

    }

    private void initMotionCall(VideoItem videoItem, final ViewPager img_item) {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        HttpClientManager client = HttpClientManager.get(context);
        client.setUsername(SharedUtil.get(context).getString("username"));
        client.setPassword(SharedUtil.get(context).getString("password"));
        client.diskCacheEnable(false);
        String url = AppConstants.LATEST_ALERTS.replace(AppConstants.REPLACER, "");
        url = url + "&uuid=" + videoItem.uuid;
        client.new NetworkTask<Void, String>(null, Http.GET) {
            @Override
            protected void onPostExecute(final String liveUrl) {
                super.onPostExecute(liveUrl);
                if (liveUrl != null) {
                    img_item.setVisibility(View.VISIBLE);
                    loadToImageView(liveUrl, img_item);
                } else {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(url));
    }

    private void loadToImageView(final String liveUrl, final ViewPager img_item) {
        try {
            final JSONArray jsonArray = new JSONArray(liveUrl);
            ArrayList<String> ImagesArray = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                ImagesArray.add(jsonArray.getJSONArray(i).getString(0));
            }
            new AutoRotateUtil(img_item);
            img_item.setAdapter(new BunchImageAdapter(context, ImagesArray));
            img_item.setPageTransformer(true, new BackgroundToForegroundTransformer());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }


    class ViewHolder {
        public ViewHolder(ViewPager imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        public ViewPager imageView;
        public String url;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ViewHolder img_item = (ViewHolder) msg.obj;
            if (!destroyed && img_item.imageView != null) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder);
                requestOptions.error(R.drawable.error);
//                Glide.with(context).setDefaultRequestOptions(requestOptions).load(img_item.url).thumbnail(0.8f).into(img_item.imageView);
            }
        }
    };


//    @Override
//    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView);
//        int childCount = recyclerView.getChildCount();
//        //We need to stop the player to avoid a potential memory leak.
//        for (int i = 0; i < childCount; i++) {
//            VideoViewHolder holder = (VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//            if (holder != null && holder.videoView != null) {
//                holder.videoView.stopPlayback();
//            }
//        }
//    }

    @Override
    public void onViewAttachedToWindow(VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        holder.videoView.start();
    }

    @Override
    public void onViewDetachedFromWindow(VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        holder.videoView.stopPlayback();
    }

    private void initLiveCall(final VideoItem videoItem, final VideoViewHolder holder) {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        HttpClientManager client = HttpClientManager.get(context);
        client.setUsername(SharedUtil.get(context).getString("username"));
        client.setPassword(SharedUtil.get(context).getString("password"));
        client.diskCacheEnable(false);
        String url = AppConstants.LIVE_VIEW.replace(AppConstants.REPLACER, "");
        url = url + "&uuid=" + videoItem.uuid + "&quality=BINI";
        client.new NetworkTask<Void, String>(null, Http.GET) {
            @Override
            protected void onPostExecute(final String liveUrl) {
                super.onPostExecute(liveUrl);
                if (liveUrl != null) {
                    holder.video_view.setVisibility(View.VISIBLE);
                    try {
                        holder.video_view.Start(liveUrl);
//                        holder.video_view.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl));
//                    context.startActivity(browserIntent);
                } else {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(url));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        public MjpegView video_view;
        public ViewPager img_items;
        public TextView device_name;
        public FrameLayout video_item;
        public View root_square_layout;


        public VideoViewHolder(View itemView) {
            super(itemView);
            root_square_layout = itemView.findViewById(R.id.root_square_layout);
            device_name = itemView.findViewById(R.id.device_name);
            img_items = itemView.findViewById(R.id.img_item);
            video_item = itemView.findViewById(R.id.video_item);
            video_view = itemView.findViewById(R.id.mpeg_player);
        }
    }

}
