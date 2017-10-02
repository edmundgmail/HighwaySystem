package com.lrs.streaming.utils;

import com.mongodb.CursorType;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;
import rx.Observable;

import java.util.concurrent.CountDownLatch;

/**
 * Created by vagrant on 10/2/17.
 */
public class MongoMonitor extends Runnable {
    @Override
    public void run() {
        CountDownLatch latch = new CountDownLatch(1);

        MongoDatabase db = MongoClients.create().getDatabase("road");
        MongoCollection<Document> collection = db.getCollection("RoadTable");
        Observable.create(subscriber ->
                collection.find()
                        .cursorType(CursorType.TailableAwait)
                        .forEach(subscriber::onNext, (aVoid, throwable) -> {
                            if (throwable == null) {
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(throwable);
                            }
                        })
        ).subscribe(System.out::println, Throwable::printStackTrace, latch::countDown);

        latch.await();
        System.out.println("Completed");
    }
}