package me.a8kj.reply.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    protected final Map<ID, T> storage = new HashMap<>();

    @Override
    public T create(T entity) {
        ID id = getId(entity);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> read(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public T update(T entity) {
        ID id = getId(entity);
        if (storage.containsKey(id)) {
            storage.put(id, entity);
            return entity;
        }
        throw new NoSuchElementException("Entity with ID " + id + " not found.");
    }

    @Override
    public boolean delete(ID id) {
        return storage.remove(id) != null;
    }

    protected abstract ID getId(T entity);
}
