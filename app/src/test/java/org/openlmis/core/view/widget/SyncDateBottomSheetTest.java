package org.openlmis.core.view.widget;

import android.view.View;

import com.google.inject.AbstractModule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.LMISTestRunner;
import org.openlmis.core.presenter.SyncErrorsPresenter;
import org.openlmis.core.utils.DateUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.FragmentTestUtil;

import java.util.Date;

import roboguice.RoboGuice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(LMISTestRunner.class)
public class SyncDateBottomSheetTest {

    protected SyncDateBottomSheet fragment;
    protected SyncErrorsPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = mock(SyncErrorsPresenter.class);
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new AbstractModule() {
            @Override
            protected void configure() {
                bind(SyncErrorsPresenter.class).toInstance(presenter);
            }
        });

        fragment = new SyncDateBottomSheet();
        fragment.setArguments(SyncDateBottomSheet.getArgumentsToMe(1, 1));
        FragmentTestUtil.startFragment(fragment);
    }

    @Test
    public void shouldShowRnrFormLastSyncedTimeCorrectly() {
        String formatRnrLastSyncTimeWithMinute = fragment.formatRnrLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_MINUTE);
        assertThat(formatRnrLastSyncTimeWithMinute, equalTo("Requisition last synced 20 minutes ago"));

        String formatRnrLastSyncTimeWithHour = fragment.formatRnrLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_HOUR);
        assertThat(formatRnrLastSyncTimeWithHour, equalTo("Requisition last synced 20 hours ago"));

        String formatRnrLastSyncTimeWithDay = fragment.formatRnrLastSyncTime(new Date().getTime() - 1 * DateUtil.MILLISECONDS_DAY);
        assertThat(formatRnrLastSyncTimeWithDay, equalTo("Requisition last synced 1 day ago"));

        String formatRnrLastSyncTimeWithDays = fragment.formatRnrLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_DAY);
        assertThat(formatRnrLastSyncTimeWithDays, equalTo("Requisition last synced 20 days ago"));
    }

    @Test
    public void shouldShowStockCardLastSyncedTimeCorrectly() {
        String formatStockCardLastSyncTimeWithMinute = fragment.formatStockCardLastSyncTime(new Date().getTime() - 1 * DateUtil.MILLISECONDS_MINUTE);
        assertThat(formatStockCardLastSyncTimeWithMinute, equalTo("Stock cards last synced 1 minute ago"));

        String formatStockCardLastSyncTimeWithMinutes = fragment.formatStockCardLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_MINUTE);
        assertThat(formatStockCardLastSyncTimeWithMinutes, equalTo("Stock cards last synced 20 minutes ago"));

        String formatStockCardLastSyncTimeWithHour = fragment.formatStockCardLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_HOUR);
        assertThat(formatStockCardLastSyncTimeWithHour, equalTo("Stock cards last synced 20 hours ago"));

        String formatStockCardLastSyncTimeWithDay = fragment.formatStockCardLastSyncTime(new Date().getTime() - 20 * DateUtil.MILLISECONDS_DAY);
        assertThat(formatStockCardLastSyncTimeWithDay, equalTo("Stock cards last synced 20 days ago"));
    }

    @Test
    public void shouldShowErrorMsgWhenFirstSyncFailed() throws Exception {
        when(presenter.hasRnrSyncError()).thenReturn(true);
        when(presenter.hasStockCardSyncError()).thenReturn(true);

        String formatRnrLastSyncTime = fragment.formatRnrLastSyncTime(0);
        assertThat(formatRnrLastSyncTime, equalTo("Initial requisition sync failed, please retry."));

        String formatStockCardLastSyncTimeWithMinute = fragment.formatStockCardLastSyncTime(0);
        assertThat(formatStockCardLastSyncTimeWithMinute, equalTo("Initial stock card sync failed, please retry."));
    }

    @Test
    public void shouldShowEmptyMsgWhenHasNotSynced() throws Exception {
        String formatRnrLastSyncTime = fragment.formatRnrLastSyncTime(0);
        assertThat(formatRnrLastSyncTime, equalTo(""));

        String formatStockCardLastSyncTimeWithMinute = fragment.formatStockCardLastSyncTime(0);
        assertThat(formatStockCardLastSyncTimeWithMinute, equalTo(""));
    }

    @Test
    public void shouldShowErrorIconWhenHasSyncError() throws Exception {
        when(presenter.hasRnrSyncError()).thenReturn(true);
        when(presenter.hasStockCardSyncError()).thenReturn(true);
        fragment.onViewCreated(null, null);

        assertThat(fragment.ivRnRError.getVisibility(), is(View.VISIBLE));
        assertThat(fragment.ivStockcardError.getVisibility(), is(View.VISIBLE));
    }
}