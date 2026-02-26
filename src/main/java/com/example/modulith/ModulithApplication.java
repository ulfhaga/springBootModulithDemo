package com.example.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.modulith.core.ApplicationModules;

@Modulith
@SpringBootApplication
public class ModulithApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
        ModulInformation();
    }

    /***
     * Varje modul listas och modulens ingående Spring-komponenterna identifieras och respektive synlighet även återges.
     */
    private static void ModulInformation() {
        ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);
        System.out.println("=== Applikationens moduler ===");
        modules.forEach(System.out::println);
    }
}
