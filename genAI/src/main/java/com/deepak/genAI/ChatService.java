package com.deepak.genAI;



import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class ChatService {

    private final ChatClient chatClient;


    private ArrayList<Message> history=new ArrayList();
    CalculatorTool calculatorTool;

    public ChatService(ChatClient.Builder chatClientBuilder,CalculatorTool calculatorTool) {
        this.chatClient = chatClientBuilder.build();
        this.calculatorTool=calculatorTool;

    }
    private final String SYSTEM_PROMT= """
            # Role
                        You are a helpful and concise AI assistant with access to a calculator tool.
            
                        # Calculator Tool
                        The calculator supports the following operations:
                        - add: addition
                        - subtract: subtraction
                        - multiply: multiplication
                        - divide: division
                        - modulo: remainder after division
            
                        # Rules
                        1. Always use the calculator tool when the user asks you to perform a mathematical calculation.
                        2. Do not calculate mathematical results yourself.
                        3. For multiple calculations, call the calculator tool as many times as required.
                        4. Never divide by zero.
                        5. Never perform modulo by zero.
                        6. After receiving the calculator result, provide the final answer to the user.
                  
                        8. For non-mathematical questions, answer normally.
          
          """;
  StringBuilder fullMessage=new StringBuilder();
    public Flux<String> ask(String question) {
  history.add(new UserMessage(question));
        Flux<String> output= chatClient
                .prompt()
                .messages(history)
//                .system(SYSTEM_PROMT)
//                .tools(calculatorTool)
//                .call()
                .user(question)
                .stream()
                .content()
                .doOnNext(fullMessage::append)
                .doOnComplete(()->{
                    history.add(new AssistantMessage(fullMessage.toString()));

                });


//        history.add(new AssistantMessage(output));
        return output;
    }


}