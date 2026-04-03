package vik.telegrambots.meetupbot.parser;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserProvider {

  private final MeetupComParser meetupComParser;

  public WebsiteParser getParser(String link) {
    var url = URI.create(link);
    return switch (url.getHost().replace("www.", "").toLowerCase()) {
      case "meetup.com", "meetu.ps" -> meetupComParser;
      default -> throw new IllegalArgumentException("Unsupported website: " + url.getHost());
    };
  }
}
