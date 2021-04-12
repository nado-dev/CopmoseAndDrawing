package me.aaronfang.demos;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


public class RetryWithTimeInJava<T> implements Function<Observable<Throwable>, ObservableSource<T>> {
    int current = -1;
    int[] timeDelay = new int[]{50, 100, 150, 200};
    @Override
    public Observable apply(@NotNull Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            ++current;
            Log.d("RetryWithTime", "retry" + current + String.format("time: %tT", new Date()));
            if (current < timeDelay.length) {
                return Observable.timer(timeDelay[current], TimeUnit.MILLISECONDS);
            }
            else {
                return Observable.error(throwable);
            }
        });
    }
}
