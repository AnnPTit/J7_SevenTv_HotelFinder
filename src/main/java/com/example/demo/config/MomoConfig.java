package com.example.demo.config;

public class MomoConfig {
	public static String PARTNER_CODE = "MOMOLRJZ20181206";
	public static String ACCESS_KEY = "mTCKt9W3eU1m39TW";
	public static String SECRET_KEY = "SetA5RDnLHvt51AULf51DyauxUo3kDU6";
	public static String PAY_QUERY_STATUS_URL = "https://test-payment.momo.vn/pay/query-status";
	public static String PAY_CONFIRM_URL = "https://test-payment.momo.vn/pay/confirm";
	public static String RETURN_URL = "http://localhost:2003/api/momo/test";
	public static String NOTIFY_URL = "http://localhost:2003/api/momo/success/payment";
	public static String IPN_URL = "https://fcf6-123-24-233-164.ngrok.io";
	public static String CREATE_ORDER_URL = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
	public static String REDIRECT_URL = "http://localhost:2003/api/payment-method/payment-momo/success";
	public static String REDIRECT_URL_ONLINE = "http://localhost:2003/api/payment-method/payment-momo/success/online";
}