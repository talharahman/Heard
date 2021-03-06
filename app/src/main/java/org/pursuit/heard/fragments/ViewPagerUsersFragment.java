package org.pursuit.heard.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.pursuit.heard.R;
import org.pursuit.heard.database.UserProfile;
import org.pursuit.heard.model.Artist;
import org.pursuit.heard.recyclerview.ArtistPresentAdapter;

import java.io.Serializable;
import java.util.List;


public class ViewPagerUsersFragment extends Fragment {

    private static final String USERNAME = "USERNAME";
    private static final String ARTISTS = "ARTISTS";

    private String otherUsername;
    private List<Artist> artists;
    private View rootView;

    public ViewPagerUsersFragment() {}

    public static ViewPagerUsersFragment newInstance(UserProfile profile) {
        ViewPagerUsersFragment fragment = new ViewPagerUsersFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, profile.getUsername());
        args.putSerializable(ARTISTS, (Serializable) profile.getArtists());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otherUsername = getArguments().getString(USERNAME);
            artists = (List<Artist>) (getArguments().getSerializable(ARTISTS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_pager_users, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView otherUsernameText = rootView.findViewById(R.id.viewPagerUser_profile_name);
        RecyclerView otherUserArtist = rootView.findViewById(R.id.recyclerView_container_viewPagerUserFragment);
        Button followButton = rootView.findViewById(R.id.viewPager_follow_button);
        ArtistPresentAdapter artistPresentAdapter = new ArtistPresentAdapter();

        otherUsernameText.setText(otherUsername);
        otherUserArtist.setLayoutManager(new LinearLayoutManager(requireContext()));
        otherUserArtist.setAdapter(artistPresentAdapter);
        artistPresentAdapter.setData(artists);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(), "following " + otherUsername, Toast.LENGTH_SHORT).show();
            }
        });
    }
}