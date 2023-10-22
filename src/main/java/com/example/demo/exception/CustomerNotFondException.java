package com.example.demo.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class CustomerNotFondException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public CustomerNotFondException() {
        super(null, "Tên đăng nhập không đúng", Status.UNAUTHORIZED);
    }
}
