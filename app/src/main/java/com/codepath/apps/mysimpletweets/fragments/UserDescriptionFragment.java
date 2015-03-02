package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;

public class UserDescriptionFragment extends Fragment {

    public static UserDescriptionFragment newInstance(User user) {
        UserDescriptionFragment fragment = new UserDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_description, container, false);
        User user = (User) getArguments().getSerializable("user");
        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(user.getDescription());
        return v;
    }
}
