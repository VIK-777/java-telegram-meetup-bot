package vik.telegrambots.meetupbot.conf;

import static org.telegram.telegrambots.abilitybots.api.db.MapDBContext.onlineInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class AppConfiguration {

  @Bean
  public TelegramClient telegramClient(@Value("${bot.token}") String botToken) {
    return new OkHttpTelegramClient(botToken);
  }

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

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
  }
}
