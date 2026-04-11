package com.springcore.ai.scaiplatform.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationProperties {
    private General general;
    private Upload upload;

    @Data
    public static class Upload {
        private String path;
    }

    @Data
    public static class General {
        private int recordtypeMapInitSize;
        private DefaultUserPreference defaultUserPreference;

        @Data
        public static class DefaultUserPreference {
            private String lang;
            private String country;
            private String timezone;
            // 2. ใช้ CamelCase สำหรับชื่อที่มีขีด (-) ใน yml (date-time -> dateTime)
            private String dateTime;
            private String date;
            private String time;
        }
    }
}
