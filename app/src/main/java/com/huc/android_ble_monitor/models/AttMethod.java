package com.huc.android_ble_monitor.models;

public enum AttMethod {
    ALL("ALL"),
    ATT_EXCHANGE_MTU("ATT_EXCHANGE_MTU"),
    ATT_ERROR("ATT_ERROR"),
    ATT_FIND_INFORMATION("ATT_FIND_INFORMATION"),
    ATT_READ("ATT_READ"),
    ATT_WRITE("ATT_WRITE"),
    ATT_READ_BY_TYPE("ATT_READ_BY_TYPE"),
    ATT_READ_BY_GROUP_TYPE("ATT_READ_BY_GROUP_TYPE"),
    ATT_FIND_BY_TYPE_VALUE("ATT_FIND_BY_TYPE_VALUE"),
    ATT_HANDLE_VALUE_NTF("ATT_HANDLE_VALUE_NTF"),
    ATT_HANDLE_VALUE_IND("ATT_HANDLE_VALUE_IND"),
    ATT_SIGNED_WRITE_CMD("ATT_SIGNED_WRITE_CMD"),
    ATT_READ_BLOB("ATT_READ_BLOB"),
    ATT_READ_MULTIPLE("ATT_READ_MULTIPLE"),
    ATT_WRITE_CMD("ATT_WRITE_CMD"),
    ATT_READ_MULTIPLE_VARIABLE("ATT_READ_MULTIPLE_VARIABLE"),
    ATT_PREPARE_WRITE("ATT_PREPARE_WRITE"),
    ATT_EXECUTE_WRITE("ATT_EXECUTE_WRITE");

    private String attMethod;

    AttMethod(String method) {
        this.attMethod = method;
    }

    public String getAttMethod() {
        return attMethod;
    }
}
