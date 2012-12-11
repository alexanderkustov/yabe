package models;

import play.data.validation.*;
import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class Comment extends Model {

    public String author;
    public Date postedAt;

    @Lob
    public String content;

    @ManyToOne
    public Post post;

    public Comment(Post post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }

public static void postComment(Long postId, @Required String author, @Required String content) {
    Post post = Post.findById(postId);
    if(validation.hasErrors()) {
        render("Application/show.html", post);
    }
    post.addComment(author, content);
    flash.success("Thanks for posting %s", author);
    show(postId);
}

}