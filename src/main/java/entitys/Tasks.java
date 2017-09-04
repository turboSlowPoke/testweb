package entitys;

import org.apache.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tasks implements Serializable {
    @Transient
    private static final Logger log = Logger.getLogger(Tasks.class);
    @Id @GeneratedValue
    private long id;
    private String type;
    private String status;
    @ManyToMany(fetch = FetchType.EAGER) @Size(max = 2)
    private List<User> users;
    private LocalDateTime dateTimeOpening;
    private LocalDateTime dateTimeEnding;

     Tasks() {
    }

    public Tasks(String type, User client) {
        this.type = type;
        this.status = TaskStatus.OPEN;
        this.users = new ArrayList<>();
        this.users.add(client);
        this.dateTimeOpening = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public User getClient() {
        return users ==null?null: users.get(0);
    }

    public User getMeneger() {
        return users==null||users.size()!=2?null:users.get(1);
    }

    public LocalDateTime getDateTimeOpening() {
        return dateTimeOpening;
    }

    public LocalDateTime getDateTimeEnding() {
        return dateTimeEnding;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMeneger(User meneger) {
         if (this.users!=null||users.size()==1)
             this.users.add(meneger);
         else
             log.error("Не смог добавить менеджера");
    }

    public void setDateTimeEnding(LocalDateTime dateTimeEnding) {
        this.dateTimeEnding = dateTimeEnding;
    }
}
