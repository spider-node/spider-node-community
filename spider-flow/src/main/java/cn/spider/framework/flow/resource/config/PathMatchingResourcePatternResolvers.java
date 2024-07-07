package cn.spider.framework.flow.resource.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;

public class PathMatchingResourcePatternResolvers extends PathMatchingResourcePatternResolver {
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith("classpath*:")) {
            return this.getPathMatcher().isPattern(locationPattern.substring("classpath*:".length())) ? this.findPathMatchingResources(locationPattern) : this.findAllClassPathResources(locationPattern.substring("classpath*:".length()));
        } else {
            return new Resource[]{this.getResourceLoader().getResource(locationPattern)};
        }
    }
}
