## Lab 4 - Crear un Servidor Spring Cloud Eureka y Cliente

**Parte 1, crear servidor**

1. Crear un nuevo Spring Boot application.
  - Colocarle como nombre de proyecto "lab-4-eureka-server”, y usa este valor para el Artifact.  
  - Usa packaging JAR y la última versión de Java
  - Versión de Boot  2.2.X o la última disponible
  - No selecciones ninguna dependencia.

2. Edita el archivo POM (o Gradle).  Agrega una sección “Dependency Management” (despues de <properties>, antes de <dependencies>) para identificar el spring cloud parent POM.  "Hoxton.SR1" que actualmente es la versión más reciente. Ejemplo:

```
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Hoxton.SR1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

3. Agrega una dependencia con group "org.springframework.cloud" y artifact "spring-cloud-starter-netflix-eureka-server".  
No se necesita especificar una versión -- eso ya esta definido en el proyecto padre.

4. Crea un application.yml (o properties) en la raiz de tu classpath (src/main/resources es lo recomendado).  Agrega los siguientes key/valor (usa el correcto formato YAML):
  ```
server:
  port: 8010
  ```

5. (Opcional) Crea un bootstrap.yml (o properties) en la raiz de tu classpath.  Agrega los siguientes key / valores (usa el correcto formato YAML):
  ```
spring:
  application:
    name: lab-4-eureka-server
  ```

6. Agrega @EnableEurekaServer a la clase Application.  Guarda tu trabajo.  Inicia el servidor. 
 Temporalmente ignora las advertencias, acerca de ejecutar una simple instancia 
 (i.e. connection refused, unable to refresh cache, backup registry not implemented, etc.). 
 Abre un navegador y ve a [http://localhost:8010](http://localhost:8010) para ver al servidor ejecutandose.

**Parte 2, crea los clientes**  
    
    En esta sección crearemos aplicaciones clientes que trabajaran juntos para componener una oración (sentence). 
	La oración contendrá un subject, verb, article, adjective y noun como:  “I saw a leaky boat” o “You have the reasonable book”. 
	5 servicios generarán aleatoriamente las palabras correspondientes, y un 6th servicio las ensamblara todas para generar una oración.

7. Crea un nuevo Spring Boot web application.  
  - Coloca como nombre de proyecto "word-client”, y usa esta valor para el Artifact.  
  - Usa JAR packaging y la última versión de Java 
  - Usa  Boot version 2.2.x o la última versión disponible
  - Agrega actuator y web como dependencias.

8. Modifica el archivo (o Gradle).  
  - Agrega la misma sección dependency management que insertaste en el POM del servidor.  
  - Agrega una dependencia con group "org.springframework.cloud" y artifact "spring-cloud-starter-netflix-eureka-client".

9. Modifica la clase Application.  Agrega @EnableEurekaClient.

10. Crea un application.yml (o properties) en la raiz de tu classpath (src/main/resources recomendado).  Agrega los siguientes key / values (usa correcto formato YAML):

```

---

eureka:
  client:
    serviceUrl:
      defaultZone: http://eurekahost:8010/eureka/      (eurekahost -> localhost, puedes configurarlo en tu host)


server:
  port: ${PORT:${SERVER_PORT:0}}   


---
spring:
  profiles: subject
words: I,You,He,She,It

---
spring:
  profiles: verb
words: ran,knew,had,saw,bought

---
spring:
  profiles: article
words: a,the

---
spring:
  profiles: adjective
words: reasonable,leaky,suspicious,ordinary,unlikely

---
spring:
  profiles: noun
