package github.scarsz.mori.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

    public static String getRealRemoteAddress(HttpServletRequest request) {
        if (StringUtils.isNotBlank(request.getHeader("CF-Connecting-IP"))) return request.getHeader("CF-Connecting-IP");
        if (StringUtils.isNotBlank(request.getHeader("X-Forwarded-For"))) return request.getHeader("X-Forwarded-For");
        return request.getRemoteAddr();
    }

}
