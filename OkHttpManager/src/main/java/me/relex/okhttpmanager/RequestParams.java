package me.relex.okhttpmanager;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public final static String DEFAULT_FILE_NAME = "file";

    private final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, StreamWrapper> streamParams = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public ConcurrentHashMap<String, FileWrapper> getFileParams() {
        return fileParams;
    }

    public ConcurrentHashMap<String, StreamWrapper> getStreamParams() {
        return streamParams;
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, int value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, File file) {
        put(key, file, null, null);
    }

    public void put(String key, File file, String contentType, String customFileName) {
        if (file == null || !file.exists()) {
            return;
        }
        if (key != null) {
            fileParams.put(key, FileWrapper.newInstance(file, contentType, customFileName));
        }
    }

    public void put(String key, InputStream stream) {
        put(key, stream, null, null, true);
    }

    public void put(String key, InputStream stream, String name, String contentType,
            boolean autoClose) {
        if (key != null && stream != null) {
            streamParams.put(key, StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }
    }

    public static class FileWrapper {
        public final File file;
        public final String contentType;
        public final String customFileName;

        public FileWrapper(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
        }

        static FileWrapper newInstance(File file, String contentType, String customFileName) {
            return new FileWrapper(file,
                    contentType == null ? APPLICATION_OCTET_STREAM : contentType,
                    customFileName == null ? DEFAULT_FILE_NAME : customFileName);
        }
    }

    public static class StreamWrapper {
        public final InputStream inputStream;
        public final String name;
        public final String contentType;
        public final boolean autoClose;

        public StreamWrapper(InputStream inputStream, String name, String contentType,
                boolean autoClose) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
            this.autoClose = autoClose;
        }

        static StreamWrapper newInstance(InputStream inputStream, String name, String contentType,
                boolean autoClose) {
            return new StreamWrapper(inputStream, name == null ? DEFAULT_FILE_NAME : name,
                    contentType == null ? APPLICATION_OCTET_STREAM : contentType, autoClose);
        }
    }

    public String getParams() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    @Override public String toString() {
        return getParams();
    }
}
