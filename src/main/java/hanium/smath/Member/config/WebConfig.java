package hanium.smath.Member.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://192.168.50.195:8080") // 허용할 도메인
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 허용할 헤더
                        .allowCredentials(true); // 자격 증명 허용
            }
        };
    }
}
