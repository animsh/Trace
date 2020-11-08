package com.animsh.trace.ui;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.animsh.trace.R;
import com.google.android.material.button.MaterialButton;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.obsez.android.lib.filechooser.internals.FileUtil;

import java.io.File;
import java.util.Objects;


public class EncryptionFragment extends Fragment {

    private LottieAnimationView animationView;
    private MaterialButton btnEncrypt, btnDecrypt;

    public EncryptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_encryption, container, false);

        // Lottie Animation Setup
        animationView = view.findViewById(R.id.animation_view);
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.playAnimation();
            }
        });
        // Lottie Animation Setup

        // Paths for Internal And External Storage
        File dir = Environment.getExternalStorageDirectory();
        String path = FileUtil.getStoragePath(requireContext(), true);
        Log.d("Internal", ": " + dir);
        Log.d("External", ": " + path);

        // Button Listeners
        btnEncrypt = view.findViewById(R.id.btn_encrypt);
        btnDecrypt = view.findViewById(R.id.btn_decrypt);

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                new ChooserDialog(getContext())
                        .withStartFile(path)
                        .withFileIconsRes(false, R.drawable.ic_paper, R.drawable.ic_folder)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(getContext(), "FILE: " + path, Toast.LENGTH_SHORT).show();
                            }
                        })
                        // to handle the back key pressed or clicked outside the dialog:
                        .withOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                Log.d("CANCEL", "CANCEL");
                                dialog.cancel(); // MUST have
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                new ChooserDialog(getContext())
                        .withStartFile(path)
                        .withFileIconsRes(false, R.drawable.ic_paper, R.drawable.ic_folder)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(getContext(), "FILE: " + path, Toast.LENGTH_SHORT).show();
                            }
                        })
                        // to handle the back key pressed or clicked outside the dialog:
                        .withOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                Log.d("CANCEL", "CANCEL");
                                dialog.cancel(); // MUST have
                            }
                        })
                        .build()
                        .show();
            }
        });
        // Button Listeners
        return view;
    }
}