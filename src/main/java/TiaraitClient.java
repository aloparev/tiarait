import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TiaraitClient
{
    public static void main( String[] args )
    {
        System.out.println( "Hello Java:" + System.getProperty("java.version"));
        NetworkClient networkClient = new NetworkClient(null, "abeta");

        networkClient.getMyPlayerNumber(); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)

        while (networkClient.isAlive()) {
// steuerung
//            float x = networkClient.getX(player, botNr);
//            float y = networkClient.getY(player, botNr);

            networkClient.isWall(7, 11); //true wenn bei Koordinate 7,11 ein Hindernis steht

            networkClient.setMoveDirection(0, 0.1f, -0.8f);
            networkClient.setMoveDirection(1, 0.1f, 1.8f);
            networkClient.setMoveDirection(2, -5.1f, -0.8f);

            ColorChange cc;
            while ((cc = networkClient.getNextColorChange()) != null) {
// cc in eigene Struktur einarbeiten
//                z.B. brett[cc.x][cc.y] = cc.newColor;
//                cc.newColor; //0 = leer, 1-4 = spieler
                log.info("cc=" + cc);
            }
        }
    }
}
