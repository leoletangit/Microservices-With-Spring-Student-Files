package pe.joedayz.sentence.controller;

import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pe.joedayz.sentence.dao.*;

import java.net.URI;
import java.util.List;

@RestController
public class SentenceController {

    private NounClient nounClient;
    private ArticleClient articleClient;
    private SubjectClient subjectClient;
    private VerbClient verbClient;
    private AdjectiveClient adjectiveClient;

    @Autowired
    public SentenceController(SubjectClient subjectClient, VerbClient verbClient, ArticleClient articleClient,
                              AdjectiveClient adjectiveClient, NounClient nounClient){
        this.subjectClient = subjectClient;
        this.verbClient = verbClient;
        this.articleClient = articleClient;
        this.adjectiveClient = adjectiveClient;
        this.nounClient = nounClient;
    }

    @GetMapping("/sentence")
    public String getSentence(){

        String sentence = "Hay problemas con los microservicios";
        try{
            return String.format("%s %s %s %s %s.",
                    subjectClient.getWord().getString(),  //3
                    verbClient.getWord().getString(),
                    articleClient.getWord().getString(),
                    adjectiveClient.getWord().getString(),
                    nounClient.getWord().getString());

        }catch (Exception ex){
            return sentence;
        }

    }


}
