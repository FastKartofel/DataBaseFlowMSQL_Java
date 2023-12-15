package org.example.DTOs;

public abstract class BaseDTO {

    private int _id;

    protected BaseDTO() {}

    protected BaseDTO(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public boolean hasExistingId() {
        return getId() > 0;
    }
}
