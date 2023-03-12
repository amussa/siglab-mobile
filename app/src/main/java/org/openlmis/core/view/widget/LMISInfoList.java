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

package org.openlmis.core.view.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.R;
import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.model.BaseInfoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LMISInfoList extends LinearLayout {
    private Context context;
    EditText totalPatientsView = null;
    private List<EditText> editTexts = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private List<BaseInfoItem> dataList;
    private boolean hasDataChanged = false;

    public LMISInfoList(Context context) {
        super(context);
        init(context);
    }

    public LMISInfoList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditText getPatientTotalView() {
        return totalPatientsView;
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        layoutInflater = LayoutInflater.from(context);
    }

    public void initView(List<BaseInfoItem> list) {
        this.dataList = list;
        addEquipmentView();
        addHeaderView();

        for (int i = 0; i < dataList.size(); i++) {
            BaseInfoItem item = dataList.get(i);
            if (item != null) {
                addItemView(item, i);
            }
        }

        editTexts.get(editTexts.size() - 2).setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private void addEquipmentView() {
        View equipmentView = layoutInflater.inflate(R.layout.item_lmis_equipment, this, false);
        Spinner spinner = equipmentView.findViewById(R.id.equipment_names);
        EditText editText = equipmentView.findViewById(R.id.equipment_serial);
        addView(equipmentView);
    }

    private void addItemView(BaseInfoItem item, int position) {
        addItemView(item, false, position);
    }

    private void addHeaderView() {
        addItemView(null, true, 0);
    }


    private void addItemView(BaseInfoItem item, boolean isHeaderView, final int position) {
        View view = layoutInflater.inflate(R.layout.item_lmis_info, this, false);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        EditText etValue = (EditText) view.findViewById(R.id.et_value);
        EditText etValue2 = (EditText) view.findViewById(R.id.et_value_2);
        EditText etValue3 = (EditText) view.findViewById(R.id.et_value_3);

        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(12);
        if (isHeaderView) {
            etValue.setFilters(inputFilters);
            etValue.setText("DPI");
            etValue.setEnabled(false);
            etValue.setGravity(Gravity.CENTER);

            etValue2.setFilters(inputFilters);
            etValue2.setText("CV (DBS/PSC)");
            etValue2.setEnabled(false);
            etValue2.setGravity(Gravity.CENTER);

            etValue3.setFilters(inputFilters);
            etValue3.setText("CV (Plasma)");
            etValue3.setEnabled(false);
            etValue3.setGravity(Gravity.CENTER);

            view.setBackgroundResource(R.color.color_mmia_info_name);
        } else {
            tvName.setText(item.getName());
            editTexts.add(etValue);
            etValue.setText(item.getValue());
            if (isTotalPatient(item)) {
                totalPatientsView = etValue;
            } else {
                etValue.addTextChangedListener(new EditTextWatcher(item));
            }
            etValue.addTextChangedListener(new EditTextWatcher(item));
            setTotalViewBackground(item, etValue);
        }
        addView(view);

        etValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if ((position + 1) < editTexts.size()) {
                        editTexts.get(position + 1).requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void setTotalViewBackground(BaseInfoItem item, EditText etValue) {
        if (isTotalInfoView(item)) {
            etValue.setBackgroundResource(R.color.color_mmia_speed_list_header);
        }
    }

    public boolean hasDataChanged() {
        return hasDataChanged;
    }

    public List<BaseInfoItem> getDataList() {
        return dataList;
    }

    public void deHighLightTotal() {
        totalPatientsView.setBackground(getResources().getDrawable(R.color.color_page_gray));
    }

    public boolean hasEmptyField() {
        for (BaseInfoItem item: dataList) {
            if (StringUtils.isEmpty(item.getValue())){
                return true;
            }
        }
        return false;
    }

    class EditTextWatcher implements android.text.TextWatcher {

        private final BaseInfoItem item;

        public EditTextWatcher(BaseInfoItem item) {
            this.item = item;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            hasDataChanged = true;
            item.setValue(editable.toString());
        }
    }

    public long getTotal() {
        long totalRegimenNumber = 0;
        for (BaseInfoItem item : dataList) {
            if (isTotalInfoView(item) || TextUtils.isEmpty(item.getValue())) {
                continue;
            }
            try {
                totalRegimenNumber += Long.parseLong(item.getValue());
            } catch (NumberFormatException e) {
                new LMISException(e).reportToFabric();
            }
        }
        return totalRegimenNumber;
    }

    private boolean isTotalInfoView(BaseInfoItem item) {
        List<String> monthDispenseLabels = Arrays.asList(getResources().getStringArray(R.array.TOTAL_MONTH_DISPENSE));
        return monthDispenseLabels.contains(item.getName()) || isTotalPatient(item);
    }

    private boolean isTotalPatient(BaseInfoItem item) {
        List<String> totalPatientLabels = Arrays.asList(getResources().getStringArray(R.array.TOTAL_PATIENTS));
        return totalPatientLabels.contains(item.getName());
    }


    public boolean isCompleted() {
        for (EditText editText : editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                editText.setError(context.getString(R.string.hint_error_input));
                editText.requestFocus();
                return false;
            }
        }
        return true;
    }

}
