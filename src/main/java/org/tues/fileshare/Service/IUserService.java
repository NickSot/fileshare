package org.tues.fileshare.Service;
import org.tues.fileshare.Entity.User;
import java.util.Optional;

public interface IUserService {
    <S extends User> S save(S s);
    public <S extends User> S findByUsername(String username);
    <S extends User> Iterable<S> saveAll(Iterable<S> iterable);
    Optional<User> findById(Long aLong);
    boolean existsById(Long aLong);
    Iterable<User> findAll();
    Iterable<User> findAllById(Iterable<Long> iterable);
    long count();
    void deleteById(Long aLong);
    void delete(User user);
    void deleteAll(Iterable<? extends User> iterable);
    void deleteAll();
}
