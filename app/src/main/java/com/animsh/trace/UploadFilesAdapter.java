package com.animsh.trace;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class UploadFilesAdapter extends RecyclerView.Adapter<UploadFilesAdapter.MyViewHolder> {

    private ArrayList<UploadModel> uploadModelArrayList;
    private Context context;

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

        holder.setData(uploadModelArrayList, position);
    }

    @Override
    public int getItemCount() {
        return uploadModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.file_title);
        }

        private void setData(ArrayList<UploadModel> uploadModelArrayList, int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UploadModel upload = uploadModelArrayList.get(position);
                    //Opening the upload file in browser using the upload url
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(upload.getFileUrl()));
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View bottomDialogLayout = ((FragmentActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
                    final BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);

                    TextView title, deleteMusicBtn, shareMusicBtn;

                    title = bottomDialogLayout.findViewById(R.id.title);
                    deleteMusicBtn = bottomDialogLayout.findViewById(R.id.btn_delete);
                    shareMusicBtn = bottomDialogLayout.findViewById(R.id.btn_share);

                    title.setSelected(true);

                    deleteMusicBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    shareMusicBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_STREAM, uploadModelArrayList.get(position).getFileUrl());

                            try {
                                context.startActivity(Intent.createChooser(share, "Share " + uploadModelArrayList.get(position).getFileUrl()));
                            } catch (FileUriExposedException e) {
                                Log.e("FileUri: ", e.getMessage());
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setContentView(bottomDialogLayout);
                    dialog.show();
                    return true;
                }
            });
        }

    }
}
