package vik.telegrambots.meetupbot.parser;

import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vik.telegrambots.meetupbot.dao.model.Event;

@Service
@RequiredArgsConstructor
public class ParserProvider implements WebsiteParser {

  private final MeetupComParser meetupComParser;

  @Override
  public Event loadAndParseEvent(String link) throws IOException, InterruptedException {
    var url = URI.create(link);
    var parser = switch (url.getHost()) {
      case "www.meetup.com", "meetu.ps" -> meetupComParser;
      default -> throw new IllegalArgumentException("Unsupported website: " + url.getHost());
    };
    return parser.loadAndParseEvent(link);
  }
}
