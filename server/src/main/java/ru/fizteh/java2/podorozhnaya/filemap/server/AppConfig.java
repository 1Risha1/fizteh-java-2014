package ru.fizteh.java2.podorozhnaya.filemap.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.ShellImpl;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

@Configuration
@EnableAutoConfiguration
@ComponentScan("ru.fizteh.java2.podorozhnaya.filemap")

@PropertySources({
        @PropertySource(
                value = "file:local.properties",
                ignoreResourceNotFound = true
        )
})
public class AppConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer placeholder() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    public static void main(String[] args) {
        int status = SpringApplication.run(AppConfig.class, args)
                .getBean(ShellImpl.class).start(args);
        System.exit(status);
    }

}

