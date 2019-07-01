package cn.rjgc.voicechat.util;

import android.media.*;
import android.media.AudioManager;

/**
 * Created by Forever on 2015/6/7.
 */
public class MediaManager {

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(String filePath,
                                 MediaPlayer.OnCompletionListener onCompletionListener){
        if(mMediaPlayer==null){
            mMediaPlayer=new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (Exception e){

        }
    }

    public static void pause(){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause=true;
        }
    }

    public static void resume(){
        if(mMediaPlayer!=null&& isPause){
            mMediaPlayer.start();
            isPause=false;
        }
    }

    /**
     * 释放资源
     */
    public static void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }
}
