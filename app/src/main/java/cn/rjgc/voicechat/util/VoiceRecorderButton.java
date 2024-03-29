package cn.rjgc.voicechat.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.rjgc.voicechat.R;

/**
 * Created by Forever on 2015/6/7.
 */
public class VoiceRecorderButton extends AppCompatButton implements AudioManager.AudioStateListener {

    private static final int STATE_NORMAL=1;
    private static final int STATE_RECORDING=2;
    private static final int STATE_WANT_TO_CANCEL=3;

    private static final int DISTANCE_Y_CANCEL=50;

    private int mCurState=STATE_NORMAL;

    private boolean isRecording=false;//还没有录音

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    private float mTime;//录音时长

    //是否出发longclick,如果触发了，需要释放资源
    private boolean mReady;

    public VoiceRecorderButton(Context context) {
        this(context, null);
    }

    public VoiceRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager=new DialogManager(getContext());

        String dir= Environment.getExternalStorageDirectory()+"/recorder_voice";
        mAudioManager=AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady=true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后回调
     */
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }

    private AudioFinishRecorderListener mListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener=listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVoiceLevelRunnable=new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime+=0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                }catch (InterruptedException e){

                }

            }
        }
    };

    private static final int MSG_AUDIO_PREPARED=0x110;
    private static final int MSG_VOICE_CHANGED=0x111;
    private static final int MSG_DIALOG_DIMISS=0x112;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //真正显示应该在audio end prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));

                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int x=(int)event.getX();
        int y=(int)event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording){
                    //根据X,Y的坐标，判断是否取消发送
                    if(wantToCancel(x,y)){
                        changeState(STATE_WANT_TO_CANCEL);
                    }else{
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if(!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }

                if(!isRecording||mTime<0.6f){
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);//1300毫秒后关闭Dialog
                }else if(mCurState==STATE_RECORDING){//正常录制结束
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if(mListener!=null){
                        mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                }else if(mCurState==STATE_WANT_TO_CANCEL){
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 回复状态及标识位
     */
    private void reset(){
        isRecording=false;
        mReady=false;
        mTime=0;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x,int y){
        //判断手指横坐标是否超出按钮范围,getWidth为按钮的宽度
        if(x<0|| x>getWidth()){
            return true;
        }
        //判断手指纵坐标是否超出按钮范围,getHeight为按钮的高度
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }

    private void changeState(int state){
        if(mCurState!=state){
            mCurState=state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.recorder_recording);

                    if(isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.recorder_want_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }
}
