package com.xiaowu.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdUtil {

    public static String gen32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
