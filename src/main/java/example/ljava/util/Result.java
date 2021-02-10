package example.ljava.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAILURE = 1;

    Object data;
    String msg;
    Integer code;

    public static Result success(Object data, String msg, Integer code) {
        return new Result(data, msg, code);
    }

    public static Result success(Object data) {
        return new Result(data, "成功", Result.CODE_SUCCESS);
    }

    public static Result failure(String msg) {
        return new Result(null, msg, Result.CODE_FAILURE);
    }

    public static Result failure(String msg, Object data, Integer code) {
        return new Result(data, msg, code);
    }
}
