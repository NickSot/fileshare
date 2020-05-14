package org.tues.fileshare.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.tues.fileshare.Entity.File;
import org.tues.fileshare.Entity.User;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {
    @Query(value = "select * from file f where STRCMP(f.filename, ?1) = 0 and STRCMP(f.file_path, ?2) = 0", nativeQuery = true)
    public <S extends File> S findByNameAndPath(String filename, String path);
}
