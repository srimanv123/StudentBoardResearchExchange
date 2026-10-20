
package com.studentexchange;

import com.studentexchange.repository.CommentRepository;

public class CommentTest {

    public static void main(String[] args) {
        CommentRepository repository = new CommentRepository();

        boolean added = repository.addComment(
                1,
                1,
                null,
                "Focus on OOP concepts, inheritance, and exception handling."
        );

        System.out.println(
                added ? "Comment added successfully!" : "Comment could not be added."
        );

        repository.displayComments(1);
    }
}
