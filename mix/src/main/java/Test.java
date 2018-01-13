import org.apache.commons.lang3.StringUtils;

public class Test {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(Long.toBinaryString(System.currentTimeMillis()));
        System.out.println(Long.MAX_VALUE);
        System.out.println((0xFFFFFFFFFFL*2+1) / 1000.0 / 3600.0 / 24.0 / 365);
        System.out.println(Math.pow(2, 41) / 1000.0 / 3600.0 / 24.0 / 365);
    }

    public static void foo(long machineId) {
        long workerIdBits = 5;
        long maxDatacenterId = 31;
        long maxWorkerId = 31;

        long datacenterId = (machineId >> workerIdBits) & maxDatacenterId;
        long workerId = machineId & maxWorkerId;

        System.out.print(machineId + " => " + StringUtils.leftPad(Long.toBinaryString(machineId), 10, "0") + ": ");
        System.out.print(StringUtils.leftPad(Long.toBinaryString(datacenterId), 5, "0") + "-");
        System.out.println(StringUtils.leftPad(Long.toBinaryString(workerId), 5, "0"));
    }
}
