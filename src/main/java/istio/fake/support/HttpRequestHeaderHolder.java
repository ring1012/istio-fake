package istio.fake.support;

import jakarta.servlet.ServletRequestListener;

import java.util.Map;


public abstract class HttpRequestHeaderHolder implements ServletRequestListener {

    public abstract Map<String, Object> getHeaderMap();

}
