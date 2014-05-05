package com.credomatic.gprod.android.gptt.fragments.ppp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.app.IFragmentService;


/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class FragmentPppFtps  extends Fragment implements IFragmentService {

    public FragmentPppFtps(){}

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ppp_ftps, container, false);
        return rootView;
    }

    @Override
    public void restoreDefaultValues() {

    }

    @Override
    public void reloadValues() {

    }
}

