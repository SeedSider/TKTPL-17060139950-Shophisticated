package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

    private ImageButton mSelectImage;
    private EditText mItemName;
    private EditText mPrice;
    private EditText mAmount;
    private Button mSubmitButton;
    private SellViewModels mViewModel;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;

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

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        mProgress.setMessage("Saving...");
        mProgress.show();

        mViewModel.itemName = mItemName.getText().toString().trim();
        mViewModel.amount = mAmount.getText().toString().trim();
        mViewModel.price = mPrice.getText().toString().trim();

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
}