package com.example.demo.constant;

import java.net.URI;

public class Constant {

    public static final class ORDER_STATUS {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
        public static Integer WAIT_PAYMENT = 4;
        public static Integer WAIT_CHECKIN = 5;
        public static Integer REFUSE = 6;
        public static Integer EXPIRED = 7;
    }

    public static final class ORDER_DETAIL {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
    }

    public static final class ROOM {
        public static Integer UNACTIVE = 0;
        public static Integer EMPTY = 1;
        public static Integer ACTIVE = 2;
    }

    public static final class COMMON_STATUS {
        public static Integer UNACTIVE = 0;
        public static Integer ACTIVE = 1;
    }

    public static final class ORDER_TIMELINE {
        public static Integer CANCEL = 0;
        public static Integer WAIT_CONFIRM = 1;
        public static Integer CHECKED_IN = 2;
        public static Integer CHECKED_OUT = 3;
        public static Integer WAIT_PAYMENT = 4;
        public static Integer WAIT_CHECKIN = 5;
        public static Integer REFUSE = 6;
        public static Integer LEAVE_EARLY = 7;
    }

    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");


}
