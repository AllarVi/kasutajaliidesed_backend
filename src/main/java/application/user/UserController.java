package application.user;

import application.BaseController;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by allarviinamae on 15/04/16.
 * <p>
 * Rest api.
 */
@RestController
public class UserController extends BaseController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/api/user")
    public ResponseEntity<PaceUser> getUser(@RequestParam(value = "email", required = false) String email) {
        if (email != null) {
            return new ResponseEntity<>(getUserFromDB(email), HttpStatus.OK);
        }
        return new ResponseEntity<>(new PaceUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public ResponseEntity<PaceUser> register(@RequestBody String updatedPaceUser) {
        if (updatedPaceUser != null) {
            PaceUser currentPaceUser = getCurrentPaceUserFromJson(updatedPaceUser);
            if (currentPaceUser != null) {
                return new ResponseEntity<>(new PaceUser(), HttpStatus.OK);
            }
            PaceUser returnedPaceUser = handlePaceUserSaving(updatedPaceUser, currentPaceUser);
            return new ResponseEntity<>(returnedPaceUser, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(new PaceUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/logout", method = RequestMethod.POST)
    public HttpStatus logout(@RequestBody String userEmail) {
        if (userEmail != null) {
            try {
                UserEmail userEmailAsString = mapFromJson(userEmail, UserEmail.class);
                PaceUser currentPaceUser = getCurrentlyLoggedInUser(userEmailAsString.getUserEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HttpStatus.OK;
        }

        return HttpStatus.OK;
    }

    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public ResponseEntity<PaceUser> login(@RequestBody String loginDataAsString) {
        if (loginDataAsString != null) {
            try {
                LoginData loginData = mapFromJson(loginDataAsString, LoginData.class);
                PaceUser currentPaceUser = getUserFromDB(loginData.getEmail());

                if (currentPaceUser != null) {
                    if (currentPaceUser.getPassword().equals(loginData.getPassword())) {
                        currentPaceUser.setAuthResponse("success");

                        PaceUser updatedPaceUser = userRepository.save(currentPaceUser);
                        return new ResponseEntity<>(updatedPaceUser, HttpStatus.OK);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(new PaceUser(), HttpStatus.OK);
    }

    private PaceUser handlePaceUserSaving(@RequestBody String updatedPaceUser, PaceUser currentPaceUser) {
        PaceUser updatedPaceUserAsObject = getUpdatedPaceUserAsObject(updatedPaceUser);

        if (currentPaceUser != null) {
            updatedPaceUserAsObject.setId(currentPaceUser.getId());
        }

        updatedPaceUserAsObject.setRole("student");
        return userRepository.save(updatedPaceUserAsObject);
    }

    private PaceUser getUpdatedPaceUserAsObject(@RequestBody String updatedPaceUser) {
        PaceUser updatedPaceUserObject = null;
        try {
            updatedPaceUserObject = mapFromJson(updatedPaceUser, PaceUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updatedPaceUserObject;
    }

    private PaceUser getCurrentPaceUserFromJson(@RequestBody String updatedPaceUser) {
        PaceUser currentPaceUser = null;
        try {
            String email = mapFromJson(updatedPaceUser, PaceUser.class).getEmail();
            currentPaceUser = userRepository.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPaceUser;
    }

    private PaceUser getCurrentlyLoggedInUser(String userEmail) {
        PaceUser currentPaceUser = userRepository.findByEmail(userEmail);

        currentPaceUser.setAuthResponse("unknown");

        PaceUser updatedPaceUser = userRepository.save(currentPaceUser);

        return updatedPaceUser;
    }

    @RequestMapping("/api/users")
    public Iterable<PaceUser> users() {
        return userRepository.findAll();
    }

    private PaceUser getUserFromDB(@RequestParam(value = "email") String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException,
            IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

}

