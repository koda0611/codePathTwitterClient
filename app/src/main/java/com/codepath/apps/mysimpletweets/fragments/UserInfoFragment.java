package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;


public class UserInfoFragment extends Fragment {

    public static UserInfoFragment newInstance(User user) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);
        User user = (User) getArguments().getSerializable("user");
        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);

        Picasso.with(getActivity()).load(user.getProfileImgUrl()).into(ivProfileImage);
        tvScreenName.setText("@" + user.getScreenName());
        tvUserName.setText(user.getName());
        return v;
    }
}

