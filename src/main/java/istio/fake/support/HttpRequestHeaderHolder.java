package istio.fake.support;

import java.util.Map;

public abstract class HttpRequestHeaderHolder {

    public abstract Map<String, Object> getHeaderMap();

    public static class DefaultHttpRequestHeaderHolder extends HttpRequestHeaderHolder{

        @Override
        public Map<String, Object> getHeaderMap() {
            return null;
        }
    }

}
