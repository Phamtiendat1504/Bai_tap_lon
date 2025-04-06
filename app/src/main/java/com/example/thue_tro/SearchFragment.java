package com.example.thue_tro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    private Spinner spinnerDistrict, spinnerRoomType;
    private EditText edtAddress;
    private Button btnApply, btnPriceAll, btnPriceUnder2M, btnPrice2Mto4M, btnPrice4Mto6M, btnPrice6Mto8M, btnPriceAbove10M;
    private String selectedDistrict, selectedRoomType, selectedPriceRange = null; // Không mặc định "Tất cả"
    private Button selectedPriceButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ
        spinnerDistrict = view.findViewById(R.id.spinnerDistrict);
        spinnerRoomType = view.findViewById(R.id.spinnerRoomType);
        edtAddress = view.findViewById(R.id.edtAddress);
        btnApply = view.findViewById(R.id.btnApply);
        btnPriceAll = view.findViewById(R.id.btnPriceAll);
        btnPriceUnder2M = view.findViewById(R.id.btnPriceUnder2M);
        btnPrice2Mto4M = view.findViewById(R.id.btnPrice2Mto4M);
        btnPrice4Mto6M = view.findViewById(R.id.btnPrice4Mto6M);
        btnPrice6Mto8M = view.findViewById(R.id.btnPrice6Mto8M);
        btnPriceAbove10M = view.findViewById(R.id.btnPriceAbove10M);

        // Mặc định nút Apply bị vô hiệu hóa
        btnApply.setEnabled(false);

        // Danh sách quận của Hà Nội
        String[] districts = {
                "Ba Đình", "Hoàn Kiếm", "Hai Bà Trưng", "Đống Đa", "Tây Hồ", "Cầu Giấy", "Thanh Xuân",
                "Hoàng Mai", "Long Biên", "Nam Từ Liêm", "Bắc Từ Liêm", "Hà Đông", "Sơn Tây", "Ba Vì",
                "Chương Mỹ", "Đan Phượng", "Đông Anh", "Gia Lâm", "Hoài Đức", "Mê Linh", "Mỹ Đức",
                "Phú Xuyên", "Phúc Thọ", "Quốc Oai", "Sóc Sơn", "Thạch Thất", "Thanh Oai", "Thanh Trì",
                "Thường Tín", "Ứng Hòa"
        };
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, districts);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        // Danh sách loại phòng
        String[] roomTypes = {"Phòng trọ", "Chung cư", "Nhà nguyên căn"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, roomTypes);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        // Xử lý sự kiện chọn quận
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = districts[position];
                updateApplyButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDistrict = null;
                updateApplyButtonState();
            }
        });

        // Xử lý sự kiện chọn loại phòng
        spinnerRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoomType = roomTypes[position];
                updateApplyButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoomType = null;
                updateApplyButtonState();
            }
        });

        // Xử lý các nút giá
        View.OnClickListener priceClickListener = v -> {
            Button clickedButton = (Button) v;
            resetPriceButtons();
            clickedButton.setSelected(true);
            selectedPriceButton = clickedButton;
            selectedPriceRange = clickedButton.getText().toString();
            updateApplyButtonState();
        };

        btnPriceAll.setOnClickListener(priceClickListener);
        btnPriceUnder2M.setOnClickListener(priceClickListener);
        btnPrice2Mto4M.setOnClickListener(priceClickListener);
        btnPrice4Mto6M.setOnClickListener(priceClickListener);
        btnPrice6Mto8M.setOnClickListener(priceClickListener);
        btnPriceAbove10M.setOnClickListener(priceClickListener);

        // Xử lý nút Áp dụng
        btnApply.setOnClickListener(v -> {
            String address = edtAddress.getText().toString().trim();
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra("city", "Hà Nội");
            intent.putExtra("district", selectedDistrict);
            intent.putExtra("address", address);
            intent.putExtra("roomType", selectedRoomType);
            intent.putExtra("priceRange", selectedPriceRange);
            Bundle bundle = getArguments();
            if (bundle != null) {
                intent.putExtra("username", bundle.getString("username"));
                intent.putExtra("role", bundle.getString("role"));
            }
            startActivity(intent);
        });
    }

    // Đặt lại trạng thái các nút giá
    private void resetPriceButtons() {
        btnPriceAll.setSelected(false);
        btnPriceUnder2M.setSelected(false);
        btnPrice2Mto4M.setSelected(false);
        btnPrice4Mto6M.setSelected(false);
        btnPrice6Mto8M.setSelected(false);
        btnPriceAbove10M.setSelected(false);
    }

    // Cập nhật trạng thái nút Áp dụng
    private void updateApplyButtonState() {
        // Yêu cầu quận, loại phòng và giá phải được chọn
        boolean isValid = selectedDistrict != null && selectedRoomType != null && selectedPriceRange != null;
        btnApply.setEnabled(isValid);
    }
}