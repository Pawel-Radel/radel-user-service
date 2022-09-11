package com.radel.radel.user.service.utils;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.radel.services.user.api.UserSearchRequestFields;

public class UserSearchRequestAttributesExtractor {

    public static Map<String, String> extract(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> result = new HashMap<>();

        if (parameterMap.isEmpty()) {
            return null;
        }

        Set<String> systemFields = UserSearchRequestFields.getValues();
        systemFields.addAll(asList("page", "size", "sort"));

        parameterMap.forEach((key, valueArray) -> {
            if (!systemFields.contains(key) && !isNull(valueArray)) {

                String value = valueArray.length == 0 ? null : valueArray[0];
                result.put(key, value);
            }
        });

        return result;
    }
}
