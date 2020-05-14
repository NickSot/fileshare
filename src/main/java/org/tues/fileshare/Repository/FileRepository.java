package org.tues.fileshare.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.tues.fileshare.Entity.File;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {

}
