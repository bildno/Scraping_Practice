package com.example.scraping.Scraping_Practice.service;

import com.example.scraping.Scraping_Practice.dto.NewsDto;
import com.example.scraping.Scraping_Practice.dto.PushRequest;
// import com.google.firebase.internal.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jsoup.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.http.ResponseEntity;


// 이 서비스 클래스는 Google 뉴스에서 '스테이블코인' 관련 뉴스를 수집하여 반환한다.
@Service
@RequiredArgsConstructor
public class NewsService {

    private final FirebaseService firebaseService;


    public List<NewsDto> fetchStablecoinNews(String keyword) throws IOException {
        // 뉴스 결과를 저장할 리스트 생성
        List<NewsDto> result = new ArrayList<>();
        // 검색어 설정
        // Google 뉴스 검색 URL 구성 (한글 뉴스 기준)
        String url = "https://news.google.com/search?q=" + keyword+"&hl=ko&gl=KR&ceid=KR%3Ako";


        try {
            // HTML 문서 요청 및 파싱
            Document doc = Jsoup.connect(url).get();
            // 뉴스 기사 요소 선택
            Elements newsHeadlines = doc.select("article");

            // 각 뉴스 기사에서 제목과 링크 추출
            for (Element item : newsHeadlines) {
                String title = item.select("a.JtKRv").text();
                String link = "https://news.google.com" + item.select("a.JtKRv").attr("href");

                // 제목과 링크가 유효하면 결과에 추가
                if (!title.isEmpty() && !link.equals("https://news.google.com")) {
                    result.add(new NewsDto(title, link));
                }
            }
            // 수집된 뉴스 개수 출력
            System.out.println("[뉴스 결과] " + result.size() + "건 수집됨");
            result.forEach(news -> System.out.println("제목: " + news.title() + " | URL: " + news.url()));
        } catch (IOException e) {
            // 예외 발생 시 출력
            e.printStackTrace();
        }

        return result;
    }

    public ResponseEntity<String> sendPushNotification(PushRequest request) {
        System.out.println("📨 sendPushNotification() 진입");
        System.out.println("🔑 토큰: " + request.getToken());
        System.out.println("📬 제목: " + request.getTitle());
        System.out.println("📬 내용: " + request.getBody());
        try {

            String response = firebaseService.sendMessage(request.getToken(), request.getTitle(), request.getBody());
            System.out.println("✅ Firebase 전송 응답: " + response);
            return ResponseEntity.ok("푸시 전송 성공: " + response);
        } catch (Exception e) {
            System.out.println("❌ 푸시 전송 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("푸시 전송 실패: " + e.getMessage());
        }
    }


    public ResponseEntity<String> sendPushDelayed(PushRequest request) {
        System.out.println("⏱️ 푸시 예약 요청 수신됨 - 5초 후 전송 예정");

        try {
            Thread.sleep(5000); // 5초 대기 (주의: 동기 블록)
            String response = firebaseService.sendMessage(request.getToken(), request.getTitle(), request.getBody());
            System.out.println("✅ 예약 푸시 전송 성공: " + response);
            return ResponseEntity.ok("푸시 전송 성공: " + response);
        } catch (Exception e) {
            System.out.println("❌ 예약 푸시 전송 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("푸시 전송 실패: " + e.getMessage());
        }
    }
}
