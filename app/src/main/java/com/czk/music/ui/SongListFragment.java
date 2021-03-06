package com.czk.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.adapter.SongListAdapter;
import com.czk.music.bean.Song;
import com.czk.music.util.HttpUtil;
import com.czk.music.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SongListFragment extends Fragment {

    private Context mContext;
    private View view;
    private List<Song> mList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_song_list,container,false);
        initSongList();
        return view;
    }

    private final int UPDATE_SONGLIST = 1;
    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_SONGLIST:
                    //更新歌曲
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    RecyclerView recyclerView = view.findViewById(R.id.song_list_recycler);
                    recyclerView.setLayoutManager(layoutManager);
                    SongListAdapter adapter = new SongListAdapter(mContext,mList);
                    recyclerView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    //初始化歌单信息（包括歌单拥有的歌曲）
    public void initSongList(){
        Bundle bundle =  getArguments();
        String disssid = (String) bundle.get("dissid");
        Log.d("songlistfrag",disssid +"hhhh");
        if(disssid!=null){
            String url = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg";
            Map<String,String> map = new ArrayMap<>();
            map.put("disstid",disssid);
            map.put("format","json");
            map.put("type","1");
            map.put("onlysong","0");
            map.put("new_format","1");
            //获取歌单数据
            HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mList = JsonUtil.getCdSong(response);
                    Message message = new Message();
                    message.what = UPDATE_SONGLIST;
                    handler.sendMessage(message);
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(mContext,"加载歌曲失败");
                }
            },map);
        }else {
            int id = (int) bundle.get("id");
            String url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid="+id+"&_=1512563984096";
            //获取歌单数据
            HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mList = JsonUtil.getTopSong(response);
                    Message message = new Message();
                    message.what = UPDATE_SONGLIST;
                    handler.sendMessage(message);
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(mContext,"加载歌曲失败");
                }
            },null);
        }

    }
}
