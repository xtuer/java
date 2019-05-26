package com.xtuer.util;

import com.alibaba.fastjson.JSON;
import com.xtuer.bean.Result;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Web 操作相关的辅助工具，例如:
 * 获取客户端 IP
 * 读写删除 Cookie
 * 读取文件到 HttpServletResponse
 * 向 HttpServletResponse 写入 Ajax 响应
 * 判断请求是否使用 Ajax，获取 URI 的文件名
 * <p>
 * 提示:
 * 1. HttpServletRequest.getRequestURI() 返回的 URI 不带有参数
 */
public final class WebUtils {
    private static Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static final String UNKNOWN = "unknown";

    /**
     * 判断请求是否 AJAX 请求
     *
     * @param request HttpServletRequest 对象
     * @return 如果是 AJAX 请求则返回 true，否则返回 false
     */
    public static boolean useAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    /**
     * 使用 AJAX 的方式把响应写入 response 中，编码使用 UTF-8，HTTP 状态码为 200
     *
     * @param response HttpServletResponse 对象，用于写入请求的响应
     * @param result   响应的数据
     */
    public static void ajaxResponse(HttpServletResponse response, Result result) {
        ajaxResponse(response, result, 200);
    }

    /**
     * 使用 AJAX 的方式把响应写入 response 中，编码使用 UTF-8，HTTP 状态码为 200
     *
     * @param response HttpServletResponse 对象，用于写入请求的响应
     * @param result   响应的数据
     * @param statusCode HTTP 状态码
     */
    public static void ajaxResponse(HttpServletResponse response, Result result, int statusCode) {
        ajaxResponse(response, JSON.toJSONString(result), statusCode);
    }

    /**
     * 使用 AJAX 的方式把响应写入 response 中，编码使用 UTF-8
     *
     * @param response   HttpServletResponse 对象，用于写入请求的响应
     * @param data       响应的数据
     * @param statusCode HTTP 状态码
     */
    public static void ajaxResponse(HttpServletResponse response, String data, int statusCode) {
        response.setContentType("application/json"); // 使用 ajax 的方式
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        try {
            // 写入数据到流里，刷新并关闭流
            PrintWriter writer = response.getWriter();
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * 获取当前线程的 request
     *
     * @return 返回 request
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 path 位于 WEB-INF 目录下的绝对路径，常用于读取静态资源，配置文件等。
     * 例如 path 为 static/books，则返回的路径为 ${project-path}/WEB-INF/static/books
     *
     * @param path 关于 WEB-INF 的相对路径
     * @return 返回 path 的绝对路径
     */
    public static String getPathInWebInf(String path) {
        return WebUtils.getRequest().getServletContext().getRealPath("/WEB-INF/" + path);
    }

    /**
     * 获取名字为 name 的 cookie 的值
     *
     * @param request HttpServletRequest 对象
     * @param name    Cookie 的名字
     * @return 返回名字为 name 的 cookie 的值，如果 name 不存在则返回 null
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 把 name/value 写入 cookie
     *
     * @param response 请求响应的 HttpServletResponse
     * @param name     cookie 的 name
     * @param value    cookie 的 value
     * @param maxAge   cookie 的过期时间，单位为秒，为 0 时删除 cookie
     */
    public static void writeCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    /**
     * 删除 cookie
     *
     * @param response 请求响应的 HttpServletResponse
     * @param name     cookie 的 name
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        writeCookie(response, name, null, 0);
    }

    /**
     * 获取客户端的 IP
     *
     * @param request HttpServletRequest 对象
     * @return 客户端的 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // 有多个 Proxy 的情况: X-Forwarded-For: client, proxy1, proxy2 是一串 IP，第一个 IP 是客户端的 IP
        // 只有 1 个 Proxy 时取到的就是客户端的 IP
        if (!(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))) {
            String[] ips = ip.split(",");
            return ips[0];
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // 没有使用 Proxy 时是客户端的 IP, 使用 Proxy 时是最近的 Proxy 的 IP
        }

        return ip;
    }

    /**
     * 获取当前请求的 Host
     *
     * @return 请求的 host
     */
    public static String getHost() {
        try {
            return new URL(WebUtils.getRequest().getRequestURL().toString()).getHost();
        } catch (MalformedURLException e) {
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    /**
     * 获取请求的 URI (没有参数, 域名等信息)
     *
     * @param request HttpServletRequest 对象
     * @return 返回请求的 URI
     */
    public static String getUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 获取请求中的文件名
     * 例如 http://localhost:8080/preview/file/temp/220059763684147200.doc?size=100 得到文件名 220059763684147200.doc
     *
     * @param request HttpServletRequest 对象
     * @return 返回请求的文件名
     */
    public static String getUriFilename(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String filename = FilenameUtils.getName(uri);

        return filename;
    }
}
