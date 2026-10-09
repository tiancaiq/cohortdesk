package com.github.tiancaiq.tlias_pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public final class Result<T> implements Serializable {
    public static final class ResultConstants {
        public static final class Success {
            public static final Integer CODE = 1;
            public static final String MSG = "success";

            private Success(){}
        }

        public static final class Error {
            public static final Integer CODE = 0;

            private Error(){}
        }

        private ResultConstants(){}
    }




    private Integer code;
    private String msg;
    private T data;




    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static <T> Result<T> success( T object ){
        Result<T> result = new Result<>();
        result.data = object;
        result.code = ResultConstants.Success.CODE;
        result.msg = ResultConstants.Success.MSG;
        return result;
    }

    public static <T> Result<T> error( String msg ){
        Result<T> result = new Result<>();
        result.code = ResultConstants.Error.CODE;
        result.msg = msg;
        return result;
    }



}
