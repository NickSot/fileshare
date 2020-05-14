package org.tues.fileshare.Service;
import org.springframework.data.jpa.repository.Query;
import org.tues.fileshare.Entity.File;
import java.util.Optional;

public interface IFileService {
    <S extends File> S save(S s);
    <S extends File> Iterable<S> saveAll(Iterable<S> iterable);
    public <S extends File> S findByNameAndPath(String filename, String path);
    Optional<File> findById(Long aLong);
    boolean existsById(Long aLong);
    Iterable<File> findAll();
    Iterable<File> findAllById(Iterable<Long> iterable);
    long count();
    void deleteById(Long aLong);
    void delete(File file);
    void deleteAll(Iterable<? extends File> iterable);
    void deleteAll();
}
