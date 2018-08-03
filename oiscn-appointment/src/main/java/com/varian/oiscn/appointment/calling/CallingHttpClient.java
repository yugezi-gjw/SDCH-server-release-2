/**
 *
 */
package com.varian.oiscn.appointment.calling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varian.oiscn.core.appointment.calling.ServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Http Connection Client for Calling System
 */
@Slf4j
public class CallingHttpClient {

    protected static String serviceUrl = null;
    protected static String contentType = null;
    protected static String charset = null;
    protected static int connectionTimeout;
    protected static int retryInterval;
    protected static int retryTimes;
    protected static String method;

    protected CloseableHttpClient client;
    protected HttpClientContext context;
    protected HttpRequestBase request;

    /**
     * Initial Http Client with configuration, and establish connection.<br>
     *
     * @throws CallingServiceException
     */
    public CallingHttpClient() throws CallingServiceException {
        try {
            client = HttpClients.createDefault();
            context = HttpClientContext.create();

            switch (method) {
                case "GET":
                    request = new HttpGet(serviceUrl);
                    break;
                case "POST":
                    request = new HttpPost(serviceUrl);
                    break;
                case "PUT":
                    request = new HttpPut(serviceUrl);
                    break;
                case "DELETE":
                    request = new HttpDelete(serviceUrl);
                    break;
                default:
                    request = new HttpGet(serviceUrl);
            }
            request.addHeader("Content-Type", contentType + ";charset=" + charset);
            request.setHeader("Connection", "keep-alive");
            request.setHeader("Referer", serviceUrl);

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout)
                    .setConnectionRequestTimeout(connectionTimeout)
                    .setSocketTimeout(5000).build();
            request.setConfig(config);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new CallingServiceException(ServerStatusEnum.SERVER_NOT_READY);
        }
    }

    public static void initConfig(ServerConfiguration config) throws CallingServiceException {
        if (config != null) {
            serviceUrl = config.getCallingServiceUrl();
            method = config.getMethod().toUpperCase();
            contentType = config.getContentType();
            charset = config.getCharset();
            connectionTimeout = config.getConnectionTimeout();
            retryInterval = config.getRetryInterval();
            retryTimes = config.getRetryTimes();

            try {
                new HttpPost(serviceUrl);
            } catch (Exception e) {
                CallingServiceException ce = new CallingServiceException(ServerStatusEnum.BAD_CONFIGURATION);
                ce.addBadItem("callingServiceUrl");
                throw ce;
            }
        }
    }

    /**
     * Send message, and return response code.<br>
     *
     * @param msg Message
     * @return Server Status Information
     * @throws CallingServiceException
     */
    protected void sendMsg(Object msg) throws CallingServiceException {
        CloseableHttpResponse response = null;
        try {
            if (request instanceof HttpPost) {
                HttpPost postRequest = (HttpPost) request;
                if (msg instanceof String) {
                    postRequest.setEntity(new StringEntity((String) msg, charset));
                } else {
                    String reqStr = new ObjectMapper().writeValueAsString(msg);
                    postRequest.setEntity(new StringEntity(reqStr, charset));
                }
            } else if (request instanceof HttpGet) {
                HttpGet getRequest = (HttpGet) request;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                String str = EntityUtils.toString(new UrlEncodedFormEntity(params));
                getRequest.setURI(new URI(getRequest.getURI() + "?" + str));
            }

            response = client.execute(request, context);

            final Header contentType = response.getFirstHeader("Content-Type");
            final String mimeType = contentType == null ? "" : contentType.getValue().split(";")[0].trim();

            switch (mimeType) {
                case MediaType.APPLICATION_JSON:
                case MediaType.TEXT_HTML:
                case MediaType.TEXT_XML:
                case MediaType.APPLICATION_FORM_URLENCODED:
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode >= 300) {
                        throw new CallingServiceException(ServerStatusEnum.SERVICE_NOT_AVAILABLE);
                    }
                    break;
                default:
                    throw new CallingServiceException(ServerStatusEnum.SERVICE_ERROR);
            }
        } catch (IOException e) {
            throw new CallingServiceException(ServerStatusEnum.SERVICE_NOT_AVAILABLE);
        } catch (URISyntaxException e) {
            throw new CallingServiceException(ServerStatusEnum.BAD_REQUEST);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    /**
     * Close the connection.<br>
     */
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }
}
