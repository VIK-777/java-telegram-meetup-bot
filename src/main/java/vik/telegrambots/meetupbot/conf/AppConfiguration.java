package vik.telegrambots.meetupbot.conf;

import static org.telegram.telegrambots.abilitybots.api.db.MapDBContext.onlineInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.abilitybots.api.db.DBContext;

@Configuration
public class AppConfiguration {

    @Bean
    public DBContext db(Environment env) {
        return onlineInstance(env.getProperty("bot.name"));
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
