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

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.LMISApp;
import org.openlmis.core.R;
import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.model.BaseInfoItem;
import org.openlmis.core.model.repository.MMIARepository;
import org.openlmis.core.view.fragment.MMIARequisitionFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import roboguice.RoboGuice;

public class LMISInfoList extends LinearLayout {
    private Context context;
    EditText totalPatientsView = null;
    private List<EditText> editTexts = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private List<BaseInfoItem> dataList;
    private boolean hasDataChanged = false;
    private Integer bgColor = R.color.color_mmia_info_name;

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
        // addEquipmentView();
        // addHeaderView();

        for (int i = 0; i < dataList.size(); i++) {
            BaseInfoItem item = dataList.get(i);
            if (item != null) {
                addItemView(item, i);
            }
        }

        // editTexts.get(editTexts.size() - 2).setImeOptions(EditorInfo.IME_ACTION_DONE);
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
        MMIARepository mmiaRepository = RoboGuice.getInjector(LMISApp.getContext()).getInstance(MMIARepository.class);
        View view = layoutInflater.inflate(R.layout.item_lmis_info, this, false);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        EditText etValue = (EditText) view.findViewById(R.id.et_value);
        if (item.getName().equals(LMISApp.getContext().getString(R.string.label_equipment))) {
            bgColor = AlternateColors.next();
        }
        if (item.getName().equals(mmiaRepository.ATTR_EQUIPMENT) || item.getName().equals(mmiaRepository.ATTR_SERIAL_NUMBER)) {
            etValue.setKeyListener(null);
            etValue.setFocusable(false);
            etValue.setFocusableInTouchMode(false);
        }

        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(12);
        if (isHeaderView) {
            etValue.setFilters(inputFilters);
            etValue.setText(R.string.label_equipment);
            etValue.setEnabled(false);
            etValue.setGravity(Gravity.CENTER);
            view.setBackgroundResource(R.color.color_mmia_info_name);
        } else {
            tvName.setText(item.getName());
            tvName.setBackgroundResource(bgColor);
            //tvName.setBackgroundResource(bgColor);
            editTexts.add(etValue);
            etValue.setText(item.getValue());
            if (isTotalPatient(item)) {
                totalPatientsView = etValue;
            } else {
                etValue.addTextChangedListener(new EditTextWatcher(item));
            }
            if (item.getType() == BaseInfoItem.TYPE.INT) {
                etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (item.getType() == BaseInfoItem.TYPE.STRING) {
                etValue.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (item.getType() == BaseInfoItem.TYPE.DATE) {
                //etValue.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        // Aqui você pode manipular a data selecionada pelo usuário
                        // e atualizar o valor do EditText com a data escolhida
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        etValue.setText(selectedDate);
                    }
                };
                etValue.setKeyListener(null);
                etValue.setFocusable(false);
                etValue.setFocusableInTouchMode(false);
                etValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtenha a data atual para definir como valor padrão no DatePicker
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        // Crie uma instância do DatePickerDialog
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MMIARequisitionFragment.activityInstance, onDateSetListener, year, month, dayOfMonth);

                        // Defina quaisquer limites de data, se necessário
                        // Exemplo: datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                        // Exiba o DatePickerDialog
                        datePickerDialog.show();
                    }
                });
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
                e.printStackTrace();
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

    private static class AlternateColors {
        private static boolean toggle = true;
        public static Integer next() {
            if (toggle) {
                toggle = false;
                return R.color.color_regime_other;
            } else {
                toggle = true;
                return R.color.color_regime_baby;
            }
        }
    }

}
