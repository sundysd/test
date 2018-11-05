package challenge;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value = "/check", method = { RequestMethod.POST })
    public Map<String, String> check() {
        int result = this.jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        if (result != 1) {
            throw new RuntimeException("Unexpected query result");
        }
        return Collections.singletonMap("health", "ok");
    }

    @RequestMapping(value = "/users", method = { RequestMethod.POST })
    public int createUsers() {
        return 0;
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public void login() {
    }

    @RequestMapping(value = "/messages", method = { RequestMethod.POST })
    public void sendMessage() {
    }

    @RequestMapping(value = "/messages", method = { RequestMethod.GET })
    public void getMessages() {
    }

}