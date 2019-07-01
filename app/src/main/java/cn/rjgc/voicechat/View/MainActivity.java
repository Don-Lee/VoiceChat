package cn.rjgc.voicechat.View;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.rjgc.voicechat.R;
import cn.rjgc.voicechat.util.MediaManager;
import cn.rjgc.voicechat.util.RecorderAdapter;
import cn.rjgc.voicechat.util.VoiceRecorderButton;

/**
 * Created by Forever on 2015/6/7.
 * todo android 6.0以上请动态获取权限
 */
public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mData=new ArrayList<Recorder>();
    private VoiceRecorderButton mVoiceRecorderButton;

    private View mAnimView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView=(ListView)findViewById(R.id.id_lv);
        mVoiceRecorderButton=(VoiceRecorderButton)findViewById(R.id.id_voice_recorder_btn);
        mVoiceRecorderButton.setAudioFinishRecorderListener(new VoiceRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder=new Recorder(seconds,filePath);
                mData.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mData.size()-1);
            }
        });

        mAdapter=new RecorderAdapter(this,mData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAnimView!=null){
                    mAnimView.setBackgroundResource(R.mipmap.adj);
                    mAnimView=null;
                }
                //播放动画
                mAnimView=view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim=(AnimationDrawable)mAnimView.getBackground();
                anim.start();
                //播放音频
                MediaManager.playSound(mData.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    public class Recorder{
        float time;
        String filePath;

        public Recorder(float time, String filePath) {
            this.time = time;
            this.filePath = filePath;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

}
