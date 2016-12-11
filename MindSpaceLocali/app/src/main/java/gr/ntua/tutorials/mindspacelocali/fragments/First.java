package gr.ntua.tutorials.mindspacelocali.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.ntua.tutorials.mindspacelocali.R;
import gr.ntua.tutorials.mindspacelocali.SignupActivity;

/**
 * Created by manoliskaramanis on 07/12/2016.
 */

public class First extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first, container, false);
        return view;
    }
}
