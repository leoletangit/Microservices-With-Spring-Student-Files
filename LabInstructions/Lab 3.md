## Lab 3 - Crear un Spring Cloud Config Server y Cliente

**Part 1 - Config Server:**

1. Crear un nuevo Spring Boot application.  Colocarle el nombre "lab-3-server”, y usar este valor para el Artifact.  
Usa el paquete Jar y la última versión de Java.  Usa una versión de Boot > 2.2.x.   No seleccionar ninguna dependencia.

2. Edita el archivo POM (o Gradle).  Agrega una sección “Dependency Management”  (despues de <properties>, antes de <dependencies>)
 para identificar el spring cloud parent POM.  "Hoxton.SR1" que es la versión mas reciente y estable (Febrero 2020).  Ejemplo:

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


3. Agrega una dependencia con group "org.springframework.cloud" y artifact "spring-cloud-config-server".  
No necesitas especificar una versión -- esto ya esta definido en el spring-cloud-dependencies BOM.

4. Edita la clase Application  (probablemene con el nombre Lab3ServerApplication). Agrega la anotación @EnableConfigServer a esta clase.

5. Ir al repositorio del proyecto Microservices-With-Spring-Student-Files y verificar que esta la carpeta "ConfigData". 
Anota el URI de el repositorio, lo necesitaras para el siguiente paso.

6. Asegurate de que en dicha carpeta de GitHub haya un archivo "lab-3-client.yml” 
(o lab-3-client.properties).  Agrega una llave denominada "lucky-word" y un valor de  "Spiderman", "Batman", "Ironman", 
o cualquier otro valor que quieras.

7. Regresa al proyecto, crea un archivo application.yml (o application.properties) en la raiz de tu classpath (src/main/resources es lo recomendado).  
Agrega la llave "spring.cloud.config.server.git.uri" y el valor "https://github.com/"TU-GITHUB-ID"/ConfigData", 
sustituye el valor Github ID  y nombre del repositorio como tu lo necesites. Configura el “server.port” a 8001.

8. Ejecuta la aplicación.  Abre el URL [http://localhost:8001/lab-3-client/default/](http://localhost:8001/lab-3-client/default/).  
Deberíamos ver el JSON resultante que será usando por Spring.  
Si el servidor no esta trabajando, revisa los pasos previos para encontrar el problema antes de continuar.

  **Part 2 - Configurar Cliente:**

9. Crea un nuevo, proyecto Spring Boot application.  Usa una versión de Boot > 2.2.x.  Denomina al proyecto "lab-3-client", 
y usa esta valor para el Artifact.  Agrega la dependencia web.  Tu puedes hacer este un JAR o WAR, 
pero las instrucciones son para trabajar con un JAR.

10.  Abre el archivo POM (o Gradle) y agrega una sección “Dependency Management” (despues de <properties>, antes de <dependencies>) para identificar el spring cloud parent pom:
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
11.  Agrega una dependencia con group "org.springframework.cloud" y artifact "spring-cloud-starter-config”. 
 Tu no necesitas especificar una versión -- esto ya esta definido en el  parent pom en la sección dependency management.

12. Agrega un archivo bootstrap.yml (o bootstrap.properties) en el root de tu classpath (src/main/resources recommended). 
 Agrega los siguientes key/values usando el formato apropiado:
spring.application.name=lab-3-client
spring.cloud.config.uri=http://localhost:8001  
server.port=8002

    _(Ojo que este archivo debe tener como nombre "boostrap" -- no "application" -- de forma que sea leido en primer lugar en el proceso 
	startup de la aplicación.  El server.port puede ser especificado en este archivo, pero,
	el URI a el config server afectará la secuencia de startup.)_

13. Agrega un REST controller para obtener y mostrar el lucky word:

    ```
    @RestController
    public class LuckyWordController {
 
      @Value("${lucky-word}") String luckyWord;
  
      @RequestMapping("/lucky-word")
      public String showLuckyWord() {
        return "The lucky word is: " + luckyWord;
      }
    }
    ```

14.  Inicia tu cliente.  Abre [http://localhost:8002/lucky-word](http://localhost:8002/lucky-word).  Tu deberías ver el mensaje lucky word en tu navegador.

  **BONUS - Profiles:**

15. Crea un archivo separado en tu repositorio GitHub denominado "lab-3-client-northamerica.yml” (o .properties).  Llena este con el key "lucky-word" y con un diferente valor.

16. Deten la aplicación cliente.  Modifica el archivo  boostrap para que contenga el  key spring.profiles.active: northamerica.  Guarda, y reinicia tu cliente.  Accede a el  URL.  ¿Qué lucky word es mostrado?  (Tu puedes ejecutar la application con -Dspring.profiles.active=northamerica en lugar de cambiar el archivo bootstrap)

### Reflection:  
1. Observa que el client necesita algunas dependencias para Spring Cloud, y el URI de el Spring Cloud server, pero sin codigo.
2. ¿Que sucede si el Config Server no esta disponible cuando la aplicación “lucky word” inicia?  Para mitigar esa posibilidad, es común ejecutar múltiples instancias de  config server en diferentes racks / zonas detras de un balanceador de carga.
3. ¿Qué sucede si nosotros cambiamos un property despues que la aplicación cliente ha iniciado?  El server toma los cambios inmediatamente, pero, el cliente no.  Más adelante veremos como Spring Cloud Bus y refresh scope puede ser usado para propagar los cambios dinamicamente.
