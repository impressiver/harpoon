package com.webmetrics.providers;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

@Singleton
public class MongoProvider implements Provider<Mongo> {
    private Mongo db;

    public Mongo get() {
        if (db == null) {
            try {
                db = new Mongo();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return db;
    }
}
