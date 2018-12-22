package com.cm.retailer.app;

import android.support.multidex.MultiDexApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Retailer extends MultiDexApplication{
    final int VERSION = 2;
    final String DATABASE_NAME = "retailer.realm";

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(DATABASE_NAME)
                .schemaVersion(VERSION)
                .build();
        Realm.setDefaultConfiguration(config);

    }

}
