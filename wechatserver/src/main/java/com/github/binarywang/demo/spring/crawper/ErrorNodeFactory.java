package com.github.binarywang.demo.spring.crawper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ErrorNodeFactory {

    public static final int FIND_NO_USER_PRIVACY = 1;
    public static final int USER_PASSWORD_NOT_CORRENT = 2;

    public static String getErrorNode(int code) {
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode errorNode = factory.objectNode();
        ObjectNode errorCode = factory.objectNode();
        errorCode.put("reason", code);
        errorNode.set("error", errorCode);
        return errorNode.toString();
    }

    public static void main(String[] args) {
        System.out.println(ErrorNodeFactory.getErrorNode(FIND_NO_USER_PRIVACY));
    }
}
