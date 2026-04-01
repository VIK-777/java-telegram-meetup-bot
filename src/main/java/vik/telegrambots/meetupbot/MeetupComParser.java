package vik.telegrambots.meetupbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vik.telegrambots.meetupbot.dao.model.Event;

@Slf4j
@Service
public class MeetupComParser {

  private final HttpClient httpClient = HttpClient.newHttpClient();
  @Autowired
  private ObjectMapper objectMapper;

  public Event loadAndParseEvent(String link) throws IOException, InterruptedException {
    var response = httpClient.send(HttpRequest.newBuilder()
        .uri(URI.create(link))
        .GET()
        .build(), HttpResponse.BodyHandlers.ofString());
    log.info("Received response, code: {}", response.statusCode());
    return parseEventDescription(response.body());
  }

  private Event parseEventDescription(String htmlResponse) throws JsonProcessingException {
    int startIndex = htmlResponse.indexOf("<script type=\"application/ld+json\">{\"@context\":\"https://schema.org\",\"@type\":\"Event\"");
    if (startIndex == -1) {
      throw new RuntimeException("Event JSON not found in response");
    }
    int endIndex = htmlResponse.indexOf("</script>", startIndex) + 10; // +10 to include </script>
    String json = htmlResponse.substring(startIndex + 35, endIndex - 10); // Adjust indices to extract just the JSON
    JsonNode root = objectMapper.readTree(json);
    log.info("Extracted JSON: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));

    // Extract fields
    String name = root.get("name").asText();
    String startDate = root.get("startDate").asText(); // e.g., "2026-04-23T17:30:00+02:00"
    String locationName = root.get("location").get("name").asText(); // e.g., "BlueBerk Office Berlin"
    String streetAddress = root.get("location").get("address").get("streetAddress").asText(); // e.g., "Perleberger Str. 3a, Berlin"
    String description = root.get("description").asText();
    String url = root.get("url").asText();

    Instant eventTime = Instant.parse(startDate); // Handles ISO 8601 format like "2026-04-23T17:30:00+02:00"

    String fullDescription = locationName + " - " + streetAddress + "\n\n" + description;

    return Event.builder()
        .name(name)
        .eventTime(eventTime)
        .description(fullDescription)
        .link(url)
        .build();
  }
}
