
package com.studentexchange;

import com.studentexchange.repository.BookmarkRepository;

public class BookmarkTest {

    public static void main(String[] args) {
        BookmarkRepository repository = new BookmarkRepository();

        boolean added = repository.addBookmark(1, 1);

        System.out.println(
                added ? "Bookmark added successfully!"
                      : "Bookmark could not be added. Check whether the resource exists or is already bookmarked."
        );

        repository.displayBookmarks(1);
    }
}
