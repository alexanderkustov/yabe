import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import java.util.List;

public class BasicTest extends UnitTest {

    @Before
    public void setup(){

      Fixtures.deleteDatabase();

    }


@Test
public void fullTest() {
    Fixtures.loadModels("data.yml");

    // Count things
    assertEquals(2, User.count());
    assertEquals(3, Post.count());
    assertEquals(3, Comment.count());

    // Try to connect as users
    assertNotNull(User.connect("bob@gmail.com", "secret"));
    assertNotNull(User.connect("jeff@gmail.com", "secret"));
    assertNull(User.connect("jeff@gmail.com", "badpassword"));
    assertNull(User.connect("tom@gmail.com", "secret"));

    // Find all of Bob's posts
    List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
    assertEquals(2, bobPosts.size());

    // Find all comments related to Bob's posts
    List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
    assertEquals(3, bobComments.size());

    // Find the most recent post
    Post frontPost = Post.find("order by postedAt desc").first();
    assertNotNull(frontPost);
    assertEquals("About the model layer", frontPost.title);

    // Check that this post has two comments
    assertEquals(2, frontPost.comments.size());

    // Post a new comment
    frontPost.addComment("Jim", "Hello guys");
    assertEquals(3, frontPost.comments.size());
    assertEquals(4, Comment.count());
}

@Test
public void postComments() {
    // Create a new user and save it
    User alex = new User("alex@gmail.com", "secret", "alex").save();

    // Create a new post
    Post alexPost = new Post(alex, "My first post", "Hello world").save();

    // Post a first comment
    new Comment(alexPost, "Jeff", "Nice post").save();
    new Comment(alexPost, "Tom", "I knew that !").save();

    // Retrieve all comments
    List<Comment> alexPostComments = Comment.find("byPost", alexPost).fetch();

    // Tests
    assertEquals(2, alexPostComments.size());

    Comment firstComment = alexPostComments.get(0);
    assertNotNull(firstComment);
    assertEquals("Jeff", firstComment.author);
    assertEquals("Nice post", firstComment.content);
    assertNotNull(firstComment.postedAt);

    Comment secondComment = alexPostComments.get(1);
    assertNotNull(secondComment);
    assertEquals("Tom", secondComment.author);
    assertEquals("I knew that !", secondComment.content);
    assertNotNull(secondComment.postedAt);
}


    @Test
    public void createPost(){

      //criar novo utilziador
      User alex = new User("alex@gmail.com", "passe", "Alex").save();

      //cria novo post
      new Post(alex, "Primeiro topico", "Ola Mundo").save();

      //testar
      assertEquals(1, Post.count());

      //listar todos posts do alex
      List<Post> alexPosts = Post.find("byAuthor",alex).fetch();

      //asserts
      assertEquals(1, alexPosts.size());
      Post firstPost = alexPosts.get(0);
      assertNotNull(firstPost);
      assertEquals(alex, firstPost.author);
      assertEquals("Primeiro topico", firstPost.title);
      assertEquals("Ola Mundo", firstPost.content);
      assertNotNull(firstPost.postedAt);


    }


    @Test
    public void createAndRetrieveUser() {
        // Criar novo user e  grava lo
      new User("alex@gmail.com", "passe", "Alex").save();

      //encontrar utilizador
      User alex = User.find("byEmail", "alex@gmail.com").first();

      //Testar
      assertNotNull(alex);
      assertEquals("Alex", alex.fullname);
    }



@Test
public void tryConnectUser(){
  //criar novo utilizador e grava
  new User("alex@gmail.com", "passe", "Alex").save();

  //testes
    assertNotNull(User.connect("alex@gmail.com","passe"));
    assertNull(User.connect("alex@gmail.com","passeErrada"));
    assertNull(User.connect("joao@gmail.com","passe"));
}

@Test
public void useTheCommentsRelation() {
    // Create a new user and save it
    User alex = new User("alex@gmail.com", "secret", "alex").save();

    // Create a new post
    Post alexPost = new Post(alex, "My first post", "Hello world").save();

    // Post a first comment
    alexPost.addComment("Jeff", "Nice post");
    alexPost.addComment("Tom", "I knew that !");

    // Count things
    assertEquals(1, User.count());
    assertEquals(1, Post.count());
    assertEquals(2, Comment.count());

    // Retrieve alex's post
    alexPost = Post.find("byAuthor", alex).first();
    assertNotNull(alexPost);

    // Navigate to comments
    assertEquals(2, alexPost.comments.size());
    assertEquals("Jeff", alexPost.comments.get(0).author);

    // Delete the post
    alexPost.delete();

    // Check that all comments have been deleted
    assertEquals(1, User.count());
    assertEquals(0, Post.count());
    assertEquals(0, Comment.count());
}


}
