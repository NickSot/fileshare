package org.tues.fileshare.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tues.fileshare.Entity.File;
import org.tues.fileshare.Repository.FileRepository;

import java.util.Optional;

@Service
public class FileService implements IFileService{
    @Autowired
    private FileRepository fileRepository;

    @Override
    public <S extends File> S save(S s) {
        return fileRepository.save(s);
    }

    @Override
    public <S extends File> Iterable<S> saveAll(Iterable<S> iterable) {
        return fileRepository.saveAll(iterable);
    }

    @Override
    public Optional<File> findById(Long aLong) {
        return fileRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return fileRepository.existsById(aLong);
    }

    @Override
    public Iterable<File> findAll() {
        return fileRepository.findAll();
    }

    @Override
    public Iterable<File> findAllById(Iterable<Long> iterable) {
        return fileRepository.findAllById(iterable);
    }

    @Override
    public long count() {
        return fileRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        fileRepository.deleteById(aLong);
    }

    @Override
    public void delete(File file) {
        fileRepository.delete(file);
    }

    @Override
    public void deleteAll(Iterable<? extends File> iterable) {
        fileRepository.deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        fileRepository.deleteAll();
    }
}
