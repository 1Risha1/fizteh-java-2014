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
        ApplicationContext context = SpringApplication.run(AppConfig.class, args);
        State st = context.getBean(StoreableState.class);
        int status = ShellImpl.start(args, st);
        System.exit(status);
    }

}

