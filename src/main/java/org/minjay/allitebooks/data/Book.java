package org.minjay.allitebooks.data;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Book {

    private Long id;
    private String name;
    private Date createdDate;

    public Book() {
    }

    public Book(String name) {
        this.name = name;
        this.createdDate = new Date();
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Temporal(TemporalType.DATE)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
