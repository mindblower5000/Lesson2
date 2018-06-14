package com.example.rumpilstilstkin.lesson2;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    Subscription subscription;
    Button main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = findViewById(R.id.am_btn_main);

        List<String> liters = Arrays.asList("a", "b", "c", "d");

        Action1<Integer> action = new Action1<Integer>() {

            @Override
            public void call(Integer s) {
                Log.d("Dto", "onNext " + s);
            }
        };

        Observable<Long> observable1 = Observable.interval(5, TimeUnit.SECONDS);

        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onCompleted() {
                Log.d("Dto", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Dto", "onError");
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer s) {
                Log.d("Dto", "onNext " + (s * 2));
            }
        };

        subscription = Observable.just("1", "2", "w3", "43", "5")
                .mergeWith(Observable.just("1", "7", "30", "3", "1"))
                .distinct()
                .filter(new Func1<String, Boolean>() {

                    @Override
                    public Boolean call(String s) {
                        return s.contains("3");
                    }
                })
                .map(new Func1<String, Integer>() {

                    @Override
                    public Integer call(String s) {
                        return Integer.parseInt(s);
                    }
                })
                .onErrorResumeNext(Observable.just("1", "2", "3")
                        .map(new Func1<String, Integer>() {

                            @Override
                            public Integer call(String s) {
                                return Integer.parseInt(s);
                            }
                        })
                )
                .onBackpressureBuffer()
                .subscribe(observer);

        // myOb();
    }

    private void myOb() {
        Observable.OnSubscribe<Integer> onSubscriber = new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    subscriber.onNext(i);
                }
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onCompleted();
            }
        };

        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onCompleted() {
                Log.d("Dto", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Dto", "onError");
            }

            @Override
            public void onNext(Integer integer) {
                // main.setText("onNext " + integer);
                Log.d("Dto", "onNext " + integer);
            }
        };

        Func1<Integer, Integer> myFunc = new Func1<Integer, Integer>() {

            @Override
            public Integer call(Integer s) {
                return s * 2;
            }
        };

        subscription = Observable
                .create(onSubscriber)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .map(myFunc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void someHot() {
        ConnectableObservable<Long> observable = Observable
                .interval(1, TimeUnit.SECONDS)
                .take(5)
                .replay();
        observable.connect();
    }

    public void action(View v) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
