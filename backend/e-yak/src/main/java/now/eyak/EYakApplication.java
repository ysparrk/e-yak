package now.eyak;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@RequiredArgsConstructor
public class EYakApplication {

    private final RedisTemplate redisTemplate;

    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(EYakApplication.class, args);
    }

}
