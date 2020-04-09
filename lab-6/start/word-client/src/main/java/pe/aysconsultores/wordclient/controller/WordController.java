package pe.aysconsultores.wordclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.aysconsultores.wordclient.model.Word;

@RestController
public class WordController {

	@Value("${words}")
	String words;

	@GetMapping("/")
	public Word getWords(){
		String[] wordArray = words.split(",");
		int i = (int)Math.round(Math.random() * (wordArray.length - 1));
		return new Word(wordArray[i]);
	}
}
