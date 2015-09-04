package me.relex.okhttpmanager;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;
import java.io.File;
import java.io.IOException;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ProgressRequestBody extends RequestBody {
    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE
    private final File mFile;
    private final ProgressListener mListener;
    private final MediaType mMediaType;

    public ProgressRequestBody(MediaType mediaType, File file, ProgressListener listener) {
        mFile = file;
        mMediaType = mediaType;
        mListener = listener;
    }

    @Override public MediaType contentType() {
        return mMediaType;
    }

    @Override public long contentLength() {
        return mFile.length();
    }

    @Override public void writeTo(BufferedSink sink) throws IOException {

        Source source = null;
        try {
            source = Okio.source(mFile);
            long total = 0;
            long read;
            long length = contentLength();

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                float percent = (float) total / length * 100;
                mListener.onProgressUpdate(percent);
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

    public interface ProgressListener {
        void onProgressUpdate(float percent);
    }
}
