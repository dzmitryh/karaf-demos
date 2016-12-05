/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.karaf.demos.deployer.bundle;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

public class Activator implements BundleActivator {

    public void start(BundleContext context) {
        System.out.println("Starting the Apache Karaf demo bundle");


        // The required parameters
        Issuer iss = new Issuer("https://idp.c2id.com");
        ClientID clientID = new ClientID("123");
        JWSAlgorithm jwsAlg = JWSAlgorithm.RS256;
        URL jwkSetURL = null;
        try {
            jwkSetURL = new URL("https://idp.c2id.com/jwks.json");
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }

        // Create validator for signed ID tokens
        IDTokenValidator validator = new IDTokenValidator(iss, clientID, jwsAlg, jwkSetURL);


        // Parse the ID token
        JWT idToken = null;
        try {
            idToken = JWTParser.parse("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6Ikt6UXE1SXQ0cUFsZ0JNeDNHUzRtNE1ySFpuT1RLWFhoIn0.eyJpc3MiOiJodHRwczovL3d3dy5lZS5jby51ayIsInN1YiI6IjQ0Nzk5NjA2NDI1MCIsImF1ZCI6Ikt6UXE1SXQ0cUFsZ0JNeDNHUzRtNE1ySFpuT1RLWFhoIiwibm9uY2UiOiIwUzZfV3pBMk0iLCJleHAiOjE0NzY4NzY5NjAsImlhdCI6MTQ3Njg3NjY2MCwiYXV0aF90aW1lIjoxNDc2ODc2NjYwLCJhY3IiOiJMMSIsImJyYW5kIjoiRUUiLCJhbXIiOiJPVFAifQ.aZ6FhTHrIEjEpJUZUlqEd-TXZ80nkz__9QbYK3o-aSM");
        } catch(ParseException e) {
            e.printStackTrace();
        }

        // Set the expected nonce, leave null if none
        Nonce expectedNonce = new Nonce("xyz..."); // or null

        IDTokenClaimsSet claims = null;

        try {
            if(idToken != null) {
                claims = validator.validate(idToken, expectedNonce);
            }
        } catch (BadJOSEException e) {
            // Invalid signature or claims (iss, aud, exp...)
        } catch (JOSEException e) {
            // Internal processing exception
        }

        System.out.println("Logged in user " + (claims !=null ? claims.getSubject() : null));
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the Apache Karaf demo bundle");
    }

}