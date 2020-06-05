package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.entity.Comment;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceJDBCTest {

    private final CommentService commentService = new CommentServiceJDBC();
    private final String gameName = "COMMENT_TEST";
    private final String playerName = "MAC";
    private final String comment = "NICE";

    @Test
    void addComment() throws CommentException {
        commentService.addComment(new Comment(playerName, gameName, comment, new java.util.Date()));

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        String commentString =
            "Comment{player='" + playerName +
            "', game='" + gameName +
            "', comment='" + comment +
            "', commentedOn=" + formattedDate +
            " 00:00:00.0}";

        List<Comment> comments = commentService.getComments(gameName);
        String dbString = comments.get(comments.size() - 1).toString();

        assertEquals(commentString, dbString);
    }

}