/*
 * This program is part of the OpenLMIS logistics management information
 * system platform software.
 *
 * Copyright © 2015 ThoughtWorks, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should
 * have received a copy of the GNU Affero General Public License along with
 * this program. If not, see http://www.gnu.org/licenses. For additional
 * information contact info@OpenLMIS.org
 */

package org.openlmis.core.view.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.inject.Inject;

import org.openlmis.core.LMISApp;
import org.openlmis.core.R;
import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.googleAnalytics.ScreenName;
import org.openlmis.core.googleAnalytics.TrackerActions;
import org.openlmis.core.manager.SharedPreferenceMgr;
import org.openlmis.core.manager.UserInfoMgr;
import org.openlmis.core.model.ReportTypeForm;
import org.openlmis.core.model.User;
import org.openlmis.core.network.InternetCheck;
import org.openlmis.core.persistence.ExportSqliteOpenHelper;
import org.openlmis.core.service.SyncService;
import org.openlmis.core.utils.Constants;
import org.openlmis.core.utils.FileUtil;
import org.openlmis.core.utils.ToastUtil;
import org.openlmis.core.utils.TrackRnREventUtil;
import org.openlmis.core.view.fragment.builders.WarningDialogFragmentBuilder;
import org.openlmis.core.view.fragment.WarningDialogFragment;
import org.openlmis.core.view.widget.IncompleteRequisitionBanner;
import org.openlmis.core.view.widget.SyncTimeView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_home_page)
public class HomeActivity extends BaseActivity {

    @InjectView(R.id.btn_stock_card)
    Button btnStockCard;

    @InjectView(R.id.btn_inventory)
    Button btnInventory;

    IncompleteRequisitionBanner incompleteRequisitionBanner;

    SyncTimeView syncTimeView;

    @InjectView(R.id.btn_kit_stock_card)
    Button btnKitStockCard;

    @InjectView(R.id.btn_lmis_abbott_m2000)
    Button btnLmisAbbottM2000;

    @InjectView(R.id.btn_lmis_abbott_alinity_m)
    Button btnLmisAbbottAlinityM;

    @InjectView(R.id.btn_lmis_roche_cobas_6800)
    Button btnLmisRocheCobas6800;

    @InjectView(R.id.btn_lmis_roche_capctm_96)
    Button btnLmisRocheCapctm96;

    @InjectView(R.id.btn_lmis_hologic_panter)
    Button btnLmisHologicPanter;

    @InjectView(R.id.btn_lmis_biosecurity)
    Button btnLmisBiosecurity;

    @InjectView(R.id.btn_lmis_mpima)
    Button btnLmisMpima;

    @InjectResource(R.integer.back_twice_interval)
    int BACK_TWICE_INTERVAL;

    @Inject
    SyncService syncService;
    @Inject
    InternetCheck internetCheck;

    @Inject
    SharedPreferenceMgr sharedPreferenceMgr;

    @Inject
    WarningDialogFragmentBuilder warningDialogFragmentBuilder;

    private boolean exitPressedOnce = false;

    @Override
    protected ScreenName getScreenName() {
        return ScreenName.HomeScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserInfoMgr.getInstance().getUser() == null) {
            // In case some users use some unknown way entered here!!!
            logout();
            finish();
        } else {
            setTitle(UserInfoMgr.getInstance().getFacilityName());
            syncTimeView = (SyncTimeView) findViewById(R.id.view_sync_time);
            incompleteRequisitionBanner = (IncompleteRequisitionBanner) findViewById(R.id.view_incomplete_requisition_banner);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        registerSyncStartReceiver();
        registerSyncFinishedReceiver();
        registerErrorFinishedReceiver();

        updateButtonConfigView();
    }

