package ru.berdennikov.wishlist.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.berdennikov.wishlist.exception.GiftNotFoundException;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;
import ru.berdennikov.wishlist.service.GiftService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.berdennikov.wishlist.web.controller.GiftRestController.REST_GIFT_URL;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GiftRestController.class)
@AutoConfigureMockMvc
class GiftRestControllerTest {

    private static final String GIFT_TITLE_1 = "Gift1";
    private static final String GIFT_TITLE_2 = "Gift2";

    private static final Gift withInvalidSizeTitle = new Gift(1L, "Gift", "Title shorter than 5 characters", Importance.LOW);
    private static final Gift withEmptyTitle = new Gift(1L, "", "Empty title", Importance.MEDIUM);
    private static final Gift updated = new Gift(1L, "Updated gift", "Updated description", Importance.HIGH);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GiftService giftService;

    private Gift gift1;
    private Gift gift2;

    @BeforeEach
    void setUp() {
        gift1 = new Gift(1L, GIFT_TITLE_1, "Description1", Importance.MEDIUM);
        gift2 = new Gift(2L, GIFT_TITLE_2, "Description2", Importance.HIGH);
    }

    @Test
    void getAll() throws Exception {
        List<Gift> gifts = List.of(gift1, gift2);
        when(giftService.getAll()).thenReturn(gifts);

        mockMvc.perform(get(REST_GIFT_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(GIFT_TITLE_1)))
                .andExpect(jsonPath("$[1].title", is(GIFT_TITLE_2)));

        verify(giftService, times(1)).getAll();
    }

    @Test
    void getFilteredByImportance() throws Exception {
        List<Gift> gifts = Collections.singletonList(gift1);
        when(giftService.getByImportance(Importance.MEDIUM)).thenReturn(gifts);

        mockMvc.perform(get(REST_GIFT_URL)
                        .param("importance", Importance.MEDIUM.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(GIFT_TITLE_1)));

        verify(giftService, times(1)).getByImportance(Importance.MEDIUM);
        verify(giftService, never()).getByImportance(Importance.HIGH);
    }

    @Test
    void getById() throws Exception {
        when(giftService.get(1L)).thenReturn(gift1);

        mockMvc.perform(get(REST_GIFT_URL + "/" + gift1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(GIFT_TITLE_1)));

        verify(giftService, times(1)).get(1L);
    }

    @Test
    void getById_notFound() throws Exception {
        when(giftService.get(1L)).thenThrow(GiftNotFoundException.class);

        mockMvc.perform(get(REST_GIFT_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(giftService, times(1)).get(1L);
    }

    @Test
    void create() throws Exception {
        Gift newGift = new Gift("New gift", "New description", Importance.HIGH);
        when(giftService.save(any(Gift.class))).thenReturn(newGift);

        mockMvc.perform(post(REST_GIFT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGift)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New gift")));

        verify(giftService, times(1)).save(any(Gift.class));
    }

    @Test
    void create_invalidTitleEmpty() throws Exception {
        mockMvc.perform(post(REST_GIFT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withEmptyTitle)))
                .andExpect(status().isBadRequest());

        verify(giftService, never()).save(any(Gift.class));
    }

    @Test
    void create_invalidTitleSize() throws Exception {
        mockMvc.perform(post(REST_GIFT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withInvalidSizeTitle)))
                .andExpect(status().isBadRequest());

        verify(giftService, never()).save(any(Gift.class));
    }

    @Test
    void update() throws Exception {
        when(giftService.update(any(Gift.class))).thenReturn(updated);

        mockMvc.perform(put(REST_GIFT_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated gift")));

        verify(giftService, times(1)).update(any(Gift.class));
    }

    @Test
    void update_invalidTitleEmpty() throws Exception {
        mockMvc.perform(put(REST_GIFT_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withEmptyTitle)))
                .andExpect(status().isBadRequest());

        verify(giftService, never()).update(any(Gift.class));
    }

    @Test
    void update_invalidTitleSize() throws Exception {
        mockMvc.perform(put(REST_GIFT_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withInvalidSizeTitle)))
                .andExpect(status().isBadRequest());

        verify(giftService, never()).update(any(Gift.class));
    }

    @Test
    void update_notFound() throws Exception {
        when(giftService.update(any(Gift.class))).thenThrow(GiftNotFoundException.class);

        mockMvc.perform(put(REST_GIFT_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        verify(giftService, times(1)).update(any(Gift.class));
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete(REST_GIFT_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(giftService, times(1)).delete(1L);
    }

    @Test
    void deleteById_notFound() throws Exception {
        doThrow(GiftNotFoundException.class).when(giftService).delete(1L);

        mockMvc.perform(delete(REST_GIFT_URL + "/1"))
                .andExpect(status().isNotFound());

        verify(giftService, times(1)).delete(1L);
    }
}