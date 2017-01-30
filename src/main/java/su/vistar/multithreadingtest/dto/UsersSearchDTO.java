package su.vistar.multithreadingtest.dto;

import java.util.List;

public class UsersSearchDTO {

    private Integer count;
    private List<UserDTO> items;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<UserDTO> getItems() {
        return items;
    }

    public void setItems(List<UserDTO> items) {
        this.items = items;
    }
}
