package com.example.thue_tro;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DangKi extends AppCompatActivity {

    private EditText edtNguoiDung, edtDangNhap, edtEmail, edtMatKhau, edtNhapLaiMatKhau, edtSoDienThoai;
    private TextView tvLoiNguoiDung, tvLoiDangNhap, tvLoiEmail, tvLoiMatKhau, tvLoiNhapLaiMatKhau, tvLoiSoDienThoai;
    private TextView btnQuayLai;
    private Button btnDangKi;
    private DatabaseReference usersReference;
    private DatabaseReference adminsReference;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private boolean hasAgreedToTerms = false;
    private AlertDialog successDialog; // Biến instance cho Dialog

    private static final String KEY_NGUOI_DUNG = "nguoi_dung";
    private static final String KEY_DANG_NHAP = "dang_nhap";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MAT_KHAU = "mat_khau";
    private static final String KEY_NHAP_LAI_MAT_KHAU = "nhap_lai_mat_khau";
    private static final String KEY_SO_DIEN_THOAI = "so_dien_thoai";
    private static final String KEY_LOI_NGUOI_DUNG_VISIBLE = "loi_nguoi_dung_visible";
    private static final String KEY_LOI_DANG_NHAP_VISIBLE = "loi_dang_nhap_visible";
    private static final String KEY_LOI_EMAIL_VISIBLE = "loi_email_visible";
    private static final String KEY_LOI_MAT_KHAU_VISIBLE = "loi_mat_khau_visible";
    private static final String KEY_LOI_NHAP_LAI_MAT_KHAU_VISIBLE = "loi_nhap_lai_mat_khau_visible";
    private static final String KEY_LOI_SO_DIEN_THOAI_VISIBLE = "loi_so_dien_thoai_visible";
    private static final String KEY_TERMS_AGREED = "terms_agreed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dang_ki);

        // Khởi tạo Firebase
        usersReference = FirebaseDatabase.getInstance("https://baitaplon-f5860-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");
        adminsReference = FirebaseDatabase.getInstance("https://baitaplon-f5860-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("admins");

        // Khởi tạo các view
        edtNguoiDung = findViewById(R.id.edtNguoiDung);
        edtDangNhap = findViewById(R.id.edtDangNhap);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtNhapLaiMatKhau = findViewById(R.id.edtNhapLaiMatKhau);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);

        tvLoiNguoiDung = findViewById(R.id.loiNguoiDung);
        tvLoiDangNhap = findViewById(R.id.loiDangNhap);
        tvLoiEmail = findViewById(R.id.loiEmail);
        tvLoiMatKhau = findViewById(R.id.loiMatKhau);
        tvLoiNhapLaiMatKhau = findViewById(R.id.loiNhapLaiMatKhau);
        tvLoiSoDienThoai = findViewById(R.id.loiSoDienThoai);

        btnDangKi = findViewById(R.id.btnDangKi);
        btnQuayLai = findViewById(R.id.btnQuayLai);

        // Ban đầu nút Đăng ký bị vô hiệu hóa
        btnDangKi.setEnabled(false);

        // Tự động chuyển tên người dùng thành chữ in hoa
        edtNguoiDung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String upperText = text.toUpperCase();
                if (!text.equals(upperText)) {
                    edtNguoiDung.removeTextChangedListener(this);
                    edtNguoiDung.setText(upperText);
                    edtNguoiDung.setSelection(upperText.length());
                    edtNguoiDung.addTextChangedListener(this);
                }
            }
        });

        // Khôi phục trạng thái nếu có
        if (savedInstanceState != null) {
            edtNguoiDung.setText(savedInstanceState.getString(KEY_NGUOI_DUNG));
            edtDangNhap.setText(savedInstanceState.getString(KEY_DANG_NHAP));
            edtEmail.setText(savedInstanceState.getString(KEY_EMAIL));
            edtMatKhau.setText(savedInstanceState.getString(KEY_MAT_KHAU));
            edtNhapLaiMatKhau.setText(savedInstanceState.getString(KEY_NHAP_LAI_MAT_KHAU));
            edtSoDienThoai.setText(savedInstanceState.getString(KEY_SO_DIEN_THOAI));
            tvLoiNguoiDung.setVisibility(savedInstanceState.getBoolean(KEY_LOI_NGUOI_DUNG_VISIBLE) ? View.VISIBLE : View.GONE);
            tvLoiDangNhap.setVisibility(savedInstanceState.getBoolean(KEY_LOI_DANG_NHAP_VISIBLE) ? View.VISIBLE : View.GONE);
            tvLoiEmail.setVisibility(savedInstanceState.getBoolean(KEY_LOI_EMAIL_VISIBLE) ? View.VISIBLE : View.GONE);
            tvLoiMatKhau.setVisibility(savedInstanceState.getBoolean(KEY_LOI_MAT_KHAU_VISIBLE) ? View.VISIBLE : View.GONE);
            tvLoiNhapLaiMatKhau.setVisibility(savedInstanceState.getBoolean(KEY_LOI_NHAP_LAI_MAT_KHAU_VISIBLE) ? View.VISIBLE : View.GONE);
            tvLoiSoDienThoai.setVisibility(savedInstanceState.getBoolean(KEY_LOI_SO_DIEN_THOAI_VISIBLE) ? View.VISIBLE : View.GONE);
            hasAgreedToTerms = savedInstanceState.getBoolean(KEY_TERMS_AGREED, false);
            btnDangKi.setEnabled(hasAgreedToTerms);
        }

        // Thiết lập nút hiển thị/ẩn mật khẩu
        setupPasswordToggle(edtMatKhau, () -> isPasswordVisible = !isPasswordVisible);
        setupPasswordToggle(edtNhapLaiMatKhau, () -> isConfirmPasswordVisible = !isConfirmPasswordVisible);

        // Sự kiện nút
        btnDangKi.setOnClickListener(v -> validateAndRegister());
        btnQuayLai.setOnClickListener(v -> finish());

        // Hiển thị dialog điều khoản nếu chưa đồng ý
        if (!hasAgreedToTerms) {
            showTermsDialog();
        }
    }

    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.terms_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CheckBox cbAgreeTerms = dialogView.findViewById(R.id.cbAgreeTerms);
        MaterialButton btnConfirmTerms = dialogView.findViewById(R.id.btnConfirmTerms);

        btnConfirmTerms.setEnabled(false);

        cbAgreeTerms.setOnCheckedChangeListener((buttonView, isChecked) -> btnConfirmTerms.setEnabled(isChecked));

        btnConfirmTerms.setOnClickListener(v -> {
            if (cbAgreeTerms.isChecked()) {
                hasAgreedToTerms = true;
                btnDangKi.setEnabled(true);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void setupPasswordToggle(EditText editText, Runnable toggleVisibility) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    toggleVisibility.run();
                    if (editText == edtMatKhau) {
                        editText.setInputType(isPasswordVisible ?
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, isPasswordVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed, 0);
                    } else if (editText == edtNhapLaiMatKhau) {
                        editText.setInputType(isConfirmPasswordVisible ?
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, isConfirmPasswordVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed, 0);
                    }
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void validateAndRegister() {
        String nguoiDung = edtNguoiDung.getText().toString().trim();
        String dangNhap = edtDangNhap.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();
        String soDienThoai = edtSoDienThoai.getText().toString().trim();

        boolean isValid = true;

        // Kiểm tra Tên người dùng
        if (nguoiDung.isEmpty()) {
            tvLoiNguoiDung.setText("Vui lòng nhập tên người dùng");
            tvLoiNguoiDung.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (nguoiDung.length() < 2) {
            tvLoiNguoiDung.setText("Tên người dùng phải có ít nhất 2 ký tự");
            tvLoiNguoiDung.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (nguoiDung.matches(".*\\d.*")) {
            tvLoiNguoiDung.setText("Tên người dùng không được chứa số");
            tvLoiNguoiDung.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!nguoiDung.matches("[A-Z\\s]+")) {
            tvLoiNguoiDung.setText("Tên người dùng phải là chữ in hoa");
            tvLoiNguoiDung.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (nguoiDung.matches(".*[!@#$%^&*].*")) {
            tvLoiNguoiDung.setText("Tên người dùng không được chứa ký tự đặc biệt");
            tvLoiNguoiDung.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiNguoiDung.setVisibility(View.GONE);
        }

        // Kiểm tra Tên đăng nhập
        if (dangNhap.isEmpty()) {
            tvLoiDangNhap.setText("Vui lòng nhập tên đăng nhập");
            tvLoiDangNhap.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (dangNhap.length() < 6) {
            tvLoiDangNhap.setText("Tên đăng nhập phải có ít nhất 6 ký tự");
            tvLoiDangNhap.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!dangNhap.matches("[a-zA-Z0-9]+")) {
            tvLoiDangNhap.setText("Tên đăng nhập chỉ được chứa chữ cái và số");
            tvLoiDangNhap.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiDangNhap.setVisibility(View.GONE);
        }

        // Kiểm tra Email
        if (email.isEmpty()) {
            tvLoiEmail.setText("Vui lòng nhập Email");
            tvLoiEmail.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvLoiEmail.setText("Email không hợp lệ");
            tvLoiEmail.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiEmail.setVisibility(View.GONE);
        }

        // Kiểm tra Mật khẩu
        if (matKhau.isEmpty()) {
            tvLoiMatKhau.setText("Vui lòng nhập mật khẩu");
            tvLoiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (matKhau.length() < 8) {
            tvLoiMatKhau.setText("Mật khẩu phải có ít nhất 8 ký tự");
            tvLoiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!matKhau.matches(".*[A-Za-z].*")) {
            tvLoiMatKhau.setText("Mật khẩu phải chứa ít nhất một chữ cái");
            tvLoiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!matKhau.matches(".*\\d.*")) {
            tvLoiMatKhau.setText("Mật khẩu phải chứa ít nhất một số");
            tvLoiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!matKhau.matches(".*[!@#$%^&*].*")) {
            tvLoiMatKhau.setText("Mật khẩu phải chứa ít nhất một ký tự đặc biệt (!@#$%^&*)");
            tvLoiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiMatKhau.setVisibility(View.GONE);
        }

        // Kiểm tra Nhập lại mật khẩu
        if (nhapLaiMatKhau.isEmpty()) {
            tvLoiNhapLaiMatKhau.setText("Vui lòng nhập lại mật khẩu");
            tvLoiNhapLaiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!nhapLaiMatKhau.equals(matKhau)) {
            tvLoiNhapLaiMatKhau.setText("Mật khẩu không khớp");
            tvLoiNhapLaiMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiNhapLaiMatKhau.setVisibility(View.GONE);
        }

        // Kiểm tra Số điện thoại
        if (soDienThoai.isEmpty()) {
            tvLoiSoDienThoai.setText("Vui lòng nhập số điện thoại");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!soDienThoai.matches("\\d+")) {
            tvLoiSoDienThoai.setText("Số điện thoại chỉ được chứa số");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (soDienThoai.length() != 10) {
            tvLoiSoDienThoai.setText("Số điện thoại phải có 10 số");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!soDienThoai.startsWith("0")) {
            tvLoiSoDienThoai.setText("Số điện thoại phải bắt đầu bằng 0");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvLoiSoDienThoai.setVisibility(View.GONE);
        }

        if (isValid) {
            checkExistingFields(dangNhap, email, soDienThoai, () -> {
                User user = new User(nguoiDung, dangNhap, email, matKhau, soDienThoai, "user");
                String userId = usersReference.push().getKey();
                usersReference.child(userId).setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("DangKi", "Đăng ký thành công: " + dangNhap);
                            if (!isFinishing() && !isDestroyed()) {
                                showSuccessDialog();
                            } else {
                                // Nếu Activity đã bị hủy, chuyển thẳng sang Dangnhap
                                startDangNhapActivity();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DangKi", "Đăng ký thất bại: " + e.getMessage());
                            if (!isFinishing() && !isDestroyed()) {
                                Toast.makeText(DangKi.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }
    }

    private void checkExistingFields(String dangNhap, String email, String phone, Runnable onSuccess) {
        boolean[] checks = new boolean[3]; // 0: dangNhap, 1: email, 2: soDienThoai
        checks[0] = checks[1] = checks[2] = true;
        int[] completedChecks = {0};

        checkField(usersReference, "dangNhap", dangNhap, () -> {
            tvLoiDangNhap.setText("Tên đăng nhập đã được sử dụng");
            tvLoiDangNhap.setVisibility(View.VISIBLE);
            checks[0] = false;
        }, () -> checkField(adminsReference, "dangNhap", dangNhap, () -> {
            tvLoiDangNhap.setText("Tên đăng nhập đã được sử dụng");
            tvLoiDangNhap.setVisibility(View.VISIBLE);
            checks[0] = false;
        }, () -> {
            completedChecks[0]++;
            proceedIfAllChecksDone(checks, completedChecks, onSuccess);
        }));

        checkField(usersReference, "email", email, () -> {
            tvLoiEmail.setText("Email đã được sử dụng");
            tvLoiEmail.setVisibility(View.VISIBLE);
            checks[1] = false;
        }, () -> checkField(adminsReference, "email", email, () -> {
            tvLoiEmail.setText("Email đã được sử dụng");
            tvLoiEmail.setVisibility(View.VISIBLE);
            checks[1] = false;
        }, () -> {
            completedChecks[0]++;
            proceedIfAllChecksDone(checks, completedChecks, onSuccess);
        }));

        checkField(usersReference, "soDienThoai", phone, () -> {
            tvLoiSoDienThoai.setText("Số điện thoại đã được sử dụng");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            checks[2] = false;
        }, () -> checkField(adminsReference, "soDienThoai", phone, () -> {
            tvLoiSoDienThoai.setText("Số điện thoại đã được sử dụng");
            tvLoiSoDienThoai.setVisibility(View.VISIBLE);
            checks[2] = false;
        }, () -> {
            completedChecks[0]++;
            proceedIfAllChecksDone(checks, completedChecks, onSuccess);
        }));
    }

    private void checkField(DatabaseReference reference, String field, String value, Runnable onExist, Runnable onNext) {
        reference.orderByChild(field).equalTo(value)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            onExist.run();
                        }
                        onNext.run();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("DangKi", "Lỗi kiểm tra " + field + ": " + databaseError.getMessage());
                        onNext.run();
                    }
                });
    }

    private void proceedIfAllChecksDone(boolean[] checks, int[] completedChecks, Runnable onSuccess) {
        if (completedChecks[0] == 3) {
            if (checks[0] && checks[1] && checks[2]) {
                onSuccess.run();
            }
        }
    }

    private void showSuccessDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.success_dialog, null);
        builder.setView(dialogView);

        successDialog = builder.create();
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        tvMessage.setText("Đăng ký tài khoản thành công");

        btnOk.setOnClickListener(v -> {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            startDangNhapActivity(); // Chuyển sang Dangnhap sau khi đóng Dialog
        });

        successDialog.setCancelable(false);
        if (!isFinishing() && !isDestroyed()) {
            successDialog.show();
        }
    }

    private void startDangNhapActivity() {
        Intent intent = new Intent(DangKi.this, Dangnhap.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NGUOI_DUNG, edtNguoiDung.getText().toString());
        outState.putString(KEY_DANG_NHAP, edtDangNhap.getText().toString());
        outState.putString(KEY_EMAIL, edtEmail.getText().toString());
        outState.putString(KEY_MAT_KHAU, edtMatKhau.getText().toString());
        outState.putString(KEY_NHAP_LAI_MAT_KHAU, edtNhapLaiMatKhau.getText().toString());
        outState.putString(KEY_SO_DIEN_THOAI, edtSoDienThoai.getText().toString());
        outState.putBoolean(KEY_LOI_NGUOI_DUNG_VISIBLE, tvLoiNguoiDung.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_LOI_DANG_NHAP_VISIBLE, tvLoiDangNhap.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_LOI_EMAIL_VISIBLE, tvLoiEmail.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_LOI_MAT_KHAU_VISIBLE, tvLoiMatKhau.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_LOI_NHAP_LAI_MAT_KHAU_VISIBLE, tvLoiNhapLaiMatKhau.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_LOI_SO_DIEN_THOAI_VISIBLE, tvLoiSoDienThoai.getVisibility() == View.VISIBLE);
        outState.putBoolean(KEY_TERMS_AGREED, hasAgreedToTerms);
    }

    public static class User {
        public String nguoiDung, dangNhap, email, matKhau, soDienThoai, role;

        public User() {}
        public User(String nguoiDung, String dangNhap, String email, String matKhau, String soDienThoai, String role) {
            this.nguoiDung = nguoiDung;
            this.dangNhap = dangNhap;
            this.email = email;
            this.matKhau = matKhau;
            this.soDienThoai = soDienThoai;
            this.role = role;
        }
    }
}