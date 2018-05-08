package com.technorabit.ibeyonde.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.R;
import com.technorabit.ibeyonde.adaptor.VideoListAdaptor;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.model.VideoItem;
import com.technorabit.ibeyonde.util.SharedUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by raja on 25/02/18.
 */

public class TabFragment extends Fragment {

    private Type type;
    private VideoListAdaptor videoListAdaptor;
    private RecyclerView recyclerView;


    public enum Type {
        MOTION(1), LIVE(2);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type getType(int value) {
            if (value == MOTION.getValue())
                return MOTION;
            else return LIVE;
        }
    }

    public static TabFragment getInstance(Type type) {
        TabFragment tab = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.getValue());
        tab.setArguments(bundle);
        return tab;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        initArgs();
        recyclerView = view.findViewById(R.id.recyclerList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        videoListAdaptor = new VideoListAdaptor(getActivity(), type);
        recyclerView.setAdapter(videoListAdaptor);
        initData();
        return view;
    }

    public void updateGrid(boolean isChecked) {
        if (!isChecked)
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        else
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initData() {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        final HttpClientManager client = HttpClientManager.get(getActivity());
        client.setUsername(SharedUtil.get(getActivity()).getString("username"));
        client.setPassword(SharedUtil.get(getActivity()).getString("password"));
        client.diskCacheEnable(false);
        client.new NetworkTask<Void, String>(null, Http.GET) {
            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);
                if (res != null) {
                    ArrayList<VideoItem> videoLists = getJsonData(res);
                    videoListAdaptor.setVideoItems(videoLists);
                } else {
                    Log.e("Res", "*******ERROR********");
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(AppConstants.GET_DEVICE_LIST.replace(AppConstants.REPLACER,
                SharedUtil.get(getActivity()).getString("username") + ":" + SharedUtil.get(getActivity()).getString("password") + "@")));
    }

    private ArrayList<VideoItem> getJsonData(String res) {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                VideoItem videoItem = new VideoItem();
                videoItem.uuid = jsonObject.getString("uuid");
                videoItem.user_name = jsonObject.getString("user_name");
                videoItem.device_name = jsonObject.getString("device_name");
                videoItem.type = jsonObject.getString("type");
                videoItem.box_name = jsonObject.getString("box_name");
                videoItem.timezone = jsonObject.getString("timezone");
                videoItem.capabilities = jsonObject.getString("capabilities");
                videoItem.version = jsonObject.getString("version");
                videoItem.setting = jsonObject.getString("setting");
                videoItem.email_alerts = jsonObject.getString("email_alerts");
                videoItem.deviceip = jsonObject.getString("deviceip");
                videoItem.visibleip = jsonObject.getString("visibleip");
                videoItem.port = jsonObject.getString("port");
                videoItem.created = jsonObject.getString("created");
                videoItem.updated = jsonObject.getString("updated");
                videoItem.token = jsonObject.getString("token");
                videoItem.expiry = jsonObject.getString("expiry");
                videoItem.ltoken = jsonObject.getString("ltoken");
                videoItems.add(videoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoItems;
    }

    private void initArgs() {
        this.type = Type.getType(getArguments().getInt("type"));
    }


    public void isDestroyed(){
        if(videoListAdaptor != null)
        videoListAdaptor.setDestroyed(true);
    }
}