package com.example.demo.config;

public class ZaloPayConfig {
	public static String APP_ID = "553";
	public static String KEY1 = "9phuAOYhan4urywHTh0ndEXiV3pKHr5Q";
	public static String KEY2 = "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3";
	public static String CREATE_ORDER_URL = "https://sandbox.zalopay.com.vn/v001/tpe/createorder";
	public static String GET_STATUS_PAY_URL = "https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid";
	public static String REDIRECT_URL = "http://localhost:2003/api/payment-method/payment-zalo/success";
}