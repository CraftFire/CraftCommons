/*
 * This file is part of CraftCommons.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * CraftCommons is licensed under the GNU Lesser General Public License.
 *
 * CraftCommons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftCommons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.commons.managers;

import com.craftfire.commons.CraftCommons;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class TranslationManager {
    private boolean initialized = false;
    private String apiKey, clientKey, accessToken;
    private TranslationService service = TranslationService.BING;
    private LoggingManager loggingManager = new LoggingManager("CraftFire.TranslationManager", "[TranslationManager]");

    public enum TranslationService {
        BING
    }

    public enum TranslationLanguage {
        ENGLISH("en");

        private String code;

        TranslationLanguage(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    public TranslationManager(String apiKey, String clientKey) {
        this(TranslationService.BING, apiKey, clientKey);
    }

    public TranslationManager(TranslationService service, String apiKey) {
        this(service, apiKey, null);
    }

    public TranslationManager(TranslationService service, String apiKey, String clientKey) {
        this.service = service;
        this.apiKey = apiKey;
        this.clientKey = clientKey;
        if (service.equals(TranslationService.BING)) {
            if (apiKey != null && clientKey != null) {
                this.accessToken = generateAccessToken(TranslationService.BING);
                if (this.accessToken == null) {
                    getLogger().error("Could not get access token for Bing translator.");
                } else {
                    getLogger().debug("Access token is: " + this.accessToken);
                    this.initialized = true;
                }
            } else {
                getLogger().error("APIKey and/or ClientKey has not been set: APIKey=" + apiKey +
                                  " & ClientKEY=" + clientKey);
            }
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public LoggingManager getLogger() {
        return this.loggingManager;
    }

    public void setLoggingManager(LoggingManager loggingManager) {
        this.loggingManager = loggingManager;
    }

    public void setService(TranslationService service) {
        this.service = service;
    }

    public TranslationService getService() {
        return this.service;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientKey() {
        return this.clientKey;
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAPIKey() {
        return this.apiKey;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    private String generateAccessToken(TranslationService service) {
        URL postURL;
        try {
            postURL = new URL("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13");
        } catch (MalformedURLException e) {
            getLogger().stackTrace(e);
            return null;
        }
        if (CraftCommons.isURLOnline(postURL)) {
            try {
                String data = "grant_type=client_credentials&client_id=" + getClientKey()
                            + "&client_secret=" + getAPIKey()
                            + "&scope=http://api.microsofttranslator.com";
                HttpURLConnection connection = (HttpURLConnection) postURL.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));
                connection.setUseCaches(false);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(data);
                Scanner s;
                if (connection.getResponseCode() != 200) {
                    s = new Scanner(connection.getErrorStream());
                    getLogger().error("Got error when trying to get access token for translation: " + s);
                } else {
                    s = new Scanner(connection.getInputStream());
                    getLogger().debug("Got response when trying to catch access token for translation: " + s) ;
                }
                s.useDelimiter("\\Z");
                wr.flush();
                wr.close();
                connection.disconnect();
                return s.next();
            } catch (ProtocolException e) {
                getLogger().stackTrace(e);
            } catch (IOException e) {
                getLogger().stackTrace(e);
            }
        }
        getLogger().error(postURL.toString() + " did not return HTTP Status 200, status returned was: " +
                          CraftCommons.getResponseCode(postURL) + ".");
        return null;
    }
}
