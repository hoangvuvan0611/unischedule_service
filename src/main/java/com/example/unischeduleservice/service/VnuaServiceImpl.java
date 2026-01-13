package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.ArticleNewsVnua;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VnuaServiceImpl implements VnuaService {
    @Value("${url.vnua.urlNewsVnua}")
    private String urlNewsVnua;

    private static final Logger log = LoggerFactory.getLogger(VnuaServiceImpl.class);
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
        List<ArticleNewsVnua> articleNewsVnuaList = items.valueStream().map(item -> ArticleNewsVnua.builder()
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
                    .build()
        ).filter(item -> item.getIs_news() && item.getNgay_dang_tin().toLocalDate().isAfter(LocalDate.now().minusDays(1))).toList();

        articleNewsVnuaList.forEach(articleNewsVnua -> {
            mailService.sendMail("hoangvuvan677@gmail.com", "Thông báo VNUA", articleNewsVnua.getTieu_de());
        });
    }
}
