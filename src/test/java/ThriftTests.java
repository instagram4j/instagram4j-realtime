import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import com.github.instagram4j.realtime.utils.ThriftUtil;
import com.github.instagram4j.realtime.utils.ThriftUtil.ThriftField;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// [2, 4, 6, 1, 0, 6, 8, 10, 13, 72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33]
// [2, 4, 6, 1, 0, 6, 8, 10, 13, 72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33]
public class ThriftTests {
    
    @ToString
    public static class NestedObj {
        @ThriftField(id = 1)
        public int i64_2 = 9;
    }
    
    @ToString
    public static class TestObject {
        @ThriftField(id = 1)
        public short i16 = 1;
        @ThriftField(id = 2)
        public int i32 = 2;
        @ThriftField(id = 3)
        public long i64 = 3;
        @ThriftField(id = 4)
        public boolean bool = true;
        @ThriftField(id = 5)
        public Integer[] list = { 3, 4, 5 };
        @ThriftField(id = 6)
        public String string = "Hello, World!";
        @ThriftField(id = 7)
        public NestedObj nested = new NestedObj();
    }
    
    @Test
    public void serialize_deserialize() throws Exception {
        TestObject obj = new TestObject();
        
        log.info(obj.toString());
        
        byte[] arr = ThriftUtil.serialize(obj);
        
        log.info("Serialized {}", Arrays.toString(arr));
        
        TestObject o = ThriftUtil.deserialize(arr, TestObject.class);
        
        log.info("deserialized {}", o.toString());
        
        Assert.assertEquals(obj.toString(), o.toString());
    }
    
}
