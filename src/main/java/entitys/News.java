package entitys;


import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
public class News {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "datetime")
    private LocalDateTime dateTime;
    @Column(name = "header")
    private String header;
    @Lob
    @Column(name = "body",columnDefinition = "TEXT")
    private String body;
    @ElementCollection
    private List<String> imageNames;
    @Column(name = "countimages")
    private int countimages;
    @Column(name = "layoutMedia")
    private String layoutMedia = "right"; //left,top,bottom,center

    public News() {
    }

    public News(LocalDateTime dateTime, String header, String body, List<String> imageNames) {
        this.dateTime = dateTime;
        this.header = header;
        this.body = body;
        this.imageNames = imageNames;
        this.countimages = imageNames!=null?imageNames.size():0;
    }

    public String getId() {
        return id.toString();
    }
    public String getDateTime() {
        return dateTime.toLocalDate().toString();
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public List<String> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<String> imageNames) {
        this.imageNames = imageNames;
    }

    public int getCountimages() {
        return countimages;
    }

    public void setCountimages(int countimages) {
        this.countimages = countimages;
    }

    public String getLayoutMedia() {
        return layoutMedia;
    }

    public void setLayoutMedia(String layoutMedia) {
        this.layoutMedia = layoutMedia;
    }

    @Override
    public String toString() {
        return "\nheader: "+header
                +"\nbody: "+body
                +"\ncountimages="+countimages;
    }
}
