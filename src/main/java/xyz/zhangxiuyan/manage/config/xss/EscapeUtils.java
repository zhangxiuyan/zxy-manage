package xyz.zhangxiuyan.manage.config.xss;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * XSS 安全工具类 — 按输出上下文提供编码/清洗策略。
 * <p>
 * 核心原则：
 * <ul>
 *   <li><b>HTML 富文本</b>：使用 OWASP HTML Sanitizer 白名单过滤（仅保留安全标签/属性）</li>
 *   <li><b>JSON API</b>：不做 HTML 清洗，JSON 中的字符串由前端框架按上下文编码输出</li>
 *   <li><b>HTTP Header</b>：按 RFC 7230 剥离控制字符</li>
 *   <li><b>URL 参数</b>：只做 URL 编码，不删除内容</li>
 * </ul>
 *
 * @see org.owasp.html.PolicyFactory
 */
public final class EscapeUtils {

    private EscapeUtils() {
    }

    // ---- OWASP HTML Sanitizer（白名单模式，只保留安全标签）----

    /**
     * 仅允许纯文本（所有 HTML 标签移除），保留换行。
     */
    private static final PolicyFactory TEXT_ONLY = new HtmlPolicyBuilder()
            .toFactory();

    /**
     * 允许基础格式化标签（b, i, u, em, strong, p, br, ul, ol, li, a）。
     * 链接仅保留 href（限定 http/https/mailto 协议）。
     */
    private static final PolicyFactory BASIC_FORMATTING = new HtmlPolicyBuilder()
            .allowElements("b", "i", "u", "em", "strong", "p", "br",
                    "ul", "ol", "li", "a", "span", "div")
            .allowUrlProtocols("http", "https", "mailto")
            .allowAttributes("href").onElements("a")
            .allowAttributes("class").globally()
            .toFactory();

    // ---- 公共方法 ----

    /**
     * HTML 富文本清洗 — 仅保留纯文本，移除所有标签。
     * 适用于用户昵称、评论等不需要富文本的字段。
     */
    public static String sanitizeHtmlToText(String input) {
        if (input == null) return null;
        return TEXT_ONLY.sanitize(input);
    }

    /**
     * HTML 富文本清洗 — 保留安全的基础格式化标签。
     * 适用于支持简单富文本的内容字段（如公告、介绍）。
     */
    public static String sanitizeHtmlBasic(String input) {
        if (input == null) return null;
        return BASIC_FORMATTING.sanitize(input);
    }

    /**
     * HTML 实体编码 — 将特殊字符转为 HTML 实体。
     * 适用于将用户数据显示在 HTML 标签内容中时使用。
     */
    public static String encodeHtml(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0, len = input.length(); i < len; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;"); break;
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * HTML 属性值编码 — 比标签内容更严格，额外编码空格和等号。
     */
    public static String encodeHtmlAttribute(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0, len = input.length(); i < len; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;"); break;
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                case ' ':  sb.append("&#32;"); break;
                case '=':  sb.append("&#61;"); break;
                case '`':  sb.append("&#96;"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * HTTP Header 值清洗 — 按 RFC 7230 剥离控制字符，仅保留可见 ASCII。
     * Header 注入防御。
     */
    public static String encodeHeader(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0, len = input.length(); i < len; i++) {
            char c = input.charAt(i);
            if (c >= 32 && c <= 126) {
                sb.append(c);
            }
            // 控制字符直接丢弃
        }
        return sb.toString();
    }

    /**
     * URL 参数值编码 — 使用 UTF-8 URL 编码。
     * 不做内容删除，仅确保参数值在 URL 中安全传输。
     */
    public static String encodeUrlParam(String input) {
        if (input == null) return null;
        try {
            return java.net.URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            return input;
        }
    }

    // ---- 场景枚举 ----

    public enum EscapeType {
        /** HTML 富文本 → 纯文本（OWASP Sanitizer） */
        SANITIZE_TEXT,
        /** HTML 富文本 → 保留基础格式（OWASP Sanitizer） */
        SANITIZE_BASIC,
        /** HTML 标签内容编码（实体转义） */
        ENCODE_HTML,
        /** HTML 属性值编码 */
        ENCODE_HTML_ATTRIBUTE,
        /** HTTP Header 值清洗 */
        ENCODE_HEADER,
        /** URL 参数值编码 */
        ENCODE_URL_PARAM
    }

    public static String escape(String input, EscapeType type) {
        if (input == null) return null;
        switch (type) {
            case SANITIZE_TEXT:       return sanitizeHtmlToText(input);
            case SANITIZE_BASIC:      return sanitizeHtmlBasic(input);
            case ENCODE_HTML:         return encodeHtml(input);
            case ENCODE_HTML_ATTRIBUTE: return encodeHtmlAttribute(input);
            case ENCODE_HEADER:       return encodeHeader(input);
            case ENCODE_URL_PARAM:    return encodeUrlParam(input);
            default:                  return input;
        }
    }
}
