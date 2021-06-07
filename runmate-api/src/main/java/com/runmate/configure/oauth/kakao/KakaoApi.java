package com.runmate.configure.oauth.kakao;

import com.runmate.controller.exception.InvalidCodeException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class KakaoApi{
    @Value("${kakao.requestCodeUrl}")
    private String requestCodeUrl;
    @Value("${kakao.requestTokenUrl}")
    private String requestTokenUri;
    @Value("${kakao.restApiKey}")
    private String restApiKey;
    @Value("${kakao.redirectUri}")
    private String redirectUri;
    @Value("${kakao.responseType}")
    private String responseType;
    @Value("${kakao.grantType}")
    private String grantType;
    @Value("${kakao.requestUserInfoUrl}")
    private String requestUserInfoUrl;
    @Value("${kakao.requestLogoutUrl}")
    private String requestLogoutUrl;

    public String getRedirectionUri() {
        return UriComponentsBuilder
                .fromUriString(requestCodeUrl)
                .queryParam("client_id",restApiKey)
                .queryParam("redirect_uri",redirectUri)
                .queryParam("response_type",responseType)
                .build().toString();
    }
    public String getAccessToken(String code) throws InvalidCodeException {
        try {
            URI uri = new URI(requestTokenUri);

            uri = new URIBuilder(uri)
                    .addParameter("client_id", restApiKey)
                    .addParameter("code",code)
                    .addParameter("grant_type",grantType)
                    .addParameter("redirect_uri",redirectUri)
                    .build();

            HttpClient httpClient= HttpClientBuilder.create().build();
            HttpResponse response=httpClient.execute(new HttpPost(uri));

            String responseJson= read(response.getEntity().getContent());
            JSONObject jsonObject=new JSONObject(responseJson);
            String token=jsonObject.getString("access_token");

            return token;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(JSONException e){
            throw new InvalidCodeException("invalidate code",e);
        }
        return null;
    }
    public InputStream getUserInfo(String code) {
        String token=getAccessToken(code);
        HttpGet httpGet=new HttpGet(requestUserInfoUrl);
        httpGet.addHeader("Authorization","Bearer "+token);

        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpGet);
        }catch (IOException exception){
            exception.printStackTrace();
        }
        return null;
    }
    private String read(InputStream is) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        StringBuilder sb=new StringBuilder();
        String tmp=null;
        while((tmp=br.readLine())!=null){
            sb.append(tmp);
        }
        return sb.toString();
    }

    public String getEmail(String code) {
        String token=getAccessToken(code);
        HttpGet httpGet=new HttpGet(requestUserInfoUrl);
        httpGet.addHeader("Authorization","Bearer "+token);

        try{
            HttpClient httpClient=HttpClientBuilder.create().build();
            HttpResponse response=httpClient.execute(httpGet);

            String responseJson= read(response.getEntity().getContent());
            JSONObject jsonObject=new JSONObject(responseJson);
            String email=((JSONObject)jsonObject.get("kakao_account")).getString("email");

            return email;
        }catch(IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
    public boolean Logout(String token){
        try {
            HttpClient client= HttpClients.custom().build();
            HttpUriRequest request= RequestBuilder.post()
                    .setUri(requestLogoutUrl)
                    .setHeader(HttpHeaders.AUTHORIZATION,token)
                    .build();
            client.execute(request);
        }catch (IOException exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}
