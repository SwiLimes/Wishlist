package ru.berdennikov.wishlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Подарок
 */
@Entity
@Table(name = "gift")
public class Gift {

    /**
     *  Уникальный идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *  Название
     */
    @NotBlank(message = "{gift.notEmpty}")
    @Size(min = 5, max = 150, message = "{gift.size}")
    private String title;

    /**
     * Описание
     */
    private String description;

    /**
     * Важность подарка
     */
    @Enumerated(EnumType.STRING)
    private Importance importance;

    public Gift() {
    }

    public Gift(String title, String description, Importance importance) {
        this(null, title, description, importance);
    }

    public Gift(Long id, String title, String description, Importance importance) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.importance = importance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }


    @Override
    public String toString() {
        return "Gift{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", importance=" + importance +
                '}';
    }
}
