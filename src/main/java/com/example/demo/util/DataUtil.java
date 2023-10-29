package com.example.demo.util;

import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;

import java.lang.reflect.Field;

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
}
