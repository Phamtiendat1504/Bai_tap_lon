package com.example.thue_tro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dangnhap extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private TextView tvUsernameError, tvPasswordError, tvRegister;
    private Button btnLogin;
    private DatabaseReference databaseReference;
    private DatabaseReference adminReference; // Thêm tham chiếu cho admins
    private boolean isPasswordVisible = false;
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 100;

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME_ERROR_VISIBLE = "username_error_visible";
    private static final String KEY_PASSWORD_ERROR_VISIBLE = "password_error_visible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dang_nhap);

        // Tham chiếu đến node users
        databaseReference = FirebaseDatabase.getInstance("https://baitaplon-f5860-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        // Tham chiếu đến node admins
        adminReference = FirebaseDatabase.getInstance("https://baitaplon-f5860-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("admins");

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvUsernameError = findViewById(R.id.tvUsernameError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        if (savedInstanceState != null) {
            edtUsername.setText(savedInstanceState.getString(KEY_USERNAME));
            edtPassword.setText(savedInstanceState.getString(KEY_PASSWORD));
            tvUsernameError.setVisibility(savedInstanceState.getBoolean(KEY_USERNAME_ERROR_VISIBLE) ? View.VISIBLE : View.GONE);
            tvPasswordError.setVisibility(savedInstanceState.getBoolean(KEY_PASSWORD_ERROR_VISIBLE) ? View.VISIBLE : View.GONE);
        }

        setupPasswordToggle(edtPassword);

        // Tạo tài khoản admin
        createAdminAccount();

        btnLogin.setOnClickListener(v -> validateAndLogin());
        tvRegister.setOnClickListener(v -> onRegisterClick(v));

        // Kiểm tra và yêu cầu quyền CALL_PHONE
        checkCallPhonePermission();
    }

    private void checkCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_PERMISSION_REQUEST_CODE);
        }
    }

    private void createAdminAccount() {
        String adminUsername = "admin12345";
        String adminPassword = "@Admin12345";
        String adminEmail = "admin12345@gmail.com";
        String adminFullName = "ADMIN";
        String adminPhone = "0987654321";

        adminReference.orderByChild("dangNhap").equalTo(adminUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            DangKi.User admin = new DangKi.User(
                                    adminFullName,
                                    adminUsername,
                                    adminEmail,
                                    adminPassword,
                                    adminPhone,
                                    "admin"
                            );
                            adminReference.child(adminUsername).setValue(admin)
                                    .addOnSuccessListener(aVoid -> Log.d("Dangnhap", "Tạo tài khoản admin thành công trong admins"))
                                    .addOnFailureListener(e -> Log.e("Dangnhap", "Lỗi tạo admin: " + e.getMessage()));
                        } else {
                            Log.d("Dangnhap", "Tài khoản admin đã tồn tại trong admins");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Dangnhap.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupPasswordToggle(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0);
                    }
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void validateAndLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        boolean isValid = true;

        if (username.isEmpty()) {
            tvUsernameError.setText("Vui lòng nhập tên đăng nhập");
            tvUsernameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (username.length() < 6) {
            tvUsernameError.setText("Tên đăng nhập phải có ít nhất 6 ký tự");
            tvUsernameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!username.matches("[a-zA-Z0-9]+")) {
            tvUsernameError.setText("Tên đăng nhập chỉ được chứa chữ cái và số");
            tvUsernameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvUsernameError.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            tvPasswordError.setText("Vui lòng nhập mật khẩu");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPasswordError.setVisibility(View.GONE);
        }

        if (isValid) {
            // Kiểm tra trong users trước
            databaseReference.orderByChild("dangNhap").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    DangKi.User user = snapshot.getValue(DangKi.User.class);
                                    if (user != null) {
                                        if (user.matKhau.equals(password)) {
                                            Intent intent = new Intent(Dangnhap.this, HomeActivity.class);
                                            intent.putExtra("username", username);
                                            intent.putExtra("role", user.role != null ? user.role : "user");
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            tvPasswordError.setText("Nhập sai mật khẩu");
                                            tvPasswordError.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                // Nếu không tìm thấy trong users, kiểm tra trong admins
                                adminReference.orderByChild("dangNhap").equalTo(username)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot adminSnapshot) {
                                                if (adminSnapshot.exists()) {
                                                    for (DataSnapshot snapshot : adminSnapshot.getChildren()) {
                                                        DangKi.User admin = snapshot.getValue(DangKi.User.class);
                                                        if (admin != null) {
                                                            if (admin.matKhau.equals(password)) {
                                                                Intent intent = new Intent(Dangnhap.this, HomeActivity.class);
                                                                intent.putExtra("username", username);
                                                                intent.putExtra("role", admin.role != null ? admin.role : "admin");
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                tvPasswordError.setText("Nhập sai mật khẩu");
                                                                tvPasswordError.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // Nếu không tìm thấy trong cả users và admins
                                                    tvUsernameError.setText("Tên đăng nhập không tồn tại");
                                                    tvUsernameError.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(Dangnhap.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Dangnhap.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void onRegisterClick(View view) {
        Intent intent = new Intent(Dangnhap.this, DangKi.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USERNAME, edtUsername.getText().toString());
        outState.putString(KEY_PASSWORD, edtPassword.getText().toString());
        outState.putBoolean(KEY_USERNAME_ERROR_VISIBLE, tvUsernameError.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_PASSWORD_ERROR_VISIBLE, tvPasswordError.getVisibility() == View.VISIBLE);
    }
}