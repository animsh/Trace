package com.animsh.trace.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.animsh.trace.R;
import com.animsh.trace.UploadModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.obsez.android.lib.filechooser.internals.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    public static String AES = "AES";
    private static Uri fileURI;
    String encrypt = "Encrypt";
    String decrypt = "Decrypt";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LottieAnimationView animationView, lockAnimation;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());
    private StorageReference reference = FirebaseStorage.getInstance().getReference().child(mAuth.getUid());

    public EncryptionFragment() {
        // Required empty public constructor
    }

    static Uri encrypt(String path, String fileName, Dialog dialog, EditText password) throws Exception {
        // Here you read the cleartext.
        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        String pass = encrypt(password.getText().toString(), "TraceecarT");
        File newFile = new File(extStore + "/" + encryptFolder + "/" + fileName + "." + pass);
        FileOutputStream fos = new FileOutputStream(newFile);
        Log.e("URI ", "encrypt: " + Uri.fromFile(newFile).toString());
        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(password.getText().toString().getBytes(),
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
        return Uri.fromFile(newFile);
    }

    static void decrypt(String path, String fileName, Dialog dialog, EditText password) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path);

        FileOutputStream fos = new FileOutputStream(extStore + "/" + decryptFolder + "/" + fileName);
        SecretKeySpec sks = new SecretKeySpec(password.getText().toString().getBytes(),
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

    public static String encrypt(String Data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);

    }

    public static String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    public static SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES");

    }

    public void uploadFIle(Uri file) {
        if (file != null) {
            String[] parts = file.toString().split("\\/");
            String name = parts[parts.length - 1];
            StorageReference fileReference = reference.child(name);
            fileReference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //UploadModel model = new UploadModel(uri.toString());
                            DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy 'at' h:mm a", Locale.getDefault());
                            String date = df.format(Calendar.getInstance().getTime());
                            String modelId = root.push().getKey();
                            UploadModel upload = new UploadModel(uri.toString(), name, modelId, date);
                            root.child("USER_FILES").child(modelId).setValue(upload);
                            Log.e("UPLOAD DONE: ", "Success");
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("UPLOAD ERROR:", "Uploading Failed");
                }
            });
        } else {
            Log.e("UPLOAD ERROR: ", "URI is null");
        }
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
        lockAnimation = view.findViewById(R.id.lock_animation_view);
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.setSpeed(2);
                animationView.playAnimation();
                lockAnimation.playAnimation();
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
        CheckBox uploadCheckBox = dialog.findViewById(R.id.upload_checkbox);
        EditText passwordForProcess = dialog.findViewById(R.id.password_et);
        MaterialButton processBtn = dialog.findViewById(R.id.process_btn);
        MaterialButton cancelBtn = dialog.findViewById(R.id.cancel_btn);

        pathOfFile.setSelected(true);


        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (processBtn.getText().toString().equals(encrypt)) {
                    if (passwordForProcess.length() != 16) {
                        passwordForProcess.setError("Password Must be of 16 characters");
                    } else {
                        dialog.dismiss();
                        if (uploadCheckBox.isChecked()) {
                            new EncryptTask(getContext(), passwordForProcess, true).execute();
                        } else {
                            new EncryptTask(getContext(), passwordForProcess, false).execute();

                        }
                    }
                } else if (processBtn.getText().toString().equals(decrypt)) {
                    if (passwordForProcess.length() != 16) {
                        passwordForProcess.setError("Password Must be of 16 characters");
                    } else {
                        String[] parts = stringFilePath.split("\\.");
                        Log.e("Part 1: ", parts[parts.length - 1]);
                        String dec = null;
                        try {
                            dec = decrypt(parts[parts.length - 1], "TraceecarT");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (passwordForProcess.getText().toString().equals(dec)) {
                            dialog.dismiss();
                            new DecryptTask(getContext(), passwordForProcess).execute();
                        } else {
                            passwordForProcess.setError("Password might be wrong");
                        }

                    }
                }
            }
        });

        browseAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (processBtn.getText() == encrypt) {
                    fileChooser(path, pathOfFile, processBtn, dialog, true, uploadCheckBox);
                } else {
                    fileChooser(path, pathOfFile, processBtn, dialog, false, uploadCheckBox);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordForProcess.setText("");
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
                fileChooser(path, pathOfFile, processBtn, dialog, true, uploadCheckBox);
            }

        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileChooser(path, pathOfFile, processBtn, dialog, false, uploadCheckBox);
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

    public void fileChooser(String path, TextView pathOfFile, Button processBtn, Dialog dialog, boolean isEncryptSelected, CheckBox upload) {
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
                            upload.setVisibility(View.VISIBLE);
                        } else {
                            processBtn.setText(decrypt);
                            upload.setVisibility(View.GONE);
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

    public class EncryptTask extends AsyncTask<String, Void, String> {

        EditText password;
        private Context context;
        private Dialog dialog;
        private boolean upload, isNull;

        public EncryptTask(Context context, EditText password, boolean upload) {
            this.context = context;
            this.password = password;
            this.upload = upload;
        }

        @Override
        protected String doInBackground(String... strings) {
            String strFileName = fileFilePath.getName();
            String[] parts = strFileName.split("\\.");
            String fileName = parts[0];
            try {
                Uri file = encrypt(stringFilePath, strFileName, dialog, password);
                if (file == null) {
                    isNull = true;
                } else {
                    isNull = false;
                }
                if (upload) {
                    uploadFIle(file);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            password.setText("");
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (isNull) {
                Toast.makeText(context, "Failed Try Again", Toast.LENGTH_LONG).show();
            } else {
                if (upload) {
                    Toast.makeText(context, "Uploading file in background", Toast.LENGTH_LONG).show();
                }
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

        EditText password;
        private Context context;
        private Dialog dialog;

        public DecryptTask(Context context, EditText password) {
            this.context = context;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            String strFileName = fileFilePath.getName();
            String[] parts = strFileName.split("\\.");
            String fileName = parts[0];
            String extension = parts[parts.length - 2];

            String wholeFileName = fileName + "." + extension;
            try {
                decrypt(stringFilePath, wholeFileName, dialog, password);
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