    private void registerSyncStartReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_START_SYNC_DATA);
        registerReceiver(syncStartReceiver, filter);
    }

    private void registerSyncFinishedReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_FINISH_SYNC_DATA);
        registerReceiver(syncFinishedReceiver, filter);
    }

    private void registerErrorFinishedReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_ERROR_SYNC_DATA);
        registerReceiver(syncErrorReceiver, filter);
    }

    @Override
    protected int getThemeRes() {
        return R.style.AppTheme_Gray;
    }

    BroadcastReceiver syncStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            syncTimeView.showSyncProgressBarAndHideIcon();
        }
    };

    BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSyncedTime();
        }
    };

    BroadcastReceiver syncErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            syncTimeView.setSyncStockCardLastYearError();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(syncStartReceiver);
        unregisterReceiver(syncFinishedReceiver);
        unregisterReceiver(syncErrorReceiver);
        super.onDestroy();
    }

    private void updateButtonConfigView() {
       List reportTypes = sharedPreferenceMgr.getReportTypesData();
       List<Pair<String, Button>> buttonConfigs= Arrays.asList(
               new Pair<>(Constants.LMIS_ABBOTT_M2000_REPORT, btnLmisAbbottM2000),
               new Pair<>(Constants.LMIS_ABBOTT_ALINITY_M_REPORT, btnLmisAbbottAlinityM),
               new Pair<>(Constants.LMIS_ROCHE_COBAS_6800_REPORT, btnLmisRocheCobas6800),
               new Pair<>(Constants.LMIS_ROCHE_CAPCTM_96_REPORT, btnLmisRocheCapctm96),
               new Pair<>(Constants.LMIS_HOLOGIC_PANTER_REPORT, btnLmisHologicPanter),
               new Pair<>(Constants.LMIS_BIOSECURITY_MATERIAL_REPORT, btnLmisBiosecurity),
               new Pair<>(Constants.LMIS_MPIMA_REPORT, btnLmisMpima)
       );
       for (Pair<String, Button> buttonConfig: buttonConfigs) {
           ReportTypeForm find = getReportType(buttonConfig.first, reportTypes);
           Button button = buttonConfig.second;
           button.setVisibility(find != null ? View.VISIBLE : View.GONE);
       }
    }

    private ReportTypeForm getReportType(String code,  List<ReportTypeForm> reportTypes) {
        for (ReportTypeForm typeForm: reportTypes){
            if (typeForm.getCode().equalsIgnoreCase(code)) {
                return typeForm;
            }
        }
        return null;
    }


    public void onClickStockCard(View view) {
        startActivity(StockCardListActivity.class);
    }

    public void onClickKitStockCard(View view) {
        startActivity(KitStockCardListActivity.class);
    }

    public void onClickInventory(View view) {
        Intent intent = new Intent(HomeActivity.this, PhysicalInventoryActivity.class);
        startActivity(intent);
    }

    public void onClickPatientDataReport(View view) {
        startActivity(new Intent(this, ViaPatientDataReportActivity.class));
    }

    public void onClickRapidTestHistory(View view) {
        Intent intent = new Intent(this, RapidTestReportsActivity.class);
        startActivity(intent);
    }

    public void onClickAL(View view) {
        startActivity(RnRFormListActivity.getIntentToMe(this, Constants.Program.AL_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectAL, Constants.AL_PROGRAM_CODE);
    }

    public void syncData() {
        Log.d("HomeActivity", "requesting immediate sync");
        syncService.requestSyncImmediately();
    }

    public void onClickMMIAHistory(View view) {
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.MMIA_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectMMIA, Constants.MMIA_PROGRAM_CODE);
    }

    public void onClickVIAHistory(View view) {
        startActivity(RnRFormListActivity.getIntentToMe(this, Constants.Program.VIA_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectVIA, Constants.VIA_PROGRAM_CODE);
    }

    public void onClickPtvStockCard(View view) {
        startActivity(RnRFormListActivity.getIntentToMe(this, Constants.Program.PTV_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectPTV, Constants.PTV_PROGRAM_CODE);
    }

    public void onClickLmisAbbotM2000History(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_ABBOTT_M2000_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_ABBOTT_M2000_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisAbbottM2000, Constants.LMIS_ABBOTT_M2000_PROGRAM_CODE);
    }

    public void onClickLmisAbbottAlinityMHistory(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_ABBOTT_ALINITY_M_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_ABBOTT_ALINITY_M_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisAbbottAlinityM, Constants.LMIS_ABBOTT_ALINITY_M_PROGRAM_CODE);
    }

    public void onClickLmisRocheCobas6800History(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_ROCHE_COBAS_6800_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_ROCHE_COBAS_6800_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisRocheCobas6800, Constants.LMIS_ROCHE_COBAS_6800_PROGRAM_CODE);
    }

    public void onClickLmisRocheCapctm96History(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_ROCHE_CAPCTM_96_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_ROCHE_CAPCTM_96_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisRocheCapctm96, Constants.LMIS_ROCHE_CAPCTM_96_PROGRAM_CODE);
    }

    public void onClickLmisHologicPanterHistory(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_HOLOGIC_PANTER_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_HOLOGIC_PANTER_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisHologicPanter, Constants.LMIS_HOLOGIC_PANTER_PROGRAM_CODE);
    }

    public void onClickLmisBiosecurityHistory(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_BIOSECURITY_MATERIAL_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_BIOSECURITY_MATERIAL_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisBiosecurityMaterial, Constants.LMIS_BIOSECURITY_MATERIAL_PROGRAM_CODE);
    }

    public void onClickLmisMpimaHistory(View view) {
        Constants.currentLmisProgram = Constants.Program.LMIS_MPIMA_PROGRAM;
        startActivity(RnRFormListActivity.getIntentToMe(this,  Constants.Program.LMIS_MPIMA_PROGRAM));
        TrackRnREventUtil.trackRnRListEvent(TrackerActions.SelectLmisMpima, Constants.LMIS_MPIMA_PROGRAM_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        incompleteRequisitionBanner.setIncompleteRequisitionBanner();
        if(sharedPreferenceMgr.isStockCardLastYearSyncError()) {
            syncTimeView.setSyncStockCardLastYearError();
        } else {
            setSyncedTime();
        }
    }

    protected void setSyncedTime() {
        if (!sharedPreferenceMgr.shouldSyncLastYearStockData() && !sharedPreferenceMgr.isSyncingLastYearStockCards()) {
            syncTimeView.showLastSyncTime();
            updateButtonConfigView();
        } else {
            syncTimeView.setSyncStockCardLastYearText();
        }
    }

    @Override
    public void onBackPressed() {
        if (exitPressedOnce) {
            moveTaskToBack(true);
        } else {
            ToastUtil.show(R.string.msg_back_twice_to_exit);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitPressedOnce = false;
                }
            }, BACK_TWICE_INTERVAL);
        }
        exitPressedOnce = !exitPressedOnce;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                startActivity(LoginActivity.class);
                finish();
                return true;
            case R.id.action_sync_data:
                syncData();
                return true;
            case R.id.action_wipe_data:
                alertWipeData();
                return true;
            case R.id.action_export_db:
                exportDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportDB() {
        File currentDB = new File(Environment.getDataDirectory(), "//data//" + LMISApp.getContext().getApplicationContext().getPackageName() + "//databases//lmis_db");
        File tempBackup = new File(Environment.getDataDirectory(), "//data//" + LMISApp.getContext().getApplicationContext().getPackageName() + "//databases//lmis_copy");
        File externalBackup = new File(Environment.getExternalStorageDirectory(), "lmis_backup");
        try {
            FileUtil.copy(currentDB, tempBackup);
            ExportSqliteOpenHelper.removePrivateUserInfo(this);
            FileUtil.copy(tempBackup, externalBackup);
            ToastUtil.show(Html.fromHtml(getString(R.string.msg_export_data_success, externalBackup.getPath())));
        } catch (Exception e) {
            new LMISException(e).reportToFabric();
            ToastUtil.show(e.getMessage());
        } finally {
            if (tempBackup.canRead()) {
                tempBackup.delete();
            }
        }
    }

    public static Intent getIntentToMe(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private void alertWipeData() {
        internetCheck.execute(validateConnectionListener());
    }

    private InternetCheck.Callback validateConnectionListener() {

        return new InternetCheck.Callback() {
            @Override
            public void launchResponse(Boolean internet) {
//                if (!internet && !LMISApp.getInstance().getFeatureToggleFor(R.bool.feature_training)) {
//                    ToastUtil.show(R.string.message_wipe_no_connection);
//                } else {
                    WarningDialogFragment wipeDataDialog = warningDialogFragmentBuilder.build(buildWipeDialogDelegate(), R.string.message_warning_wipe_data, R.string.btn_positive, R.string.btn_negative);
                    wipeDataDialog.show(getFragmentManager(), "WipeDataWarning");
                //}
            }
        };
    }

    private WarningDialogFragment.DialogDelegate buildWipeDialogDelegate() {
        return new WarningDialogFragment.DialogDelegate() {
            @Override
            public void onPositiveClick() {
                setRestartIntent();
                LMISApp.getInstance().wipeAppData();
            }
        };
    }

    private void setRestartIntent() {
        int requestCode = 100;
        int startAppInterval = 500;

        User currentUser = UserInfoMgr.getInstance().getUser();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.PARAM_USERNAME, currentUser.getUsername());
        intent.putExtra(Constants.PARAM_PASSWORD, currentUser.getPassword());

        PendingIntent mPendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + startAppInterval, mPendingIntent);
    }

}
