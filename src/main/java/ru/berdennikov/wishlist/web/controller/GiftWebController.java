package ru.berdennikov.wishlist.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.berdennikov.wishlist.exception.GiftNotFoundException;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;
import ru.berdennikov.wishlist.service.GiftService;

import java.util.List;

/**
 * Контроллер для работы с подарками через пользовательский интерфейс
 */
@Controller
@RequestMapping(GiftWebController.GIFT_WEB_URL)
public class GiftWebController {

    public static final String GIFT_WEB_URL = "/gifts";

    private static final String GIFT_FORM = "giftForm";
    private static final String GIFT_NOT_FOUND_FORM = "giftNotFound";
    private static final String WISHLIST_VIEW = "wishlist";
    private static final String WISHLIST_REDIRECT = "redirect:/gifts";

    private final GiftService giftService;

    @Autowired
    public GiftWebController(GiftService giftService) {
        this.giftService = giftService;
    }

    /**
     * Отображает все подарки из списка пожеланий
     * @param model модель для передачи данных в представление
     * @return список пожеланий
     */
    @GetMapping
    public String showAll(@RequestParam(required = false) Importance importance, Model model) {
        List<Gift> gifts;
        if (importance != null) {
            gifts = giftService.getByImportance(importance);
            model.addAttribute("selectedImportance", importance);
        } else {
            gifts = giftService.getAll();
        }
        model.addAttribute("gifts", gifts);
        return WISHLIST_VIEW;
    }

    /**
     * Возвращает форму создания подарка
     * @param model модель для передачи данных в представление
     * @return форма создания/редактирования
     */
    @GetMapping("/create")
    public String showCrete(Model model) {
        model.addAttribute("gift", new Gift());
        model.addAttribute("importanceList", Importance.values());
        return GIFT_FORM;
    }

    /**
     * Возвращает форму редактирования
     * @param id идентификатор подарка
     * @param model модель для передачи данных в представление
     * @return форма редактирования
     */
    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable long id, Model model) {
        model.addAttribute("gift", giftService.get(id));
        return GIFT_FORM;
    }

    /**
     * Создает новый подарок или обновляет уже существующий
     * @param gift подарок
     * @param bindingResult валидация
     * @return перенаправляет на список пожеланий
     */
    @PostMapping("/createOrUpdate")
    public String createOrUpdate(@ModelAttribute @Valid Gift gift, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return GIFT_FORM;
        }
        if (gift.getId() == null) {
            giftService.save(gift);
        } else {
            giftService.update(gift);
        }
        return WISHLIST_REDIRECT;
    }

    /**
     * Удаляет подарок по идентификатору
     * @param id идентификатор подарка
     * @return перенаправляет на список пожеланий
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        giftService.delete(id);
        return WISHLIST_REDIRECT;
    }

    @ExceptionHandler(GiftNotFoundException.class)
    public String handleNotFound(GiftNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return GIFT_NOT_FOUND_FORM;
    }
}
