package com.animsh.trace.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.animsh.trace.R;
import com.animsh.trace.UploadFilesAdapter;
import com.animsh.trace.UploadModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class BrowseFragment extends Fragment {

    public static UploadFilesAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<UploadModel> uploadModels;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid()).child("USER_FILES");
    public static LottieAnimationView animationView;

    public BrowseFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        animationView = view.findViewById(R.id.animation_view);
        recyclerView = view.findViewById(R.id.encrypted_files_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        uploadModels = new ArrayList<>();
        adapter = new UploadFilesAdapter(uploadModels, requireContext());
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadModels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UploadModel model = dataSnapshot.getValue(UploadModel.class);
                    uploadModels.add(model);
                }
                if(uploadModels.size() != 0){
                    animationView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}