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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import org.openlmis.core.LMISApp;
import org.openlmis.core.R;
import org.openlmis.core.googleAnalytics.ScreenName;
import org.openlmis.core.googleAnalytics.TrackerActions;
import org.openlmis.core.googleAnalytics.TrackerCategories;
import org.openlmis.core.presenter.InventoryPresenter;
import org.openlmis.core.utils.ToastUtil;
import org.openlmis.core.view.adapter.InventoryListAdapter;
import org.openlmis.core.view.fragment.SimpleDialogFragment;
import org.openlmis.core.view.viewmodel.InventoryViewModel;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import rx.Subscriber;
import rx.functions.Action1;

public abstract class InventoryActivity extends SearchBarActivity implements InventoryPresenter.InventoryView, SimpleDialogFragment.MsgDialogCallBack {

    @InjectView(R.id.fast_scroller)
    RecyclerViewFastScroller fastScroller;

    @InjectView(R.id.products_list)
    public RecyclerView productListRecycleView;

    @InjectView(R.id.tv_total)
    public TextView tvTotal;

    @InjectView(R.id.action_panel)
    public ViewGroup bottomBtn;

    @InjectView(R.id.btn_complete)
    public Button btnDone;

    @InjectView(R.id.btn_save)
    public View btnSave;

    protected InventoryListAdapter mAdapter;

    @NonNull
    protected Subscriber<List<InventoryViewModel>> getOnViewModelsLoadedSubscriber() {
        return new Subscriber<List<InventoryViewModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.show(e.getMessage());
                loaded();
            }

            @Override
            public void onNext(List<InventoryViewModel> inventoryViewModels) {
                setUpFastScroller(inventoryViewModels);
                mAdapter.refresh();
                setTotal(inventoryViewModels.size());
                loaded();
            }
        };
    }

    protected Action1<Object> onNextMainPageAction = getOnNextMainPageAction();

    @NonNull
    protected Action1<Object> getOnNextMainPageAction() {
        return new Action1<Object>() {
            @Override
            public void call(Object o) {
                loaded();
                goToNextPage();
            }
        };
    }

    protected Action1<Throwable> errorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            loaded();
            showErrorMessage(throwable.getMessage());
        }
    };

    @Override
    protected ScreenName getScreenName() {
        return ScreenName.InventoryScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        initUI();

        trackInventoryEvent(TrackerActions.SelectInventory);
    }

    protected abstract void initRecyclerView();

    public void initUI() {
        loading();
    }

    protected void trackInventoryEvent(TrackerActions action) {
        LMISApp.getInstance().trackEvent(TrackerCategories.Inventory, action);
    }

    protected abstract void goToNextPage();

    @Override
    public boolean onSearchStart(String query) {
        mAdapter.filter(query);
        setUpFastScroller(mAdapter.getFilteredList());
        return false;
    }

    @Override
    public void showErrorMessage(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public boolean validateInventory() {
        int position = mAdapter.validateAll();
        if (position >= 0) {
            clearSearch();
            productListRecycleView.scrollToPosition(position);
            return false;
        }
        return true;
    }

    protected void setTotal(int total) {
        tvTotal.setText(getString(R.string.label_total, total));
    }

    @Override
    public void positiveClick(String tag) {
        this.finish();
    }

    @Override
    public void negativeClick(String tag) {

    }

    public void setUpFastScroller(List<InventoryViewModel> viewModels) {
        List<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < viewModels.size(); i++) {
            String name = viewModels.get(i).getProductName();
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }

        fastScroller.setRecyclerView(productListRecycleView);
        fastScroller.setUpAlphabet(mAlphabetItems);
    }
}
