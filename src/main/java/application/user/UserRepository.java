package application.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<PaceUser, Long> {

    PaceUser findByEmail(String lastName);

}
