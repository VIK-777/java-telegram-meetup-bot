package vik.telegrambots.meetupbot.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.abilitybots.api.db.DBContext;

import static org.telegram.abilitybots.api.db.MapDBContext.onlineInstance;

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
}
