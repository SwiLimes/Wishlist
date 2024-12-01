package ru.berdennikov.wishlist.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.berdennikov.wishlist.web.controller.GiftWebController.GIFT_FORM;
import static ru.berdennikov.wishlist.web.controller.GiftWebController.GIFT_NOT_FOUND_FORM;
import static ru.berdennikov.wishlist.web.controller.GiftWebController.GIFT_WEB_URL;
import static ru.berdennikov.wishlist.web.controller.GiftWebController.WISHLIST_REDIRECT;
import static ru.berdennikov.wishlist.web.controller.GiftWebController.WISHLIST_VIEW;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SqlGroup({
        @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS, scripts = "classpath:db/create_db.sql"),
        @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/populate_data.sql"),
        @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:db/clear_data.sql")
})
class GiftControllerTest {

    private static final String GIFTS_ATTRIBUTE = "gifts";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void showAll() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(WISHLIST_VIEW))
                .andExpect(model().attributeExists(GIFTS_ATTRIBUTE))
                .andExpect(model().attribute(GIFTS_ATTRIBUTE, hasSize(2)));
    }

    @Test
    void showFilteredByImportance() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL).param("importance", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(view().name(WISHLIST_VIEW))
                .andExpect(model().attributeExists(GIFTS_ATTRIBUTE))
                .andExpect(model().attribute(GIFTS_ATTRIBUTE, hasSize(1)));
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL + "/create"))
                .andExpect(status().isOk())
                .andExpect(view().name(GIFT_FORM));
    }

    @Test
    void add() throws Exception {
        mockMvc.perform(post(GIFT_WEB_URL + "/createOrUpdate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Gift500")
                        .param("description", "Description500")
                        .param("importance", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(WISHLIST_REDIRECT));
    }

    @Test
    void addWithErrors() throws Exception {
        mockMvc.perform(post(GIFT_WEB_URL + "/createOrUpdate"))
                .andExpect(status().isOk())
                .andExpect(view().name(GIFT_FORM))
                .andExpect(model().hasErrors());
    }

    @Test
    void showEditForm() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL + "/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(GIFT_FORM))
                .andExpect(model().attributeExists("gift"));
    }

    @Test
    void showEditNotFoundForm() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL + "/edit/1000"))
                .andExpect(status().isOk())
                .andExpect(view().name(GIFT_NOT_FOUND_FORM));
    }

    @Test
    void edit() throws Exception {
        mockMvc.perform(post(GIFT_WEB_URL + "/createOrUpdate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("title", "Gift500")
                        .param("description", "Description500")
                        .param("importance", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(WISHLIST_REDIRECT));
    }

    @Test
    void deleteNotFoundForm() throws Exception {
        mockMvc.perform(get(GIFT_WEB_URL + "/delete/1000"))
                .andExpect(status().isOk())
                .andExpect(view().name(GIFT_NOT_FOUND_FORM));
    }
}