package com.varian.oiscn.patient.integration.service;

import com.varian.oiscn.base.integration.config.HisPatientInfo;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.patient.integration.config.HisServerStatusEnum;
import com.varian.oiscn.patient.integration.exception.HisServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/20/2017
 * @Modified By:
 */
@Slf4j
public class HisPatientHttpClient {

    private String serviceUrl = null;
    private String method;
    private int connectionTimeout;

    public CloseableHttpClient client;
    public HttpClientContext context;
    public HttpRequestBase request;

    /**
     *  Initialize configuration from config file
     * @param config
     */
    private void initConfig(HisPatientInfoConfiguration config) {
        if(config != null) {
            HisPatientInfo info = config.getPatientInfoServer();
            if(info != null) {
                serviceUrl = info.getHisPatientInfoServiceUrl();
                method = info.getMethod();
                connectionTimeout = info.getConnectionTimeout();
            }

        }
    }

    /**
     * Initialize http client with configuration, create http connection with server side
     * @throws HisServiceException
     */
    public HisPatientHttpClient(String params) throws HisServiceException {
        initConfig(HisPatientInfoConfigService.getConfiguration());
        String serverUrl = null;
        try {

            if(isNotEmpty(params)) {
                StringBuffer serviceUrlBuffer = new StringBuffer();
                serviceUrlBuffer.append(serviceUrl).append("&").append(params);
                serverUrl = serviceUrlBuffer.toString();
            }

            client = HttpClients.createDefault();
            context = HttpClientContext.create();

            switch (method) {
                case "GET":
                    request = new HttpGet(serverUrl);
                    break;
                case "POST":
                    //Ignore
                    break;
                default:
                    request = new HttpGet(serverUrl);
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout)
                    .setConnectionRequestTimeout(connectionTimeout)
                    .setSocketTimeout(5000).build();
            request.setConfig(requestConfig);

        } catch(Exception e) {
            log.error(e.getMessage());
            throw new HisServiceException(HisServerStatusEnum.SERVER_NOT_OK);
        }
    }

    /**
     * Send http request
     * @return
     * @throws HisServiceException
     */
    public String sendMessage() throws HisServiceException {
        String responseMsg = null;
        CloseableHttpResponse response = null;
        try {

            response = client.execute(request, context);

            final Header contentType = response.getFirstHeader("Content-Type");
            final String mimeType = contentType == null ? "" : contentType.getValue().split(";")[0].trim();

            switch (mimeType) {
                case MediaType.APPLICATION_JSON:
                case MediaType.TEXT_HTML:
                case MediaType.TEXT_XML:
                case MediaType.TEXT_PLAIN:
                    int statusCode = response.getStatusLine().getStatusCode();
                    if(statusCode >= 300) {
                        throw new HisServiceException(HisServerStatusEnum.SERVICE_NOT_AVAILABLE);
                    }
                    else if(statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        if(entity != null) {
                            ContentType cType = ContentType.getOrDefault(entity);
                            Charset charset = cType.getCharset();
                            if(charset == null) {
                                charset = HTTP.DEF_CONTENT_CHARSET;
                            }

                            final StringBuffer b = new StringBuffer();
                            final char[] tmp = new char[1024];
                            final InputStream inputStream = entity.getContent();
                            final Reader reader = new InputStreamReader(inputStream, charset);

                            try {
                                int i;
                                while((i = reader.read(tmp)) != -1) {
                                    b.append(tmp, 0 , i);
                                }
                            } catch(final ConnectionClosedException ignore) {
                                //Ignore
                            } finally {
                                try {
                                    inputStream.close();
                                    reader.close();
                                } catch(Exception ex) {
                                    log.error(ex.getMessage());
                                }

                            }
                            responseMsg = b.toString();

                        }
                    }
                    break;
                default:
                    throw new HisServiceException(HisServerStatusEnum.SERVICE_ERROR);
            }

        } catch (IOException e) {
            throw new HisServiceException(HisServerStatusEnum.SERVICE_NOT_AVAILABLE);
        } finally {
            if(response != null) {
                try {
                    response.close();
                } catch(Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return responseMsg;
    }

    /**
     * Close the connection
     */
    public void close() {
        if(client != null) {
            try {
                client.close();
            } catch(IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}
