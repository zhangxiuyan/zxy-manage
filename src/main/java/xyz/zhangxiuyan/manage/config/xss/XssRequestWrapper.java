package xyz.zhangxiuyan.manage.config.xss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = readAndCleanBody(request);
    }

    // -----------------------------
    // 1. Query Param 清理
    // -----------------------------
    @Override
    public String getParameter(String name) {
        return EscapeUtils.escape(super.getParameter(name), EscapeUtils.EscapeType.QUERY_PARAM);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;

        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = EscapeUtils.escape(values[i], EscapeUtils.EscapeType.QUERY_PARAM);
        }
        return cleaned;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> origin = super.getParameterMap();
        Map<String, String[]> cleaned = new HashMap<>();

        for (Map.Entry<String, String[]> e : origin.entrySet()) {
            String[] arr = e.getValue();
            String[] newArr = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                newArr[i] = EscapeUtils.escape(arr[i], EscapeUtils.EscapeType.QUERY_PARAM);
            }
            cleaned.put(e.getKey(), newArr);
        }
        return cleaned;
    }

    // -----------------------------
    // 2. Header 清理
    // -----------------------------
    @Override
    public String getHeader(String name) {
        return EscapeUtils.escape(super.getHeader(name), EscapeUtils.EscapeType.HEADER);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list = Collections.list(super.getHeaders(name));
        List<String> cleaned = new ArrayList<>();
        for (String v : list) {
            cleaned.add(EscapeUtils.escape(v, EscapeUtils.EscapeType.HEADER));
        }
        return Collections.enumeration(cleaned);
    }

    // -----------------------------
    // 3. JSON Body 清理（递归清理）
    // -----------------------------
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);

        return new ServletInputStream() {
            @Override public boolean isFinished() { return bais.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener readListener) {}
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

    // -----------------------------
    // JSON 递归清理
    // -----------------------------
    private JsonNode cleanJsonNode(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> fields = node.fieldNames();
            Map<String, JsonNode> newMap = new LinkedHashMap<>();

            while (fields.hasNext()) {
                String key = fields.next();
                JsonNode value = node.get(key);

                // 清理 JSON value
                if (value.isTextual()) {
                    String cleaned = EscapeUtils.escape(value.asText(), EscapeUtils.EscapeType.JSON_VALUE);
                    newMap.put(key, MAPPER.getNodeFactory().textNode(cleaned));
                } else {
                    newMap.put(key, cleanJsonNode(value));
                }
            }
            return MAPPER.valueToTree(newMap);
        }

        if (node.isArray()) {
            List<JsonNode> newList = new ArrayList<>();
            for (JsonNode child : node) {
                newList.add(cleanJsonNode(child));
            }
            return MAPPER.valueToTree(newList);
        }

        return node;
    }
}
