package at.tectrain.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import at.tectrain.app.service.CalculatorService;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private CalculatorService service;

    CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public int add (@RequestParam int a, @RequestParam int b)
    {
        return service.add(a, b);
    }

    @GetMapping("/subtract")
    public int subtract (@RequestParam int a, @RequestParam int b)
    {
        return service.subtract(a, b);
    }

    @GetMapping("/multiply")
    public int multiply (@RequestParam int a, @RequestParam int b)
    {
        return service.multiply(a, b);
    }

    @GetMapping("/div")
    public int div (@RequestParam int a, @RequestParam int b)
    {
        return service.div(a, b);
    }

}
