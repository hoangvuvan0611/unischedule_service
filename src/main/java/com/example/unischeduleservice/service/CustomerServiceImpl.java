package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.ReviewDTO;
import com.example.unischeduleservice.exceptions.CustomException;
import com.example.unischeduleservice.models.Review;
import com.example.unischeduleservice.repository.ReviewRepository;
import com.example.unischeduleservice.utils.TranslatorUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final MailService mailService;
    private final ReviewRepository reviewRepository;

    @Value("${mail.to}")
    private String mailTo;

    @Override
    public void sendReviewUniScheduleApp(ReviewDTO review) throws CustomException {
        if (!StringUtils.hasText(review.getTitle())) {
            review.setTitle(TranslatorUtil.toLocale("title.send.mail.noti.review.uni.app"));
        }
        String content = buildFeedbackEmail(review.getUserCode(), review.getUsername(), review.getMessage());
        try {
            mailService.sendMail(mailTo, review.getTitle(), content);
            reviewRepository.save(Review.builder()
                    .title(review.getTitle())
                    .userCode(review.getUserCode())
                    .username(review.getUsername())
                    .message(review.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException(TranslatorUtil.toLocale("error.send.mail.noti.review.uni.app"), e.getMessage());
        }
    }

    private String buildFeedbackEmail(String userId, String userName, String content) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px;">
                <h3 style="color: #4CAF50;">📩 Phản hồi mới</h3>
                
                <p><b>Mã người dùng:</b> %s</p>
                <p><b>Tên người dùng:</b> %s</p>
                <p><b>Thời gian:</b> %s</p>
                
                <h4>Nội dung:</h4>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 5px;">
                    %s
                </div>
                
                <hr style="margin: 20px 0;">
                <p style="color: #999; font-size: 12px;">
                    Hệ thống tự động • %s
                </p>
            </body>
            </html>
            """.formatted(
                userId,
                userName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                content,
                LocalDate.now().getYear()
        );
    }
}
