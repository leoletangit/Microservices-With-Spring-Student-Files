package pe.joedayz.sentence.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pe.joedayz.sentence.model.Word;


@FeignClient(value="ADJECTIVE")
public interface AdjectiveClient {

	@GetMapping("/")
	Word getWord();
}
