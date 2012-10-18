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

import com.craftfire.commons.Util;
import com.craftfire.commons.classes.AnalyticsData;
import com.craftfire.commons.classes.AnalyticsItem;
import com.craftfire.commons.exceptions.AnalyticsException;

import java.io.*;
import java.net.*;

public class AnalyticsManager {
    private URL url;
    private AnalyticsData data;
    private LoggingManager loggingManager = new LoggingManager("CraftFire.AnalyticsManager", "[AnalyticsManager]");

    public AnalyticsManager(URL url, String name, String version) {
        this.url = url;
        this.data = new AnalyticsData(name, version);
    }

    public AnalyticsManager(String url, String name, String version) throws MalformedURLException {
        this.url = new URL(url);
        this.data = new AnalyticsData(name, version);
    }

    public void submit() throws AnalyticsException, IOException {
        if (Util.isURLOnline(getURL())) {
            String data = getParameters();
            HttpURLConnection connection = (HttpURLConnection) getURL().openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));
            connection.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            connection.disconnect();
        }
        String error = getURL().toString() + " did not return HTTP Status 200, status returned was: " +
                       Util.getResponseCode(getURL()) + ".";
        getLogging().error(error);
        throw new AnalyticsException(this, error);
    }

    public String getParameters() {
        String data = getData().getName().getKeyUTF8() + "=" + getData().getName().getValueUTF8() + "&"
                    + getData().getVersion().getKeyUTF8() + "=" + getData().getVersion().getValueUTF8() + "&"
                    + getData().getOSName().getKeyUTF8() + "=" + getData().getOSName().getValueUTF8() + "&"
                    + getData().getOSVersion().getKeyUTF8() + "=" + getData().getOSVersion().getValueUTF8() + "&"
                    + getData().getOSArch().getKeyUTF8() + "=" + getData().getOSArch().getValueUTF8() + "&"
                    + getData().getMaxMemory().getKeyUTF8() + "=" + getData().getMaxMemory().getValueUTF8() + "&"
                    + getData().getTotalMemory().getKeyUTF8() + "=" + getData().getTotalMemory().getValueUTF8() + "&"
                    + getData().getJavaVersion().getKeyUTF8() + "=" + getData().getJavaVersion().getValueUTF8() + "&";
        for (AnalyticsItem item : getData().getCustomData().values()) {
            data += item.getKeyUTF8() + "=" + item.getValueUTF8() + "&";
        }
        return data.substring(0, data.length() - 1);
    }

    public LoggingManager getLogging() {
        return this.loggingManager;
    }

    public void setLoggingManager(LoggingManager loggingManager) {
        this.loggingManager = loggingManager;
    }

    public AnalyticsData getData() {
        return this.data;
    }

    public void addData(String key, String value) {
        getData().add(key, value);
    }

    public URL getURL() {
        return this.url;
    }

    public void setURL(URL url) {
        this.url = url;
    }
}
