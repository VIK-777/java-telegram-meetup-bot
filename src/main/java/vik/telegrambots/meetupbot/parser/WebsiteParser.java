package vik.telegrambots.meetupbot.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import vik.telegrambots.meetupbot.dao.model.Event;

@Slf4j
public abstract class WebsiteParser {

  @Autowired
  private HttpClient httpClient;

  public Event loadAndParseEvent(String link) throws IOException, InterruptedException {
    var response = httpClient.send(HttpRequest.newBuilder()
        .uri(URI.create(link))
        .GET()
        .build(), HttpResponse.BodyHandlers.ofString());
    log.info("Received response, code: {}", response.statusCode());
    return parseEventDescription(response.body());
  }

  protected abstract Event parseEventDescription(String htmlResponse) throws JsonProcessingException;
}