words: boat,book,vote,seat,backpack,partition,groundhog

  ```

11. Crea un archivo bootstrap.yml (o properties) en la raiz de tu classpath.  Agrega los siguientes key / valores (usa el correcto formato YAML ):
  
    ```
      ---
    spring:
      profiles: subject
      application:
        name: lab-4-subject

    ---
    spring:
      profiles: verb
      application:
        name: lab-4-verb

    ---
    spring:
      profiles: article
      application:
        name: lab-4-article

    ---
    spring:
      profiles: adjective
      application:
        name: lab-4-adjective

    ---
    spring:
      profiles: noun
      application:
        name: lab-4-noun

      ```

12. Agrega una clase Controler
  - Colocale en el paquete o subpaquete que desees.
  - Coloca el nombre de la clase que tu desees. Sugiero WordController, y anota este con @RestController.
  - Agrega una propiedad de tipo String denominada “words”.  Anota esta con @Value("${words}”).
  - Agrega el siguiente método para servir el recurso:

  ```
@RestController
public class WordController {

  @Value("${words}")
  String words;

  @GetMapping("/")
  public String getWords(){
    String[] wordArray = words.split(",");
    int i = (int)Math.round(Math.random() * (wordArray.length - 1));
    return wordArray[i];
  }
}

  ```

13. Agrega @EnableEurekaClient a la clase Application.



14. Crea un nuevo Spring Boot web application.  
  - Coloca como nombre de proyecto "sentence", y usa esta valor para el Artifact.  
  - Usa JAR packaging y la última versión de Java 
  - Usa  Boot version 2.2.x o la última versión disponible
  - Agrega actuator y web como dependencias.

15. Modifica el archivo (o Gradle).  
  - Agrega la misma sección dependency management que insertaste en el POM del servidor.  
  - Agrega una dependencia con group "org.springframework.cloud" y artifact "spring-cloud-starter-netflix-eureka-client".

16. Modifica la clase Application.  Agrega @EnableEurekaClient.


19. Crea un archivo application.yml (o properties) en la raiz de tu classpath (src/main/resources recomendado).  Agrega los siguientes key / valores (usa el correcto formato YAML):

  ```
---
eureka:
  client:
    serviceUrl:
      defaultZone: http://eurekahost:8010/eureka/  (eurekahost -> localhost, puedes configurarlo en tu host)

---
spring:
  profiles: sentence
server:
  port: 8020
words: NotApplicable

  ```

20. Crea un bootstrap.yml con el siguiente contenido:

  ```
