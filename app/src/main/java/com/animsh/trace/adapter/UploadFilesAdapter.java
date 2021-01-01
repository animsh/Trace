package com.animsh.trace.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.FileUriExposedException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.animsh.trace.R;
import com.animsh.trace.model.UploadModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.animsh.trace.ui.BrowseFragment.adapter;
import static com.animsh.trace.ui.BrowseFragment.animationView;

public class UploadFilesAdapter extends RecyclerView.Adapter<UploadFilesAdapter.MyViewHolder> {

    private ArrayList<UploadModel> uploadModelArrayList;
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public UploadFilesAdapter(ArrayList<UploadModel> uploadModelArrayList, Context context) {
        this.uploadModelArrayList = uploadModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upload_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(uploadModelArrayList.get(position).getName());
        holder.timeAndDate.setText(uploadModelArrayList.get(position).getDate());
        holder.setData(uploadModelArrayList, position);
    }

    @Override
    public int getItemCount() {
        return uploadModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView timeAndDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.file_title);
            timeAndDate = itemView.findViewById(R.id.file_time);
        }

        private void setData(ArrayList<UploadModel> uploadModelArrayList, int position) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View bottomDialogLayout = ((FragmentActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
                    final BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);

                    TextView title, deleteMusicBtn, shareMusicBtn, downloadBtn;

                    title = bottomDialogLayout.findViewById(R.id.title);
                    deleteMusicBtn = bottomDialogLayout.findViewById(R.id.btn_delete);
                    shareMusicBtn = bottomDialogLayout.findViewById(R.id.btn_share);
                    downloadBtn = bottomDialogLayout.findViewById(R.id.btn_download);

                    title.setSelected(true);
                    title.setText(uploadModelArrayList.get(position).getName());

                    deleteMusicBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mPostKey = uploadModelArrayList.get(position).getFileKey();
                            DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference()
                                    .child(auth.getUid()).child("USER_FILES").child(mPostKey);
                            String path = uploadModelArrayList.get(position).getFileUrl();
                            mPostReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(path);
                                    reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            animationView.setVisibility(View.VISIBLE);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(itemView.getContext(), "File Deleted", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    shareMusicBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, uploadModelArrayList.get(position).getFileUrl());

                            try {
                                context.startActivity(Intent.createChooser(share, "Share " + uploadModelArrayList.get(position).getFileUrl()));
                            } catch (FileUriExposedException e) {
                                Log.e("FileUri: ", e.getMessage());
                            }
                            dialog.dismiss();
                        }
                    });

                    downloadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UploadModel upload = uploadModelArrayList.get(position);
                            //Opening the upload file in browser using the upload url
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(upload.getFileUrl()));
                            itemView.getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.setContentView(bottomDialogLayout);
                    dialog.show();
                }
            });
        }

    }
}
