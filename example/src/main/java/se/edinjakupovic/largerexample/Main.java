package se.edinjakupovic.largerexample;


import se.edinjakupovic.ApplicationContext;
import se.edinjakupovic.Configuration;

@Configuration
public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = ApplicationContext.fromConfiguration(Config.class);
    }
}