---
spring:
  profiles:
    active: sentence
  application:
    name: lab-4-sentence

  ```

21. Agrega una clase Controller para ensamblar y retornar la sentencia:
  - Colocale como nombre de clase SentenceController y anotala con @RestController.
  - Usa @Autowired para obtener un DiscoveryClient (importar de Spring Cloud).
  - Agrega el siguiente código para construir la sentencia usando las palabras obtenidas de los servicios cliente:

  ```
  @GetMapping("/sentence")
  public String getSentence(){

    return String.format("%s %s %s %s %s.",
        getWord("LAB-4-SUBJECT"),
        getWord("LAB-4-VERB"),
        getWord("LAB-4-ARTICLE"),
        getWord("LAB-4-ADJECTIVE"),
        getWord("LAB-4-NOUN") );
  }

  private String getWord(String applicationName) {
    List<ServiceInstance> list = client.getInstances(applicationName);
    if (list != null && list.size() > 0 ) {
      URI uri = list.get(0).getUri();
      if (uri !=null ) {
        return (new RestTemplate()).getForObject(uri,String.class);
      }
    }
    return null;
  }
  ```

22. Ejecuta todos los servicios word-client y servicio sentence.  (Ejecutalos con tu IDE, o con los jars construidos (mvn clean package) y ejecutalo desde la linea de comando (java -jar name-of-jar.jar), cualquiera de las formas que tu veas conveniente).  Puesto que cada servicio usa un puerto diferente, ellos deberían poder ejecutarse sin problemas en el mismo computador.  Abre [http://localhost:8020/sentence](http://localhost:8020/sentence) para ver la sentencia completa.  Refresh the URL and watch the sentence change.


 	
  **BONUS - Refactorizar para usar Spring Cloud Config Server.**  

  
  Podemos usar Eureka junto con el config server para eliminar la necesidad de que cada cliente configure la ubicación del servidor Eureka.
  


1. Agrega un nuevo archivo a tu repositorio GitHub (el mismo repositorio usado en el laboratorio anterior) denominado “application.yml” (o properties).  Agrega el siguiente key / valor:

  ```
  eureka:
    client:
      serviceUrl:
        defaultZone: http://eurekahost:8010/eureka/

  ```

2. Abre el proyecto config-server de la carpeta common.  Es el mismo que obtuvimos del lab 3.  Modifica el application.yml para apuntar a tu propio repositorio github.  Guarda todo y ejecuta este servidor.  (Este será el config server para los siguientes laboratorios)  

3. Edita el application.properties o application.yml de cada proyecto cliente.  Elimina el eureka client serviceUrl defaultZone key/valor.  Nosotros obtendremos este del config server.

4. En cada proyecto cliente, agrega el sigiente key/valor al bootstrap.yml (o bootstrap.properties): 
  
    ```
      spring:
        cloud:
          config:
            uri: http://localhost:8001
    ```
5. Agrega una dependencia adicional para spring-cloud-config-client. 

6. Asegurate que el Eureka server aun se este ejecutandose.  Inicia (o reinicia) cada cliente. Abre [http://localhost:8020/sentence](http://localhost:8020/sentence) para ver la sentencia completa.

7. Si gustas, puedes mover las propiedades “words” al repositorio GitHub para ver como ellas pueden ser servidas por el config server.  Un simple application.yml luciría así:

  ```
  ---
  spring:
    profiles: subject
  words: I,You,He,She,It
  
  ---
  spring:
    profiles: verb
  words: ran,knew,had,saw,bought

  ---
  spring:
    profiles: article
  words: a,the

  ---
  spring:
    profiles: adjective
  words: reasonable,leaky,suspicious,ordinary,unlikely

  ---
  spring:
    profiles: noun
  words: boat,book,vote,seat,backpack,partition,groundhog  
  ```

  **BONUS - Refactorizar para usar multiples Eureka Servers**  
    
  Para hacer aplicaciones más tolerantes a fallas, nosotros podemos ejecutar múltiples servidores Eureka.  En la vida real deberíamos poder ejecutar varias copias en diferentes racks / data centers, pero para simular eso, haremos lo siguiente:

8.  Deten todos los servicios ejecutandose.

9.  Edita el archivo de tu computadora /etc/hosts (c:\WINDOWS\system32\drivers\etc\hosts en Windows).  Agrega la siguientes lineas y guarda tu trabajo:

  ```
  # START section for Microservices with Spring Course
  127.0.0.1       eurekahost
  127.0.0.1       eureka-primary
  127.0.0.1       eureka-secondary
  127.0.0.1       eureka-tertiary
  # END section for Microservices with Spring Course
  ```

10.  En el proyecto eureka-server, agrega un application.yml con multiples profiles:

primary, secondary, tertiary.  El valor del server.port debería ser 8011, 8012, y 8013 respectivamente.  El eureka.client.serviceUrl.defaultZone para cada profile deberia apuntar al "eureka-*" URLs de los otros dos; por ejemplo, el valor primary debería ser: http://eureka-secondary:8012/eureka/,http://eureka-tertiary:8013/eureka/




11.  Ejecuta la aplicación 3 veces, usando -Dspring.profiles.active=primary (y secondary, y tertiary) para activar el profile en cuestión.  El resultado debería ser 3 Eureka servers comunicandose unos a otros.

12.  En tu proyecto GitHub, modifica el application.properties eureka.client.serviceUrl.defaultZone para incluir los URIs de los otros tres Eureka servers (separado por comas, no espacios).

13.  Inicia todos los clientes.  Abre [http://localhost:8020/sentence](http://localhost:8020/sentence) para ver la sentencia completa.

14.  Para testear Eureka’s fault tolerance, deten el 1 o 2 de las instancias Eureka.  Reinicia 1 o 2 de los clientes para asegurarte de que ellos no tienen dificultades para encontrar al  Eureka que se esta ejecutando. 



