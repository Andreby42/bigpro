package com.thc.platform.modules.ocr.util.checker;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BeanChecker {
    public BeanChecker() {
    }

    public static void assertNotNull(Object obj, String errMsg) throws RuntimeException {
        if (obj == null) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotEmpty(String str, String errMsg) throws RuntimeException {
        if (StringUtils.isEmpty(str)) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotEmpty(Collection collection, String errMsg) throws RuntimeException {
        if (CollectionUtils.isEmpty(collection)) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotEmpty(Map map, String errMsg) throws RuntimeException {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertPositive(Long o, String errMsg) throws RuntimeException {
        if (o == null || o <= 0L) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertPositive(Integer o, String errMsg) throws RuntimeException {
        if (o == null || o <= 0) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertPositive(Float o, String errMsg) throws RuntimeException {
        if (o == null || o <= 0.0F) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertPositive(BigDecimal o, String errMsg) throws RuntimeException {
        if (o == null || o.compareTo(new BigDecimal("0")) <= 0) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertLongPositive(List<Long> objs, String errMsg) throws RuntimeException {
        Iterator var2 = objs.iterator();

        while (var2.hasNext()) {
            Long o = (Long) var2.next();
            assertPositive(o, errMsg);
        }

    }

    public static void assertIntegerPositive(List<Integer> objs, String errMsg) throws RuntimeException {
        Iterator var2 = objs.iterator();

        while (var2.hasNext()) {
            Integer o = (Integer) var2.next();
            assertPositive(o, errMsg);
        }

    }

    public static void assertNotNegative(Long o, String errMsg) throws RuntimeException {
        if (o == null || o < 0L) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotNegative(Integer o, String errMsg) throws RuntimeException {
        if (o == null || o < 0) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotNegative(BigDecimal o, String errMsg) throws RuntimeException {
        if (o == null || o.compareTo(new BigDecimal("0")) < 0) {
            throw new RuntimeException(errMsg);
        }
    }

    public static void assertNotNegativeLong(List<Long> objs, String errMsg) throws RuntimeException {
        Iterator var2 = objs.iterator();

        while (var2.hasNext()) {
            Long o = (Long) var2.next();
            assertNotNegative(o, errMsg);
        }

    }

    public static void assertNotNegativeInteger(List<Integer> objs, String errMsg) throws RuntimeException {
        Iterator var2 = objs.iterator();

        while (var2.hasNext()) {
            Integer o = (Integer) var2.next();
            assertNotNegative(o, errMsg);
        }

    }

    public static void assertPattern(String str, String pattern, String errMsg) throws RuntimeException {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(pattern) || !Pattern.matches(pattern, str)) {
            throw new RuntimeException(errMsg);
        }
    }
}
