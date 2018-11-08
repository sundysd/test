package challenge;

import challenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public ResponseEntity createUsers(@RequestBody UserRequest request) {
        String sql = "SELECT userid from user where username = '" + request.getUsername() + "'";

        List<Integer> certs = this.jdbcTemplate.queryForList(sql, Integer.class);
        if (certs.isEmpty()) {
            int id = insertUser(request.getUsername(), request.getPassword());
            UserResponse user = new UserResponse();
            user.setId(new Long(id));
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User already exists.");
        }
    }

    @Transactional
    public int insertUser(String username, String password) {
        jdbcTemplate.update("INSERT INTO user(username, password) VALUES(?,?)", username, password);
        int id = jdbcTemplate.queryForObject("select last_insert_rowid();", Integer.class);
        return id;
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public ResponseEntity login(@RequestBody UserRequest request) {
        String sql = "SELECT userid from user where username = '" + request.getUsername()
                + "' and password = '" + request.getPassword() + "'"  ;

        List<Integer> certs = this.jdbcTemplate.queryForList(sql, Integer.class);
        if (!certs.isEmpty()) {
            int id = insertUser(request.getUsername(), request.getPassword());
            UserResponse user = new UserResponse();
            user.setId(new Long(id));
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User not exists.");
        }
    }

    @RequestMapping(value = "/messages", method = { RequestMethod.POST })
    public ResponseEntity sendMessage(@RequestBody MessageRequest request) {
        Content msgToSave = request.getContent();
        int id = 0;
        try{
            if (msgToSave instanceof Text) {
                id = insertMessage(request.getSender(), request.getRecipient(), (Text) msgToSave);
            } else if (msgToSave instanceof Image) {
                id = insertMessage(request.getSender(), request.getRecipient(), (Image) msgToSave);
            } else if (msgToSave instanceof Video) {
                id = insertMessage(request.getSender(), request.getRecipient(), (Video) msgToSave);
            } else {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Bad message type in Content type, only support text, video, image.");
            }
            String sql = "select messageId, timestamp from message where messageId = " + id;
            List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sql);

            MessageResponse msg = new MessageResponse();
            rows.stream().forEach((row) -> {
                msg.setId(new Long((int) row.get("messageId")));

                Calendar t = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss",Locale.getDefault());
                Date dt = null; //replace 4 with the column index
                try {
                    dt = sdf.parse((String) row.get("timestamp"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                t.setTime(dt);

                msg.setTimestamp(dt);
            });

            System.out.println("user "+ request.getSender() + " sends message to " + request.getRecipient());
            //TODO add other logic here to really send message from sender to recipient
            return ResponseEntity.ok(msg);
        }catch(Exception e) {
            System.out.println(e.getStackTrace());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error while sending messages.");
        }

    }

    @Transactional
    public int insertMessage(long sender, long recipient, Text text) {
        jdbcTemplate.update("INSERT INTO message(sender, recipient,type, text) VALUES(?,?,?,?)",
                sender, recipient, "text", text.getText());
        int id = jdbcTemplate.queryForObject("select last_insert_rowid();", Integer.class);
        return id;
    }
    @Transactional
    public int insertMessage(long sender, long recipient, Image image) {
        jdbcTemplate.update("INSERT INTO message(sender, recipient,type, url, height, width) VALUES(?,?,?,?)",
                sender, recipient, "image", image.getUrl(), image.getHeight(), image.getWidth());
        int id = jdbcTemplate.queryForObject("select last_insert_rowid();", Integer.class);
        return id;
    }
    @Transactional
    public int insertMessage(long sender, long recipient, Video video) {
        jdbcTemplate.update("INSERT INTO message(sender, recipient,type, url, source) VALUES(?,?,?,?,?)",
                sender, recipient, "video", video.getUrl(), video.getSource());
        int id = jdbcTemplate.queryForObject("select last_insert_rowid();", Integer.class);
        return id;
    }

    @RequestMapping(value = "/messages", method = { RequestMethod.GET },
            produces = { "application/json" },
            consumes = { "application/json" })
    public ResponseEntity getMessages(@RequestParam("recipient") int recipient,
                            @RequestParam("start") int start,
                            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        System.out.println("finding message with recipient "+ recipient + " starting from message " + start + " limit to " + limit + " messages");
        String sql = "select * from message where recipient = " + recipient + " and messageId >= " + start + " order by messageid ";

        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sql);

        List<Message> list = new ArrayList();
        rows.stream().forEach((row) -> {
            Message msg = new Message();
            msg.setId(new Long((int) row.get("messageId")));
            msg.setRecipient(new Long((int) row.get("recipient")));
            msg.setRecipient(new Long((int) row.get("sender")));

            Calendar t = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss",Locale.getDefault());
            Date dt = null; //replace 4 with the column index
            try {
                dt = sdf.parse((String) row.get("timestamp"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            t.setTime(dt);

            msg.setTimestamp(dt);
            String type = (String) row.get("type");
            Content content = null;
            if ("text".equalsIgnoreCase(type)) {
                content  = new Text((String) row.get("text"));
            } else if ("image".equalsIgnoreCase(type)) {
                content  = new Image((String) row.get("url"), (int) row.get("height"), (int) row.get("width"));
            } else if ("video".equalsIgnoreCase(type)) {
                content = new Video((String) row.get("url"), (String) row.get("source"));
            }
            msg.setContent(content);
            list.add(msg);
        });
        return ResponseEntity.ok(list);

    }

}
