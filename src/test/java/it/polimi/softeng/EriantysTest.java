package it.polimi.softeng;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EriantysTest {

    @Test
    public void testNoArguments() {
        String[] args={};
        assertEquals(0,Eriantys.main(args));
    }
    @Test
    public void testHelpMessage() {
        String[] args={"--help"};
        assertEquals(0,Eriantys.main(args));
    }
    @Test
    public void testServerClientConflict() {
        String[] args={"-c","-s"};
        assertEquals(-1,Eriantys.main(args));
        String[] args2={"-s","-c"};
        assertEquals(-1,Eriantys.main(args2));
    }

    @Test
    public void testDuplicateIP() {
        String[] args={"-ip","0.0.0.0","-ip","0.0.0.1"};
        assertEquals(-1,Eriantys.main(args));
    }

    @Test
    public void testDuplicatePort() {
        String[] args={"-p","0","-p","1"};
        assertEquals(-1,Eriantys.main(args));
    }
}
