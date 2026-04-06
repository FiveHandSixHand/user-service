package com.fhsh.daitda.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasRole {
    String[] value(); // 여러 권한을 허용할 수 있도록 배열로 변경
}