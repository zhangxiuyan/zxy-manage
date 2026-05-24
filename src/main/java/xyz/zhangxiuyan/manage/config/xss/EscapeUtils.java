package xyz.zhangxiuyan.manage.config.xss;

/**
 * 提供字符串转义与对 POJO/Map/List 结构的递归转义（用于 JSON 深度转义）
 */
public final class EscapeUtils {

    private EscapeUtils() {}

    /**
     * HTML 转义（基础版）
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                case '/': sb.append("&#x2F;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * HTML Attribute 属性值安全编码
     */
    public static String escapeHtmlAttribute(String input) {
        if (input == null) return null;
        return escapeHtml(input)
                .replace("(", "&#40;")
                .replace(")", "&#41;")
                .replace(":", "&#58;");
    }

    /**
     * JavaScript String 安全编码
     */
    public static String escapeJsString(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\'': sb.append("\\'"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '<': sb.append("\\u003C"); break;
                case '>': sb.append("\\u003E"); break;
                case '&': sb.append("\\u0026"); break;
                case '=': sb.append("\\u003D"); break;
                case '-': sb.append("\\u002D"); break;
                default:
                    if (c < 32 || c > 126) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * JSON Value 安全清洗（不破坏业务）
     * 保留正常字符，只过滤危险标签特有模式
     */
    public static String escapeJsonValue(String input) {
        if (input == null) return null;

        String cleaned = input.replace("\u0000", ""); // NULL 字符消失攻击
        cleaned = cleaned.replaceAll("(?i)<script.*?>.*?</script>", "");
        cleaned = cleaned.replaceAll("(?i)javascript:", "");
        cleaned = cleaned.replaceAll("(?i)onerror=", "");
        cleaned = cleaned.replaceAll("(?i)onload=", "");
        cleaned = cleaned.replaceAll("(?i)alert\\s*\\(", "");
        return cleaned;
    }

    /**
     * URL 参数编码（避免 XSS，不做完整 URL 编码）
     */
    public static String escapeUrlParam(String input) {
        if (input == null) return null;
        try {
            return java.net.URLEncoder.encode(input, "UTF-8")
                    .replace("+", "%20")
                    .replace("%2B", "%2B");
        } catch (Exception e) {
            return input;
        }
    }

    /**
     * Query param 安全清理（过滤 XSS 特征 + 合法 URL 编码）
     */
    public static String escapeQueryParam(String input) {
        if (input == null) return null;
        return escapeUrlParam(
                input.replaceAll("(?i)<.*?>", "")
                        .replaceAll("(?i)script", "")
                        .replaceAll("(?i)javascript:", "")
                        .replaceAll("(?i)onerror=", "")
        );
    }

    /**
     * HTTP Header 安全清理（只允许可见 ASCII）
     * RFC7230 标准：header-value 不允许控制字符
     */
    public static String escapeHeader(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 32 && c <= 126) {
                sb.append(c);
            } else {
                sb.append('?'); // 禁止控制符
            }
        }
        return sb.toString();
    }

    /**
     * 企业级总入口（按场景路由）
     */
    public enum EscapeType {
        HTML,
        HTML_ATTRIBUTE,
        JSON_VALUE,
        JS_STRING,
        URL_PARAM,
        QUERY_PARAM,
        HEADER
    }

    public static String escape(String input, EscapeType type) {
        if (input == null) return null;

        switch (type) {
            case HTML:
                return escapeHtml(input);
            case HTML_ATTRIBUTE:
                return escapeHtmlAttribute(input);
            case JSON_VALUE:
                return escapeJsonValue(input);
            case JS_STRING:
                return escapeJsString(input);
            case URL_PARAM:
                return escapeUrlParam(input);
            case QUERY_PARAM:
                return escapeQueryParam(input);
            case HEADER:
                return escapeHeader(input);
            default:
                return input;
        }
    }
}

