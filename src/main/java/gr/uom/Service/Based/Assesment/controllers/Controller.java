package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class Controller {

    @Autowired
    private Service appService;

    @RequestMapping("/")
    public void handleSimpleRequest() throws IOException, ExecutionException, InterruptedException {
        appService.runCommand();
    }
}
