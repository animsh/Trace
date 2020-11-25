package com.animsh.trace.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.animsh.trace.R;
import com.google.android.material.button.MaterialButton;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.obsez.android.lib.filechooser.internals.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionFragment extends Fragment {

    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    public static String encryptFolder = "Trace/Encrypted Files";
    public static String decryptFolder = "Trace/Decrypted Files";
    public static String stringFilePath;
    public static File fileFilePath;
    String encrypt = "Encrypt";
    String decrypt = "Decrypt";
    private LottieAnimationView animationView;


    public EncryptionFragment() {
        // Required empty public constructor
    }

    static void encrypt(String path, String fileName, Dialog dialog) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.

        FileOutputStream fos = new FileOutputStream(extStore + "/" + encryptFolder + "/" + fileName + ".enc");

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes

        int b;
        byte[] d = new byte[10 * 1024 * 1024];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        dialog.dismiss();
    }

    static void decrypt(String path, String fileName, Dialog dialog) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path);

        FileOutputStream fos = new FileOutputStream(extStore + "/" + decryptFolder + "/" + fileName);
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[10 * 1024 * 1024];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
        dialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_encryption, container, false);

        // Paths for Internal And External Storage
        File dir = Environment.getExternalStorageDirectory();
        String path = FileUtil.getStoragePath(requireContext(), true);
        Log.d("Internal", ": " + dir);
        Log.d("External", ": " + path);

        // Permission Checker
        if (CheckPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            createApplicationFolder(path, dir);
        } else {
            // you do not have permission go request runtime permissions
            RequestPermission((Activity) getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);
        }

        // Lottie Animation Setup
        animationView = view.findViewById(R.id.animation_view);
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.playAnimation();
            }
        });
        // Lottie Animation Setup

        // Encryption and Decryption Confirmation Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView pathOfFile = dialog.findViewById(R.id.path_of_file);
        ImageView browseAgain = dialog.findViewById(R.id.browse_again);
        MaterialButton processBtn = dialog.findViewById(R.id.process_btn);
        MaterialButton cancelBtn = dialog.findViewById(R.id.cancel_btn);

        pathOfFile.setSelected(true);

        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (processBtn.getText().toString().equals(encrypt)) {
                    dialog.dismiss();
                    new EncryptTask(getContext()).execute();
                } else if (processBtn.getText().toString().equals(decrypt)) {
                    dialog.dismiss();
                    new DecryptTask(getContext()).execute();
                }
            }
        });

        browseAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (processBtn.getText() == encrypt) {
                    fileChooser(path, pathOfFile, processBtn, dialog, true);
                } else {
                    fileChooser(path, pathOfFile, processBtn, dialog, false);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // Encryption and Decryption Confirmation Dialog

        // Button Listeners
        MaterialButton btnEncrypt = view.findViewById(R.id.btn_encrypt);
        MaterialButton btnDecrypt = view.findViewById(R.id.btn_decrypt);

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileChooser(path, pathOfFile, processBtn, dialog, true);
            }

        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileChooser(path, pathOfFile, processBtn, dialog, false);
            }
        });
        // Button Listeners
        return view;
    }

    private void createApplicationFolder(String path, File dir) {
        File media = new File(path, "Trace");
        File mediaStorageDir = new File(dir, encryptFolder);
        File mediaStorageDirs = new File(dir, decryptFolder);

        if (!media.exists()) {
            if (!media.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        if (!mediaStorageDirs.exists()) {
            if (!mediaStorageDirs.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
    }

    public void fileChooser(String path, TextView pathOfFile, Button processBtn, Dialog dialog, boolean isEncryptSelected) {
        new ChooserDialog(getContext())
                .withStartFile(path)
                .withFileIconsRes(false, R.drawable.ic_paper, R.drawable.ic_folder)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        stringFilePath = path;
                        fileFilePath = pathFile;
                        pathOfFile.setText(stringFilePath);
                        if (isEncryptSelected) {
                            processBtn.setText(encrypt);
                        } else {
                            processBtn.setText(decrypt);
                        }
                        dialog.show();
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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {
            case REQUEST_RUNTIME_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // you have permission go ahead
//                    createApplicationFolder();
                } else {
                    // you do not have permission show toast.
                }
                return;
            }
        }
    }

    public void RequestPermission(Activity thisActivity, String Permission, int Code) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Permission)) {
            } else {
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Permission},
                        Code);
            }
        }
    }

    public boolean CheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static class EncryptTask extends AsyncTask<String, Void, String> {

        private Context context;
        private Dialog dialog;

        public EncryptTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String strFileName = fileFilePath.getName();
            String[] parts = strFileName.split("\\.");
            String fileName = parts[0];
            try {
                encrypt(stringFilePath, strFileName, dialog);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    public class DecryptTask extends AsyncTask<String, Void, String> {

        private Context context;
        private Dialog dialog;

        public DecryptTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String strFileName = fileFilePath.getName();
            String[] parts = strFileName.split("\\.");
            String fileName = parts[0];
            String extension = null;
            if (parts[1] != null) {
                extension = parts[1];
            }
            String wholeFileName = fileName + "." + extension;
            try {
                decrypt(stringFilePath, wholeFileName, dialog);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }
}