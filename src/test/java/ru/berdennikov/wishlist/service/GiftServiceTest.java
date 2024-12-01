package ru.berdennikov.wishlist.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.berdennikov.wishlist.exception.GiftNotFoundException;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;
import ru.berdennikov.wishlist.repository.GiftRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest {

    @Mock
    private GiftRepository giftRepository;

    @InjectMocks
    private GiftService giftService;

    @Test
    void findAll() {
        giftService.getAll();
        verify(giftRepository, times(1)).findAll();
    }

    @Test
    void findByImportance() {
        Gift gift1 = new Gift();
        gift1.setImportance(Importance.MEDIUM);
        Gift gift2 = new Gift();
        gift2.setImportance(Importance.HIGH);
        Gift gift3 = new Gift();
        gift1.setImportance(Importance.HIGH);
        List<Gift> gifts = List.of(gift2, gift3);
        when(giftRepository.findByImportance(Importance.HIGH)).thenReturn(gifts);

        List<Gift> filtered = giftService.getByImportance(Importance.HIGH);
        assertEquals(gifts, filtered);
        verify(giftRepository, times(1)).findByImportance(Importance.HIGH);
    }

    @Test
    void findById_giftExists() {
        Gift gift = new Gift();
        gift.setId(1L);
        gift.setTitle("Mock gift");
        when(giftRepository.findById(1L)).thenReturn(Optional.of(gift));

        Gift result = giftService.get(1L);

        assertEquals(gift, result);
        verify(giftRepository, times(1)).findById(1L);
    }

    @Test
    void findById_giftDoesNotExist() {
        when(giftRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GiftNotFoundException.class, () -> giftService.get(1L));
        verify(giftRepository, times(1)).findById(1L);
    }

    @Test
    void save_valid() {
        Gift created = new Gift();
        created.setTitle("Mock created");
        when(giftRepository.save(created)).thenReturn(created);

        Gift saved = giftService.save(created);

        assertEquals(created, saved);
        verify(giftRepository, times(1)).save(created);
    }

    @Test
    void save_invalid() {
        assertThrows(IllegalArgumentException.class, () -> giftService.save(null));
        verify(giftRepository, never()).save(null);
    }

    @Test
    void update_valid() {
        Long id = 1L;
        Gift gift = new Gift();
        gift.setId(id);
        gift.setTitle("Updated gift");
        when(giftRepository.findById(gift.getId())).thenReturn(Optional.of(gift));

        Gift testGift = giftService.update(gift);

        assertEquals(gift, testGift);
        assertEquals(gift.getTitle(), testGift.getTitle());
        verify(giftRepository, times(1)).save(gift);
    }

    @Test
    void update_invalid() {
        assertThrows(IllegalArgumentException.class, () -> giftService.update(null));
        verify(giftRepository, never()).findById(1L);
        verify(giftRepository, never()).save(null);
    }


    @Test
    void delete() {
        when(giftRepository.existsById(1L)).thenReturn(true);

        giftService.delete(1L);

        verify(giftRepository, times(1)).existsById(1L);
        verify(giftRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_doesNotExist() {
        when(giftRepository.existsById(1L)).thenReturn(false);

        assertThrows(GiftNotFoundException.class, () -> giftService.delete(1L));
        verify(giftRepository, times(1)).existsById(1L);
        verify(giftRepository, never()).deleteById(1L);
    }
}