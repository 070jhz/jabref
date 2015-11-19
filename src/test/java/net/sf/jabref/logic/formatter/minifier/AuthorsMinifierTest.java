package net.sf.jabref.logic.formatter.minifier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthorsMinifierTest {
    private AuthorsMinifier formatter;

    @Before
    public void setUp() {
        formatter = new AuthorsMinifier();
    }

    @After
    public void teardown() {
        formatter = null;
    }

    @Test
    public void minifyAuthorNames() {
        expectCorrect("Simon Harrer", "Simon Harrer");
        expectCorrect("Simon Harrer and others", "Simon Harrer and others");
        expectCorrect("Simon Harrer and J�rg Lenhard", "Simon Harrer and J�rg Lenhard");
        expectCorrect("Simon Harrer and J�rg Lenhard and Guido Wirtz", "Simon Harrer and others");
        expectCorrect("Simon Harrer and J�rg Lenhard and Guido Wirtz and others", "Simon Harrer and others");
    }

    @Test
    public void formatEmptyFields() {
        expectCorrect("", "");
        expectCorrect(null, null);
    }

    private void expectCorrect(String input, String expected) {
        Assert.assertEquals(expected, formatter.format(input));
    }
}