/*
 * This program is part of the OpenLMIS logistics management information
 * system platform software.
 *
 * Copyright © 2015 ThoughtWorks, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should
 * have received a copy of the GNU Affero General Public License along with
 * this program. If not, see http://www.gnu.org/licenses. For additional
 * information contact info@OpenLMIS.org
 */

package org.openlmis.core.network;

import android.os.AsyncTask;

import com.google.inject.Inject;

import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetCheck extends AsyncTask<InternetCheck.Callback, Void, InternetListener> {

    private final String GOOGLE_DNS_IP_ADDRESS = "8.8.8.8";
    private final int PORT = 53;
    private final int TIMEOUT = 1500;

    @Inject
    public InternetCheck() {
    }

    public interface Callback {
        void launchResponse(Boolean internet);
    }

    @Override
    public InternetListener doInBackground(Callback... callbacks) {
        Callback callback = null;
        try {
            if (callbacks.length > 0) {
                callback = callbacks[0];
            } else {
                throw new Exception("No callback supplied");
            }
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(GOOGLE_DNS_IP_ADDRESS, PORT), TIMEOUT);
            sock.close();
            return new InternetListener(true, callback, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new InternetListener(false, callback, e);
        }
    }

    @Override
    protected void onPostExecute(InternetListener internetListener) {
        if (internetListener.getCallback() != null) {
            internetListener.launchCallback();
        } else {
            internetListener.getException().printStackTrace();
        }
    }
}