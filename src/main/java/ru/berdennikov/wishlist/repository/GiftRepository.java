package ru.berdennikov.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berdennikov.wishlist.model.Gift;
import ru.berdennikov.wishlist.model.Importance;

import java.util.List;

/**
 * Репозиторий для работы с подарками.
 */
@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByImportance(Importance importance);
}
