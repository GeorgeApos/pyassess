package gr.uom.Service.Based.Assesment.model;


import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "comment", columnDefinition = "TEXT")
    String content;

    public Comment(String content) {
        this.content = content;
    }

    public Comment() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String comment) {
        this.content = comment;
    }
}
