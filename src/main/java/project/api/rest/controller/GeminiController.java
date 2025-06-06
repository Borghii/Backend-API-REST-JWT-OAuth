package project.api.rest.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gemini")
@Controller
public class GeminiController {

    private final ChatClient geminiClient;

    @Autowired
    public GeminiController(ChatClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @GetMapping("/nickname/{name}")
    public ResponseEntity<String> getNicknameByName(@PathVariable String name){
        String nickname = geminiClient.prompt("Give me only one Argentine nickname for the name "+name+"." +
                        "Respond with only the nickname, nothing else, no punctuation, no explanations.")
                .call()
                .content();

        return new ResponseEntity<>(nickname, HttpStatus.OK);
    }



}
