import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * 1. notifyElementRemoved(): 调用 remove() 删除元素时触发
 * 2. notifyElementExpired(): 元素超时时触发
 * 3. notifyElementEvicted(): 元素个数超过 maxElementsInMemory 被放到硬盘上时触发
 *
 * 注意: 这几个事件不会同时触发
 */
public class MyCacheEventListener implements CacheEventListener {
    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        System.out.println("notifyElementRemoved: " + element);
    }

    /**
     * put(), 但是缓存里没有，新创建
     * @param cache
     * @param element
     * @throws CacheException
     */
    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        System.out.println("notifyElementPut: " + element);
    }

    /**
     * put(), 但是已经存在缓存里，更新
     * @param cache
     * @param element
     * @throws CacheException
     */
    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        System.out.println("notifyElementUpdated: " + element);
    }

    /**
     * 注意: 元素过期不会自动触发, 下面 2 种情况时才会检查元素是否过期:
     *     1. 元素 x 有 get 操作时才检查它是否过期, 这时不会检查元素 y 是否过期
     *     2. 元素个数超过 maxElementsInMemory 时, 检查所有元素是否过期
     * @param cache
     * @param element
     */
    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        System.out.println("notifyElementExpired: " + element);
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        System.out.println("notifyElementEvicted: " + element);
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }
}
