package challenge;

import challenge.model.Content;
import challenge.model.MessageDBObj;
import challenge.model.MessageRequest;
import challenge.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value = "/check", method = { RequestMethod.POST },
            produces = { "application/json" },
            consumes = { "application/json" })
    public Map<String, String> check() {
        int result = this.jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        if (result != 1) {
            throw new RuntimeException("Unexpected query result");
        }
        return Collections.singletonMap("health", "ok");
    }

    @RequestMapping(value = "/users", method = { RequestMethod.POST },
            produces = { "application/json" },
            consumes = { "application/json" })
    public ResponseEntity createUsers(@RequestBody UserRequest request) {
        int userid = this.jdbcTemplate.queryForObject("SELECT userid from users where username = '" + request.getUsername() + "'", Integer.class);
        if (userid >=0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists.");
        insertUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User created.");
    }

    @Transactional
    private int insertUser(String username, String password) {
        return jdbcTemplate.update("INSERT INTO users(username, password) VALUES(?,?)", username, password);
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST },
            produces = { "application/json" },
            consumes = { "application/json" })
    public void login() {
    }

    @RequestMapping(value = "/messages", method = { RequestMethod.POST },
            produces = { "application/json" },
            consumes = { "application/json" })
    public ResponseEntity sendMessage(@RequestBody MessageRequest request) {
        System.out.println("user "+ request.getSender() + " sends message to " + request.getRecipient());
        insertMessage(request.getSender(), request.getRecipient(), request.getContent());
        return ResponseEntity.ok("message sent.");
    }

    @Transactional
    private int insertMessage(long sender, long recipient, Content content) {
        return jdbcTemplate.update("INSERT INTO messages(sender, recipient,type, timestamp) VALUES(?,?,?)", sender, recipient, content.getType(),new java.util.Date());
    }
/*
    @RequestMapping(value = "/messages", method = { RequestMethod.GET },
            produces = { "application/json" },
            consumes = { "application/json" })
    public void getMessages(@RequestParam("recipient") int recipient,
                            @RequestParam("start") int start,
                            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        System.out.println("finding message with recipient "+ recipient + " starting from message " + start + " limit to " + limit + " messages");
        List<MessageDBObj> data = this.jdbcTemplate.query("select top "+ limit +" * from messages where recipient = " + recipient + " and id >= " + start + " order by id ", MessageDBObj.class);
    }*/

}
