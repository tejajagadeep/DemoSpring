import com.eventapp.AuthenticationService.Filter.JwtUtils;
import com.eventapp.AuthenticationService.Kafka.ConsumeService;
import com.eventapp.AuthenticationService.Model.UserDetails;
import com.eventapp.AuthenticationService.Service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth/v1")
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController
{
    private Map<String,String> mapObj = new HashMap<String,String>();

    @Autowired
    ConsumeService consumeService;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    public String generateToken(String username, String password) throws ServletException
    {
        String jwtToken;

        if(username==null || password == null)
        {
            throw new ServletException("Please enter valid username and password");
        }

        boolean flag= userService.loginUser(username, password);
        String role=userService.getRoleByUserAndPass(username,password);
        System.out.println(role+"--from dbb-- inside token"+username);

        if(!flag)
        {
            throw new ServletException("Invalid credentials");

        }
        else
        {
            System.out.println(role+"--last---");

            jwtToken=Jwts.builder().setSubject(username).claim("role",role)
                    .setIssuedAt(new Date(System.currentTimeMillis())).
                    setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                    .signWith(SignatureAlgorithm.HS256,"secret key").compact();

        }

        System.out.println(role+"--last---");
        System.out.println(jwtToken+"------token");
        return jwtToken;
    }

    @PostMapping("/login")
    public ResponseEntity<?> performLogin(@RequestBody UserDetails user)
    {
        System.out.println(user.getUsername()+"----");
        boolean check=userService.loginUser(user.getUsername(),user.getPassword());

        if(check){
            String role=userService.getRoleByUserAndPass(user.getUsername(), user.getPassword());
            System.out.println(role);
            try
            {
                String jwtToken = generateToken(user.getUsername(), user.getPassword());
                System.out.println(jwtToken+"inside login");
                if(role.equalsIgnoreCase("admin"))
                {
                    mapObj.put("message", "Admin successfully logged in");
                    mapObj.put("jwtToken",jwtToken);
                    mapObj.put("role",role);
                    return new ResponseEntity<>(mapObj, HttpStatus.CREATED);

                }
                else if(role.equalsIgnoreCase("User"))
                {

                    mapObj.put("message", "User successfully logged in");
                    mapObj.put("jwtToken", jwtToken);
                    mapObj.put("role",role);
                    return new ResponseEntity<>(mapObj, HttpStatus.CREATED);
                }

            }

            catch( ServletException e)
            {
                System.out.println(e+"exception");
                mapObj.put("message", "User not logged in!");
                mapObj.put("jwtToken", null);
                mapObj.put("user not found",null);
            }

        }
        return new ResponseEntity<>(mapObj, HttpStatus.OK);

    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String token) {
        System.out.println(token+"---------");
        if (jwtUtils.validateJwtToken(token)) {
            // return ResponseEntity.ok("Valid token");
            System.out.println("-------validate-------");
            Map<String, String> userInfo = new HashMap<>();
            String authToken = token.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(authToken);
            String role = jwtUtils.getRoleFromToken(authToken);
            System.out.println(username+"-------***********-------");
            System.out.println(role+"-------***********-------");
            userInfo.put(username, role);
            System.out.println(userInfo.get(username)+"++++++");
            return ResponseEntity.status(HttpStatus.OK).body(userInfo);
        } else {
            System.out.println("-------***********-------");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
