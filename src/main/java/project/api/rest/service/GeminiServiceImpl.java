package project.api.rest.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiServiceImpl implements GeminiService {

    private final ChatClient geminiClient;

    @Autowired
    public GeminiServiceImpl(ChatClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public String getNickname(String name) {
        return geminiClient.prompt("Give me only one Argentine nickname for the name "+name+". " +
                        "Respond with only the nickname, nothing else, no punctuation, no explanations.")
                .call()
                .content();
    }
}
