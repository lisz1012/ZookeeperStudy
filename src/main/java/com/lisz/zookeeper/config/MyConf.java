package com.lisz.zookeeper.config;

// 这个类才是未来应该关心的，配置的内容才是业务中最关心的，这个类的操作可能很复杂，这里先简化成一个conf字符串。至于zk怎么用，其实就是一个工具。
public class MyConf {

    private String conf;

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }
}
