package gr.uom.Service.Based.Assesment.model;


import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "comment", columnDefinition = "TEXT")
    String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    public Comment() {

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
