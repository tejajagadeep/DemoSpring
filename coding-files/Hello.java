package com.stackroute.test.service;

import com.stackroute.domain.Blog;
import com.stackroute.exception.BlogAlreadyExistsException;
import com.stackroute.exception.BlogNotFoundException;
import com.stackroute.repository.BlogRepository;
import com.stackroute.service.BlogServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {
    // Mock BlogRepository
    @Mock
    private BlogRepository blogRepository;
    // Inject Mocks to BlogServiceImpl
    @InjectMocks
    private BlogServiceImpl blogServiceImpl;
    @Mock
    private Blog blog, blog1;
    private List<Blog> blogList;
    private Optional optional;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        blog = new Blog(1, "SampleBlog", "Imneet", "SampleBlogforTesting");
        blog1 = new Blog(2, "Blog 1", "John", "Sample Blog 1 for Testing");
        optional = Optional.of(blog);
    }

    @AfterEach
    public void tearDown() {
        blog = null;
        blogRepository.deleteAll();
    }

    /*
     * write test case for saveBlog() method, by mocking the repository
     * */
    @Test
    public void givenBlogToSaveThenShouldReturnSavedBlog() {
        when(blogRepository.save(blog)).thenReturn(blog);
                assertThat(blogServiceImpl.saveBlog(blog)).isEqualTo(blog);
    }

    /*
     * write test case for asserting RuntimeException when saveBlog() method is called, by mocking the repository
     */
    @Test
    public void givenBlogToSaveThenShouldNotReturnSavedBlog() {
        Blog blog3 = new Blog(1, "SampleBlog", "Imneet", "SampleBlogforTesting");
        when(blogRepository.existsById(blog.getBlogId())).thenReturn(true);
        BlogAlreadyExistsException exception = assertThrows(
                BlogAlreadyExistsException.class,
                ()->blogServiceImpl.saveBlog(blog3)
        );
        String expected = "Blog with ID " + blog.getBlogId() + " already exists.";
        String actual = exception.getMessage();
        assert(expected.contains(actual));
    }

    /*
     * write test case for getAllBlogs() method, by mocking the repository
     */
    @Test
    public void givenGetAllBlogsThenShouldReturnListOfAllBlogs() {
        when(blogRepository.findAll()).thenReturn(new ArrayList<>(Collections.singleton(blog)));
        assertEquals(1, blogServiceImpl.getAllBlogs().size());
//    	assertThat(blogServiceImpl.getAllBlogs().get(0).getAuthorName()).isEqualTo(blog.getAuthorName());
    }

    /*
     * write test case for getBlogById() method, by mocking the repository
     */
    @Test
    public void givenBlogIdThenShouldReturnRespectiveBlog() {
        when(blogRepository.findById(blog.getBlogId())).thenReturn(optional);
        assertEquals(blog, blogServiceImpl.getBlogById(blog.getBlogId()));
    }

    /*
     * write test case for deleteBlog() method, by mocking the repository
     */
    @Test
    void givenBlogIdToDeleteThenShouldReturnDeletedBlog() {

        when(blogRepository.findById(1)).thenReturn(optional);
        blogServiceImpl.deleteBlog(blog.getBlogId());

        // Then
        verify(blogRepository, times(1)).deleteById(blog.getBlogId());
    }

    /*
     * write test case for asserting BlogNotFoundException when deleteBlog() method is called, by mocking the repository
     */
    @Test
    void givenBlogIdToDeleteThenShouldNotReturnDeletedBlog() {
        when(blogRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(BlogNotFoundException.class, ()->blogServiceImpl.deleteBlog(blog.getBlogId()));
    }

    /*
     * write test case for updateBlog() method, by mocking the repository
     */
    @Test
    public void givenBlogToUpdateThenShouldReturnUpdatedBlog() {
        Blog updatedBlog = new Blog(1, "SampleBlog", "Imneet", "SampleBlogforTestingUpdation");
        when(blogRepository.findById(1)).thenReturn(optional);
        when(blogRepository.save(any(Blog.class))).thenReturn(updatedBlog);
        assertEquals(updatedBlog, blogServiceImpl.updateBlog(blog));
    }

    /*
     * write test case for asserting BlogNotFoundException when updateBlog() method is called, by mocking the repository
     */
    @Test
    public void givenBlogToUpdateThenShouldNotReturnUpdatedBlog() {

        when(blogRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(BlogNotFoundException.class, ()->blogServiceImpl.updateBlog(blog));
    }


    /*
     * write test case for asserting BlogNotFoundException when getBlogById() method is called, by mocking the repository
     */
    @Test
    public void givenNonExistentBlogIdThenShouldThrowBlogNotFoundException() {
        when(blogRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(BlogNotFoundException.class, ()->blogServiceImpl.getBlogById(blog.getBlogId()));
    }

    /*
     * write test case for asserting BlogNotFoundException when updateBlog() method is called, by mocking the repository
     */
    @Test
    public void givenUpdateNonExistentBlogThenShouldThrowBlogNotFoundException() {
        when(blogRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(BlogNotFoundException.class, ()->blogServiceImpl.updateBlog(blog));
    }
}
