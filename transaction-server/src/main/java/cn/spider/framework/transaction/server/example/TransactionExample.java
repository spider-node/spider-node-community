package cn.spider.framework.transaction.server.example;

import cn.spider.framework.transaction.server.example.enums.ExampleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionExample {

    // 请求链路id
    private String requestId;

    /**
     * 事务组信息,key是事务组id，value是事务组信息
     */
    private Map<String, Set<String>> transactionElementGroup;

    /**
     * 实例状态
     */
    private ExampleStatus exampleStatus;
}
