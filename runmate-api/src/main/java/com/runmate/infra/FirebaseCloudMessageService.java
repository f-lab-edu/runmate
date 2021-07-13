package com.runmate.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.runmate.dto.infra.FcmMessage;
import com.runmate.infra.exception.ExternalServiceRequestException;
import com.runmate.infra.exception.FCMTokenGeneratingException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FirebaseCloudMessageService {

    private static final String FIREBASE_CONFIG_PATH = "firebase/firebase_service_key.json";
    private static final String FIREBASE_CLOUD_MESSAGING_SCOPE = "https://www.googleapis.com/auth/cloud-platform";
    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/runmate-ea952/messages:send";

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageService.class);

    @Async
    public void sendMessageTo(String targetToken, String title, String body) {
        try {
            String message = makeMessage(targetToken, title, body);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request).execute();
            logger.debug(Objects.requireNonNull(response.body()).string());
        } catch (JsonProcessingException e) {
            throw new FCMTokenGeneratingException();
        } catch (IOException e) {
            throw new ExternalServiceRequestException();
        }
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
                .createScoped(List.of(FIREBASE_CLOUD_MESSAGING_SCOPE));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
