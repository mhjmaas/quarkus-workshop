package nl.terra10.api.model;

import java.util.Objects;

public class Todo {
    private int id;
    private String option;

    public Todo() {

    }

    public Todo(int id, String option) {
        this.id = id;
        this.option = option;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOption() {
        return this.option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Todo)) {
            return false;
        }
        Todo other = (Todo) obj;
        return Objects.equals(other.id, this.id) && Objects.equals(other.option, this.option);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.option);
    }
}
