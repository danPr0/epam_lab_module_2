package com.epam.esm;

import com.epam.esm.config.app.EmbeddedTomcat;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
@EnableWebMvc
public class AppLauncher {

    public static void main(String[] ars) throws Exception {
//        System.out.println(SpringVersion.getVersion());
        EmbeddedTomcat.start();
    }
}