package com.example.demo.constant;

import java.net.URI;

public class Constant {
    public static final String citizenId = "034203007140";

    public static final class ORDER_STATUS {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
        public static Integer WAIT_PAYMENT = 4;
        public static Integer WAIT_CHECKIN = 5;
        public static Integer REFUSE = 6;
        public static Integer EXPIRED = 7;
        public static Integer EXPIRED_PAYMENT = 8;
        public static Integer EXPIRED_CHECKIN = 9;
    }

    public static final class ORDER_DETAIL {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
        public static Integer EXPIRED_PAYMENT = 8;
        public static Integer REFUSE = 6;
        public static Integer EXPIRED_CHECKIN = 9;
    }

    public static final class ROOM {
        public static Integer UNACTIVE = 0;
        public static Integer EMPTY = 1;
        public static Integer ACTIVE = 2;
        public static Integer WAIT_CLEAN = 3;
    }

    public static final class MANAGE_BOOKING {
        public static Integer UNACTIVE = 0;
        public static Integer WAIT_ROOM = 1;
        public static Integer ACTIVE = 2;
        public static Integer CHECKED_IN = 3;
        public static Integer CHECKED_OUT = 4;
    }

    public static final class COMMON_STATUS {
        public static Integer UNACTIVE = 0;
        public static Integer ACTIVE = 1;
    }

    public static final class MESSAGE_STATUS {
        public static Integer ERROR = 0;
        public static Integer SUCCESS = 1;
    }

    public static final class ORDER_TIMELINE {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
        public static Integer WAIT_PAYMENT = 4;
        public static Integer WAIT_CHECKIN = 5;
        public static Integer CHANGE_ROOM = 10;
        public static Integer LEAVE_EARLY = 7;
    }

    public static final class DISCOUNT_PROGRAM {
        public static String NOT_EFFECTIVE_YET = "Chờ triển khai";
        public static String ON_GOING = "Đang triển khai";
        public static String EXPIRED = "Quá hạn";
    }

    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");

    public static final class BOOKING {
        public static Integer NEW = -1;
        public static Integer SUCCESS = 1;
        public static Integer XEP_PHONG = 2;
        public static Integer CHECKIN = 3;
        public static Integer CHECKOUT = 4;
        public static Integer CANCEL = 5;
        public static Integer REFUSE = 0;
        public static Integer CANCELED = 6;

    }

    public static final class HISTORY_TYPE {
        public static Integer CREATE = 1;
        public static Integer CANCEL = 2;
    }

}
