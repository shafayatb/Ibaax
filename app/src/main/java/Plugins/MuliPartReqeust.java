package Plugins;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.Preference;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.CharsetUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iBaax on 2/23/16.
 */
@SuppressWarnings("deprecation")

public class MuliPartReqeust extends Request<String> {

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final Map<String, String> mStringPart;
    private final MultipartProgressListener multipartProgressListener;
    Context context;
    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;
    AppPrefs prefs;
    private String FILE_PART_NAME = "Photo";
    private Map<String, String> headerParams;
    private long fileLength = 0L;
    private String Boundary = "*****";

    public MuliPartReqeust(Context context, String url, Response.ErrorListener errorListener,
                           Response.Listener<String> listener, File file, long fileLength,
                           Map<String, String> mStringPart,
                           Map<String, String> headerParams, String partName,
                           MultipartProgressListener progLitener) {
        super(Method.POST, url, errorListener);
        this.context = context;
        this.mListener = listener;
        this.mFilePart = file;
        this.fileLength = fileLength;
        this.mStringPart = mStringPart;
        this.headerParams = headerParams;
        this.FILE_PART_NAME = partName;
        this.multipartProgressListener = progLitener;
        prefs = new AppPrefs(context);
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
       // entity.setBoundary(Boundary);
        try {
            entity.setCharset(CharsetUtils.get("UTF-8"));
            buildMultipartEntity();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpentity = entity.build();
    }


    private void buildMultipartEntity() throws UnsupportedEncodingException {
         //File sourceFile = new File(mFilePart.getPath());
         //entity.addPart("Photo", new FileBody(sourceFile));
        entity.addPart("Photo", new FileBody(mFilePart, ContentType.create("image/gif"), mFilePart.getName()));
        //entity.addPart("ID",new StringBody("6426"));
        //
        if (mStringPart != null) {
            for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
               // entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
                entity.addTextBody(entry.getKey(), entry.getValue());
            }
        }
    }

    /*@Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Photo",mFilePart.getPath());
        params.put("ID","6426");
        return params;
    }*/

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
       /* Map headers = new HashMap();
        if (!Preference.getInstance().getCookie().equals(""))
            headers.put("Cookie", Preference.getInstance().getCookie());*/
        //final Map<String, String> headers = new ArrayMap<String, String>();
        //headers.put("Authorization", "Bearer " + prefs.gettOAuthToken());
        return headerParams;
    }

    @Override
    public String getBodyContentType() {
        Log.v("esty",httpentity.getContentType().getValue());
        return httpentity.getContentType().getValue();
        //return "application/x-www-form-urlencoded; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity.writeTo(new CountingOutputStream(bos, fileLength,
                    multipartProgressListener));
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {
//          System.out.println("Network Response "+ new String(response.data, "UTF-8"));
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return Response.success(new String(response.data), getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

//Override getHeaders() if you want to put anything in header


    public static interface MultipartProgressListener {
        void transferred(long transfered, int progress);
    }

    public static class CountingOutputStream extends FilterOutputStream {
        private final MultipartProgressListener progListener;
        private long transferred;
        private long fileLength;

        public CountingOutputStream(final OutputStream out, long fileLength,
                                    final MultipartProgressListener listener) {
            super(out);
            this.fileLength = fileLength;
            this.progListener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            if (progListener != null) {
                this.transferred += len;
                int prog = (int) (transferred * 100 / fileLength);
                this.progListener.transferred(this.transferred, prog);
            }
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progListener != null) {
                this.transferred++;
                int prog = (int) (transferred * 100 / fileLength);
                this.progListener.transferred(this.transferred, prog);
            }
        }

    }
}
