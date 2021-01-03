package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated.viewmodels.SellViewModels;

public class SellActivity extends AppCompatActivity {

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    private ImageButton mSelectImage;
    private EditText mItemName;
    private EditText mPrice;
    private EditText mAmount;
    private Button mSubmitButton;
    private SellViewModels mViewModel;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        mViewModel = new ViewModelProvider(this).get(SellViewModels.class);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sell");
        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        mItemName = (EditText) findViewById(R.id.itemName);
        mAmount = (EditText) findViewById(R.id.amount);
        mPrice = (EditText) findViewById(R.id.price);
        mSubmitButton = (Button) findViewById(R.id.submitButton);

        mProgress = new ProgressDialog(this);

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        STORAGE_PERMISSION_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Network network : connectivityManager.getAllNetworks()) {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        startPosting();
                    }
                    else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Toast.makeText(SellActivity.this,
                                "Connect to Wifi for sending data",
                                Toast.LENGTH_SHORT)
                                .show();
//                        startPosting();
                    }
                }
            }
        });
    }

    private void startPosting() {

        mProgress.setMessage("Saving...");
        mProgress.show();

        mViewModel.itemName = mItemName.getText().toString().trim();
        mViewModel.amount = mAmount.getText().toString().trim();
        mViewModel.price = mPrice.getText().toString().trim();

        int price = Preferences.getIncomeToday(getBaseContext()) + Integer.parseInt(mViewModel.price);
        Preferences.setIncomeToday(getBaseContext(), price);


        if(!TextUtils.isEmpty(mViewModel.itemName) && !TextUtils.isEmpty(mViewModel.amount) && !TextUtils.isEmpty(mViewModel.price) && mViewModel.imageUri != null) {
            final StorageReference filepath = mStorage.child("Item_Images").child(mViewModel.imageUri.getLastPathSegment());

            final UploadTask uploadTask = filepath.putFile(mViewModel.imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();

                                DatabaseReference newSelledItem = mDatabase.push();
                                newSelledItem.child("item_name").setValue(mViewModel.itemName);
                                newSelledItem.child("amount").setValue(mViewModel.amount);
                                newSelledItem.child("price").setValue(mViewModel.price);
                                newSelledItem.child("image").setValue(downloadUrl.toString());

                                mProgress.dismiss();

                                startActivity(new Intent(SellActivity.this, MainActivity.class));
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            mViewModel.imageUri = data.getData();

            mSelectImage.setImageURI(mViewModel.imageUri);
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(SellActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SellActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
            else {
                ReasonDialog();
            }
        }
    }

    public void ReasonDialog() {
        dialog = new AlertDialog.Builder(SellActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.sell_pop_dialog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.why_sell_permission);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        STORAGE_PERMISSION_CODE);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SellActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        dialog.show();
    }
}