package com.poscoict.cateringpass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Component
public class Firebase {
	@Value("${job.base.dir:/}")
	public String baseDir;

	@PostConstruct
	public void init() throws IOException {
		System.out.println(System.getProperty("job.base.dir"));
		String serviceAccountKey = "mobilereactdemo-firebase-adminsdk-0azo7-d894f168bf.json";
		FileInputStream serviceAccount = new FileInputStream(baseDir + File.separator + serviceAccountKey); // 여기

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://mobilereactdemo.firebaseio.com") // 여기
				.build();

		FirebaseApp.initializeApp(options);
	}

	public void doingSomething() throws InterruptedException, ExecutionException {

		String registrationToken = "cVIiCzm0nN4:APA91bFXrG5p4E9vw8NtmLE0dD2JhVtI1EB8A_Z2aGpj8ITkNeKPnrPUEvaTl9owTaAaRczLxyK9XMzP7eFMO4WjMbi4OXC6qsPrnORmP6qkHiI84NSlo7TQCmVDwAL8j9qm2y4saQPG"; // 여기
//		String registrationToken = "cEBH29ABogY:APA91bFXanrknaOnLUeGGQv_Y5WvhNaX7boYPXc7iP44WuozetmP3jIFww609owSh-OHQLUjAHcSpRTkGe6u2cuhtbZgVWmfv3Gew2qT2pYmFXVxvsLkaQTvWvgHzxPfq0KCJVvRkB72";
//		String registrationToken = "cXNzS9psOEc:APA91bFW0n-SdpD6IOvzA8ztqaUbIrDFoH0_zwrgIvK-2TbCAYvoPw3zaYMsB81sWsrbTgkTahkObjjXmBldsmVXHFcLUmkWUO-gXA4RUZnlMedaBrICVWpXgaxnHMDmoZ4vV21MCnbL";
		Message message = Message.builder().putData("first", "첫번째 값").putData("second", "두번째 값은 ")
				.setNotification(new Notification("Job예제",
						"Catering Pass 의 Job으로 사용해볼께요. (전송시각 : " + new SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
								.format(new Date(System.currentTimeMillis())) + ")"))
				.setToken(registrationToken).build();
		String response = FirebaseMessaging.getInstance().sendAsync(message).get();
		System.out.println();
		System.out.println("Successfully send message:" + response);
		System.out.println();
	}
}
