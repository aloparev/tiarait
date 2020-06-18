import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {
    static Board bb;

    @Before
    public void setup() {
        bb = new Board(1);
    }

    @Test
    public void getZz() {
        assertEquals(146, bb.getZz(18, 4));
        assertEquals(395, bb.getZz(11, 12));
    }

    @Test
    public void getXy() {
        Cell one = bb.getXy(146);
        Cell two = bb.getXy(395);

        assertEquals(18, one.x);
        assertEquals(4, one.y);

        assertEquals(11, two.x);
        assertEquals(12, two.y);
    }
}