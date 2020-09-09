package com.aspect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.annotation.RedisLock;
import com.util.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * redis锁拦截器实现
 *
 * @author MAZHEN
 */
@Aspect
@Component
public class RedisLockInterceptor {
    private static final Integer MAX_RETRY_COUNT = 10;
    private static final String LOCK_PRE_FIX = "lockPreFix";
    private static final String TIME_OUT = "timeOut";

    //@Autowired
    //private RedisManager redisManager;

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.annotation.RedisLock)")
    public void redisLockAspect() {
    }

    @Around("redisLockAspect()")
    public Map<String, Object> lockAroundAction(ProceedingJoinPoint proceeding) {
        //获取redis锁
        Map<String, Object> getLockResult = this.getLock(proceeding, 0, System.currentTimeMillis());
        return getLockResult;
    }

    /**
     * 获取锁
     *
     * @param proceeding
     * @return
     */
    private Map<String, Object> getLock(ProceedingJoinPoint proceeding, int count, long currentTime) {
        //获取注解中的参数
        Map<String, Object> annotationArgs = this.getAnnotationArgs(proceeding);
        String lockPrefix = (String) annotationArgs.get(LOCK_PRE_FIX);
        long expire = (long) annotationArgs.get(TIME_OUT);
        String key = this.getFirstArg(proceeding);
        if (StringUtils.isEmpty(lockPrefix) || StringUtils.isEmpty(key)) {
            return this.argErrResult("锁前缀或业务参数不能为空");
        }
        String lockName = lockPrefix + "_" + key;
        String value = String.valueOf(currentTime);
        if (redisUtil.set(lockName, value)) {
            //获取锁成功
            redisUtil.expire(lockName, expire);
            return this.buildSuccessResult();
        } else {
            //获取锁失败,为防止其它线程正在设置过时时间时误删，添加第一个条件
            if ((System.currentTimeMillis() - currentTime > 5000)
                    && (redisUtil.ttl(lockName) < 0
                    || System.currentTimeMillis() - currentTime > expire)) {
                //强制删除锁，并尝试再次获取锁
                redisUtil.del(lockName);
                if (count < MAX_RETRY_COUNT) {
                    return getLock(proceeding, count++, currentTime);
                }
            }
            return this.buildGetLockErrorResult("请重试！！！");
        }
    }

    /**
     * 获取锁参数
     *
     * @param proceeding
     * @return
     */
    private Map<String, Object> getAnnotationArgs(ProceedingJoinPoint proceeding) {
        Class target = proceeding.getTarget().getClass();
        Method[] methods = target.getMethods();
        String methodName = proceeding.getSignature().getName();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Map<String, Object> result = new HashMap<String, Object>();
                RedisLock redisLock = method.getAnnotation(RedisLock.class);
                result.put(LOCK_PRE_FIX, redisLock.lockPrefix());
                result.put(TIME_OUT, redisLock.timeUnit().toSeconds(redisLock.timeOut()));
                return result;
            }
        }
        return null;
    }

    /**
     * 获取第一个String类型的参数为锁的业务参数
     *
     * @param proceeding
     * @return
     */
    public String getFirstArg(ProceedingJoinPoint proceeding) {
        Object[] args = proceeding.getArgs();
        if (args != null && args.length > 0) {
            for (Object object : args) {
                String type = object.getClass().getName();
                if ("java.lang.String".equals(type)) {
                    return (String) object;
                }
            }
        }
        return null;
    }

    public Map<String, Object> argErrResult(String mes) {
        Map<String, Object> result = new HashMap<String, Object>();
        //TODO
        //result.put("code", "9");
        result.put("msg", mes);
        return result;
    }

    public Map<String, Object> buildGetLockErrorResult(String mes) {
        Map<String, Object> result = new HashMap<String, Object>();
        //TODO
        //result.put("code", "9");
        result.put("msg", mes);
        return result;
    }

    public Map<String, Object> buildSuccessResult() {
        Map<String, Object> result = new HashMap<String, Object>();
        //TODO
        //result.put("code", "1");
        result.put("msg", "处理成功");
        return result;
    }
}
