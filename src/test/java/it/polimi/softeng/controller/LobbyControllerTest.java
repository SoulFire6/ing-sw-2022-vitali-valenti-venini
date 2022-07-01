package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for lobby controller
 */
public class LobbyControllerTest {
    LobbyController controller;

    /**
     * Tests creating a new game
     */
    @Test
    public void testCreateGame() {
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add("test");
        assertThrows(InvalidPlayerNumException.class, ()->controller=new LobbyController(playerNames,false,"testInvalidPlayerNum",null));
    }
}
