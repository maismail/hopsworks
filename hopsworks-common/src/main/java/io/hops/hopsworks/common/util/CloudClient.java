/*
 * This file is part of Hopsworks
 * Copyright (C) 2020, Logical Clocks AB. All rights reserved
 *
 * Hopsworks is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Hopsworks is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package io.hops.hopsworks.common.util;

import io.hops.hopsworks.common.proxies.client.HttpClient;
import io.hops.hopsworks.exceptions.UserException;
import io.hops.hopsworks.restutils.RESTCodes;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
@DependsOn("Settings")
public class CloudClient {
  
  private final static Logger LOGGER = Logger.getLogger(CloudClient.class.getName());
  
  @EJB
  private Settings settings;
  
  @EJB
  private HttpClient httpClient;
  
  public void notifyToSendEmail(String userEmail, String subject, String message)
      throws UserException {
    JSONObject json = new JSONObject();
    json.put("email", userEmail);
    json.put("subject", subject);
    json.put("message", message);
  
    JSONObject data = new JSONObject();
    data.put("data", json);
    
    String recoverPasswordUrl = settings.getCloudEventsEndPoint() + "/recoverpassword";
    
    HttpPost request = new HttpPost(recoverPasswordUrl);
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader("x-api-key", settings.getCloudEventsEndPointAPIKey());
    try {
      request.setEntity(new StringEntity(data.toString()));
    } catch (UnsupportedEncodingException ex) {
      LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
      throw new UserException(RESTCodes.UserErrorCode.PASSWORD_RESET_UNSUCCESSFUL, Level.SEVERE, null,
          ex.getMessage(), ex);
    }
  
    try {
      int statusCode = httpClient.execute(request, httpResponse -> {
        String responseStr = EntityUtils.toString(httpResponse.getEntity());
        LOGGER.log(Level.INFO, responseStr);
        return httpResponse.getStatusLine().getStatusCode();
      });
      
      if(statusCode != 200){
        throw new UserException(RESTCodes.UserErrorCode.PASSWORD_RESET_UNSUCCESSFUL,
            Level.SEVERE);
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
      throw new UserException(RESTCodes.UserErrorCode.PASSWORD_RESET_UNSUCCESSFUL, Level.SEVERE, null,
          ex.getMessage(), ex);
    }
  }
  
}
