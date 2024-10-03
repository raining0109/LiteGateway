package me.raining.gateway.dubbo;

import java.util.Date;

import me.raining.gateway.dubbo.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
@Component
public class Task implements CommandLineRunner {

    @Reference
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        String result = userService.sayHi("raining");
        System.out.println("Receive result ======> " + result);

        new Thread(()-> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println(new Date() + " Receive result ======> " + userService.sayHi("raining"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
