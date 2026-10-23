package com.hollow.util;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * 跳转地址工具类。
 *
 * 需要"处理完回到来源页面"时，Referer 由浏览器提供、可以被伪造，
 * 直接拿来 sendRedirect 会形成开放重定向（外部站点可以借本站做跳板）。
 * 这里只保留本站路径和查询串，主机与协议一律丢弃，跳转目标永远是本站。
 */
public class Yoa_RedirectUtil {

    /**
     * 从 Referer 中取出本站的路径，取不到或不合法时返回 fallback。
     *
     * @param request  当前请求
     * @param fallback 取不到时的默认地址，应自带 contextPath，如 /hollow/app/my
     */
    public static String safeBackPath(HttpServletRequest request, String fallback) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return fallback;
        }

        try {
            URI uri = URI.create(referer);
            String path = uri.getPath();

            // 只接受落在本应用上下文下的路径，外部站点的 path 通常不带这个前缀
            if (path == null || !path.startsWith(request.getContextPath() + "/")) {
                return fallback;
            }

            if (uri.getRawQuery() == null) {
                return path;
            }
            return path + "?" + uri.getRawQuery();

        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
