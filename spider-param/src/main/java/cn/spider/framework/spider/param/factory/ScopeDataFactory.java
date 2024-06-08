package cn.spider.framework.spider.param.factory;

import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.spider.param.ParamVerticle;
import cn.spider.framework.spider.param.example.ParamExample;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 构造参数实例
 */
public class ScopeDataFactory extends BasePooledObjectFactory<ParamExample> {
    @Override
    public ParamExample create() throws Exception {
        RocksdbUtil rocksdbUtil = ParamVerticle.factory.getBean(RocksdbUtil.class);
        ParamExample paramExample = new ParamExample(rocksdbUtil);
        return paramExample;
    }

    @Override
    public PooledObject<ParamExample> wrap(ParamExample paramExample) {
        return new DefaultPooledObject<>(paramExample);
    }

    @Override
    public void destroyObject(PooledObject<ParamExample> p, DestroyMode destroyMode) throws Exception {
        super.destroyObject(p, destroyMode);
    }
}
