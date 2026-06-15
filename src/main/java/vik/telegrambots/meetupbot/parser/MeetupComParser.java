package vik.telegrambots.meetupbot.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import vik.telegrambots.meetupbot.dao.model.Event;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetupComParser extends WebsiteParser {

  private final ObjectMapper objectMapper;

  @Override
  protected Event parseEventDescription(String htmlResponse) throws JsonProcessingException {
    // Find a <script ... type="application/ld+json" ...>...</script> that contains an object with "@type":"Event".
    String json = getJsonFromResponse(htmlResponse);

    JsonNode root = objectMapper.readTree(json);
    log.info("Extracted JSON: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));

    // Extract fields defensively
    String name = root.path("name").asText(null);
    String startDate = root.path("startDate").asText(null);
    JsonNode locationNode = root.path("location");
    String locationName = locationNode.path("name").asText("");
    String streetAddress = locationNode.path("address").path("streetAddress").asText("");
    String description = root.path("description").asText("");
    String url = root.path("url").asText("");

    Instant eventTime = null;
    if (startDate != null && !startDate.isEmpty()) {
      // Parse offsets like 2026-06-18T19:00:00+02:00
      eventTime = OffsetDateTime.parse(startDate).toInstant();
    }
    String location = (locationName + (streetAddress.isEmpty() ? "" : " - " + streetAddress)).replace(",,", ",");

    return Event.builder()
        .name(name)
        .eventTime(eventTime)
        .description(description)
        .link(url)
        .location(location)
        .build();
  }

  private static @NonNull String getJsonFromResponse(String htmlResponse) {
    String json = null;
    int pos = 0;
    while (true) {
      int scriptStart = htmlResponse.indexOf("<script", pos);
      if (scriptStart == -1) break;
      int tagEnd = htmlResponse.indexOf('>', scriptStart);
      if (tagEnd == -1) break;
      String openTag = htmlResponse.substring(scriptStart, tagEnd + 1);
      if (openTag.contains("type=\"application/ld+json\"") || openTag.contains("type='application/ld+json'")) {
        int scriptEnd = htmlResponse.indexOf("</script>", tagEnd + 1);
        if (scriptEnd == -1) break;
        String content = htmlResponse.substring(tagEnd + 1, scriptEnd).trim();
        if (content.contains("\"@type\":\"Event\"") || content.contains("\"@type\": \"Event\"")) {
          json = content;
          break;
        }
        pos = scriptEnd + 9;
      } else {
        pos = tagEnd + 1;
      }
    }

    if (json == null) {
      throw new RuntimeException("Event JSON not found in response");
    }
    return json;
  }
}
