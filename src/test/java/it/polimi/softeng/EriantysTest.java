package it.polimi.softeng;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EriantysTest {

    private boolean checkErrorFromArgs(String[] args, String testMessage) {
        String msg=null;
        try {
            Eriantys.main(args);
        }
        catch (IllegalArgumentException iae) {
            msg=iae.getMessage();
        }
        if (msg==null) {
            return testMessage==null;
        }
        if (!msg.equals(testMessage)) {
            System.out.println(msg);
            return false;
        }

        return true;
    }

    @Test
    public void testNoArguments() {
        String[] args={};
        assertTrue(checkErrorFromArgs(args,null));
    }
    @Test
    public void testHelpMessage() {
        String[] args={"--help"};
        assertTrue(checkErrorFromArgs(args,null));
    }
    @Test
    public void testServerClientConflict() {
        String[] args={"-c","-s"};
        String[] args2={"-s","-c"};
        assertTrue(checkErrorFromArgs(args,"Already client: cannot be server"));
        assertTrue(checkErrorFromArgs(args2,"Already server: cannot be client"));
    }
    @Test
    public void testDuplicateIP() {
        String[] args={"-ip","0.0.0.0","-ip","0.0.0.1"};
        assertTrue(checkErrorFromArgs(args,"Already set ip"));
    }

    @Test
    public void testDuplicatePort() {
        String[] args={"-p","0","-p","1"};
        assertTrue(checkErrorFromArgs(args,"Already set port"));
    }
}
