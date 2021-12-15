import com.ecwid.consul.v1.ConsulClient;

public class demo {
    public static void main(String[] args) {
        ConsulClient consulClient = new ConsulClient("localhost");

        String key = consulClient.getKVValue("jwt.key")
                .getValue().getDecodedValue();
        System.out.println(key);
    }
}
