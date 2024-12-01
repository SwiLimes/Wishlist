package ru.berdennikov.wishlist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.berdennikov.wishlist.exception.GiftNotFoundException;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;
import ru.berdennikov.wishlist.repository.GiftRepository;

import java.util.List;

/**
 * Сервис для работы с подарками
 */
@Service
public class GiftService {

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);

    private final GiftRepository repository;

    @Autowired
    public GiftService(GiftRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает список всех подарков
     *
     * @return список подарков
     */
    public List<Gift> getAll() {
        log.info("Get all");
        return repository.findAll();
    }

    /**
     * Возвращает подарок по идентификатору, если существует. Иначе выбрасывает исключение
     *
     * @param id идентификатор подарка
     * @return подарок, если существует
     * @throws GiftNotFoundException если подарок не найден
     */
    public Gift get(Long id) {
        log.info("Get with id {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new GiftNotFoundException(id));
    }

    /**
     * Возвращает список подарков, отфильтрованных по важности
     *
     * @param importance важность подарка
     * @return список подарков по важности
     */
    public List<Gift> getByImportance(Importance importance) {
        log.info("Get with importance {}", importance);
        return repository.findByImportance(importance);
    }

    /**
     * Сохраняет подарок
     *
     * @param gift подарок
     * @return сохраненный подарок
     */
    public Gift save(Gift gift) {
        Assert.notNull(gift, "Gift must not be null");
        log.info("Save gift {}", gift);
        return repository.save(gift);
    }

    /**
     * Обновляет подарок
     *
     * @param gift подарок
     * @throws GiftNotFoundException если подарок не найден
     */
    public Gift update(Gift gift) {
        Assert.notNull(gift, "Gift must not be null");
        log.info("Update gift {}", gift);
        Long id = gift.getId();
        Gift existing = get(id);
        if (existing == null) {
            throw new GiftNotFoundException(id);
        }

        existing.setTitle(gift.getTitle());
        existing.setDescription(gift.getDescription());
        existing.setImportance(gift.getImportance());
        repository.save(existing);
        return gift;
    }

    /**
     * Удаляет подарок
     *
     * @param id идентификатор подарка
     * @throws GiftNotFoundException если подарок не найден
     */
    public void delete(Long id) {
        log.info("Delete gift {}", id);
        if (!repository.existsById(id)) {
            throw new GiftNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
