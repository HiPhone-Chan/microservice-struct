package com.chf.core.constants;

public interface ErrorCodeContants {
    String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
    String INVALID_PASSWORD = "INVALID_PASSWORD";
    String LOGIN_ALREADY_USED = "LOGIN_ALREADY_USED";

    String INVALID_DATA = "INVALID_DATA";
    String DATA_FORMAT = "DATA_FORMAT";
    String TOO_MUCH_DATA = "TOO_MUCH_DATA";
    String LACK_OF_DATA = "LACK_OF_DATA";
    String CONDITION_NOT_RIGHT = "CONDITION_NOT_RIGHT";
    String BAD_PARAMETERS = "BAD_PARAMETERS";
    String REQUEST_FREQUENCE = "REQUEST_FREQUENCE";
    String NO_PRIVILEGE = "NO_PRIVILEGE"; // 没权限处理
    String API_NOT_FINISH = "API_NOT_FINISH";
    // 用户
    String USER_NOT_EXISTS = "USER_NOT_EXISTS";
    String CODE_ERROR = "CODE_ERROR";
    String CODE_EXPIRED = "CODE_EXPIRED";
    String SMS_TOPLIMIT = "SMS_TOPLIMIT";

    String OUT_OF_RANGE = "OUT_OF_RANGE";
    String ACCESS_DENIED = "ACCESS_DENIED";
    String UNAUTHORIZED = "UNAUTHORIZED";
    String VALIDATION_FAIL = "VALIDATION_FAIL";
    String METHOD_NOT_SUPPORTED = "METHOD_NOT_SUPPORTED";
    String CONCURRENCY_FAILURE = "CONCURRENCY_FAILURE";
    String OTHER_ERROR = "OTHER_ERROR";

    String MESSAGE_SEND_FAIL = "MESSAGE_SEND_FAIL";
    String HTTP_FAIL = "HTTP_FAIL";

    // 订单相关
    String AMOUNT_ERROR = "AMOUNT_ERROR";
    String CANNOT_GET_PRICE = "CANNOT_GET_PRICE";
    String CANCEL_TOO_MANY = "CANCEL_TOO_MANY";
    String EXISTS_ORDER = "EXISTS_ORDER";
    String ORDER_NOT_EXISTS = "ORDER_NOT_EXISTS";
    String ERROR_ORDER_STATUS = "ERROR_ORDER_STATUS";
    String ERROR_DRIVER_STATUS = "ERROR_DRIVER_STATUS";
    String STATUS_CHANGE_FAIL = "STATUS_CHANGE_FAIL";
    // 第三方API
    String API_ERROR = "API_ERROR";

}