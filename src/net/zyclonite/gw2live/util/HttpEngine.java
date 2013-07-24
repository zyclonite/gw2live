/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.gw2live.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

/**
 *
 * @author zyclonite
 */
public class HttpEngine {

    private static final Log LOG = LogFactory.getLog(HttpEngine.class);
    public static ClientHttpEngine getHttpEngine() {
        final SSLContext sslContext;
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                    }
                }}, new SecureRandom());

            sslSocketFactory = new SSLSocketFactory(sslContext);
        } catch (NoSuchAlgorithmException ex) {
        } catch (KeyManagementException ex) {
            LOG.error(ex);
        }

        final PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(10);

        final HttpClient httpClient = new DefaultHttpClient(cm);
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 5000);
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        final ClientConnectionManager conManager = httpClient.getConnectionManager();
        if (sslSocketFactory != null) {
            SchemeRegistry schemeRegistry = conManager.getSchemeRegistry();
            schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));
        }
        final ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        return engine;
    }
}
