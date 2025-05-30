package org.openlmis.core.training;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;

import org.openlmis.core.LMISApp;
import org.openlmis.core.manager.SharedPreferenceMgr;
import org.openlmis.core.service.SyncUpManager;
import org.openlmis.core.utils.Constants;

import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TrainingSyncAdapter {
    @Inject
    SyncUpManager syncUpManager;

    @Inject
    SharedPreferenceMgr sharedPreferenceMgr;

    Context context;

    protected String onPerformSync() {
        context = LMISApp.getContext();
        triggerFakeSync();
        return "immediate sync up requested";
    }

    private void triggerFakeSync() {
        sendSyncStartBroadcast();

        boolean isFakeSyncRnrSuccessful = syncUpManager.fakeSyncRnr();
        if (isFakeSyncRnrSuccessful) {
            sharedPreferenceMgr.setRnrLastSyncTime();
        }

        boolean isFakeSyncStockSuccessful = syncUpManager.fakeSyncStockCards();
        if (isFakeSyncStockSuccessful) {
            sharedPreferenceMgr.setStockLastSyncTime();
        }

        syncUpManager.fakeSyncUpUnSyncedStockCardCodes();
        if (!sharedPreferenceMgr.hasSyncedVersion()) {
            sharedPreferenceMgr.setSyncedVersion(true);
        }
        syncUpManager.fakeSyncUpCmms();
        syncUpManager.fakeSyncRapidTestForms();
        sendSyncFinishedBroadcast();
    }

    private void sendSyncStartBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Constants.INTENT_FILTER_START_SYNC_DATA);
        context.sendBroadcast(intent);
    }

    private void sendSyncFinishedBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Constants.INTENT_FILTER_FINISH_SYNC_DATA);
        context.sendBroadcast(intent);
    }

    public void requestSync() {
        Single.create(new Single.OnSubscribe<String>() {
            @Override
            public void call(SingleSubscriber<? super String> singleSubscriber) {
                singleSubscriber.onSuccess(onPerformSync());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String message) {
                Log.d("training sync", message);
            }
        });
    }
}
