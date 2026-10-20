
package com.studentexchange;

import com.studentexchange.repository.ResourceRepository;

public class ResourceTest {

    public static void main(String[] args) {
        ResourceRepository repository = new ResourceRepository();

        boolean added = repository.addResource(
                1,
                1,
                "Java Programming Notes",
                "Revision notes covering Java fundamentals and OOP.",
                "NOTES",
                "https://docs.oracle.com/en/java/"
        );

        System.out.println(
                added ? "Resource added successfully!" : "Resource could not be added."
        );

        repository.displayActiveResources();
    }
}
