
package com.studentexchange;

import com.studentexchange.repository.PostRepository;

public class PostTest {

    public static void main(String[] args) {

        PostRepository repository = new PostRepository();

        boolean created = repository.createPost(
                1,
                1,
                "How to prepare for the Java exam?",
                "What topics should I focus on for the Java programming exam?",
                "QUESTION"
        );

        System.out.println(
                created ? "Post created successfully!"
                        : "Post creation failed."
        );

        repository.displayActivePosts();
    }
}
