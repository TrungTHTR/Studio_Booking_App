package com.example.demofacebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.demofacebook.Adapter.Chat.Booking.ExpandableListAdapter;
import com.example.demofacebook.HomePage.HomeActivity;
import com.example.demofacebook.Model.BookingGroupItem;
import com.example.demofacebook.Model.SlotBooking;
import com.example.demofacebook.Model.SlotBookingItem;
import com.example.demofacebook.Model.Studio;

import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingStudioActivity extends AppCompatActivity {
    private Button btn_Continue;
    private Button btn_Cancel;
    Toolbar toolbar;
    private Studio studio;
    private List<SlotBooking> slotBookings;
    ExpandableListView expandableListView;
    List<BookingGroupItem> mGroupList;
    Map<BookingGroupItem, List<SlotBookingItem>> mListItems;
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_studio);
        initToolBar();
        studio = loadStudio();
        slotBookings = loadSlotBooking();
        loadDataOrderDetail();

        btn_Continue = findViewById(R.id.btn_ContinuePayment);
        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), PaymentBookingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderId", "1");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btn_Cancel = findViewById(R.id.btn_CancelBooking);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                startActivity(intent);
            }
        });

        expandableListView = findViewById(R.id.ExpandableListBooking);
        mListItems = getListItems();
        mGroupList = new ArrayList<>(mListItems.keySet());
        expandableListAdapter = new ExpandableListAdapter(mGroupList, mListItems, getApplicationContext());
        expandableListView.setAdapter(expandableListAdapter);
    }

    private void loadDataOrderDetail() {
        TextView bookingPrice = findViewById(R.id.tv_BookingPrice);
        TextView fees = findViewById(R.id.tv_BookingFee);
        TextView total = findViewById(R.id.tv_TotalPrice);
        double percentFees = 0.05;

        int totalPrice = 0;
        for (int i = 0; i < slotBookings.size(); i++) {
            totalPrice = totalPrice + slotBookings.get(i).getPrice();
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        int roundedPrice = customRound(totalPrice * percentFees);

        bookingPrice.setText(numberFormat.format(totalPrice) + "VND");
        fees.setText(numberFormat.format(roundedPrice) + "VND");
        total.setText(numberFormat.format(roundedPrice + totalPrice) + "VND");
    }

    public static int customRound(double number) {
        double fractionalPart = number - (int) number;

        if (fractionalPart >= 0.5) {
            return (int) Math.round(number);
        } else {
            return (int) Math.floor(number);
        }
    }

    private Studio loadStudio() {
        if (getIntent().getExtras() != null) {
            Studio studio = (Studio) getIntent().getExtras().get("studio");
            if (studio != null) {
                return studio;
            }
        }
        return null;
    }

    private List<SlotBooking> loadSlotBooking() {
        if (getIntent().getExtras() != null) {
            List<SlotBooking> slotBooking = (List<SlotBooking>) getIntent().getExtras().get("bookingSlot");
            return slotBooking;
        }
        return null;
    }

    private Map<BookingGroupItem, List<SlotBookingItem>> getListItems() {
        Map<BookingGroupItem, List<SlotBookingItem>> listMap = new HashMap<>();

        BookingGroupItem groupItem = new BookingGroupItem(studio.getStudioId(), studio.getAvatarStudio(), studio.getName(), studio.getName(), formatDateString(slotBookings.get(0).getSlotDate()));

        List<SlotBookingItem> items = new ArrayList<>();
        for (int i = 0; i < slotBookings.size(); i++) {
            String starTime = formatTimeString(slotBookings.get(i).getStartTime());
            String endTime = formatTimeString(slotBookings.get(i).getEndTime());
            items.add(new SlotBookingItem(slotBookings.get(i).getSlotId(), starTime + " - " + endTime));
        }
        listMap.put(groupItem, items);

        return listMap;
    }

    public String formatTimeString(String string) {
        ZonedDateTime zonedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.parse(string);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String timeOnly = zonedDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            return timeOnly;
        }
        return null;
    }

    public String formatDateString(String string) {
        ZonedDateTime zonedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.parse(string);

            int day = zonedDateTime.getDayOfMonth();
            int month = zonedDateTime.getMonthValue();
            int year = zonedDateTime.getYear();
            return day + "-" + month + "-" + year;
        }
        return null;
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.ToolBarBookingStudioActivity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Detail");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}