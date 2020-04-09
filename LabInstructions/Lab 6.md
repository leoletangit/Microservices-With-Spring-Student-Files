## Lab 6 - Usando Feign Declarative REST clients

**Part 1, Start existing services.**

1.  Vamos a iniciar desde cero: deten todos los micro servicios que iniciaste en los ejercicios anteriores.  
Si usas un IDE cierra todos los proyectos que no tengan que ver con el "lab-6" o "common".

2.  Abre el common-config-server y el common-eureka-server. No olvides que el application.yml del common-config-server apunte a 
tu repositorio de github.  

3.  Inicia 5 copias diferentes de el word-client, usando los profiles "subject", "verb", "article", "adjective", y "noun".  
     Hay varias formas de hacer eso dependiendo de tus preferencias:
  - Si usas Maven, abre diferentes command prompts y en el directorio target ejecuta estos comandos:
    - mvn spring-boot:run -Dspring-boot.run.profiles=subject"
    - mvn spring-boot:run -Dspring-boot.run.profiles=verb"
    - mvn spring-boot:run -Dspring-boot.run.profiles=article"
    - mvn spring-boot:run -Dspring-boot.run.profiles=adjective"
    - mvn spring-boot:run -Dspring-boot.run.profiles=noun"
  - Si usas sólo java:
    - java -jar -Dspring.profiles.active=subject target/word-client-0.0.1-SNAPSHOT.jar ( y así para los otros profiles)
	
	
	
	
4.  Verifica que el servidor Eureka se ejecuta en  [http://localhost:8010](http://localhost:8010).  
   Ignora las advertencias de que solo estas ejecutando una simple instancia; esto es lo esperado. 
   Asegurate que tus 5 aplicaciones son listadas en la sección  "Application", esto puede durar unos minutos, ten paciencia.	

5.  Opcional - Si haces clic en cualquiera de los servicios. Reemplaza el "/info" con "/" y refresca varias veces. 
Tu puedes ver las palabras (words) generadas aleatoriamente.

6.  Ejecuta el sentence project.  Refresh Eureka to see it appear in the list.  Test to make sure it works by opening [http://localhost:8020/sentence](http://localhost:8020/sentence).  You should see several random sentences appear.  We will refactor this code to make use of Feign.

  
  
  **Part 2 - Refactor**

7.  First, take a look at the sentence project.  It has been refactored a bit from previous examples.  The controller has been simplified to do only web work, the task of assembling the sentence is now in the service layer.  The SentenceService uses @Autowire to reference individual DAO components which have been created to obtain the words from the remote resources.  Since all of the remote resources are structurally the same, there is a fair bit of inheritance in the dao package to make things easy.  But each uses the same Ribbon client technology and RestTemplate used previously.

8.  Open the POM.  Add another dependency for:

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>


9.  Edit the main Application configuration class and @EnableFeignClients.  Elimina el bean RestTemplate del lab 5. 

10.  Refactor the “Noun” service to use Feign.  Create a new interface in the dao package called NounClient. 
 Annotate it with @FeignClient.  What value should you use for the service ID?  The existing code should tell you.

11.  Next, provide the method signature to be implemented by Feign.  To think this through, take a fresh look at the lab-6-word-server WordController.  Note the annotation used and return type.  You can actually copy/paste this signature as-is, except 1) remove the method implementation, and 2) there is no need for @ResponseBody as this is implied, and 3) it will be necessary to add method=RequestMethod.GET to clarify that this is a GET request.

12.  Edit the SentenceService.  Replace the private WordDao nounService and its associated setter with the NounClient you just made. Depending on how you built your NounClient, you may need to refactor the buildSentence method slightly.

13.  Notice that you have probably introduced an error on the SentenceServiceImplTest test class.  Make adjustments to accomodate the NounClient.

14.  Stop any previously running sentence server and launch your new one.  Test it to make sure it works by opening [http://localhost:8020/sentence](http://localhost:8020/sentence).  The application should work the same as it did before, though now it is using a declarative Feign client to make the noun call.

  **BONUS - Additional Refactoring**

15. If you like, you can also refactor the subject, verb, article, and adjective clients.  This can be done reasonably easy by copy paste.  Remove the old DAO implementations and note that you will no longer have any executable code to maintain or unit test (you still must integration test, of course).


**Reflection:**

1. While we have no dao code that requires UNIT testing, we still need to perform INTEGRATION testing.  Still, it is nice to be freed of the need to do as much coding.

2. Our application will still fail if we can’t find at least one of each kind of word server.  We will improve this later when we discuss circuit breakers with Hystrix.

3. To improve performance, can we run each of the calls in parallel?  We will improve this later when discussing Hystrix. 
