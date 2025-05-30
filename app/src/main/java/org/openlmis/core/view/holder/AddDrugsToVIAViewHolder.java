package org.openlmis.core.view.holder;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.R;
import org.openlmis.core.utils.SingleTextWatcher;
import org.openlmis.core.utils.TextStyleUtil;
import org.openlmis.core.view.viewmodel.AddDrugsToViaInventoryViewModel;

import roboguice.inject.InjectView;


public class AddDrugsToVIAViewHolder extends BaseViewHolder {

    @InjectView(R.id.tv_product_name)
    TextView productName;

    @InjectView(R.id.tv_short_code)
    TextView tvShortCode;

    @InjectView(R.id.touchArea_checkbox)
    LinearLayout taCheckbox;

    @InjectView(R.id.checkbox)
    CheckBox checkBox;

    @InjectView(R.id.action_panel)
    View actionPanel;

    @InjectView(R.id.tx_quantity)
    EditText txQuantity;

    @InjectView(R.id.action_divider)
    View actionDivider;

    @InjectView(R.id.ly_quantity)
    TextInputLayout lyQuantity;

    public AddDrugsToVIAViewHolder(View itemView) {
        super(itemView);
        txQuantity.setHint(R.string.label_hint_amount_requisition);
        taCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerCheckbox();
            }
        });
    }

    public void populate(String queryKeyWord, final AddDrugsToViaInventoryViewModel viewModel) {
        setItemViewListener(viewModel);
        checkBox.setChecked(viewModel.isChecked());

        productName.setText(TextStyleUtil.getHighlightQueryKeyWord(queryKeyWord, viewModel.getStyledName()));
        tvShortCode.setText(TextStyleUtil.getHighlightQueryKeyWord(queryKeyWord, viewModel.getStyledUnit()));

        populateEditPanel(viewModel.getQuantity());
        if (viewModel.isValid()) {
            lyQuantity.setErrorEnabled(false);
        } else {
            lyQuantity.setError(context.getResources().getString(R.string.msg_inventory_check_failed));
        }

    }

    protected void setItemViewListener(final AddDrugsToViaInventoryViewModel viewModel) {

        final EditTextWatcher textWatcher = new EditTextWatcher(viewModel);
        txQuantity.removeTextChangedListener(textWatcher);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showEditPanel(View.VISIBLE);
                } else {
                    showEditPanel(View.GONE);
                    populateEditPanel(StringUtils.EMPTY);

                    viewModel.setQuantity(StringUtils.EMPTY);
                }
                viewModel.setChecked(isChecked);
            }
        });

        txQuantity.addTextChangedListener(textWatcher);
    }

    protected void populateEditPanel(String quantity) {
        txQuantity.setText(quantity);
    }

    protected void showEditPanel(int visible) {
        actionDivider.setVisibility(visible);
        actionPanel.setVisibility(visible);
    }

    private void triggerCheckbox() {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    class EditTextWatcher extends SingleTextWatcher {

        private final AddDrugsToViaInventoryViewModel viewModel;

        public EditTextWatcher(AddDrugsToViaInventoryViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            viewModel.setQuantity(editable.toString());
        }
    }
}
