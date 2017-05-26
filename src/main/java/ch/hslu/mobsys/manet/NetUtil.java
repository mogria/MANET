package ch.hslu.mobsys.manet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * This class is responsible for ...
 *
 * @author Moritz Küttel
 */
public final class NetUtil {
    private NetUtil() {
        // static utility class
    }

    public static InetAddress getMulticastInetAddress() throws UnknownHostException {
        return InetAddress.getByAddress(new byte[]{(byte)239, (byte)255, (byte)255, (byte)250});
    }
    public static InetSocketAddress getMulticastInetSocketAddress() throws UnknownHostException {
        return new InetSocketAddress(getMulticastInetAddress(), 1337);
    }

    public static InetSocketAddress getLoopbackInetSocketAddress() throws UnknownHostException {
        return new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 1337);
    }

    public static NetworkInterface getCorrectNetworkInterface(final boolean useLoopback) throws IOException {
        return Collections.list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .filter(ni -> {
                    try {
                        return ni.isLoopback() == useLoopback && ni.isUp(); // && ni.supportsMulticast();
                    } catch (SocketException ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IOException("Could not find suitable Ipv4 address to bind to"));
    }

    public static InetAddress getIpv4Address(final NetworkInterface ni) throws IOException {
        return Collections.list(ni.getInetAddresses())
                .stream()
                .filter(addr -> addr.getAddress().length == 4)
                .findFirst()
                .orElseThrow(() -> new IOException("Could not find suitable Ipv4 address to bind to"));
    }

}
