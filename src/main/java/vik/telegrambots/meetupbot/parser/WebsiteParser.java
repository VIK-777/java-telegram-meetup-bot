package vik.telegrambots.meetupbot.parser;

import java.io.IOException;
import vik.telegrambots.meetupbot.dao.model.Event;

public interface WebsiteParser {

  Event loadAndParseEvent(String link) throws IOException, InterruptedException;
}
