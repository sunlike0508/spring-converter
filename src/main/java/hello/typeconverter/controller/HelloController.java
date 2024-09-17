package hello.typeconverter.controller;


import hello.typeconverter.type.IpPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data");

        Integer intValue = Integer.parseInt(data);

        System.out.println(intValue);

        return "ok";
    }


    @GetMapping("/hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println(data);

        return "ok";
    }


    @GetMapping("/hello-v3")
    public String helloV2(@RequestParam IpPort ipPort) {
        log.info("ipPort: {}", ipPort);

        return "ok";
    }
}
