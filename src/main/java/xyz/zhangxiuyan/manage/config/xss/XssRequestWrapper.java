package xyz.zhangxiuyan.manage.config.xss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * XSS 请求包装器 — 对请求参数、Header、JSON Body 做安全处理。
 * <p>
 * 策略：
 * <ul>
 *   <li>Query 参数：URL 编码（不删除内容，避免破坏合法数据如 "description"）</li>
 *   <li>Header：剥离控制字符（RFC 7230）</li>
 *   <li>JSON Body：对字符串值使用 OWASP HTML Sanitizer（纯文本模式，移除 HTML 标签）</li>
 * </ul>
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = readAndCleanBody(request);
    }

    // ---- Query 参数 ----

    @Override
    public String getParameter(String name) {
        return EscapeUtils.escape(super.getParameter(name), EscapeUtils.EscapeType.ENCODE_URL_PARAM);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = EscapeUtils.escape(values[i], EscapeUtils.EscapeType.ENCODE_URL_PARAM);
        }
        return cleaned;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> origin = super.getParameterMap();
        Map<String, String[]> cleaned = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> e : origin.entrySet()) {
            String[] arr = e.getValue();
            String[] newArr = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                newArr[i] = EscapeUtils.escape(arr[i], EscapeUtils.EscapeType.ENCODE_URL_PARAM);
            }
            cleaned.put(e.getKey(), newArr);
        }
        return cleaned;
    }

    // ---- Header ----

    @Override
    public String getHeader(String name) {
        return EscapeUtils.escape(super.getHeader(name), EscapeUtils.EscapeType.ENCODE_HEADER);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list = Collections.list(super.getHeaders(name));
        List<String> cleaned = new ArrayList<>();
        for (String v : list) {
            cleaned.add(EscapeUtils.escape(v, EscapeUtils.EscapeType.ENCODE_HEADER));
        }
        return Collections.enumeration(cleaned);
    }

    // ---- JSON Body ----

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override public boolean isFinished() { return bais.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener listener) {}
            @Override public int read() { return bais.read(); }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    private byte[] readAndCleanBody(HttpServletRequest request) throws IOException {
        if (!isJsonRequest(request)) {
            return toByteArray(request.getInputStream());
        }

        byte[] bodyBytes = toByteArray(request.getInputStream());
        if (bodyBytes.length == 0) return bodyBytes;

        String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
        JsonNode root = MAPPER.readTree(bodyStr);
        JsonNode cleaned = cleanJsonNode(root);

        return MAPPER.writeValueAsBytes(cleaned);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String ct = request.getContentType();
        return ct != null && ct.toLowerCase().contains("application/json");
    }

    private byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * 递归清洗 JSON 节点中的字符串值，使用 OWASP HTML Sanitizer（纯文本模式）。
     */
    private JsonNode cleanJsonNode(JsonNode node) {
        if (node.isObject()) {
            Map<String, JsonNode> newMap = new LinkedHashMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    String cleaned = EscapeUtils.sanitizeHtmlToText(value.asText());
                    newMap.put(key, MAPPER.getNodeFactory().textNode(cleaned));
                } else {
                    newMap.put(key, cleanJsonNode(value));
                }
            }
            return MAPPER.valueToTree(newMap);
        }

        if (node.isArray()) {
            List<JsonNode> newList = new ArrayList<>(node.size());
            for (JsonNode child : node) {
                newList.add(cleanJsonNode(child));
            }
            return MAPPER.valueToTree(newList);
        }

        // 数字/布尔/null 不处理
        return node;
    }
}
