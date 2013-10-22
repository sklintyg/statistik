package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hsa")
public class HSAStore {

    @Id
    private String id;

    private String data;

    public HSAStore() {
    }

    public HSAStore(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id == null) {
            this.id = id;
        } else if (!this.id.equals(id)) {
            throw new IllegalStateException("Can not change id from " + this.id + " to " + id);
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
