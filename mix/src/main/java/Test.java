import org.apache.commons.lang3.StringUtils;

public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 1024; ++i) {
            foo(i);
        }
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
