package com.mayproject.seckill.exception;

import com.mayproject.seckill.result.CodeMsg;

public class GrobalException extends RuntimeException{

    private static final long serialVersionID = 1L;

    private CodeMsg cm;

    public CodeMsg getCm() {
        return cm;
    }

    public GrobalException(CodeMsg msg){
        super(msg.toString());
        this.cm = msg;
    }
}
