package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.ArticleNewsFromAdminVnua;
import com.example.unischeduleservice.dto.ArticleNewsVnua;
import com.example.unischeduleservice.dto.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotiServiceImpl implements NotiService {

    @Value("${vnua.url.urlNewsVnua}")
    private String urlNewsVnua;
    @Value("${vnua.url.urlNewsFromAdminVnua}")
    private String urlNewsFromAdminVnua;
    @Value("${vnua.url.urlLogin}")
    private String urlLogin;
    @Value("${vnua.usernameVnua}")
    private String usernameVnua;
    @Value("${vnua.passwordVnua}")
    private String passwordVnua;

    private static final Logger log = LoggerFactory.getLogger(NotiServiceImpl.class);
    private RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MailService mailService;

    @Override
    @Scheduled(fixedRate = 3600000, initialDelay = 6000)
    public void sendMailNotiNewsVnua() {
        log.info("Sending Vnua News Vnua");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = """
                {"filter":{"id":null,"is_noi_dung":false,"is_hinh_dai_dien":true,"ky_hieu":"hd","is_tom_tat":false,"is_quyen_xem":true},"additional":{"paging":{"limit":10,"page":1},"ordering":[{"name":"do_uu_tien","order_type":1},{"name":"ngay_dang_tin","order_type":1}]}}
                """;
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        String response = restTemplate.postForObject(urlNewsVnua, entity, String.class);
        if (StringUtils.isEmpty(response)) {
            return;
        }
        JsonNode jsonNode;
        try {
             jsonNode = objectMapper.readTree(response);
        } catch (JsonProcessingException jpe) {
            log.error(jpe.getMessage(), jpe);
            mailService.sendMail("hoangvuvan677@gmail.com", "Thông báo VNUA", "Lỗi lấy thông tin thông báo vnua: " + jpe.getMessage());
            return;
        }
        JsonNode items = jsonNode.path("data").path("ds_bai_viet");
        if (items.isEmpty()) {
            return;
        }
        items.valueStream().map(item -> ArticleNewsVnua.builder()
                    .id(item.get("id").asText())
                    .ky_hieu(item.get("ky_hieu").asText())
                    .so_luong(item.get("so_luong").asLong())
                    .is_hien_thi(item.get("is_hien_thi").asBoolean())
                    .tieu_de(item.get("tieu_de").asText())
                    .tom_tat(item.get("tom_tat").asText())
                    .ngay_dang_tin(LocalDateTime.parse(item.get("ngay_dang_tin").asText()))
                    .ngay_hieu_chinh(item.get("ngay_hieu_chinh").asText())
                    .do_uu_tien(item.get("do_uu_tien").asLong())
                    .is_news(item.get("is_news").asBoolean())
                    .kieu_hien_thi_ngang(item.get("kieu_hien_thi_ngang").asBoolean())
                    .build())
                .filter(item -> item.getIs_news() && item.getNgay_dang_tin().isAfter(LocalDateTime.now().minusHours(2)))
                .forEach(articleNewsVnua -> {
                    mailService.sendMail("hoangvuvan677@gmail.com", "Thông báo đào tạo đại học VNUA", articleNewsVnua.getTieu_de());
                });
    }

    @Override
//    @Scheduled(fixedRate = 3600000, initialDelay = 6000)
    public void sendMailNotiNewsFromAdminVnua() {
        log.info("Sending Vnua News from Amin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getTokenVnua(null));
        String body = """
                {"filter":{"id":null,"is_noi_dung":true,"is_web":true},"additional":{"paging":{"limit":10,"page":1},"ordering":[{"name":"ngay_gui","order_type":1}]}}
                """;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        JsonNode jsonNode;
        try {
            String response = restTemplate.postForObject(urlNewsFromAdminVnua, entity, String.class);
            if (StringUtils.isEmpty(response)) {
                throw new RuntimeException();
            }
            jsonNode = objectMapper.readTree(response);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            mailService.sendMail("hoangvuvan677@gmail.com", "Lỗi lấy thông tin thông báo VNUA", "Lỗi lấy thông tin thông báo từ quản trị viên: "  + ex.getMessage());
            return;
        }
        JsonNode items = jsonNode.path("data").path("ds_thong_bao");
        if (items.isEmpty()) {
            return;
        }
        items.valueStream().map(item ->
                ArticleNewsFromAdminVnua.builder()
                        .id(item.get("id").asText())
                        .doi_tuong_search(item.get("doi_tuong_search").asText())
                        .doi_tuong(item.get("doi_tuong").asText())
                        .phan_cap_search(item.get("phan_cap_search").asText())
                        .phan_cap_sinh_vien(item.get("phan_cap_sinh_vien").asText())
                        .tieu_de(item.get("tieu_de").asText())
                        .noi_dung(item.get("noi_dung").asText())
                        .is_phai_xem(item.get("is_phai_xem").asBoolean())
                        .ngay_gui(LocalDateTime.parse(item.get("ngay_gui").asText()))
                        .is_da_doc(item.get("is_da_doc").asBoolean())
                        .phan_hoi(item.get("phan_hoi").isNull() ? "" : item.get("phan_hoi").asText())
                        .is_xem_phan_hoi(item.get("is_xem_phan_hoi").asBoolean())
                        .ngay_xem(item.get("ngay_xem").asText())
                        .build())
                .filter(item -> item.getNgay_gui().isAfter(LocalDateTime.now().minusHours(2)))
                .forEach(item -> {
                    mailService.sendMail("hoangvuvan677@gmail.com", item.getTieu_de(), item.getNoi_dung());
                });
    }

    @Override
    public String getTokenVnua(LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = loginRequest == null ? String.format("""
                    {
                        "username": "%s",
                        "password": "%s",
                    }
                """, usernameVnua, passwordVnua) :
                String.format("""
                    {
                        "username": "%s",
                        "password": "%s",
                    }
                """, loginRequest.getUsername(), loginRequest.getPassword());
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        JsonNode jsonNode;
        try {
            String response = restTemplate.postForObject(urlLogin, entity, String.class);
            jsonNode = objectMapper.readTree(response);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            mailService.sendMail("hoangvuvan677@gmail.com", "Lỗi đăng nhập vnua lấy token", "Lỗi lấy thông tin token: " + ex.getMessage());
            return null;
        }
        if (jsonNode.isEmpty()) {
            mailService.sendMail("hoangvuvan677@gmail.com", "Lỗi đăng nhập vnua lấy token", "Đăng nhập lỗi");
            return null;
        }
        return jsonNode.get("token").asText();
    }

    @Override
    public void sendMailNotiNewsFromAdminVnuaWithEachUser(LoginRequest loginRequest) {
        log.info("Sending Vnua News from Amin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getTokenVnua(loginRequest));
        String body = """
                {"filter":{"id":null,"is_noi_dung":true,"is_web":true},"additional":{"paging":{"limit":10,"page":1},"ordering":[{"name":"ngay_gui","order_type":1}]}}
                """;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        JsonNode jsonNode;
        try {
            String response = restTemplate.postForObject(urlNewsFromAdminVnua, entity, String.class);
            if (StringUtils.isEmpty(response)) {
                throw new RuntimeException();
            }
            jsonNode = objectMapper.readTree(response);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            mailService.sendMail("hoangvuvan677@gmail.com", "Lỗi lấy thông tin thông báo VNUA", "Lỗi lấy thông tin thông báo từ quản trị viên: "  + ex.getMessage());
            return;
        }
        JsonNode items = jsonNode.path("data").path("ds_thong_bao");
        if (items.isEmpty()) {
            return;
        }
        items.valueStream().map(item ->
                        ArticleNewsFromAdminVnua.builder()
                                .id(item.get("id").asText())
                                .doi_tuong_search(item.get("doi_tuong_search").asText())
                                .doi_tuong(item.get("doi_tuong").asText())
                                .phan_cap_search(item.get("phan_cap_search").asText())
                                .phan_cap_sinh_vien(item.get("phan_cap_sinh_vien").asText())
                                .tieu_de(item.get("tieu_de").asText())
                                .noi_dung(item.get("noi_dung").asText())
                                .is_phai_xem(item.get("is_phai_xem").asBoolean())
                                .ngay_gui(LocalDateTime.parse(item.get("ngay_gui").asText()))
                                .is_da_doc(item.get("is_da_doc").asBoolean())
                                .phan_hoi(item.get("phan_hoi").isNull() ? "" : item.get("phan_hoi").asText())
                                .is_xem_phan_hoi(item.get("is_xem_phan_hoi").asBoolean())
                                .ngay_xem(item.get("ngay_xem").asText())
                                .build())
                .filter(item -> item.getNgay_gui().isAfter(LocalDateTime.now().minusHours(2)))
                .forEach(item -> {
                    mailService.sendMail("hoangvuvan677@gmail.com", item.getTieu_de(), item.getNoi_dung());
                });
    }
}
