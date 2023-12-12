package com.example.demo.util;

import com.example.demo.entity.Customer;
import com.example.demo.entity.InformationCustomer;
import com.example.demo.model.Mail;
import com.example.demo.service.MailService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String dateToString2(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy\nHH:mm:ss");
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
        } else {
            return false;
        }
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        // Chuyển đổi thành ZonedDateTime
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj.equals("")) {
            return true;
        }
        return false;
    }

    public static java.time.ZonedDateTime getCurrentDateTime() {
        return ZonedDateTime.now();
    }

    public static Date setFixedTime(Date originalDate, int hour, int minute, int second) {
        // Chuyển đổi Date thành LocalDateTime
        LocalDateTime localDateTime = originalDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Thiết lập giờ cố định
        localDateTime = localDateTime.withHour(hour).withMinute(minute).withSecond(second);

        // Chuyển đổi LocalDateTime trở lại thành Date
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    // Convert ngày theo định dạng
    public static String convertDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }

    public static String formatMoney(BigDecimal money) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(money);
    }


    public static void sendMailCommon(String mailTo, String subject, String content, MailService mailService) {
        // Create a Mail object and send the email
        Mail mail = new Mail();
        mail.setMailFrom("nguyenvantundz2003@gmail.com");
        mail.setMailTo(mailTo);
        mail.setMailContent(content);
        mail.setMailSubject(subject);
        mailService.sendEmail(mail);
    }

    public static String dateToStringSql(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateRange = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate.minusDays(1))) {
            dateRange.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dateRange;
    }


    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static List<String> cutStringIntoParts(String inputString, int partLength) {
        List<String> result = new ArrayList<>();
        int len = inputString.length();

        for (int i = 0; i < len; i += partLength) {
            int end = Math.min(i + partLength, len);
            result.add(inputString.substring(i, end));
        }

        return result;
    }
}
