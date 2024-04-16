// FILEPATH: /C:/Users/922320/Videos/files/movie-backend/wishlist-service/src/test/java/com/cts/wishlistservice/controller/WishlistControllerTest.java

import com.cts.wishlistservice.controller.WishlistController;
import com.cts.wishlistservice.dto.MovieDto;
import com.cts.wishlistservice.dto.WishlistDto;
import com.cts.wishlistservice.service.JwtService;
import com.cts.wishlistservice.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private WishlistController wishlistController;

    private String token = "Bearer validToken";
    private String username = "testUser";
    private MovieDto movieDto = new MovieDto();

    @BeforeEach
    public void setup() {
        when(jwtService.isTokenValid(token.substring(7), username)).thenReturn(true);
    }

    @Test
    public void testGetWishlist() {
        when(wishlistService.getWishlists(username)).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = wishlistController.getWishlist(token, username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(wishlistService, times(1)).getWishlists(username);
    }

    @Test
    public void testDeleteWishlist() {
        String id = "1";
        when(wishlistService.deleteWishlist(username, id)).thenReturn(new WishlistDto());

        ResponseEntity<Object> response = wishlistController.deleteWishlist(token, username, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(wishlistService, times(1)).deleteWishlist(username, id);
    }

    @Test
    public void testAddWishlist() {
        when(wishlistService.addWishlist(username, movieDto)).thenReturn(new WishlistDto());

        ResponseEntity<Object> response = wishlistController.addWishlist(token, username, movieDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(wishlistService, times(1)).addWishlist(username, movieDto);
    }
}
