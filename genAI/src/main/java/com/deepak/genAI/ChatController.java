package com.deepak.genAI;


import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatService deepSeekService;

    public ChatController(ChatService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/chat")
    public Flux<String> ask(@RequestBody String question) {

        return deepSeekService.ask(question);
    }

}