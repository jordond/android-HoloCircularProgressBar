package ca.hoogit.countdownexample;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    private ImageButton mButton, mButton2;
    private Button mClick;
    private TextView mText;
    private TextView mCurrent;
    private HoloCircularProgressBar mProgress;
    public shotCounter mCountdown;
    public shotUpCounter mCountup;

    private long mCurrentValue;
    private long mElapsed;
    private boolean mIsRunning;
    private boolean mIsRunning2;

    private boolean mIsCountdown;

    private boolean swap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (ImageButton) findViewById(R.id.button);
        mButton2 = (ImageButton) findViewById(R.id.button2);
        mText = (TextView) findViewById(R.id.seconds);
        mProgress = (HoloCircularProgressBar) findViewById(R.id.progressbar);
        mCurrent = (TextView) findViewById(R.id.current);
        mClick = (Button) findViewById(R.id.click);

        mProgress.setProgress(0.0f);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) {
                    pauseTimer();
                    mButton.setImageResource(R.drawable.ic_av_play_arrow);
                } else {
                    mProgress.setIsCountdown(true);
                    startCountdown(mCurrentValue);
                    mButton.setImageResource(R.drawable.ic_av_pause);
                }
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning2) {
                    pauseTimer2();
                    mButton2.setImageResource(R.drawable.ic_av_play_arrow);
                } else {
                    mProgress.setProgress(0.0f);
                    mProgress.setIsCountdown(false);
                    startCountup(10 * 1000);
                    mButton2.setImageResource(R.drawable.ic_av_pause);
                }
            }
        });

        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swap) {
                    mProgress.setThumbColor(Color.CYAN);
                } else {
                    mProgress.setThumbColor(Color.RED);
                }
                swap = !swap;
            }
        });

        mCurrentValue = TimeUnit.SECONDS.toMillis(10);
        mElapsed = TimeUnit.SECONDS.toMillis(0);

    }

    private void pauseTimer() {
        mCountdown.cancel();
        mIsRunning = false;
    }

    private void pauseTimer2() {
        mCountup.stop();
        mIsRunning2 = false;
    }

    public void startCountdown(long milliseconds) {
        mCountdown = new shotCounter(milliseconds, 100, new shotCounter.onShotEvents() {
            @Override
            public void onTick(long millisUntilFinished) {
                mIsRunning = true;
                mCurrentValue = millisUntilFinished;
                mCurrent.setText(String.valueOf(mCurrentValue));
                updateProgress(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mIsRunning = false;
                mCurrentValue = TimeUnit.SECONDS.toMillis(10);
                mProgress.setProgress(0.0f);
            }
        });
        mCountdown.start();
    }


    private void startCountup(long val) {
        mCountup = new shotUpCounter(val, 100, new shotCounter.onShotEvents() {
            @Override
            public void onTick(long elapsed) {
                mIsRunning2 = true;
                mElapsed = elapsed;
                mCurrent.setText(String.valueOf(elapsed));
                updateProgress2(elapsed);
            }

            @Override
            public void onFinish() {
                mIsRunning = false;
                mCurrentValue = TimeUnit.SECONDS.toMillis(0);
                mProgress.setProgress(0.0f);
            }
        });
        mCountup.start();
    }

    public void updateProgress(long milliseconds) {
        float secondsLeft = milliseconds / 1000.0f;
        float progress = (secondsLeft / 10.0f);

        mText.setText(String.format("%.1f", secondsLeft));

        ObjectAnimator animation = ObjectAnimator.ofFloat(mProgress, "progress", progress);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

    }

    private void updateProgress2(long elapsed) {
        float seconds = elapsed / 1000.0f;
        float progress = seconds / 10f;

        mText.setText(String.format("%.1f", seconds));
        ObjectAnimator animation = ObjectAnimator.ofFloat(mProgress, "progress", progress);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("currentValue", mCurrentValue);
        outState.putBoolean("isRunning", mIsRunning);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentValue = savedInstanceState.getLong("currentValue");
        mIsRunning = savedInstanceState.getBoolean("isRunning");
    }

    @Override
    protected void onPause() {
        if (mIsRunning) {
            mCountdown.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mIsRunning) {
            startCountdown(mCurrentValue);
            mButton.setImageResource(R.drawable.ic_av_pause);
        }
        updateProgress(mCurrentValue);
        super.onResume();
    }

    public static class shotCounter extends CountDownTimer {

        public onShotEvents listener;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public shotCounter(long millisInFuture, long countDownInterval, onShotEvents listener) {
            super(millisInFuture, countDownInterval);
            this.listener = listener;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            listener.onTick(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            listener.onFinish();
        }

        public interface onShotEvents {
            void onTick(long millisUntilFinished);

            void onFinish();
        }
    }

    public static class shotUpCounter extends  CountUpTimer {

        public shotCounter.onShotEvents listener;

        public shotUpCounter(long max, long interval, shotCounter.onShotEvents l) {
            super(max, interval);
            this.listener = l;
        }

        @Override
        public void onTick(long elapsedTime) {
            listener.onTick(elapsedTime);
        }

        @Override
        public void onFinish() {
            listener.onFinish();
        }
    }

}
