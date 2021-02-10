package example.ljava.util;

import java.util.Map;

import com.google.common.collect.Maps;

import org.springframework.data.domain.Page;

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

    public static Result success() {
        return new Result(null, "成功", Result.CODE_SUCCESS);
    }

    public static Result success(Page data) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("rows", data.toList());
        map.put("total", data.getTotalElements());
        return new Result(map, "成功", Result.CODE_SUCCESS);
    }

    public static Result success(Object data) {
        return new Result(data, "成功", Result.CODE_SUCCESS);
    }

    public static Result failure() {
        return new Result(null, "失败", Result.CODE_FAILURE);
    }

    public static Result failure(String msg) {
        return new Result(null, msg, Result.CODE_FAILURE);
    }
    
    public static Result failure(String msg, Object data) {
        return new Result(data, msg, Result.CODE_FAILURE);
    }

    public static Result failure(String msg, Object data, Integer code) {
        return new Result(data, msg, code);
    }
}
