package com.mk.eap.component.oid.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mk.eap.common.utils.PropertyUtil;

import lombok.extern.apachecommons.CommonsLog;

/**
 * 基于Twitter-Snowflake算法和Zookeeper实现的分布式ID生成器（64Bit自增ID），参考https://github.com/twitter/snowflake<br/>
 * <p>
 * ID为64位非负long类型整数，结构如下<br/>
 * <ui>
 * <li>1 bits 固定为0</li>
 * <li>41 bits 时间戳（time stamp）</li>
 * <li>10 bits 集群机器ID（machine id）或进程ID，可简单理解为集群机器ID</li>
 * <li>12 bits 序列号（sequence number）</li>
 * </ui>
 *
 */
@Component
@CommonsLog
public class ZKSnowflakeIDGenerator extends AbstractSnowflakeIDGenerator {

    private final static Logger logger = LoggerFactory.getLogger(ZKSnowflakeIDGenerator.class);
    /**
     * ZK管理器
     */
    @Autowired
    private ZkManager zkManager;
    /**
     * Zk连接状态监听器
     */
    private ZkConnectionStateListener stateListener = new ZkConnectionStateListener(this);
    /**
     * ID生成器是否处于工作状态
     */
    private volatile boolean isWorking = false;
 //前端js最大支持16个数字而标准的生成最大为19个数字所以workerIdBits、datacenterIdBits、sequenceBits一共缩减9位(3个数字)
 // ==============================Fields===========================================
    /** 开始时间截 (2018-02-09 16：36) */
    private final long twepoch = 1518165360613L;

    /** 机器id所占的位数 */
    private final long workerIdBits = 3L;//5L;

    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 4L;//5L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 6L;//12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 工作机器ID(0~31) */
    private long workerId;

    /** 数据中心ID(0~31) */
    private long datacenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
    

    public ZKSnowflakeIDGenerator(ZkManager zkManager) throws UnsupportedEncodingException, InterruptedException {
        this.zkManager = zkManager;
    }

    public ZKSnowflakeIDGenerator(){
    	String datacenterId = PropertyUtil.getPropertyByKey("dataCenterId");
        log.warn("dataCenterId=" + datacenterId);
        if (null != datacenterId && datacenterId.trim().length() > 0) {
        	this.datacenterId = Long.parseLong(datacenterId);
        } else {
        	this.datacenterId = 0L;
        }
    }

    @PostConstruct
    public void init() throws Exception {
        // 监听连接状态
        zkManager.addConnectionStateListener(stateListener);
        // 生成初始集群机器ID
        rebuildDatacenterId();
        isWorking = true;

        sequence = 0L;
        lastTimestamp = -1L;

        log.info("idGenerator is isWorking!");
    }


    @Override
    public long getId() {
    	logger.warn("集群号："+this.datacenterId + ", 机器号：" + this.workerId);
        if (isWorking) {
            return nextId();
        }
        // 处于异常状态（会话过期，连接中断等），ID生成器停止工作
        throw new RuntimeException("IDGenerator is not isWorking!");
    }

    /**
     * 获取下一个ID
     *
     * @return
     */
    private synchronized long nextId() {
    	long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }
    
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * 重新生成集群机器ID，两种情况下需要rebuild，且isWorking必须为false<br/>
     * <ui>
     * <li>应用初始化，获取初始集群机器ID</li>
     * <li>重连后的恢复操作，由ZK连接状态监听器触发</li>
     * <ui/>
     *
     * @throws Exception
     */
    public void rebuildDatacenterId() throws Exception {
        if (isWorking) {
            throw new RuntimeException("IDGenerator is isWorking , no need to rebuild datacenter id ");
        }
        workerId = buildWorkerId();
    }

    /**
     * 生成唯一的集群机器ID
     *
     * @return
     */
    private synchronized long buildWorkerId() throws Exception {
        try {
            // 已分配的集群机器ID
            List<String> usedMachineIds = zkManager.getChildren();
//            log.warn("usedMachineIds.size()="+usedMachineIds.size());
//            log.warn("usedMachineIds="+usedMachineIds.get(0));
//            if (datacenterIdBits <= usedMachineIds.size()) {
//                throw new RuntimeException(String.format("reach limit of max_datacenter_id:%s , useIds.size:%s",
//                		datacenterIdBits, usedMachineIds.size()));
//            }
            // 尚未分配的集群机器ID
            List<Long> unusedMachineIds = LongStream.range(0, 1<<workerIdBits)
                    .filter(value -> !usedMachineIds.contains(String.valueOf(value)))
                    .boxed().collect(Collectors.toList());
            // 随机选择一个尚未分配的集群机器ID
            Long workerId = unusedMachineIds.get(RANDOM.nextInt(unusedMachineIds.size()));
            if (zkManager.tryCreate(ZKPaths.makePath("/", workerId.toString()), true)) {
                // 成功创建则返回
                return workerId;
            }
            // 创建失败则递归调用
            Thread.sleep(RANDOM.nextInt(500)); // 为了降低竞争冲突概率，可选
            return buildWorkerId();
        } catch (KeeperException.NoNodeException e) {
            // zkManager.getChildren()可能数据节点尚不存在
            long workerId = 0;
            if (zkManager.tryCreate(String.valueOf(workerId), true)) {
                return workerId;
            }
            Thread.sleep(RANDOM.nextInt(500));
            return buildWorkerId();
        }
    }

    /**
     * 暂停工作，进入休眠状态
     */
    public void suspend() {
        this.isWorking = false;
    }

    /**
     * 恢复工作
     */
    public void recover() {
        this.isWorking = true;
    }

    /**
     * Spring容器关闭前先停止ID生成器的工作，并关闭ZK管理器
     */
    @Override
    @PreDestroy
    public void close() throws IOException {
        log.info("close zkManager before shutdown...");
        suspend();
        CloseableUtils.closeQuietly(zkManager);
    }

    /**
     * 重新与Zookeeper建立连接，由ZK连接状态监听器触发
     */
    public void reconnect() {
        log.info("try to reconnect...");
        if (isWorking) {
            return;
        }
        try {
            zkManager.connect();
        } catch (Exception e) {
            log.error("reconnect fail!!", e);
        }
    }
}
