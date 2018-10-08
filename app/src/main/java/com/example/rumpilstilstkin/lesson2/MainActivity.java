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

        Action1<Long> sdf = new Action1<Long>() {

            @Override
            public void call(Long s) {
                Log.d("Dto", "onNext " + s);
            }
        };

        Observable<Long> observable1 = Observable.interval(5, TimeUnit.SECONDS);

        Observer<Long> observer = new Observer<Long>() {

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
            public void onNext(Long s) {
                Log.d("Dto", "onNext " + (s));
            }
        };

       // observable1.subscribe(observer);

        Observable<Integer> obs = Observable.just(1, 2, 3, 4);

        /*obs.subscribe(new Observer<Integer>() {

            @Override
            public void onCompleted() {
                Log.d("Dto", "onComplate");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d("Dto", "onNext + " + integer);
            }
        });

       /* Observable.just(1, 32, 3, 4, 44, 5, 8, 2, 99)
                .filter(new Func1<Integer, Boolean>() {

                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 5;
                    }
                })
                .take(5)
                .distinct()
                .mergeWith(Observable.just(35530, 12))
                .map(new Func1<Integer, String>() {

                         @Override
                         public String call(Integer s) {
                             return String.valueOf(s * 10);
                         }
                     }

                )
                .subscribe(new Observer<String>() {

                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(String s) {
                                   Log.d("Dto", s);
                               }
                           }
                );*/

       // someHot();

        //obs.subscribe(observer);

        Observable.just("2", "13", "y43", "5", "y43", "1", "7", "30", "3", "1")
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
                .doOnError(new Action1<Throwable>() {

                               @Override
                               public void call(Throwable throwable) {
                                   Log.d("Dto", "onError");
                               }
                           }

                )
                .onErrorResumeNext(Observable.just(2, 4, 5));
        // .onBackpressureBuffer()
        //  .subscribe(observer);
         myOb();
    }

    private void myOb() {
        Observable.OnSubscribe<Integer> onSubscriber = new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10000; i++) {
                    Log.d("Dto", "onCall " + i);
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
               //  main.setText("onNext " + integer);
                Log.d("Dto", "onNext " + integer);
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("Dto", "interrupted " + e);
                    return;
                }
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
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void someHot() {
        ConnectableObservable<Long> observable = Observable
                .interval(1, TimeUnit.SECONDS)
                .replay();
        observable.connect();

        subscription = observable.subscribe(new Action1<Long>() {

            @Override
            public void call(Long aLong) {
                Log.d("Dto", "call = " + aLong);
            }
        });
    }

    public void action(View v) {
        if (subscription != null) {
            if (subscription.isUnsubscribed()) {
                someHot();
            }
            else {
                subscription.unsubscribe();
            }
        }
    }

    private void backpressure() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(60)
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        Log.d("Dto", "post " + aLong);
                    }
                })
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                        Log.d("Dto", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Dto", "onError " + e);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d("Dto", "onNext " + aLong);
                        try {
                            TimeUnit.MICROSECONDS.sleep(500);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
