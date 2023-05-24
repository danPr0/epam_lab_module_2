package com.epam.esm.config.app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


public class WebInit implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) {
        System.out.println("SDKFJSDKLFJLSDKFJSDFJKLSDJFL");
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();

        root.scan("com.epam.esm");
        sc.addListener(new ContextLoaderListener(root));

        //Dispatcher servlet set-up
        ServletRegistration.Dynamic appServlet =
                sc.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/");

        //Profile set-up
        sc.setInitParameter("spring.profiles.active", "prod");
    }
}