package com.technorabit.ibeyonde.fragment.dailog;


import com.technorabit.ibeyonde.R;

/**
 * Created by raja on 10/10/17.
 */

public class LoadingDialog extends BaseFragmentDialog {
    @Override
    protected void initRootView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.loading_dialog_layout;
    }

    @Override
    protected String setTitle() {
        return null;
    }
}
