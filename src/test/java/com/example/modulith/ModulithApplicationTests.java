package com.example.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ModulithApplicationTests {

    @Test
    void verifiesModularStructure() {
        // Verify that the application follows modulith principles
        ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

        // This will fail if there are violations of module boundaries
        modules.verify();

        System.out.println("=== Application Modules ===");
       // modules.forEach(System.out::println);
        modules.forEach(module -> {
            System.out.println("Module: " + module.getName());
            System.out.println("  Display Name: " + module.getDisplayName());
            System.out.println("  Base Package: " + module.getBasePackage());
        });
    }

    /**
     * Generera dokumentation.
     * @See https://docs.spring.io/spring-modulith/reference/documentation.html
     */
    @Test
    void generatesModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

        // Generate documentation (creates diagrams and documentation using Documenter)
        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();

        System.out.println("Documentation generated in target/modulith-docs");
    }
}
