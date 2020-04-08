## Lab 5 - Usando Ribbon Clients

**Parte 1, Ejecuta el Config Server, Eureka, y los micro servicios words**

1.  Vamos a iniciar desde cero: deten todos los micro servicios que iniciaste en los ejercicios anteriores.  
Si usas un IDE cierra todos los proyectos que no tengan que ver con el "lab-5" o "common".

2.  Abre el common-config-server y el common-eureka-server. No olvides que el application.yml del common-config-server apunte a tu repositorio de github.  

3.  Inicia 5 copias diferentes de el lab-5-word-server, usando los profiles "subject", "verb", "article", "adjective", y "noun".  
     Hay varias formas de hacer eso dependiendo de tus preferencias:
  - Si usas Maven, abre diferentes command prompts y en el directorio target ejecuta estos comandos:
    - mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=subject"
    - mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=verb"
    - mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=article"
    - mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=adjective"
    - mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=noun"
  - Si usas sólo java:
    - java -jar -Dspring.profiles.active=subject target/word-client-0.0.1-SNAPSHOT.jar ( y así para los otros profiles)
	
	
		
4.  Verifica que el servidor Eureka se ejecuta en  [http://localhost:8010](http://localhost:8010).  
   Ignora las advertencias de que solo estas ejecutando una simple instancia; esto es lo esperado. Asegurate que tus 5 aplicaciones son listadas en la sección  "Application", esto puede durar unos minutos, ten paciencia.	

5.  Opcional - Si haces clic en cualquiera de los servicios. Reemplaza el "/info" con "/" y refresca varias veces. Tu puedes ver las palabras (words) generadas aleatoriamente.

  **Parte 2, Modifica el servicio sentence para usar Ribbon**	

6.  Ejecuta el proyecto sentence-client.  Refresca Eureka para ver si aparece en la lista.  
     Verifica si trabaja abriendo este link [http://localhost:8020/sentence](http://localhost:8020/sentence).  
	 Tu veras diferentes sentencias aleatorias.  Nosotros refactorizamos este código para usar Ribbon.

7.  Deten el proyecto sentence-client.  Agrega la dependencia org.springframework.cloud / spring-cloud-starter-netflix-ribbonn.

8.  Ir a la clase Application.java.  Crea un nuevo método @Bean que instancia y retorna un nuevo RestTemplate.  
El método @Bean debería ser anotado con @LoadBalanced - esto asociará el RestTemplate con Ribbon.  El código luce así:

  ```
    //  This "LoadBalanced" RestTemplate 
    //  is automatically hooked into Ribbon:
    @Bean @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }  
  ```

9.  Abre el SentenceController.java.  Reemplaza el @Autowired DiscoveryClient con un @Autowired RestTemplate.  
     

10.  Refactoriza el método getWord. Usa el método restTemplate's getForObject para llamar al servicio.  
     El primer argumento debería ser una concatenación de "http://" y el service ID.  
	 El segundo argumento debería ser simplemente un String.class; 
	 La llamada debería verse así:

  ```
    return template.getForObject("http://" + service, String.class);
  ```

11. Ejecuta el proyecto.  Prueba que este trabaja abriendo [http://localhost:8020/sentence](http://localhost:8020/sentence). 
    La aplicación debería trabajar como antes, pero, ahora esta usando Ribbon, que le da balanceo de carga del lado del cliente.

  **BONUS - Multiples Clientes**  En este punto ya se ha refactorizado el código usando Ribbon, 
   pero, no hemos visto el poder de Ribbon como balanceador del lado del cliente.  
    Para ilustrar esto ejecutamos dos servicios word de tipo “noun”.  
	 Veras que el servicio sentence se adapta para hacer uso de los dos servicios "noun".

12. Localiza y deten la copia del servicio  “word” que esta sirviendo nouns.  

13. Abre el word-client.  Edita el bootstrap.yml y agrega la siguiente configuración de Eureka:
  ```
    # Allow Eureka to recognize two apps of the same type on the same host as separate instances:
    eureka:
      instance:
        instanceId: ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}
  ```
14. Ir a el POM.  Elimina la dependencia de DevTools.  
    DevTools es magnifico para automaticamente detectar cambios y reiniciarlos, pero, esto interferirá en los siguientes pasos.

15. Inicia una copia de word-client usando el profile “noun”, como hiciste anteriormente.

16. Mientras este nuevo servidor se esta ejecutando, edita la clase WordController.java.  Comenta la variable “String words” 
    y reemplaza esto con la versión hard-coded:
  ```
    String words = “icicle,refrigerator,blizzard,snowball”;
  ```
17. Inicia otra copia de word-client usando el profile “noun”.  Como cada uno se ejecuta en su propio puerto, 
   no habrá conflicto.  Tendremos dos servicios noun presentando diferentes listas de words.  
    Ambos se registrarán con Eureka, y el Ribbon load balancer en el servicio sentence pronto aprenderá que ambos existen.

18. Retorna a la página de Eureka en [http://localhost:8010](http://localhost:8010).  Refresca este varias veces.  
    Una vez que el registro esta completo, veremos dos servicios “NOUN” ejecutandose, cada uno con su propio instance ID 
	

19. Refresca la pagina sentence en el navegador en [http://localhost:8020/sentence](http://localhost:8020/sentence).  
   El loadbalancer distribuirá la carga entre los dos servicios, la mitad del tiempo tu sentence mostrará “cold” nouns que hard-coded antes.

20. Deten uno de los servicios  NOUN y refresca tu servicio sentence en el navegador varias veces.  
    Veras que este falla la mitad del tiempo, porque ya una de las instancias no esta disponible.  
	 En efecto, puesto, que el  default load balancer esta basado en un algoritmo round-robin, la falla ocurrirá cada segundo que el noun es usado.
	 Si continuamos refrescando un buen tiempo, tu veras que las fallas eventualmente se detienen ya que el cliente ribbon sera actualizado con la lista de servicios que proporciona Eureka.
	

**Reflection:**

1. You may be wondering about the Eureka registration delay that occurs.  After all, you can see from your application logs that each application registers itself with Eureka immediately.  The cause results from the need to synchronize between Eureka clients and servers; they all need to have the same metadata.  A 30 second heartbeat interval means that you could need up to three heartbeats for synchronization to occur.  You can decrease this interval, but 30 seconds is probably fine for most production cases.

2. The registration delay also affects when you stopped the NOUN server, and you may be surprised that the Ribbon load balancer did not direct us away from the server that was clearly not available.  We can address this by using different Ping, Rule, or LoadBalancer strategies.  By default Ribbon relies on Eureka to provide a list of healthy servers, and we’ve seen that with Default settings Eureka can take a while to notice a server’s absence.  We could use a different strategy, and also employ a rule that avoids non-functioning servers.  We will discuss this more when we explore Hystrix. 

3. Our application will still fail if we can’t find at least one of each kind of word server.  We will improve this later when we discuss circuit breakers with Hystrix.

4. To improve performance, can we run each of the calls in parallel?  We will improve this later when discussing Ribbon and Hystrix.

5. We will see an alternative to the RestTemplate when we discuss Feign.
