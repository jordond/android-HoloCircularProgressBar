/**
 * Copyright (C) 2015, Jordon de Hoog
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ca.hoogit.countdownexample;

/**
 * @author jordon
 * @date 17/07/15
 * @description
 *
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Simple timer class which count up until stopped.
 * Inspired by {@link android.os.CountDownTimer}
 */
public abstract class CountUpTimer {

    private final long interval;
    private final long max;
    private long base;

    public CountUpTimer(long max, long interval) {
        this.max = max;
        this.interval = interval;
    }

    public void start() {
        base = SystemClock.elapsedRealtime();
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void stop() {
        handler.removeMessages(MSG);
    }

    public void reset() {
        synchronized (this) {
            base = SystemClock.elapsedRealtime();
        }
    }

    abstract public void onTick(long elapsedTime);

    public abstract void onFinish();

    private static final int MSG = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountUpTimer.this) {
                long elapsedTime = SystemClock.elapsedRealtime() - base;
                if (elapsedTime >= max) {
                    onFinish();
                } else {
                    onTick(elapsedTime);
                    sendMessageDelayed(obtainMessage(MSG), interval);
                }
            }
        }
    };
}