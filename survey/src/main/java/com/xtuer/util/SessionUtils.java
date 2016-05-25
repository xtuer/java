package com.xtuer.util;

import javax.servlet.http.HttpSession;

public class SessionUtils {
    public static int getIntegerFromSession(HttpSession session, String name, int defaultValue) {
        int value = defaultValue;

        if (session.getAttribute(name) != null) {
            try {
                value = Integer.parseInt(session.getAttribute(name).toString());
            } catch (Exception ex) {
            }
        }

        return value;
    }
}
