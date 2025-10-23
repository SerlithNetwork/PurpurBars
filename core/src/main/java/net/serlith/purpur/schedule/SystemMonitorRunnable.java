package net.serlith.purpur.schedule;

import net.serlith.purpur.util.RollingAverage;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

public class SystemMonitorRunnable implements Runnable {

    private static final OperatingSystemMXBean OS_BEAN;

    private static final RollingAverage SYSTEM_AVERAGE = new RollingAverage(10);
    private static final RollingAverage PROCESS_AVERAGE = new RollingAverage(10);

    static {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            OS_BEAN = JMX.newMXBeanProxy(mbs, name, OperatingSystemMXBean.class);
        } catch (Exception e) {
            throw new UnsupportedOperationException("OperatingSystemMXBean is not supported by the system", e);
        }
    }

    @Override
    public void run() {
        BigDecimal systemCpuLoad = new BigDecimal(OS_BEAN.getSystemCpuLoad());
        BigDecimal processCpuLoad = new BigDecimal(OS_BEAN.getProcessCpuLoad());

        if (systemCpuLoad.signum() != -1) {
            SYSTEM_AVERAGE.add(systemCpuLoad);
        }
        if (processCpuLoad.signum() != -1) {
            PROCESS_AVERAGE.add(processCpuLoad);
        }
    }

    public double getSystemCpuLoad() {
        return SYSTEM_AVERAGE.getAverage();
    }

    public double getProcessCpuLoad() {
        return PROCESS_AVERAGE.getAverage();
    }

    public interface OperatingSystemMXBean {
        double getSystemCpuLoad();
        double getProcessCpuLoad();
    }

}
