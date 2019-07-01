package cn.rjgc.voicechat.util;

import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

/**
 * Created by Forever on 2015/6/7.
 * 单例模式
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepared;

    private static AudioManager mInstance;

    public AudioManager(String dir){
        mDir=dir;
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mListener;
    public void setOnAudioStateListener(AudioStateListener listener){
        mListener=listener;
    }

    public static AudioManager getInstance(String dir){
        if(mInstance==null){
            //同步
            synchronized (AudioManager.class){
                if(mInstance==null){
                    mInstance=new AudioManager(dir);
                }
            }
        }

        return mInstance;
    }

    public void prepareAudio(){
        try {

            isPrepared=false;

            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath=file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared=true;
            if(mListener!=null){
                mListener.wellPrepared();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名称
     * @return
     */
    private String generateFileName(){
        return UUID.randomUUID().toString()+".amr";
    }

    /**
     *
     * @param maxLevel 7
     * @return 1-7之间的数
     */
    public int getVoiceLevel(int maxLevel){
        if(isPrepared){
            try {
                //mMediaRecorder.getMaxAmplitude()返回1-32767
                return maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1;
            }catch (Exception e){

            }
        }
        return 1;
    }
    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder=null;
    }
    public void cancel(){

        release();
        if(mCurrentFilePath!=null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath=null;
        }
    }

    public String getCurrentFilePath(){
        return mCurrentFilePath;
    }
}
