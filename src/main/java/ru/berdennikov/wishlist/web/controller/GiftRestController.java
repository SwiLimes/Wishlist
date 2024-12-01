package ru.berdennikov.wishlist.web.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.berdennikov.wishlist.exception.GiftNotFoundException;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;
import ru.berdennikov.wishlist.service.GiftService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = GiftRestController.REST_GIFT_URL)
public class GiftRestController {

    public static final String REST_GIFT_URL = "/api/gifts";

    private static final Logger log = LoggerFactory.getLogger(GiftRestController.class);

    private final GiftService giftService;

    @Autowired
    public GiftRestController(GiftService giftService) {
        this.giftService = giftService;
    }

    /**
     * Возвращает список подарков отфильтрованных по важности. Если важность не указана, то возвращает все подарки
     *
     * @param importance важность
     * @return список подарков в формате JSON. 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Gift>> getAll(@RequestParam(required = false) Importance importance) {
        List<Gift> gifts;
        if (importance != null) {
            log.info("Get gifts filtered by importance {}", importance.name());
            gifts = giftService.getByImportance(importance);
        } else {
            log.info("Get all gifts");
            gifts = giftService.getAll();
        }
        return ResponseEntity.ok().body(gifts);
    }

    /**
     * Возвращает подарок по ID
     *
     * @param id идентификатор подарка
     * @return подарок в формате JSON. 200 OK если подарок существует. 404 если не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<Gift> get(@PathVariable long id) {
        try {
            log.info("Get gift with id {}", id);
            Gift gift = giftService.get(id);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(gift);
        } catch (GiftNotFoundException exception) {
            log.error("Get gift with id {} error: {}", id, exception.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Создает новый подарок
     *
     * @param gift данные подарка в формате JSON
     * @return созданный подарок в формате JSON. 201 Created. 400 Bad Request при ошибке валидации
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Gift> create(@RequestBody @Valid Gift gift) {
        log.info("Create gift {}", gift);
        Gift saved = giftService.save(gift);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_GIFT_URL + "/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    /**
     * Обновляет подарок
     *
     * @param gift данные подарка в формате JSON
     * @param id   идентификатор подарка
     * @return обновленный подарок в формате JSON. 200 OK. 400 Bad Request при ошибке валидации. 404 Not Found если не найден
     */
    @PutMapping("/{id}")
    public ResponseEntity<Gift> update(@RequestBody @Valid Gift gift, @PathVariable long id) {
        try {
            log.info("Update gift with id {}", id);
            Gift updated = giftService.update(gift);
            return ResponseEntity.ok().body(updated);
        } catch (GiftNotFoundException exception) {
            log.error("Update gift with id {} error: {}", id, exception.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет подарок
     *
     * @param id идентификатор подарка
     * @return 204 No Content если подарок удален. 404 Not Found если не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        try {
            log.info("Delete gift with id {}", id);
            giftService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (GiftNotFoundException exception) {
            log.error("Delete gift with id {} error: {}", id, exception.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
