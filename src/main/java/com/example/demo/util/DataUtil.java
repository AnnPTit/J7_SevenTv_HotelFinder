package com.example.demo.util;

import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtil {
    private static final char KEY_ESCAPE = '\\';

    public static String likeSpecialToStr(String str) {
        str = str.trim();
        str = str.replace("_", KEY_ESCAPE + "_");
        str = str.replace("%", KEY_ESCAPE + "%");
        return str;
    }

    public static String makeLikeStr(String str) {
        if (isNullOrEmpty(str)) {
            return "%%";
        }
        return "%" + str + "%";
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static InformationCustomer convertCustomerToInformationCustomer(Customer customer) {
        InformationCustomer informationCustomer = new InformationCustomer();
        informationCustomer.setFullname(customer.getFullname());
        informationCustomer.setGender(customer.getGender());
        informationCustomer.setBirthday(customer.getBirthday());
        informationCustomer.setPhoneNumber(customer.getPhoneNumber());
        informationCustomer.setCitizenId(customer.getCitizenId());
        informationCustomer.setPassport(customer.getPassport());
        informationCustomer.setEmail(customer.getEmail());
        informationCustomer.setAddress(customer.getAddress());
        informationCustomer.setNationality(customer.getNationality());
        informationCustomer.setCreateAt(customer.getCreateAt());
        informationCustomer.setCreateBy(customer.getCreateBy());
        informationCustomer.setUpdateAt(customer.getUpdateAt());
        informationCustomer.setUpdatedBy(customer.getUpdatedBy());
        informationCustomer.setDeleted(customer.getDeleted());
        informationCustomer.setStatus(customer.getStatus());

        return informationCustomer;
    }

    public static String currencyFormat(BigDecimal n) {
        return new DecimalFormat("#,###").format(n);
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static boolean isInOneMonth(Date date) {
        // Lấy ngày hôm nay
        Date today = new Date();

        // Tạo một Calendar để tính toán thời gian
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        // Tính ngày sau 1 tháng
        calendar.add(Calendar.MONTH, 1);
        Date oneMonthFromNow = calendar.getTime();

        // Kiểm tra xem date có nằm trong khoảng 1 tháng từ ngày hiện tại trở đi
        if (date.after(today) && date.before(oneMonthFromNow)) {
            return true;
        } else if (date.equals(today) || date.equals(oneMonthFromNow)) {
            return true;
        } else {
            return false;
        }
    }

}
