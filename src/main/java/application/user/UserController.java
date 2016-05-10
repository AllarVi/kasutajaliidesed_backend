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
            return new ResponseEntity<>(getPaceUser(email), HttpStatus.OK);
        }
        return new ResponseEntity<>(new PaceUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public ResponseEntity<PaceUser> saveNewPaceUser(@RequestBody String updatedPaceUser) {
        if (updatedPaceUser != null) {
            PaceUser currentPaceUser = getCurrentPaceUserFromJson(updatedPaceUser);
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

    private PaceUser handlePaceUserSaving(@RequestBody String updatedPaceUser, PaceUser currentPaceUser) {
        PaceUser updatedPaceUserAsObject = getUpdatedPaceUserAsObject(updatedPaceUser);

        if (currentPaceUser != null) {
            updatedPaceUserAsObject.setId(currentPaceUser.getId());
        }

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
            currentPaceUser = userRepository.findByEmail(mapFromJson(updatedPaceUser, PaceUser.class)
                    .getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPaceUser;
    }

    private PaceUser getCurrentlyLoggedInUser(@RequestBody String userEmail) {
        PaceUser currentPaceUser;
        currentPaceUser = userRepository.findByEmail(userEmail);

        currentPaceUser.setAuthResponse("unknown");

        PaceUser updatedPaceUser = userRepository.save(currentPaceUser);

        return updatedPaceUser;
    }

    @RequestMapping("/api/users")
    public Iterable<PaceUser> users() {
        return userRepository.findAll();
    }

    private PaceUser getPaceUser(@RequestParam(value = "email") String email) {
        return userRepository.findByEmail(email);
    }

    private <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException,
            IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

}

