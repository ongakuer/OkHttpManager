package me.relex.okhttpmanager;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public abstract class StreamRequestBody extends RequestBody {

    public static StreamRequestBody create(final MediaType mediaType,
            final InputStream inputStream) {

        return new StreamRequestBody() {

            @Override public MediaType contentType() {
                return mediaType;
            }

            @Override public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
}
